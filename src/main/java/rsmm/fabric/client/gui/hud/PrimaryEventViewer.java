package rsmm.fabric.client.gui.hud;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.math.MatrixStack;
import rsmm.fabric.client.gui.CursorType;
import rsmm.fabric.client.gui.element.RSMMScreen;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.option.Options;

public class PrimaryEventViewer extends MeterEventViewer {
	
	private double dX;
	private boolean resizing;
	
	public PrimaryEventViewer(MultimeterHud hud) {
		super(hud);
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		if (!isDraggingMouse()) {
			CursorType cursor;
			
			if (isHovered(mouseX, mouseY)) {
				cursor = isBorderHovered(mouseX) ? CursorType.HRESIZE : CursorType.HAND;
			} else {
				cursor = CursorType.ARROW;
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
		int d = hud.settings.columnWidth + hud.settings.gridSize;
		int c = 0;
		
		while (dX >= d) {
			dX -= d;
			c++;
		}
		while (dX <= -d) {
			dX += d;
			c--;
		}
		
		if (c != 0) {
			if (resizing) {
				int columns = Options.HUD.HISTORY.get();
				Options.HUD.HISTORY.set(columns + c);
				Options.validate();
				hud.updateWidth();
			} else {
				hud.stepForward(c);
			}
		}
		
		return true;
	}
	
	@Override
	protected void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (hud.isPaused()) {
			if (!isDraggingMouse() && isHovered(mouseX, mouseY) && !isBorderHovered(mouseX)) {
				drawHighlight(matrices, getHoveredColumn(mouseX), 0x808080);
			}
			
			drawHighlight(matrices, Options.HUD.SELECTED_COLUMN.get(), 0xFFFFFF);
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
		return Options.HUD.HISTORY.get();
	}
	
	@Override
	protected int getMarkerColumn() {
		long firstTick = hud.getSelectedTick() - Options.HUD.SELECTED_COLUMN.get();
		long lastTick = firstTick + Options.HUD.HISTORY.get();
		long currentTick = hud.client.getLastServerTick() + 1;
		
		return (currentTick < firstTick || currentTick > lastTick) ? -1 : (int)(currentTick - firstTick);
	}
	
	private boolean isBorderHovered(double mouseX) {
		return Math.round(mouseX) >= (getX() + getWidth() - 1);
	}
}
