package rsmm.fabric.client.gui.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.element.action.MousePress;

public class Button extends ButtonWidget implements IElement {
	
	protected final MultimeterClient client;
	protected final Supplier<Text> textSupplier;
	protected final MousePress<Button> onPress;
	
	public Button(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, MousePress<Button> onPress) {
		super(x, y, width, height, textSupplier.get(), button -> {});
		
		this.client = client;
		this.textSupplier = textSupplier;
		this.onPress = onPress;
	}
	
	@Override
	public void onPress() {
		onPress.press(this);
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		TextureManager textureManager = minecraftClient.getTextureManager();
		TextRenderer font = minecraftClient.textRenderer;
		
		textureManager.bindTexture(WIDGETS_LOCATION);
		
		int i = getYImage(isHovered());
		int halfWidth = width / 2;
		int topBorder = 2;
		
		int x0 = x;
		int x1 = x + halfWidth;
		int y0 = y;
		int y1 = y + topBorder;
		int textureX0 = 0;
		int textureX1 = 200 - halfWidth;
		int textureY0 = 46 + i * 20;
		int textureY1 = textureY0 + 20 - height + topBorder;
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		drawTexture(matrices, x0, y0, textureX0, textureY0, halfWidth, topBorder);
		drawTexture(matrices, x1, y0, textureX1, textureY0, halfWidth, topBorder);
		drawTexture(matrices, x0, y1, textureX0, textureY1, halfWidth, height - topBorder);
		drawTexture(matrices, x1, y1, textureX1, textureY1, halfWidth, height - topBorder);
		
		int rgb = active ? 0xFFFFFF : 0xA0A0A0;
		int a = MathHelper.ceil(alpha * 255.0F);
		int color = rgb | (a << 24);
		
		Text message = getMessage();
		int textWidth = font.getWidth(message);
		int textX = x + (width - textWidth) / 2;
		int textY = y + (height - font.fontHeight + 1) / 2;
		
		font.drawWithShadow(matrices, message, textX, textY, color);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		return isHovered(mouseX, mouseY) && super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
		return super.charTyped(chr, modifiers);
	}
	
	@Override
	public boolean isDraggingMouse() {
		return false;
	}
	
	@Override
	public void setDraggingMouse(boolean dragging) {
		
	}
	
	@Override
	public void onRemoved() {
		
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public void unfocus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void updateMessage() {
		setMessage(textSupplier.get());
	}
	
	public static void playClickSound(MultimeterClient client) {
		SoundManager soundManager = client.getMinecraftClient().getSoundManager();
		SoundInstance sound = PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F);
		soundManager.play(sound);
	}
}
