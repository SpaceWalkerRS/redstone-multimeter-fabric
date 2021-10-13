package rsmm.fabric.client.gui.element;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.action.MousePress;
import rsmm.fabric.client.gui.element.action.MouseRelease;
import rsmm.fabric.client.gui.widget.Button;

public class TextElement implements IElement {
	
	private static final int SPACING = 2;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final Supplier<List<Text>> textSupplier;
	private final Supplier<List<Text>> tooltipSupplier;
	private final MousePress<TextElement> mousePress;
	private final MouseRelease<TextElement> mouseRelease;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private List<Text> text;
	private boolean visible;
	private boolean rightAligned;
	
	public TextElement(MultimeterClient client, int x, int y, boolean rightAligned, Supplier<List<Text>> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<TextElement> mousePress, MouseRelease<TextElement> mouseRelease) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.textSupplier = textSupplier;
		this.tooltipSupplier = tooltipSupplier;
		this.mousePress = mousePress;
		this.mouseRelease = mouseRelease;
		
		this.x = x;
		this.y = y;
		this.visible = true;
		this.rightAligned = rightAligned;
		
		this.update();
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int left = x;
		int right = x + width;
		int textY = y;
		
		for (Text t : text) {
			int textX = rightAligned ? right - font.getWidth(t) : left;
			drawText(matrices, textX, textY, t);
			
			textY += font.fontHeight + SPACING;
		}
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if (mousePress.press(this)) {
			Button.playClickSound(client);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (mouseRelease.release(this)) {
			Button.playClickSound(client);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
		return false;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
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
	
	public void update() {
		text = textSupplier.get();
		
		updateWidth();
		updateHeight();
	}
	
	protected void updateWidth() {
		width = 0;
		
		for (Text t : text) {
			int textWidth = font.getWidth(t);
			
			if (textWidth > width) {
				width = textWidth;
			}
		}
	}
	
	protected void updateHeight() {
		height = (text.size() - 1) * (font.fontHeight + SPACING) + font.fontHeight;
	}
	
	protected void drawText(MatrixStack matrices, int x, int y, Text text) {
		font.draw(matrices, text, x, y, 0xFFFFFFFF);
	}
}
