package rsmm.fabric.client.gui.element;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface IParentElement extends IElement {
	
	@Override
	default void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible()) {
				child.render(matrices, mouseX, mouseY, delta);
			}
		}
	}
	
	@Override
	default void mouseMove(double mouseX, double mouseY) {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible()) {
				child.mouseMove(mouseX, mouseY);
			}
		}
	}
	
	@Override
	default boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean clicked = IElement.super.mouseClick(mouseX, mouseY, button);
		
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		
		if (hoveredElement != null && hoveredElement.mouseClick(mouseX, mouseY, button)) {
			setFocusedElement(hoveredElement);
			clicked = true;
		} else {
			setFocusedElement(null);
			clicked = false;
		}
		
		return clicked;
	}
	
	@Override
	default boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean released = IElement.super.mouseRelease(mouseX, mouseY, button);
		
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible() && child.mouseRelease(mouseX, mouseY, button)) {
				released = true;
			}
		}
		
		return released;
	}
	
	@Override
	default boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isDraggingMouse()) {
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
			IElement child = children.get(index);
			
			if (child.isVisible()) {
				child.tick();
			}
		}
	}
	
	@Override
	default List<Text> getTooltip(int mouseX, int mouseY) {
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		return hoveredElement == null ? Collections.emptyList() : hoveredElement.getTooltip(mouseX, mouseY);
	}
	
	@Override
	default void update() {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).update();
		}
	}
	
	public List<IElement> getChildren();
	
	default void removeChildren() {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).onRemoved();
		}
		
		children.clear();
	}
	
	default IElement getHoveredElement(double mouseX, double mouseY) {
		if (isHovered(mouseX, mouseY)) {
			List<IElement> children = getChildren();
			
			for (int index = 0; index < children.size(); index++) {
				IElement child = children.get(index);
				
				if (child.isVisible() && child.isHovered(mouseX, mouseY)) {
					return child;
				}
			}
		}
		
		return null;
	}
	
	public IElement getFocusedElement();
	
	public void setFocusedElement(IElement element);
	
}
