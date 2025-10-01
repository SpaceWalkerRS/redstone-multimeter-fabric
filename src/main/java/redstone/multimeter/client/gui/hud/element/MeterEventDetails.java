package redstone.multimeter.client.gui.hud.element;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.hud.MultimeterHud;
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
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		EventLog event = getEvent();

		if (event != null) {
			renderer.tooltip(event.getTooltip(), -15, 0);
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseDrag(MouseEvent.Drag event) {
		return false;
	}

	@Override
	public boolean mouseScroll(MouseEvent.Scroll event) {
		return false;
	}

	@Override
	public boolean keyPress(KeyEvent.Press event) {
		return false;
	}

	@Override
	public boolean keyRelease(KeyEvent.Release event) {
		return false;
	}

	@Override
	public boolean typeChar(CharacterEvent.Type event) {
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
			setWidth(hud.font.width(event.getTooltip()) + 8);
		} else {
			setWidth(0);
		}
	}

	public void updateHeight() {
		EventLog event = getEvent();

		if (event != null) {
			setHeight(hud.font.height(event.getTooltip()) + 8);
		} else {
			setHeight(0);
		}
	}
}
