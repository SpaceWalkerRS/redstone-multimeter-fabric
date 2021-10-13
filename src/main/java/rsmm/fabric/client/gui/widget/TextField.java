package rsmm.fabric.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TextField extends TextFieldWidget implements IButton {
	
	private final Supplier<String> textSupplier;
	private final Supplier<List<Text>> tooltipSupplier;
	
	private boolean deaf;
	
	public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Supplier<String> textSupplier, Consumer<String> textChangedListener) {
		this(textRenderer, x, y, width, height, textSupplier, () -> Collections.emptyList(), textChangedListener);
	}
	
	public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Supplier<String> textSupplier, Supplier<List<Text>> tooltipSupplier, Consumer<String> textChangedListener) {
		super(textRenderer, x + 1, y + 1, width - 2, height - 2, new LiteralText(textSupplier.get()));
		
		this.textSupplier = textSupplier;
		this.tooltipSupplier = tooltipSupplier;
		
		this.update();
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
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (isFocused()) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				unfocus();
			}
			
			return true;
		}
		
		return false;
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
		update();
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
	public int getHeight() {
		return height + 2;
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
		if (!isFocused()) {
			deaf = true;
			setText(textSupplier.get());
			deaf = false;
		}
	}
	
	@Override
	public boolean isActive() {
		return active && super.isActive();
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
}
