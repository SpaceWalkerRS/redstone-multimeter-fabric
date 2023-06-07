package redstone.multimeter.client.gui.hud.element;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import redstone.multimeter.client.Keybinds;
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		PoseStack poses = graphics.pose();

		poses.pushPose();
		drawCursorMeter(graphics, mouseX, mouseY);
		poses.translate(0, 0, -1);
		drawHighlights(graphics, mouseX, mouseY);
		poses.translate(0, 0, -1);
		drawNames(graphics, mouseX, mouseY);
		poses.translate(0, 0, -1);
		hud.renderer.renderRect(graphics, 0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		poses.popPose();
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
		if (Keybinds.OPEN_METER_CONTROLS.isUnbound() || cursorMeter != null) {
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

	private void drawCursorMeter(GuiGraphics graphics, int mouseX, int mouseY) {
		if (cursorMeter != null) {
			PoseStack poses = graphics.pose();

			poses.pushPose();

			int startX = mouseX + cursorOffsetX;
			int startY = mouseY + cursorOffsetY;
			int alpha = 0xDD;

			int x = startX;
			int y = startY;

			if (cursorOriginRow == hud.getSelectedRow()) {
				drawHighlight(graphics, x, y, true);

				poses.translate(0, 0, -0.1);
			}

			x = startX + hud.settings.gridSize + 1;
			y = startY + hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.lineHeight) / 2;

			drawName(graphics, cursorMeter, x, y, ColorUtils.setAlpha(0xFFFFFF, alpha));

			poses.translate(0, 0, -0.1);

			x = startX;
			y = startY;
			int w = getWidth();
			int h = hud.settings.rowHeight + 2 * hud.settings.gridSize;
			int color = ColorUtils.setAlpha(hud.settings.colorBackground, alpha);

			hud.renderer.renderRect(graphics, x, y, w, h, color);

			poses.popPose();
		}
	}

	private void drawHighlights(GuiGraphics graphics, int mouseX, int mouseY) {
		if (hud.isOnScreen()) {
			if (cursorMeter == null && isHovered(mouseX, mouseY)) {
				drawHighlight(graphics, hud.getHoveredRow(mouseY), false);
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

				drawHighlight(graphics, highlightRow, true);
			}
		}
	}

	private void drawHighlight(GuiGraphics graphics, int row, boolean selection) {
		int x = 0;
		int y = row * (hud.settings.rowHeight + hud.settings.gridSize);

		drawHighlight(graphics, x, y, selection);
	}

	private void drawHighlight(GuiGraphics graphics, int x, int y, boolean selection) {
		int width = getWidth() - hud.settings.gridSize;
		int height = hud.settings.rowHeight + hud.settings.gridSize;
		int color = selection ? hud.settings.colorHighlightSelected : hud.settings.colorHighlightHovered;

		hud.renderer.renderHighlight(graphics, x, y, width, height, color);
	}

	private void drawNames(GuiGraphics graphics, int mouseX, int mouseY) {
		if (hud.settings.rowHeight < hud.font.lineHeight) {
			return;
		}

		int startX = hud.settings.gridSize + 1;
		int startY = hud.settings.gridSize + 1 + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.lineHeight) / 2;

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

				drawName(graphics, meter, x, y, 0xFFFFFFFF);
			}
		}
	}

	private void drawName(GuiGraphics graphics, Meter meter, int x, int y, int color) {
		MutableComponent name = Component.literal(meter.getName());

		if (meter.isHidden()) {
			name.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
		}

		hud.renderer.renderText(graphics, name, x, y, color);
	}

	public void updateWidth() {
		int width = 0;

		for (Meter meter : hud.meters) {
			int nameWidth = hud.font.width(meter.getName());

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
		hud.client.sendPacket(packet);

		return true;
	}
}
