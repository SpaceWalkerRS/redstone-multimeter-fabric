package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.network.packets.MeterIndexPacket;
import redstone.multimeter.util.ColorUtils;
import redstone.multimeter.util.TextUtils;

public class MeterListRenderer extends AbstractElement {
	
	private static final int MARGIN = 3;
	
	private final MultimeterHud hud;
	
	private int cursorOriginRow;
	private int cursorRow;
	private Meter cursorMeter;
	private int cursorOffsetX;
	private int cursorOffsetY;
	
	public MeterListRenderer(MultimeterHud hud) {
		super(0, 0, 0, 0);
		
		this.hud = hud;
		
		this.cursorOriginRow = -1;
		this.cursorRow = -1;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		matrices.push();
		drawCursorMeter(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawHighlights(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawNames(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		hud.renderer.renderRect(matrices, 0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		matrices.pop();
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			cursorOriginRow = hud.getHoveredRow(mouseY);
		}

		return consumed || cursorOriginRow >= 0;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (cursorMeter == null) {
				if (cursorRow < 0) {
					consumed = hud.selectMeter(cursorOriginRow);
				}
			} else {
				consumed = changeMeterIndex(cursorMeter, cursorOriginRow, hud.getHoveredRow(mouseY));
			}

			cursorOriginRow = -1;
			cursorRow = -1;
			cursorMeter = null;
		}

		return consumed;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		cursorRow = isHovered(mouseX, mouseY) ? hud.getHoveredRow(mouseY) : -1;

		if (cursorMeter == null && cursorOriginRow >= 0) {
			cursorMeter = hud.meters.get(cursorOriginRow);
			cursorOffsetX = getX() - (int)mouseX;
			cursorOffsetY = getY() - (int)mouseY + cursorOriginRow * (hud.settings.rowHeight + hud.settings.gridSize);
		}
		
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
	public Tooltip getTooltip(int mouseX, int mouseY) {
		if (KeyBindings.OPEN_METER_CONTROLS.isUnbound() || cursorMeter != null) {
			return super.getTooltip(mouseX, mouseY);
		}

		int hoveredRow = hud.getHoveredRow(mouseY);

		if (hoveredRow < 0 || hoveredRow == hud.getSelectedRow()) {
			return super.getTooltip(mouseX, mouseY);
		}

		return Tooltip.of(TextUtils.formatKeybindInfo(KeyBindings.OPEN_METER_CONTROLS));
	}
	
	@Override
	public void update() {
		
	}
	
	private void drawCursorMeter(MatrixStack matrices, int mouseX, int mouseY) {
		if (cursorMeter != null) {
			matrices.push();

			int startX = mouseX + cursorOffsetX;
			int startY = mouseY + cursorOffsetY;
			int alpha = 0xDD;

			int x = startX;
			int y = startY;

			if (cursorOriginRow == hud.getSelectedRow()) {
				drawHighlight(matrices, x, y, true);

				matrices.translate(0, 0, -0.1);
			}

			x = startX + hud.settings.gridSize + 1;
			y = startY + hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.fontHeight) / 2;

			drawName(matrices, cursorMeter, x, y, ColorUtils.setAlpha(0xFFFFFF, alpha));

			matrices.translate(0, 0, -0.1);

			x = startX;
			y = startY;
			int w = getWidth();
			int h = hud.settings.rowHeight + 2 * hud.settings.gridSize;
			int color = ColorUtils.setAlpha(hud.settings.colorBackground, alpha);

			hud.renderer.renderRect(matrices, x, y, w, h, color);

			matrices.pop();
		}
	}
	
	private void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (cursorMeter == null && isHovered(mouseX, mouseY)) {
				drawHighlight(matrices, hud.getHoveredRow(mouseY), false);
			}
			
			int selectedRow = hud.getSelectedRow();
			Meter selectedMeter = hud.meters.get(selectedRow);
			
			if (selectedMeter != cursorMeter && selectedRow >= 0) {
				int highlightRow = selectedRow;

				if (selectedRow > cursorOriginRow && selectedRow <= cursorRow) {
					highlightRow--;
				}
				if (selectedRow < cursorOriginRow && selectedRow >= cursorRow) {
					highlightRow++;
				}

				drawHighlight(matrices, highlightRow, true);
			}
		}
	}
	
	private void drawHighlight(MatrixStack matrices, int row, boolean selection) {
		int x = 0;
		int y = row * (hud.settings.rowHeight + hud.settings.gridSize);

		drawHighlight(matrices, x, y, selection);
	}

	private void drawHighlight(MatrixStack matrices, int x, int y, boolean selection) {
		int width = getWidth() - hud.settings.gridSize;
		int height = hud.settings.rowHeight + hud.settings.gridSize;
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;

		hud.renderer.renderHighlight(matrices, x, y, width, height, color);
	}
	
	private void drawNames(MatrixStack matrices, int mouseX, int mouseY) {
		if (hud.settings.rowHeight < hud.font.fontHeight) {
			return;
		}
		
		int startX = hud.settings.gridSize + 1;
		int startY = hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.fontHeight) / 2;
		
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			
			if (meter != cursorMeter) {
				int offset = index;

				if (cursorMeter != null && cursorRow >= 0) {
					if (index > cursorOriginRow && index <= cursorRow) {
						offset--;
					}
					if (index < cursorOriginRow && index >= cursorRow) {
						offset++;
					}
				}

				int x = startX;
				int y = startY + offset * (hud.settings.rowHeight + hud.settings.gridSize);

				drawName(matrices, meter, x, y, 0xFFFFFFFF);
			}
		}
	}
	
	private void drawName(MatrixStack matrices, Meter meter, int x, int y, int color) {
		MutableText name = new LiteralText(meter.getName());

		if (meter.isHidden()) {
			name.formatted(Formatting.GRAY, Formatting.ITALIC);
		}

		hud.renderer.renderText(matrices, name, x, y, color);
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
	
	private boolean changeMeterIndex(Meter meter, int oldIndex, int index) {
		if (meter == null || oldIndex < 0 || index < 0) {
			return false;
		}

		hud.meters.remove(oldIndex);
		hud.meters.add(index, meter);

		MeterIndexPacket packet = new MeterIndexPacket(meter.getId(), index);
		hud.client.getPacketHandler().send(packet);

		return true;
	}
}
