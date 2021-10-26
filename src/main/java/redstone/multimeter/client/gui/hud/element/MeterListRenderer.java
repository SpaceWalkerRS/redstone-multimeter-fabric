package redstone.multimeter.client.gui.hud.element;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		drawHighlights(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawNames(matrices);
		matrices.translate(0, 0, -1);
		hud.renderer.drawRect(matrices, 0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
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
	public void focus() {
		
	}
	
	@Override
	public void unfocus() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	private void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (isHovered(mouseX, mouseY)) {
				drawHighlight(matrices, hud.getHoveredRow(mouseY), false);
			}
			
			int selectedRow = hud.getSelectedRow();
			
			if (selectedRow >= 0) {
				drawHighlight(matrices, selectedRow, true);
			}
		}
	}
	
	private void drawHighlight(MatrixStack matrices, int row, boolean selected) {
		int h = hud.settings.rowHeight + hud.settings.gridSize;
		int x = 0;
		int y = row * h;
		int width = getWidth() - hud.settings.gridSize;
		int height = h;
		
		hud.renderer.drawHighlight(matrices, x, y, width, height, selected);
	}
	
	private void drawNames(MatrixStack matrices) {
		int x = hud.settings.gridSize + 1;
		int y = hud.settings.gridSize + 1;
		
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			MutableText name = new LiteralText(meter.getName());
			
			if (meter.isHidden()) {
				name.formatted(Formatting.GRAY, Formatting.ITALIC);
			}
			
			hud.renderer.drawText(matrices, name, x, y, 0xFFFFFF);
			
			y += hud.settings.rowHeight + hud.settings.gridSize;
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
		setHeight(hud.meters.size() * (hud.settings.rowHeight + hud.settings.gridSize) + hud.settings.gridSize);
	}
}
