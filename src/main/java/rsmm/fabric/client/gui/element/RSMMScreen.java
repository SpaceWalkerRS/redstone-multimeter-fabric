package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;

public abstract class RSMMScreen extends Screen implements IParentElement {
	
	public final MultimeterClient multimeterClient;
	private final List<IElement> content;
	
	private IElement focused;
	private boolean dragging;
	
	protected RSMMScreen(MultimeterClient multimeterClient) {
		super(new LiteralText(""));
		
		this.multimeterClient = multimeterClient;
		this.content = new ArrayList<>();
	}
	
	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		mouseMove(mouseX, mouseY);
	}
	
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		return isHovered(mouseX, mouseY) && mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		return mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return mouseScroll(mouseX, mouseY, amount);
	}
	
	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return keyPress(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean charTyped(char chr, int modifiers) {
		return typeChar(chr, modifiers);
	}

	@Override
	public boolean isDraggingMouse() {
		return dragging;
	}
	
	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		renderContent(matrices, mouseX, mouseY, delta);
		
		List<Text> tooltip = getTooltip(mouseX, mouseY);
		
		if (!tooltip.isEmpty()) {
			renderTooltip(matrices, tooltip, mouseX, mouseY + 15);
		}
	}
	
	@Override
	protected final void init() {
		removeChildren();
		initScreen();
	}
	
	protected abstract void initScreen();
	
	@Override
	public void tick() {
		IParentElement.super.tick();
	}
	
	@Override
	public void removed() {
		onRemoved();
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (IParentElement.super.keyPress(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (shouldCloseOnEsc() && (keyCode == GLFW.GLFW_KEY_ESCAPE)) {
			onClose();
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<IElement> getChildren() {
		return content;
	}
	
	@Override
	public IElement getFocusedElement() {
		return focused;
	}
	
	@Override
	public void setFocusedElement(IElement element) {
		IElement focused = getFocusedElement();
		
		if (element == focused) {
			return;
		}
		
		if (focused != null) {
			focused.unfocus();
		}
		
		this.focused = element;
		
		if (element != null) {
			element.focus();
		}
	}
	
	@Override
	public final int getX() {
		return 0;
	}
	
	@Override
	public final void setX(int x) {
		
	}
	
	@Override
	public final int getY() {
		return 0;
	}
	
	@Override
	public final void setY(int y) {
		
	}
	
	@Override
	public final int getWidth() {
		return width;
	}
	
	@Override
	public final void setWidth(int width) {
		
	}
	
	@Override
	public final int getHeight() {
		return height;
	}
	
	@Override
	public final void setHeight(int height) {
		
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
	
	@Override
	public void setVisible(boolean visible) {
		
	}
	
	protected void addContent(IElement element) {
		content.add(element);
	}
	
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		IParentElement.super.render(matrices, mouseX, mouseY, delta);
	}
}
