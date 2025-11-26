package redstone.multimeter.client.gui.element;

import org.lwjgl.glfw.GLFW;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public interface Element {

	void render(GuiRenderer renderer, int mouseX, int mouseY);

	default void renderSecondPass(GuiRenderer renderer, int mouseX, int mouseY) {
	}

	default boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= this.getX() && mouseX <= (this.getX() + this.getWidth()) && mouseY >= this.getY() && mouseY <= (this.getY() + this.getHeight());
	}

	void mouseMove(double mouseX, double mouseY);

	boolean mouseClick(MouseEvent.Click event);

	boolean mouseRelease(MouseEvent.Release event);

	boolean mouseDrag(MouseEvent.Drag event);

	boolean mouseScroll(MouseEvent.Scroll event);

	boolean keyPress(KeyEvent.Press event);

	boolean keyRelease(KeyEvent.Release event);

	boolean typeChar(CharacterEvent.Type event);

	boolean isHovered();

	void setHovered(boolean hovered);

	boolean isDraggingMouse();

	void setDraggingMouse(boolean draggingMouse);

	void onRemoved();

	boolean isFocused();

	void setFocused(boolean focused);

	void tick();

	int getX();

	void setX(int x);

	int getY();

	void setY(int y);

	int getWidth();

	void setWidth(int width);

	int getHeight();

	void setHeight(int height);

	boolean isVisible();

	void setVisible(boolean visible);

	Tooltip getTooltip(int mouseX, int mouseY);

	void update();

	static void setCursor(CursorType type) {
		GLFW.glfwSetCursor(MultimeterClient.MINECRAFT.getWindow().handle(), type.getCursor());
	}
}
