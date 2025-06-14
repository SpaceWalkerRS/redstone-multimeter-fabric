package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;

import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.element.Element;
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

			if (isHovered()) {
				if (isMouseOverBorder(mouseX)) {
					cursor = CursorType.HRESIZE;
				} else if (hud.isPaused()) {
					cursor = CursorType.HAND;
				}
			}

			Element.setCursor(cursor);
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
					resizing = true;
				}

				consumed = true;
			}
			if (hud.isPaused() && !hud.isFocusMode() && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				int column = getHoveredColumn(mouseX);
				int max = getColumnCount() - 1;

				if (column > max) {
					column = max;
				}

				Options.HUD.SELECTED_COLUMN.set(column);
				IButton.playClickSound();

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
	protected void drawHighlights(GuiGraphics graphics, int mouseX, int mouseY) {
		PoseStack poses = graphics.pose();

		poses.pushPose();

		if (hud.isPaused() || !Options.HUD.HIDE_HIGHLIGHT.get()) {
			if (!isDraggingMouse() && isMouseOver(mouseX, mouseY) && !isMouseOverBorder(mouseX)) {
				drawHighlight(graphics, getHoveredColumn(mouseX), 1, 0, hud.meters.size(), false);
			}

			drawHighlight(graphics, Options.HUD.SELECTED_COLUMN.get(), 1, 0, hud.meters.size(), true);
		}

		poses.translate(0, 0, -0.1);

		if (hud.hasTickMarker()) {
			long tick = hud.getTickMarker();
			int column = hud.getColumn(tick);

			if (column >= 0) {
				drawHighlight(graphics, column, 1, 0, hud.meters.size(), hud.settings.colorHighlightTickMarker);
			}
		}

		poses.popPose();
	}

	@Override
	protected void drawDecorators(GuiGraphics graphics) {
		if (hud.settings.rowHeight < hud.font.lineHeight) {
			return;
		}

		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long currentTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderPulseLengths(graphics, x, y, firstTick, currentTick, meter);
		});
	}

	@Override
	protected void drawMeterEvents(GuiGraphics graphics) {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = hud.client.getPrevGameTime() + 1;

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderTickLogs(graphics, x, y, firstTick, lastTick, meter);
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

		switch (hud.getDirectionalityX()) {
		default:
		case LEFT_TO_RIGHT:
			return x >= (getX() + getWidth() - 1);
		case RIGHT_TO_LEFT:
			return x <= getX() + 1;
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
			if (hud.getDirectionalityX() == Directionality.X.RIGHT_TO_LEFT) {
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
