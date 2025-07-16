package redstone.multimeter.client.gui.hud.element;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.Orientation;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;

public abstract class MeterEventViewer extends AbstractElement {

	protected final MultimeterHud hud;

	protected MeterEventViewer(MultimeterHud hud) {
		super(0, 0, 0, 0);

		this.hud = hud;
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		renderer.pushMatrix();
		if (!hud.client.isPreviewing()) {
			drawHighlights(renderer, mouseX, mouseY);
			renderer.translate(0, 0, -1);
			drawDecorators(renderer);
			renderer.translate(0, 0, -1);
			drawMeterEvents(renderer);
			renderer.translate(0, 0, -1);
		}
		drawGridLines(renderer);
		renderer.translate(0, 0, -1);
		renderer.fill(0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		renderer.popMatrix();
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	@Override
	public boolean keyPress(int keyCode) {
		return false;
	}

	@Override
	public boolean keyRelease(int keyCode) {
		return false;
	}

	@Override
	public boolean typeChar(char chr) {
		return false;
	}

	@Override
	public void tick() {
	}

	@Override
	public void update() {
	}

	protected void drawMeterLogs(MeterEventRenderEvent event) {
		int x = 0;
		int y = 0;

		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			event.accept(x, y, meter);

			y += hud.settings.rowHeight + hud.settings.gridSize;
		}
	}

	protected abstract void drawHighlights(GuiRenderer renderer, int mouseX, int mouseY);

	protected void drawHighlight(GuiRenderer renderer, int column, int columnCount, int row, int rowCount, boolean selection) {
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;
		drawHighlight(renderer, column, columnCount, row, rowCount, color);
	}

	protected void drawHighlight(GuiRenderer renderer, int column, int columnCount, int row, int rowCount, int color) {
		int d = hud.settings.gridSize;
		int w = hud.settings.columnWidth + d;
		int h = hud.settings.rowHeight + d;

		int x0 = column * w;
		int y0 = row * h;
		int x1 = x0 + columnCount * w + d;
		int y1 = y0 + rowCount * h + d;

		renderer.borders(x0, y0, x1, y1, d, color);
	}

	protected abstract void drawDecorators(GuiRenderer renderer);

	protected abstract void drawMeterEvents(GuiRenderer renderer);

	private void drawGridLines(GuiRenderer renderer) {
		renderer.pushMatrix();

		int columns = getColumnCount();
		int rows = hud.meters.size();
		int marker = getCurrentTickMarkerColumn();

		// current tick marker
		if (marker >= 0) {
			int x0 = marker * (hud.settings.columnWidth + hud.settings.gridSize);
			int y0 = hud.settings.gridSize;
			int x1 = x0 + hud.settings.gridSize;
			int y1 = y0 + getHeight() - 2 * hud.settings.gridSize;
			int color = hud.settings.colorGridMarker;

			renderer.fill(x0, y0, x1, y1, color);
		}

		renderer.translate(0, 0, -0.1);

		// horizonal lines
		for (int i = 0; i <= rows; i++) {
			int x0 = 0;
			int y0 = i * (hud.settings.rowHeight + hud.settings.gridSize);
			int x1 = x0 + getWidth();
			int y1 = y0 + hud.settings.gridSize;
			int color = hud.settings.colorGridMain;

			renderer.fill(x0, y0, x1, y1, color);
		}

		renderer.translate(0, 0, -0.1);

		// vertical lines
		for (int i = 0; i <= columns; i++) {
			int x0 = i * (hud.settings.columnWidth + hud.settings.gridSize);
			int y0 = 0;
			int x1 = x0 + hud.settings.gridSize;
			int y1 = y0 + getHeight();
			int color = (i > 0 && i < columns && i % 5 == 0) ? hud.settings.colorGridInterval : hud.settings.colorGridMain;

			renderer.fill(x0, y0, x1, y1, color);
		}

		renderer.popMatrix();
	}

	protected abstract int getColumnCount();

	protected int getCurrentTickMarkerColumn() {
		return -1;
	}

	public int getHoveredColumn(double mouseX) {
		int max = getColumnCount() - 1;
		int column = Math.min(max, (int)((mouseX - getX()) / (hud.settings.columnWidth + hud.settings.gridSize)));

		if (hud.getOrientationX() == Orientation.X.RIGHT_TO_LEFT) {
			column = max - column;
		}

		return column;
	}

	public void updateWidth() {
		int columns = getColumnCount();

		if (columns == 0) {
			setWidth(0);
		} else {
			setWidth(columns * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize);
		}
	}

	public void updateHeight() {
		setHeight(hud.meters.size() * (hud.settings.rowHeight + hud.settings.gridSize) + hud.settings.gridSize);
	}

	protected interface MeterEventRenderEvent {

		public void accept(int x, int y, Meter meter);

	}
}
