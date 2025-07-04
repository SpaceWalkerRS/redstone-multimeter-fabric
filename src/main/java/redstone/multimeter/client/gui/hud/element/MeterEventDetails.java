package redstone.multimeter.client.gui.hud.element;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.log.EventLog;

public class MeterEventDetails extends AbstractElement {

	private final MultimeterHud hud;

	public MeterEventDetails(MultimeterHud hud) {
		super(0, 0, 0, 0);

		this.hud = hud;
	}

	private EventLog getEvent() {
		if (!hud.client.isPreviewing() && hud.isFocusMode()) {
			EventLog event = hud.getFocussedEvent();

			if (event != null) {
				return event;
			}
		}

		return null;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		EventLog event = getEvent();

		if (event != null) {
			renderEventDetails(graphics, event);
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	private void renderEventDetails(GuiGraphics graphics, EventLog event) {
		Tooltip tooltip = event.getTooltip();
		List<Component> lines = tooltip.getLines();

		int x = 0;
		int y = 0;

		int width = tooltip.getWidth(hud.font) + 8;
		int height = tooltip.getHeight(hud.font) + 8;

		int backgroundColor = 0xF0100010;
		int borderColor0 = hud.isOnScreen() ? 0x505000FF : 0xFF8000FF;
		int borderColor1 = hud.isOnScreen() ? 0x5028007F : 0xFF5000C0;

		PoseStack poses = graphics.pose();

		poses.pushPose();
		poses.translate(0, 0, 400);

		// background
		hud.renderer.renderRect(graphics, x    , y + 1         , width    , height - 2, backgroundColor); // center, left/right outer borders
		hud.renderer.renderRect(graphics, x + 1, y             , width - 2, 1         , backgroundColor); // top outer border
		hud.renderer.renderRect(graphics, x + 1, y + height - 1, width - 2, 1         , backgroundColor); // bottom outer border

		// inner border
		hud.renderer.renderGradient(graphics, x + 1        , y + 2         , 1        , height - 4, borderColor0, borderColor1); // left
		hud.renderer.renderRect    (graphics, x + 1        , y + height - 2, width - 2, 1         , borderColor1);               // bottom
		hud.renderer.renderGradient(graphics, x + width - 2, y + 2         , 1        , height - 4, borderColor0, borderColor1); // right
		hud.renderer.renderRect    (graphics, x + 1        , y + 1         , width - 2, 1         , borderColor0);               // top

		boolean leftToRight = Options.HUD.DIRECTIONALITY_X.get() == Directionality.X.LEFT_TO_RIGHT;
		boolean topToBottom = Options.HUD.DIRECTIONALITY_Y.get() == Directionality.Y.TOP_TO_BOTTOM;

		int textX = x + 4;
		int textY = y + 4;

		for (int index = 0; index < lines.size(); index++) {
			Component line = lines.get(index);

			int lineX = textX + (leftToRight ? 0 : (width - 8) - hud.font.width(line));
			int lineY = textY + (topToBottom ? 1 : -1) * (index + (topToBottom ? 0 : 1)) * (hud.font.lineHeight + 1) + (topToBottom ? 0 : (height - 4));

			hud.renderer.renderText(graphics, line, lineX, lineY, 0xFFFFFFFF);
		}

		poses.popPose();
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
	public void tick() {
	}

	@Override
	public void update() {
	}

	public void updateWidth() {
		EventLog event = getEvent();

		if (event != null) {
			setWidth(event.getTooltip().getWidth(hud.font) + 8);
		} else {
			setWidth(0);
		}
	}

	public void updateHeight() {
		EventLog event = getEvent();

		if (event != null) {
			setHeight(event.getTooltip().getHeight(hud.font) + 8);
		} else {
			setHeight(0);
		}
	}
}
