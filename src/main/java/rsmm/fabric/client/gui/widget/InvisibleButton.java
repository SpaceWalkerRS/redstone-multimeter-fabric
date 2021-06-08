package rsmm.fabric.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.action.MousePress;

public class InvisibleButton extends Button {
	
	public InvisibleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, MousePress<Button> onPress) {
		this(client, x, y, width, height, textSupplier, () -> Collections.emptyList(), onPress);
	}
	
	public InvisibleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<Button> onPress) {
		super(client, x, y, width, height, textSupplier, tooltipSupplier, onPress);
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		String message = getMessage();
		
		if (!message.isEmpty()) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			TextRenderer font = minecraftClient.textRenderer;
			
			int rgb = active ? (isHovered() ? 0xC0C0C0 : 0xFFFFFF) : 0x909090;
			int a = MathHelper.ceil(alpha * 255.0F);
			int color = rgb | (a << 24);
			
			int textWidth = font.getStringWidth(message);
			int textX = x + width - (width + textWidth) / 2;
			int textY = y + (height - font.fontHeight) / 2 + 1;
			
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO
			);
			GlStateManager.blendFunc(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
			);
			
			font.drawWithShadow(message, textX, textY, color);
		}
	}
}
