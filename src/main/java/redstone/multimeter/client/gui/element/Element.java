package redstone.multimeter.client.gui.element;

import org.lwjgl.glfw.GLFW;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public interface Element {

	void render(GuiRenderer renderer, int mouseX, int mouseY);

	default boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= this.getX() && mouseX <= (this.getX() + this.getWidth()) && mouseY >= this.getY() && mouseY <= (this.getY() + this.getHeight());
	}

	void mouseMove(double mouseX, double mouseY);

	boolean mouseClick(double mouseX, double mouseY, int button);

	boolean mouseRelease(double mouseX, double mouseY, int button);

	boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);

	boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);

	boolean keyPress(int keyCode, int scanCode, int modifiers);

	boolean keyRelease(int keyCode, int scanCode, int modifiers);

	boolean typeChar(char chr, int modifiers);

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
		GLFW.glfwSetCursor(MultimeterClient.MINECRAFT.window.getWindow(), type.getCursor());
	}
}
