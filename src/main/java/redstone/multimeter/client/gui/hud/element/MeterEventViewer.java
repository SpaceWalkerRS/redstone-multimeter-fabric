package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.opengl.GL11;

import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;

public abstract class MeterEventViewer extends AbstractElement {

	protected final MultimeterHud hud;

	protected MeterEventViewer(MultimeterHud hud) {
		super(0, 0, 0, 0);

		this.hud = hud;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		GL11.glPushMatrix();
		if (!hud.client.isPreviewing()) {
			drawHighlights(mouseX, mouseY);
			GL11.glTranslated(0, 0, -1);
			drawDecorators();
			GL11.glTranslated(0, 0, -1);
			drawMeterEvents();
			GL11.glTranslated(0, 0, -1);
		}
		drawGridLines();
		GL11.glTranslated(0, 0, -1);
		hud.renderer.renderRect(0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		GL11.glPopMatrix();
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

	protected abstract void drawHighlights(int mouseX, int mouseY);

	protected void drawHighlight(int column, int columnCount, int row, int rowCount, boolean selection) {
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;
		drawHighlight(column, columnCount, row, rowCount, color);
	}

	protected void drawHighlight(int column, int columnCount, int row, int rowCount, int color) {
		int w = hud.settings.columnWidth + hud.settings.gridSize;
		int h = hud.settings.rowHeight + hud.settings.gridSize;
		int x = column * w;
		int y = row * h;
		int width = columnCount * w;
		int height = rowCount * h;

		hud.renderer.renderHighlight(x, y, width, height, color);
	}

	protected abstract void drawDecorators();

	protected abstract void drawMeterEvents();

	private void drawGridLines() {
		GL11.glPushMatrix();

		int columns = getColumnCount();
		int rows = hud.meters.size();
		int marker = getCurrentTickMarkerColumn();

		int lineX;
		int lineY;
		int lineWidth;
		int lineHeight;
		int color;

		// current tick marker
		if (marker >= 0) {
			lineX = marker * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = hud.settings.gridSize;
			lineWidth = hud.settings.gridSize;
			lineHeight = getHeight() - 2 * hud.settings.gridSize;
			color = hud.settings.colorGridMarker;

			hud.renderer.renderRect(lineX, lineY, lineWidth, lineHeight, color);
		}

		GL11.glTranslated(0, 0, -0.1);

		// horizonal lines
		for (int i = 0; i <= rows; i++) {
			lineX = 0;
			lineY = i * (hud.settings.rowHeight + hud.settings.gridSize);
			lineWidth = getWidth();
			lineHeight = hud.settings.gridSize;
			color = hud.settings.colorGridMain;

			hud.renderer.renderRect(lineX, lineY, lineWidth, lineHeight, color);
		}

		GL11.glTranslated(0, 0, -0.1);

		// vertical lines
		for (int i = 0; i <= columns; i++) {
			lineX = i * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = 0;
			lineWidth = hud.settings.gridSize;
			lineHeight = getHeight();
			color = (i > 0 && i < columns && i % 5 == 0) ? hud.settings.colorGridInterval : hud.settings.colorGridMain;

			hud.renderer.renderRect(lineX, lineY, lineWidth, lineHeight, color);
		}

		GL11.glPopMatrix();
	}

	protected abstract int getColumnCount();

	protected int getCurrentTickMarkerColumn() {
		return -1;
	}

	public int getHoveredColumn(double mouseX) {
		int max = getColumnCount() - 1;
		int column = Math.min(max, (int)((mouseX - getX()) / (hud.settings.columnWidth + hud.settings.gridSize)));

		if (hud.getDirectionalityX() == Directionality.X.RIGHT_TO_LEFT) {
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
