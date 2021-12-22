package redstone.multimeter.client.gui.hud.element;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;

public class MeterListRenderer extends AbstractElement {
	
	private static final int MARGIN = 3;
	
	private final MultimeterHud hud;
	
	public MeterListRenderer(MultimeterHud hud) {
		super(0, 0, 0, 0);
		
		this.hud = hud;
	}
	
	@Override
	public void render(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		drawHighlights(mouseX, mouseY);
		GlStateManager.translated(0, 0, -1);
		drawNames();
		GlStateManager.translated(0, 0, -1);
		hud.renderer.renderRect(0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		return hud.selectMeter(hud.getHoveredRow(mouseY));
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
	public boolean keyPress(int key) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int key) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr) {
		return false;
	}
	
	@Override
	public void onRemoved() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public List<Text> getTooltip(int mouseX, int mouseY) {
		return null;
	}
	
	@Override
	public void update() {
		
	}
	
	private void drawHighlights(int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (isHovered(mouseX, mouseY)) {
				drawHighlight(hud.getHoveredRow(mouseY), false);
			}
			
			int selectedRow = hud.getSelectedRow();
			
			if (selectedRow >= 0) {
				drawHighlight(selectedRow, true);
			}
		}
	}
	
	private void drawHighlight(int row, boolean selection) {
		int h = hud.settings.rowHeight + hud.settings.gridSize;
		int x = 0;
		int y = row * h;
		int width = getWidth() - hud.settings.gridSize;
		int height = h;
		
		hud.renderer.renderHighlight(x, y, width, height, selection);
	}
	
	private void drawNames() {
		int x = hud.settings.gridSize + 1;
		int y = hud.settings.gridSize + 1;
		
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			Text name = new LiteralText(meter.getName());
			
			if (meter.isHidden()) {
				name.setStyle(new Style().setFormatting(Formatting.GRAY).setItalic(true));
			}
			
			hud.renderer.renderText(name, x, y, 0xFFFFFF);
			
			y += hud.settings.rowHeight + hud.settings.gridSize;
		}
	}
	
	public void updateWidth() {
		int width = 0;
		
		for (Meter meter : hud.meters) {
			int nameWidth = hud.font.getStringWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		setWidth(width + MARGIN);
	}
	
	public void updateHeight() {
		setHeight(hud.meters.size() * (hud.settings.rowHeight + hud.settings.gridSize) + hud.settings.gridSize);
	}
}
