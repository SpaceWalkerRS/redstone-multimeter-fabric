package redstone.multimeter.client.gui.hud.element;

import com.mojang.blaze3d.vertex.PoseStack;

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
	public void render(PoseStack poses, int mouseX, int mouseY) {
		poses.pushPose();
		drawHighlights(poses, mouseX, mouseY);
		poses.translate(0, 0, -1);
		drawDecorators(poses);
		poses.translate(0, 0, -1);
		drawMeterEvents(poses);
		poses.translate(0, 0, -1);
		drawGridLines(poses);
		poses.translate(0, 0, -1);
		hud.renderer.renderRect(poses, 0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		poses.popPose();
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
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
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
	}

	@Override
	public void onRemoved() {
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

	protected abstract void drawHighlights(PoseStack poses, int mouseX, int mouseY);

	protected void drawHighlight(PoseStack poses, int column, int columnCount, int row, int rowCount, boolean selection) {
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;
		drawHighlight(poses, column, columnCount, row, rowCount, color);
	}

	protected void drawHighlight(PoseStack poses, int column, int columnCount, int row, int rowCount, int color) {
		int w = hud.settings.columnWidth + hud.settings.gridSize;
		int h = hud.settings.rowHeight + hud.settings.gridSize;
		int x = column * w;
		int y = row * h;
		int width = columnCount * w;
		int height = rowCount * h;

		hud.renderer.renderHighlight(poses, x, y, width, height, color);
	}

	protected abstract void drawDecorators(PoseStack poses);

	protected abstract void drawMeterEvents(PoseStack poses);

	private void drawGridLines(PoseStack poses) {
		poses.pushPose();

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

			hud.renderer.renderRect(poses, lineX, lineY, lineWidth, lineHeight, color);
		}

		poses.translate(0, 0, -0.1);

		// horizonal lines
		for (int i = 0; i <= rows; i++) {
			lineX = 0;
			lineY = i * (hud.settings.rowHeight + hud.settings.gridSize);
			lineWidth = getWidth();
			lineHeight = hud.settings.gridSize;
			color = hud.settings.colorGridMain;

			hud.renderer.renderRect(poses, lineX, lineY, lineWidth, lineHeight, color);
		}

		poses.translate(0, 0, -0.1);

		// vertical lines
		for (int i = 0; i <= columns; i++) {
			lineX = i * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = 0;
			lineWidth = hud.settings.gridSize;
			lineHeight = getHeight();
			color = (i > 0 && i < columns && i % 5 == 0) ? hud.settings.colorGridInterval : hud.settings.colorGridMain;

			hud.renderer.renderRect(poses, lineX, lineY, lineWidth, lineHeight, color);
		}

		poses.popPose();
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
