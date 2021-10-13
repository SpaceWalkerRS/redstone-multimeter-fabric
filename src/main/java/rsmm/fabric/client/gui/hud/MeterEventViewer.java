package rsmm.fabric.client.gui.hud;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.gui.element.AbstractElement;
import rsmm.fabric.common.Meter;

public abstract class MeterEventViewer extends AbstractElement implements HudRenderer {
	
	protected final MultimeterHud hud;
	
	protected MeterEventViewer(MultimeterHud hud) {
		super(0, 0, 0, 0);
		
		this.hud = hud;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		drawHighlights(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawDecorators(matrices);
		matrices.translate(0, 0, -1);
		drawMeterEvents(matrices);
		matrices.translate(0, 0, -1);
		drawGridLines(matrices);
		matrices.translate(0, 0, -1);
		drawRect(hud, matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), hud.settings.colorBackground);
		matrices.pop();
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
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
	public void focus() {
		
	}
	
	@Override
	public void unfocus() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	protected void drawMeterLogs(MeterEventRenderEvent event) {
		int x = getX();
		int y = getY();
		
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			event.accept(x, y, meter);
			
			y += hud.settings.rowHeight + hud.settings.gridSize;
		}
	}
	
	protected abstract void drawHighlights(MatrixStack matrices, int mouseX, int mouseY);
	
	protected void drawHighlight(MatrixStack matrices, int column, int color) {
		int x = getX() + column * (hud.settings.columnWidth + hud.settings.gridSize);
		int y = getY();
		int width = hud.settings.columnWidth + hud.settings.gridSize;
		int height = getHeight() - hud.settings.gridSize;
		
		drawHighlight(hud, matrices, x, y, width, height, color);
	}
	
	protected abstract void drawDecorators(MatrixStack matrices);
	
	protected abstract void drawMeterEvents(MatrixStack matrices);
	
	private void drawGridLines(MatrixStack matrices) {
		matrices.push();
		
		int columns = getColumnCount();
		int rows = hud.meters.size();
		int marker = getMarkerColumn();
		
		int lineX;
		int lineY;
		int color;
		
		// marker
		if (marker >= 0 && marker <= columns) {
			lineX = getX() + marker * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = getY() + hud.settings.gridSize;
			color = hud.settings.colorGridMarker;
			
			drawRect(hud, matrices, lineX, lineY, lineX + hud.settings.gridSize, getY() + getHeight() - hud.settings.gridSize, color);
		}
		
		matrices.translate(0, 0, -0.1);
		
		// horizonal lines
		for (int i = 0; i <= rows; i++) {
			lineY = getY() + i * (hud.settings.rowHeight + hud.settings.gridSize);
			color = hud.settings.colorGridMain;
			
			drawRect(hud, matrices, getX(), lineY, getX() + getWidth(), lineY + hud.settings.gridSize, color);
		}
		
		matrices.translate(0, 0, -0.1);
		
		// vertical lines
		for (int i = 0; i <= columns; i++) {
			lineX = getX() + i * (hud.settings.columnWidth + hud.settings.gridSize);
			color = (i > 0 && i < columns && i % 5 == 0) ? hud.settings.colorGridInterval : hud.settings.colorGridMain;
			
			drawRect(hud, matrices, lineX, getY(), lineX + hud.settings.gridSize, getY() + getHeight(), color);
		}
		
		matrices.pop();
	}
	
	protected abstract int getColumnCount();
	
	protected int getMarkerColumn() {
		return -1;
	}
	
	public int getHoveredColumn(double mouseX) {
		return (int)((mouseX - getX()) / (hud.settings.columnWidth + hud.settings.gridSize));
	}
	
	public void updateWidth() {
		setWidth(getColumnCount() * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize);
	}
	
	public void updateHeight() {
		setHeight(hud.getTableHeight());
	}
	
	protected interface MeterEventRenderEvent {
		
		public void accept(int x, int y, Meter meter);
		
	}
}
