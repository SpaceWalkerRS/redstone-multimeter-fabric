package rsmm.fabric.client.gui.element;

import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.text.Text;

public interface IParentElement extends IElement {
	
	@Override
	default void render(int mouseX, int mouseY, float delta) {
		for (IElement child : getChildren()) {
			child.render(mouseX, mouseY, delta);
		}
	}
	
	@Override
	default void mouseMove(double mouseX, double mouseY) {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).mouseMove(mouseX, mouseY);
		}
	}
	
	@Override
	default boolean mouseClick(double mouseX, double mouseY, int button) {
		if (!isHovered(mouseX, mouseY)) {
			return false;
		}
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(true);
		}
		
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		
		if (hoveredElement != null && hoveredElement.mouseClick(mouseX, mouseY, button)) {
			setFocusedElement(hoveredElement);
			return true;
		} else {
			setFocusedElement(null);
			return false;
		}
	}
	
	@Override
	default boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(false);
		}
		
		boolean released = false;
		
		List<IElement> children = getChildren();
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.mouseRelease(mouseX, mouseY, button)) {
				released = true;
			}
		}
		
		return released;
	}
	
	@Override
	default boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isDraggingMouse() && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			IElement focused = getFocusedElement();
			
			if (focused != null) {
				return focused.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
			}
		}
		
		return false;
	}
	
	@Override
	default boolean mouseScroll(double mouseX, double mouseY, double amount) {
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		return hoveredElement != null && hoveredElement.mouseScroll(mouseX, mouseY, amount);
	}
	
	@Override
	default boolean keyPress(int keyCode, int scanCode, int modifiers) {
		IElement focused = getFocusedElement();
		return focused != null && focused.keyPress(keyCode, scanCode, modifiers);
	}
	
	@Override
	default boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		IElement focused = getFocusedElement();
		return focused != null && focused.keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	default boolean typeChar(char chr, int modifiers) {
		IElement focused = getFocusedElement();
		return focused != null && focused.typeChar(chr, modifiers);
	}
	
	@Override
	default void onRemoved() {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}
	}
	
	@Override
	default void unfocus() {
		setFocusedElement(null);
	}
	
	@Override
	default void tick() {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).tick();
		}
	}
	
	@Override
	default List<List<Text>> getTooltip(double mouseX, double mouseY) {
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		return hoveredElement == null ? Collections.emptyList() : hoveredElement.getTooltip(mouseX, mouseY);
	}
	
	public List<IElement> getChildren();
	
	default void clearChildren() {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}
		
		children.clear();
	}
	
	default IElement getHoveredElement(double mouseX, double mouseY) {
		if (isHovered(mouseX, mouseY)) {
			for (IElement child : getChildren()) {
				if (child.isHovered(mouseX, mouseY)) {
					return child;
				}
			}
		}
		
		return null;
	}
	
	public IElement getFocusedElement();
	
	public void setFocusedElement(IElement element);
	
}
