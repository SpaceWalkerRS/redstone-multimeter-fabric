package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.input.Keyboard;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.network.packets.MeterIndexPacket;
import redstone.multimeter.util.ColorUtils;
import redstone.multimeter.util.TextUtils;

public class MeterListRenderer extends AbstractElement {

	private static final int MARGIN = 1;

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
	public void render(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		drawCursorMeter(mouseX, mouseY);
		GlStateManager.translated(0, 0, -1);
		drawHighlights(mouseX, mouseY);
		GlStateManager.translated(0, 0, -1);
		drawNames(mouseX, mouseY);
		GlStateManager.translated(0, 0, -1);
		hud.renderer.renderRect(0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		GlStateManager.popMatrix();
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (button == MOUSE_BUTTON_LEFT) {
			cursorOriginRow = hud.getHoveredRow(mouseY);
		}

		return consumed || cursorOriginRow >= 0;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (button == MOUSE_BUTTON_LEFT) {
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
		cursorRow = isMouseOver(mouseX, mouseY) ? hud.getHoveredRow(mouseY) : -1;

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
	public boolean keyPress(int keyCode) {
		if (!hud.hasSelectedMeter()) {
			return false;
		}

		switch (keyCode) {
		case Keyboard.KEY_UP:
			moveSelection(Screen.isControlDown() ? -hud.getSelectedRow() : -1);
			break;
		case Keyboard.KEY_DOWN:
			moveSelection(Screen.isControlDown() ? (hud.meters.size() - 1) - hud.getSelectedRow() : 1);
			break;
		default:
			return false;
		}

		return true;
	}

	@Override
	public boolean keyRelease(int keyCode) {
		return false;
	}

	@Override
	public boolean typeChar(char chr) {
		return false;
	}

	@Override
	public void tick() {
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		if (Keybinds.OPEN_METER_CONTROLS.getKeyCode() == Keyboard.KEY_NONE || cursorMeter != null) {
			return super.getTooltip(mouseX, mouseY);
		}

		int hoveredRow = hud.getHoveredRow(mouseY);

		if (hoveredRow < 0 || hoveredRow == hud.getSelectedRow()) {
			return super.getTooltip(mouseX, mouseY);
		}

		return Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.OPEN_METER_CONTROLS));
	}

	@Override
	public void update() {
	}

	private void drawCursorMeter(int mouseX, int mouseY) {
		if (cursorMeter != null) {
			GlStateManager.pushMatrix();

			int startX = mouseX + cursorOffsetX;
			int startY = mouseY + cursorOffsetY;
			int alpha = 0xDD;

			int x = startX;
			int y = startY;

			if (cursorOriginRow == hud.getSelectedRow()) {
				drawHighlight(x, y, true);

				GlStateManager.translated(0, 0, -0.1);
			}

			x = startX + hud.settings.gridSize + 1;
			y = startY + hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.textRenderer.fontHeight) / 2;

			drawName(cursorMeter, x, y, ColorUtils.setAlpha(0xFFFFFF, alpha));

			GlStateManager.translated(0, 0, -0.1);

			x = startX;
			y = startY;
			int w = getWidth();
			int h = hud.settings.rowHeight + 2 * hud.settings.gridSize;
			int color = ColorUtils.setAlpha(hud.settings.colorBackground, alpha);

			hud.renderer.renderRect(x, y, w, h, color);

			GlStateManager.popMatrix();
		}
	}

	private void drawHighlights(int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (cursorMeter == null && isMouseOver(mouseX, mouseY)) {
				drawHighlight(hud.getHoveredRow(mouseY), false);
			}

			int selectedRow = hud.getSelectedRow();

			if ((cursorMeter == null || selectedRow != cursorOriginRow) && selectedRow >= 0) {
				int highlightRow = selectedRow;

				if (cursorMeter != null) {
					if (selectedRow > cursorOriginRow && selectedRow <= cursorRow) {
						highlightRow--;
					}
					if (selectedRow < cursorOriginRow && selectedRow >= cursorRow) {
						highlightRow++;
					}
				}

				drawHighlight(highlightRow, true);
			}
		} else if (hud.isFocusMode()) {
			int focussedRow = hud.getFocussedRow();

			if (focussedRow != -1) {
				drawHighlight(focussedRow, true);
			}
		}
	}

	private void drawHighlight(int row, boolean selection) {
		int x = 0;
		int y = row * (hud.settings.rowHeight + hud.settings.gridSize);

		drawHighlight(x, y, selection);
	}

	private void drawHighlight(int x, int y, boolean selection) {
		int width = getWidth() - hud.settings.gridSize;
		int height = hud.settings.rowHeight + hud.settings.gridSize;
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;

		hud.renderer.renderHighlight(x, y, width, height, color);
	}

	private void drawNames(int mouseX, int mouseY) {
		if (hud.settings.rowHeight < hud.textRenderer.fontHeight) {
			return;
		}

		int startX = hud.settings.gridSize + 1;
		int startY = hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.textRenderer.fontHeight) / 2;

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

				drawName(meter, x, y, 0xFFFFFFFF);
			}
		}
	}

	private void drawName(Meter meter, int x, int y, int color) {
		Text name = new LiteralText(meter.getName());

		if (meter.isHidden()) {
			name.setFormatting(Formatting.GRAY, Formatting.ITALIC);
		}

		hud.renderer.renderText(name, x, y, color);
	}

	public void updateWidth() {
		int width = 0;

		for (Meter meter : hud.meters) {
			int nameWidth = hud.textRenderer.getWidth(meter.getName());

			if (nameWidth > width) {
				width = nameWidth;
			}
		}

		setWidth(width + 2 * hud.settings.gridSize + MARGIN);
	}

	public void updateHeight() {
		setHeight(hud.meters.size() * (hud.settings.rowHeight + hud.settings.gridSize) + hud.settings.gridSize);
	}

	private void moveSelection(int amount) {
		int row = (hud.getSelectedRow() + amount) % hud.meters.size();
		hud.selectMeter(row < 0 ? row + hud.meters.size() : row);
	}

	private boolean changeMeterIndex(Meter meter, int oldIndex, int index) {
		if (meter == null || oldIndex < 0 || index < 0) {
			return false;
		}

		hud.meters.remove(oldIndex);
		hud.meters.add(index, meter);

		MeterIndexPacket packet = new MeterIndexPacket(meter.getId(), index);
		hud.client.sendPacket(packet);

		return true;
	}
}
