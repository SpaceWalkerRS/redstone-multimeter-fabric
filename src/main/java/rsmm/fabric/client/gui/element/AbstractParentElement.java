package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParentElement extends AbstractElement implements IParentElement {
	
	private final List<IElement> children = new ArrayList<>();
	
	private IElement focused;
	
	protected AbstractParentElement(int x, int y, int width, int height) {
		super(x, y, width, height);
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
	
	protected abstract void onChangedX(int x);
	
	protected abstract void onChangedY(int y);
	
	protected void addChild(IElement element) {
		children.add(element);
	}
}
