package rsmm.fabric.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
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

public class Button extends ButtonWidget implements IElement, IButton {
	
	protected final MultimeterClient client;
	protected final TextRenderer font;
	protected final Supplier<Text> textSupplier;
	protected final Supplier<List<Text>> tooltipSupplier;
	protected final MousePress<Button> onPress;
	
	public Button(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, MousePress<Button> onPress) {
		this(client, x, y, width, height, textSupplier, () -> Collections.emptyList(), onPress);
	}
	
	public Button(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<Button> onPress) {
		super(x, y, width, height, textSupplier.get(), button -> {});
		
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.textSupplier = textSupplier;
		this.tooltipSupplier = tooltipSupplier;
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
		
		textureManager.bindTexture(WIDGETS_TEXTURE);
		
		int i = getYImage(isHovered());
		int leftWidth = width / 2;
		int rightWidth = width - leftWidth;
		int topBorder = 2;
		
		int x0 = x;
		int x1 = x + leftWidth;
		int y0 = y;
		int y1 = y + topBorder;
		int textureX0 = 0;
		int textureX1 = 200 - rightWidth;
		int textureY0 = 46 + i * 20;
		int textureY1 = textureY0 + 20 - height + topBorder;
		
		RenderSystem.setShader(() -> GameRenderer.getPositionTexShader());
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		drawTexture(matrices, x0, y0, textureX0, textureY0, leftWidth, topBorder);
		drawTexture(matrices, x1, y0, textureX1, textureY0, rightWidth, topBorder);
		drawTexture(matrices, x0, y1, textureX0, textureY1, leftWidth, height - topBorder);
		drawTexture(matrices, x1, y1, textureX1, textureY1, rightWidth, height - topBorder);
		
		int rgb = active ? 0xFFFFFF : 0xA0A0A0;
		int a = MathHelper.ceil(alpha * 255.0F);
		int color = (a << 24) | rgb;
		
		Text message = getMessage();
		int textX = getTextX(message);
		int textY = getTextY();
		
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
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public List<Text> getTooltip(int mouseX, int mouseY) {
		return tooltipSupplier.get();
	}
	
	@Override
	public void update() {
		setMessage(textSupplier.get());
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int getTextX() {
		return getTextX(getMessage());
	}
	
	protected int getTextX(Text text) {
		return x + width - (width + font.getWidth(text)) / 2;
	}
	
	public int getTextY() {
		return y + height - (height + font.fontHeight) / 2;
	}
	
	public static void playClickSound(MultimeterClient client) {
		SoundManager soundManager = client.getMinecraftClient().getSoundManager();
		SoundInstance sound = PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F);
		soundManager.play(sound);
	}
}
