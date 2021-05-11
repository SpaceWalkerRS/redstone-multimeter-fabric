package rsmm.fabric.client.gui.element;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

public interface IElement extends Drawable {
	
	public void mouseMove(double mouseX, double mouseY);
	
	public boolean mouseClick(double mouseX, double mouseY, int button);
	
	public boolean mouseRelease(double mouseX, double mouseY, int button);
	
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);
	
	public boolean mouseScroll(double mouseX, double mouseY, double amount);
	
	default boolean isHovered(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= getY() && mouseY <= (getY() + getHeight());
	}
	
	public boolean keyPress(int keyCode, int scanCode, int modifiers);
	
	public boolean keyRelease(int keyCode, int scanCode, int modifiers);
	
	public boolean typeChar(char chr, int modifiers);
	
	public boolean isDraggingMouse();
	
	public void setDraggingMouse(boolean dragging);
	
	public void onRemoved();
	
	public void focus();
	
	public void unfocus();
	
	default void tick() {
		
	}
	
	public int getX();
	
	public void setX(int x);
	
	public int getY();
	
	public void setY(int y);
	
	public int getWidth();
	
	public void setWidth(int width);
	
	public int getHeight();
	
	public void setHeight(int height);
	
	default List<List<Text>> getTooltip(double mouseX, double mouseY) {
		return Collections.emptyList();
	}
}
