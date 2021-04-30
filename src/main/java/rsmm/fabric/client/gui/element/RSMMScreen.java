package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;

public abstract class RSMMScreen extends Screen implements IParentElement {
	
	protected final MultimeterClient multimeterClient;
	private final List<IElement> content;
	
	private IElement focused;
	
	protected RSMMScreen(MultimeterClient multimeterClient) {
		super(new LiteralText(""));
		
		this.multimeterClient = multimeterClient;
		this.content = new ArrayList<>();
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		mouseMove(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return mouseScroll(mouseX, mouseY, amount);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return keyPress(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers) {
		return typeChar(chr, modifiers);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		IParentElement.super.render(matrices, mouseX, mouseY, delta);
		
		List<Text> tooltip = getTooltip(mouseX, mouseY);
		
		if (!tooltip.isEmpty()) {
			renderTooltip(matrices, tooltip, mouseX, mouseY + 15);
		}
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public void focus() {
		
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
	public int getX() {
		return 0;
	}
	
	@Override
	public void setX(int x) {
		
	}
	
	@Override
	public int getY() {
		return 0;
	}
	
	@Override
	public void setY(int y) {
		
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void setHeight(int height) {
		
	}
	
	protected void addContent(IElement element) {
		content.add(element);
	}
}
