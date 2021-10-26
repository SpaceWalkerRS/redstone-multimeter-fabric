package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.math.MatrixStack;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.gui.widget.Button;
import redstone.multimeter.client.option.Options;

public class PrimaryEventViewer extends MeterEventViewer {
	
	private double dX;
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
			
			RSMMScreen.setCursor(hud.client, cursor);
		}
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean wasDragging = isDraggingMouse();
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && !wasDragging) {
			if (isDraggingMouse()) {
				if (isBorderHovered(mouseX)) {
					resizing = true;
				}
				
				success = true;
			}
			if (hud.isPaused() && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				int column = getHoveredColumn(mouseX);
				int max = getColumnCount() - 1;
				
				if (column > max) {
					column = max;
				}
				
				Options.HUD.SELECTED_COLUMN.set(column);
				Button.playClickSound(hud.client);
				
				success = true;
			}
		}
		
		return success;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dX = 0.0D;
			resizing = false;
		}
		
		return super.mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!isDraggingMouse()) {
			return false;
		}
		
		dX += deltaX;
		int width = hud.settings.columnWidth + hud.settings.gridSize;
		double d = width / 2.0D;
		int c = 0;
		
		while (dX > d) {
			dX -= width;
			c++;
		}
		while (dX < -d) {
			dX += width;
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
	
	@Override
	protected void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (hud.isPaused() || !Options.HUD.HIDE_HIGHLIGHT.get()) {
			if (!isDraggingMouse() && isHovered(mouseX, mouseY) && !isBorderHovered(mouseX)) {
				drawHighlight(matrices, getHoveredColumn(mouseX), 1, 0, hud.meters.size(), false);
			}
			
			drawHighlight(matrices, Options.HUD.SELECTED_COLUMN.get(), 1, 0, hud.meters.size(), true);
		}
	}
	
	@Override
	protected void drawDecorators(MatrixStack matrices) {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long currentTick = hud.client.getLastServerTick() + 1;
		
		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderPulseLengths(matrices, x, y, firstTick, currentTick, meter);
		});
	}
	
	@Override
	protected void drawMeterEvents(MatrixStack matrices) {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = hud.client.getLastServerTick() + 1;
		
		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderTickLogs(matrices, x, y, firstTick, lastTick, meter);
		});
	}
	
	@Override
	protected int getColumnCount() {
		return Options.HUD.COLUMN_COUNT.get();
	}
	
	@Override
	protected int getMarkerColumn() {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = firstTick + Options.HUD.COLUMN_COUNT.get();
		long currentTick = hud.client.getLastServerTick() + 1;
		
		return (currentTick < firstTick || currentTick > lastTick) ? -1 : (int)(currentTick - firstTick);
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
}
