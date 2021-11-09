package redstone.multimeter.client.gui.element;

import java.util.List;

import net.minecraft.text.Text;

public interface IParentElement extends IElement {
	
	@Override
	default void render(int mouseX, int mouseY) {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible()) {
				child.render(mouseX, mouseY);
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
		boolean consumed = IElement.super.mouseClick(mouseX, mouseY, button);
		
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		
		if (hoveredElement != null && hoveredElement.mouseClick(mouseX, mouseY, button)) {
			setFocusedElement(hoveredElement);
			consumed = true;
		} else {
			setFocusedElement(null);
			consumed = false;
		}
		
		return consumed;
	}
	
	@Override
	default boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = IElement.super.mouseRelease(mouseX, mouseY, button);
		
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible() && child.mouseRelease(mouseX, mouseY, button)) {
				consumed = true;
			}
		}
		
		return consumed;
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
	default boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		IElement hoveredElement = getHoveredElement(mouseX, mouseY);
		return hoveredElement != null && hoveredElement.mouseScroll(mouseX, mouseY, scrollX, scrollY);
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
		return hoveredElement != null ? hoveredElement.getTooltip(mouseX, mouseY) : null;
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
