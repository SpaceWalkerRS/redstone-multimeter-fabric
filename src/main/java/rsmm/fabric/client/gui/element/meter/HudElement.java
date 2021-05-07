package rsmm.fabric.client.gui.element.meter;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.HudSettings;
import rsmm.fabric.client.gui.MultimeterHudRenderer;
import rsmm.fabric.client.gui.element.AbstractParentElement;

public class HudElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final MultimeterHudRenderer hudRenderer;
	
	private int x;
	private int y;
	private int width;
	
	private MeterControlsElement meterControls;
	
	private boolean draggingTicksTable;
	private double mouseDragDeltaX;
	private boolean mouseDragged;
	
	public HudElement(MultimeterClient client, int x, int y, int width) {
		this.client = client;
		this.hudRenderer = client.getHudRenderer();
		
		this.x = x;
		this.y = y;
		this.width = width;
		
		this.meterControls = new MeterControlsElement(this.client, x, y + hudRenderer.getTotalHeight(), width);
		
		addChild(meterControls);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		
		hudRenderer.render(x, y);
		hudRenderer.updateHoveredElements(x, y, mouseX, mouseY);
		hudRenderer.renderSelectedMeterIndicator(x, y, meterControls.getSelectedMeter());
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseY >= y && mouseY <= (y + hudRenderer.getTableHeight())) {
			int hoveredTickColumn = hudRenderer.getHoveredTickColumn();
			
			if (hoveredTickColumn >= 0) {
				draggingTicksTable = true;
			}
			
			success = true;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean success = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			draggingTicksTable = false;
			mouseDragDeltaX = 0.0D;
			
			if (!mouseDragged && mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + hudRenderer.getTotalHeight())) {
				int hoveredRow = hudRenderer.getHoveredRow();
				int hoveredNameColumn = hudRenderer.getHoveredNameColumn();
				
				if (hoveredRow >= 0 && hoveredNameColumn >= 0) {
					meterControls.selectMeter(hoveredRow);
				} else {
					meterControls.selectMeter(-1);
				}
				
				success = true;
			}
			
			mouseDragged = false;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean dragged = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (draggingTicksTable) {
				mouseDragDeltaX += deltaX;
				int scrollAmount = (int)Math.round(mouseDragDeltaX / (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE));
				mouseDragDeltaX -= scrollAmount * (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE);
				
				hudRenderer.stepForward(scrollAmount);
				
				dragged = true;
			}
			
			mouseDragged = true;
		}
		
		return dragged;
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
		meterControls.setX(x);
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
		meterControls.setY(y + hudRenderer.getTotalHeight());
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
		meterControls.setWidth(width);
	}
	
	@Override
	public int getHeight() {
		return hudRenderer.getTotalHeight() + meterControls.getHeight();
	}
	
	@Override
	public void setHeight(int height) {
		
	}
	
	@Override
	public List<List<Text>> getTooltip(double mouseX, double mouseY) {
		List<List<Text>> tooltip = super.getTooltip(mouseX, mouseY);
		
		if (tooltip.isEmpty()) {
			tooltip = hudRenderer.getTextForTooltip();
		}
		
		return tooltip;
	}
}
