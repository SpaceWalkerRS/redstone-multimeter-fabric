package redstone.multimeter.client.gui;

import org.lwjgl.glfw.GLFW;

public enum CursorType {

	ARROW(GLFW.GLFW_ARROW_CURSOR),
	IBEAM(GLFW.GLFW_IBEAM_CURSOR),
	CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
	HAND(GLFW.GLFW_HAND_CURSOR),
	HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
	VRESIZE(GLFW.GLFW_VRESIZE_CURSOR);

	private final int shape;
	private Long cursor;

	private CursorType(int shape) {
		this.shape = shape;
	}

	public long getCursor() {
		if (cursor == null) {
			cursor = GLFW.glfwCreateStandardCursor(shape);
		}

		return cursor;
	}
}
