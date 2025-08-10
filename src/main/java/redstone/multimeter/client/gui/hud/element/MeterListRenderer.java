package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.network.packets.MeterIndexPacket;
import redstone.multimeter.util.ColorUtils;

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
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		renderer.pushMatrix();
		drawCursorMeter(renderer, mouseX, mouseY);
		renderer.translate(0, 0, -1);
		drawHighlights(renderer, mouseX, mouseY);
		renderer.translate(0, 0, -1);
		drawNames(renderer, mouseX, mouseY);
		renderer.translate(0, 0, -1);
		renderer.fill(0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		renderer.popMatrix();
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
			} else if (isMouseOver(mouseX, mouseY)) {
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
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (!hud.hasSelectedMeter()) {
			return false;
		}

		switch (keyCode) {
		case GLFW.GLFW_KEY_UP:
			moveSelection(Screen.hasControlDown() ? -hud.getSelectedRow() : -1);
			break;
		case GLFW.GLFW_KEY_DOWN:
			moveSelection(Screen.hasControlDown() ? (hud.meters.size() - 1) - hud.getSelectedRow() : 1);
			break;
		default:
			return false;
		}

		return true;
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
	public void tick() {
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		if (Keybinds.OPEN_METER_CONTROLS.isUnbound() || cursorMeter != null) {
			return super.getTooltip(mouseX, mouseY);
		}

		int hoveredRow = hud.getHoveredRow(mouseY);

		if (hoveredRow < 0 || hoveredRow == hud.getSelectedRow()) {
			return super.getTooltip(mouseX, mouseY);
		}

		return Tooltips.keybind(Keybinds.OPEN_METER_CONTROLS);
	}

	@Override
	public void update() {
	}

	private void drawCursorMeter(GuiRenderer renderer, int mouseX, int mouseY) {
		if (cursorMeter != null) {
			renderer.pushMatrix();

			int startX = mouseX + cursorOffsetX;
			int startY = mouseY + cursorOffsetY;
			int alpha = 0xDD;

			int x = startX;
			int y = startY;

			if (cursorOriginRow == hud.getSelectedRow()) {
				drawHighlight(renderer, x, y, true);

				renderer.translate(0, 0, -0.1);
			}

			x = startX + hud.settings.gridSize + 1;
			y = startY + hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.height()) / 2;

			drawName(renderer, cursorMeter, x, y, ColorUtils.setAlpha(0xFFFFFF, alpha));

			renderer.translate(0, 0, -0.1);

			int x0 = startX;
			int y0 = startY;
			int x1 = x0 + getWidth();
			int y1 = y0 + hud.settings.rowHeight + 2 * hud.settings.gridSize;
			int color = ColorUtils.setAlpha(hud.settings.colorBackground, alpha);

			renderer.fill(x0, y0, x1, y1, color);

			renderer.popMatrix();
		}
	}

	private void drawHighlights(GuiRenderer renderer, int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (cursorMeter == null && isMouseOver(mouseX, mouseY)) {
				drawHighlight(renderer, hud.getHoveredRow(mouseY), false);
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

				drawHighlight(renderer, highlightRow, true);
			}
		} else if (hud.isFocusMode()) {
			int focussedRow = hud.getFocussedRow();

			if (focussedRow != -1) {
				drawHighlight(renderer, focussedRow, true);
			}
		}
	}

	private void drawHighlight(GuiRenderer renderer, int row, boolean selection) {
		int x = 0;
		int y = row * (hud.settings.rowHeight + hud.settings.gridSize);

		drawHighlight(renderer, x, y, selection);
	}

	private void drawHighlight(GuiRenderer renderer, int x, int y, boolean selection) {
		int x0 = x;
		int y0 = y;
		int x1 = x0 + getWidth();
		int y1 = y0 + hud.settings.rowHeight + 2 * hud.settings.gridSize;
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;

		renderer.borders(x0, y0, x1, y1, color);
	}

	private void drawNames(GuiRenderer renderer, int mouseX, int mouseY) {
		if (hud.settings.rowHeight < hud.font.height()) {
			return;
		}

		int startX = hud.settings.gridSize + 1;
		int startY = hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.height()) / 2;

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

				drawName(renderer, meter, x, y, 0xFFFFFFFF);
			}
		}
	}

	private void drawName(GuiRenderer renderer, Meter meter, int x, int y, int color) {
		Text name = Texts.literal(meter.getName());

		if (meter.isHidden()) {
			name.format(Formatting.GRAY, Formatting.ITALIC);
		}

		renderer.drawString(name, x, y, color);
	}

	public void updateWidth() {
		int width = 0;

		for (Meter meter : hud.meters) {
			int nameWidth = hud.font.width(meter.getName());

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
