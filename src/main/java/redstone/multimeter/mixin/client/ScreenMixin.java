package redstone.multimeter.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.OptionsScreen;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(Screen.class)
public class ScreenMixin {
	
	private static final int ROW_HEIGHT = 24;
	
	@Shadow @Final private List<Element> children;
	@Shadow @Final private List<Selectable> selectables;
	@Shadow @Final private List<Drawable> drawables;
	
	@Inject(
			method = "init(Lnet/minecraft/client/MinecraftClient;II)V",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
		if (!((Screen)(Object)this instanceof net.minecraft.client.gui.screen.option.OptionsScreen)) {
			return;
		}
		
		MultimeterClient multimeterClient = ((IMinecraftClient)client).getMultimeterClient();
		
		int index = children.size() - 1;
		Element option = children.get(index - 1);
		Element done = children.get(index);
		
		if (!(option instanceof ButtonWidget && done instanceof ButtonWidget)) {
			return;
		}
		
		ButtonWidget optionButton = (ButtonWidget)option;
		ButtonWidget doneButton = (ButtonWidget)done;
		
		int centerX = width / 2;
		int buttonWidth = 10 + 2 * IButton.DEFAULT_WIDTH;
		int buttonHeight = IButton.DEFAULT_HEIGHT;
		int x = centerX - 5 - IButton.DEFAULT_WIDTH;
		int y = optionButton.y + ROW_HEIGHT;
		
		Text message = new LiteralText(String.format("%s Options", RedstoneMultimeterMod.MOD_NAME));
		ButtonWidget rsmmOptionsButton = new ButtonWidget(x, y, buttonWidth, buttonHeight, message, button -> multimeterClient.openScreen(new OptionsScreen(multimeterClient)));
		children.add(index, rsmmOptionsButton);
		selectables.add(index, rsmmOptionsButton);
		drawables.add(index, rsmmOptionsButton);
		
		doneButton.y += ROW_HEIGHT;
	}
}
