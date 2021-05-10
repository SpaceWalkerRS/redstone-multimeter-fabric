package rsmm.fabric.client.gui.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import rsmm.fabric.client.MultimeterClient;

public class InvisibleButton extends Button {
	
	public InvisibleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, OnPress onPress) {
		super(client, x, y, width, height, textSupplier, onPress);
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		String message = getMessage();
		
		if (!message.isEmpty()) {
			MinecraftClient minecraftClient = client.getMinecraftClient();
			TextRenderer font = minecraftClient.textRenderer;
			
			int rgb = active ? (isHovered() ? 0xC0C0C0 : 0xFFFFFF) : 0xA0A0A0;
			int a = MathHelper.ceil(alpha * 255.0F);
			int color = rgb | (a << 24);
			
			int textWidth = font.getStringWidth(message);
			int textX = x + (width - textWidth) / 2;
			int textY = y + (height - font.fontHeight + 1) / 2;
			
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(
				GlStateManager.SrcFactor.SRC_ALPHA,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
			);
			RenderSystem.enableDepthTest();
			
			font.drawWithShadow(message, textX, textY, color);
		}
	}
}
