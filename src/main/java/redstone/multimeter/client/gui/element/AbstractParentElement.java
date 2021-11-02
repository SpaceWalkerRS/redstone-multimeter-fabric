package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParentElement extends AbstractElement implements IParentElement {
	
	private final List<IElement> children = new ArrayList<>();
	
	private boolean focused;
	private IElement focusedElement;
	
	protected AbstractParentElement(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	@Override
	public boolean isFocused() {
		return focused;
	}
	
	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
		
		if (!isFocused()) {
			setFocusedElement(null);
		}
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		onChangedX(x);
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		onChangedY(y);
	}
	
	@Override
	public List<IElement> getChildren() {
		return children;
	}
	
	@Override
	public IElement getFocusedElement() {
		return focusedElement != null && focusedElement.isFocused() ? focusedElement : null;
	}
	
	@Override
	public void setFocusedElement(IElement element) {
		IElement focused = this.focusedElement;
		
		if (element == focused) {
			return;
		}
		
		if (focused != null) {
			focused.setFocused(false);
		}
		
		this.focusedElement = element;
		
		if (element != null) {
			element.setFocused(true);
		}
	}
	
	protected abstract void onChangedX(int x);
	
	protected abstract void onChangedY(int y);
	
	protected void addChild(IElement element) {
		children.add(element);
	}
}
