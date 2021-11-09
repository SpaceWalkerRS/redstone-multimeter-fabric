package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.OptionsScreen;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends Screen {
	
	private static final int ROW_HEIGHT = 24;
	
	protected SettingsScreenMixin(Text title) {
		super(title);
	}
	
	@Inject(
			method = "init",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		MultimeterClient multimeterClient = ((IMinecraftClient)minecraft).getMultimeterClient();
		
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
		
		String message = String.format("%s Options", RedstoneMultimeterMod.MOD_NAME);
		ButtonWidget rsmmOptionsButton = new ButtonWidget(x, y, buttonWidth, buttonHeight, message, button -> multimeterClient.openScreen(new OptionsScreen(multimeterClient)));
		children.add(index, rsmmOptionsButton);
		buttons.add(index, rsmmOptionsButton);
		
		doneButton.y += ROW_HEIGHT;
	}
}
