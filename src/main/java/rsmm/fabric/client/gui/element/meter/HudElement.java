package rsmm.fabric.client.gui.element.meter;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.MultimeterHudRenderer;
import rsmm.fabric.client.gui.element.AbstractParentElement;

public class HudElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final MultimeterHudRenderer hudRenderer;
	
	private int x;
	private int y;
	private int width;
	
	private MeterControlsElement meterControls;
	
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		
		hudRenderer.render(matrices, x, y);
		hudRenderer.updateHoveredElements(x, y, mouseX, mouseY);
		hudRenderer.renderSelectedMeterIndicator(matrices, x, y, meterControls.getSelectedMeter());
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && mouseY <= (y + hudRenderer.getTotalHeight())) {
			int hoveredRow = hudRenderer.getHoveredRow();
			int hoveredNameColumn = hudRenderer.getHoveredNameColumn();
			
			if (hoveredRow >= 0 && hoveredNameColumn >= 0) {
				success = meterControls.selectMeter(hoveredRow);
			} else {
				meterControls.selectMeter(-1);
			}
		}
		
		return success;
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
