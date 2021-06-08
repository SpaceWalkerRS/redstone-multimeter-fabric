package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParentElement implements IParentElement {
	
	private final List<IElement> children = new ArrayList<>();
	
	private boolean dragging;
	private boolean visible = true;
	private IElement focused;
	
	@Override
	public boolean isDraggingMouse() {
		return dragging;
	}
	
	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
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
	public List<IElement> getChildren() {
		return children;
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
	
	protected void addChild(IElement child) {
		children.add(child);
	}
}
