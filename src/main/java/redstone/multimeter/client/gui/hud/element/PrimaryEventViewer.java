package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;

public class PrimaryEventViewer extends MeterEventViewer {

	private double dx;
	private boolean resizing;

	public PrimaryEventViewer(MultimeterHud hud) {
		super(hud);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		if (!isDraggingMouse()) {
			CursorType cursor = CursorType.ARROW;

			if (isHovered(mouseX, mouseY)) {
				if (isBorderHovered(mouseX)) {
					cursor = CursorType.HRESIZE;
				} else if (hud.isPaused()) {
					cursor = CursorType.HAND;
				}
			}

			setCursor(hud.client.getMinecraft(), cursor);
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean wasDragging = isDraggingMouse();
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && !wasDragging) {
			if (isDraggingMouse()) {
				dx = 0.0D;

				if (isBorderHovered(mouseX)) {
					resizing = true;
				}

				consumed = true;
			}
			if (hud.isPaused() && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				int column = getHoveredColumn(mouseX);
				int max = getColumnCount() - 1;

				if (column > max) {
					column = max;
				}

				Options.HUD.SELECTED_COLUMN.set(column);
				IButton.playClickSound(hud.client);

				consumed = true;
			}
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dx = 0.0D;
			resizing = false;
		}

		return super.mouseRelease(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!isDraggingMouse()) {
			return false;
		}

		return drag(deltaX);
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (isDraggingMouse() || Math.abs(scrollX) < 1.0D) {
			return false;
		}

		return drag(scrollX);
	}

	@Override
	protected void drawHighlights(PoseStack poses, int mouseX, int mouseY) {
		poses.pushPose();

		if (hud.isPaused() || !Options.HUD.HIDE_HIGHLIGHT.get()) {
			if (!isDraggingMouse() && isHovered(mouseX, mouseY) && !isBorderHovered(mouseX)) {
				drawHighlight(poses, getHoveredColumn(mouseX), 1, 0, hud.meters.size(), false);
			}

			drawHighlight(poses, Options.HUD.SELECTED_COLUMN.get(), 1, 0, hud.meters.size(), true);
		}

		poses.translate(0, 0, -0.1);

		if (hud.hasTickMarker()) {
			long tick = hud.getTickMarker();
			int column = hud.getColumn(tick);

			if (column >= 0) {
				drawHighlight(poses, column, 1, 0, hud.meters.size(), hud.settings.colorHighlightTickMarker);
			}
		}

		poses.popPose();
	}

	@Override
	protected void drawDecorators(PoseStack poses) {
		if (hud.settings.rowHeight < hud.font.lineHeight) {
			return;
		}

		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long currentTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderPulseLengths(poses, x, y, firstTick, currentTick, meter);
		});
	}

	@Override
	protected void drawMeterEvents(PoseStack poses) {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderTickLogs(poses, x, y, firstTick, lastTick, meter);
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

	private boolean isBorderHovered(double mouseX) {
		long x = Math.round(mouseX);

		switch (hud.getDirectionalityX()) {
		default:
		case LEFT_TO_RIGHT:
			return x >= (getX() + getWidth() - 1);
		case RIGHT_TO_LEFT:
			return x <= getX() + 1;
		}
	}

	private boolean drag(double deltaX) {
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
			if (hud.getDirectionalityX() == Directionality.X.RIGHT_TO_LEFT) {
				c *= -1;
			}

			if (resizing) {
				int columns = Options.HUD.COLUMN_COUNT.get();
				Options.HUD.COLUMN_COUNT.set(columns + c);
				Options.validate();
				hud.updateWidth();
			} else {
				hud.stepBackward(c);
			}
		}

		return true;
	}
}
