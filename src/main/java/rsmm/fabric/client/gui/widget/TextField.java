package rsmm.fabric.client.gui.widget;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import rsmm.fabric.client.gui.element.IElement;

public class TextField extends TextFieldWidget implements IElement {
	
	private final Supplier<String> textSupplier;
	
	private boolean deaf;
	
	public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Supplier<String> textSupplier, Consumer<String> textChangedListener) {
		super(textRenderer, x + 1, y + 1, width - 2, height - 2, new LiteralText(textSupplier.get()));
		
		this.textSupplier = textSupplier;
		
		this.updateMessage();
		this.setChangedListener((text) -> {
			if (!deaf) {
				textChangedListener.accept(text);
			}
		});
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
		return super.mouseReleased(mouseX, mouseY, button);
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
		return super.keyPressed(keyCode, scanCode, modifiers) || isFocused();
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
		setFocused(true);
	}
	
	@Override
	public void unfocus() {
		setFocused(false);
		updateMessage();
	}
	
	@Override
	public int getX() {
		return x - 1;
	}
	
	@Override
	public void setX(int x) {
		this.x = x + 1;
	}
	
	@Override
	public int getY() {
		return y - 1;
	}
	
	@Override
	public void setY(int y) {
		this.y = y + 1;
	}
	
	@Override
	public int getWidth() {
		return width + 2;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width - 2;
	}
	
	@Override
	public int getHeight() {
		return height + 2;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height - 2;
	}
	
	public void updateMessage() {
		if (!isFocused()) {
			deaf = true;
			setText(textSupplier.get());
			deaf = false;
		}
	}
}
