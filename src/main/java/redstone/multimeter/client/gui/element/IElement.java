package redstone.multimeter.client.gui.element;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

public interface IElement extends Drawable {
	
	public void mouseMove(double mouseX, double mouseY);
	
	default boolean mouseClick(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(true);
		}
		
		return false;
	}
	
	default boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setDraggingMouse(false);
		}
		
		return false;
	}
	
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);
	
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);
	
	default boolean isHovered(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= getY() && mouseY <= (getY() + getHeight());
	}
	
	public boolean keyPress(int keyCode, int scanCode, int modifiers);
	
	public boolean keyRelease(int keyCode, int scanCode, int modifiers);
	
	public boolean typeChar(char chr, int modifiers);
	
	public boolean isDraggingMouse();
	
	public void setDraggingMouse(boolean dragging);
	
	public void onRemoved();
	
	public boolean isFocused();
	
	public void setFocused(boolean focused);
	
	public void tick();
	
	public int getX();
	
	public void setX(int x);
	
	public int getY();
	
	public void setY(int y);
	
	public int getWidth();
	
	public int getHeight();
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	public List<Text> getTooltip(int mouseX, int mouseY);
	
	public void update();
	
}
