package rsmm.fabric.client.gui.hud;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import rsmm.fabric.client.gui.element.AbstractElement;
import rsmm.fabric.common.Meter;

public class MeterListRenderer extends AbstractElement implements HudRenderer {
	
	private static final int MARGIN = 3;
	
	private final MultimeterHud hud;
	
	public MeterListRenderer(MultimeterHud hud) {
		super(0, 0, 0, 0);
		
		this.hud = hud;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		drawHighlights(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawNames(matrices);
		matrices.translate(0, 0, -1);
		drawRect(hud, matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), hud.settings.colorBackground);
		matrices.pop();
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
	
	private void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (isHovered(mouseX, mouseY)) {
			int row = hud.getHoveredRow(mouseY);
			int max = hud.meters.size() - 1;
			
			if (row > max) {
				row = max;
			}
			
			drawHighlight(matrices, row, 0x707070);
		}
		if (hud.isOnScreen()) {
			int selectedRow = hud.getSelectedRow();
			
			if (selectedRow >= 0) {
				drawHighlight(matrices, selectedRow, 0xFFFFFF);
			}
		}
	}
	
	private void drawHighlight(MatrixStack matrices, int row, int color) {
		int x = getX();
		int y = getY() + row * (hud.settings.rowHeight + hud.settings.gridSize);
		int width = getWidth() - hud.settings.gridSize;
		int height = hud.settings.rowHeight + hud.settings.gridSize;
		
		drawHighlight(hud, matrices, x, y, width, height, color);
	}
	
	private void drawNames(MatrixStack matrices) {
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			MutableText name = new LiteralText(meter.getName());
			
			if (meter.isHidden()) {
				name.formatted(Formatting.GRAY, Formatting.ITALIC);
			}
			
			int nameWidth = hud.font.getWidth(name);
			int nameX = (getX() + getWidth()) - (nameWidth + 1);
			int nameY = (getY() + hud.settings.gridSize + 1) + index * (hud.settings.rowHeight + hud.settings.gridSize);
			
			drawText(hud, matrices, name, nameX, nameY, 0xFFFFFF);
		}
	}
	
	public void updateWidth() {
		int width = 0;
		
		for (Meter meter : hud.meters) {
			int nameWidth = hud.font.getWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		setWidth(width + MARGIN);
	}
	
	public void updateHeight() {
		setHeight(hud.getTableHeight());
	}
}
