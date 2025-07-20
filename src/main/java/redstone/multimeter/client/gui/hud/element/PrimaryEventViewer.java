package redstone.multimeter.client.gui.hud.element;

import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.hud.Orientation;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;

public class PrimaryEventViewer extends MeterEventViewer {

	private double dx;
	private boolean resizing;
	private boolean updateCursor;

	public PrimaryEventViewer(MultimeterHud hud) {
		super(hud);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		if (this.updateCursor) {
			this.updateCursor = false;

			// TODO: LegacyLWJGL3 compat
			//if (this.isHovered()) {
			//	if (this.isMouseOverBorder(mouseX)) {
			//		Element.setCursor(CursorType.HRESIZE);
			//	} else if (this.hud.isPaused()) {
			//		Element.setCursor(CursorType.HAND);
			//	} else {
			//		Element.setCursor(CursorType.ARROW);
			//	}
			//} else {
			//	Element.setCursor(CursorType.ARROW);
			//}
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean wasDragging = isDraggingMouse();
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && !wasDragging) {
			if (isDraggingMouse()) {
				dx = 0.0D;

				if (isMouseOverBorder(mouseX)) {
					setResizing(true);
				}

				consumed = true;
			}
			if (hud.isPaused() && !hud.isFocusMode() && button == MOUSE_BUTTON_RIGHT) {
				int column = getHoveredColumn(mouseX);
				int max = getColumnCount() - 1;

				if (column > max) {
					column = max;
				}

				Options.HUD.SELECTED_COLUMN.set(column);
				Button.playClickSound();

				consumed = true;
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == MOUSE_BUTTON_LEFT) {
			setResizing(false);
		}

		return super.mouseRelease(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (hud.isFocusMode() || !isDraggingMouse()) {
			return false;
		}

		return stepAndResize(deltaX);
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (hud.isFocusMode() || isDraggingMouse() || Math.abs(scrollX) < 1.0D) {
			return false;
		}

		return stepAndResize(scrollX);
	}

	@Override
	public void setHovered(boolean hovered) {
		boolean wasHovered = this.isHovered();
		super.setHovered(hovered);

		this.updateCursor = hovered || wasHovered;
	}

	@Override
	protected void drawHighlights(GuiRenderer renderer, int mouseX, int mouseY) {
		renderer.pushMatrix();

		if (hud.isPaused() || !Options.HUD.HIDE_HIGHLIGHT.get()) {
			if (!isDraggingMouse() && isMouseOver(mouseX, mouseY) && !isMouseOverBorder(mouseX)) {
				drawHighlight(renderer, getHoveredColumn(mouseX), 1, 0, hud.meters.size(), false);
			}

			drawHighlight(renderer, Options.HUD.SELECTED_COLUMN.get(), 1, 0, hud.meters.size(), true);
		}

		renderer.translate(0, 0, -0.1);

		if (hud.hasTickMarker()) {
			long tick = hud.getTickMarker();
			int column = hud.getColumn(tick);

			if (column >= 0) {
				drawHighlight(renderer, column, 1, 0, hud.meters.size(), hud.settings.colorHighlightTickMarker);
			}
		}

		renderer.popMatrix();
	}

	@Override
	protected void drawDecorators(GuiRenderer renderer) {
		if (hud.settings.rowHeight < hud.font.height()) {
			return;
		}

		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long currentTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderPulseLengths(renderer, x, y, firstTick, currentTick, meter);
		});
	}

	@Override
	protected void drawMeterEvents(GuiRenderer renderer) {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderTickLogs(renderer, x, y, firstTick, lastTick, meter);
		});
	}

	@Override
	protected int getColumnCount() {
		return Options.HUD.COLUMN_COUNT.get();
	}

	@Override
	protected int getCurrentTickMarkerColumn() {
		return hud.getColumn(hud.getCurrentTick(), true);
	}

	private boolean isMouseOverBorder(double mouseX) {
		long x = Math.round(mouseX);

		switch (hud.getOrientationX()) {
		default:
		case LEFT_TO_RIGHT:
			return x >= (getX() + getWidth() - 1);
		case RIGHT_TO_LEFT:
			return x <= getX() + 1;
		}
	}

	private void setResizing(boolean resizing) {
		if (this.resizing != resizing) {
			this.resizing = resizing;
			this.dx = 0.0D;

			// TODO: LegacyLWJGL3 Comopat
			//if (this.resizing) {
			//	Element.setCursor(CursorType.HRESIZE);
			//} else {
			//	Element.setCursor(CursorType.ARROW);
			//}
		}
	}

	private boolean stepAndResize(double deltaX) {
		dx += deltaX;

		int width = hud.settings.columnWidth + hud.settings.gridSize;
		double d = width / 2.0D;
		int c = 0;

		while (dx > d) {
			dx -= width;
			c++;
		}
		while (dx < -d) {
			dx += width;
			c--;
		}

		if (c != 0) {
			if (hud.getOrientationX() == Orientation.X.RIGHT_TO_LEFT) {
				c *= -1;
			}

			if (resizing) {
				int columns = Options.HUD.COLUMN_COUNT.get();
				Options.HUD.COLUMN_COUNT.set(columns + c);
				Options.validate();
				hud.updateWidth();
			} else {
				hud.scroll(c, false);
			}
		}

		return true;
	}
}
