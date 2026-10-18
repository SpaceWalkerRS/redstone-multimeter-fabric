package redstone.multimeter.client.gui;

import org.lwjgl.sdl.SDLMouse;

public enum CursorType {

	ARROW(SDLMouse.SDL_SYSTEM_CURSOR_POINTER),
	IBEAM(SDLMouse.SDL_SYSTEM_CURSOR_TEXT),
	CROSSHAIR(SDLMouse.SDL_SYSTEM_CURSOR_CROSSHAIR),
	HAND(SDLMouse.SDL_SYSTEM_CURSOR_MOVE),
	HRESIZE(SDLMouse.SDL_SYSTEM_CURSOR_EW_RESIZE),
	VRESIZE(SDLMouse.SDL_SYSTEM_CURSOR_NS_RESIZE);

	private final int shape;
	private Long cursor;

	private CursorType(int shape) {
		this.shape = shape;
	}

	public long getCursor() {
		if (this.cursor == null) {
			this.cursor = SDLMouse.SDL_CreateSystemCursor(this.shape);
		}

		return this.cursor;
	}
}
