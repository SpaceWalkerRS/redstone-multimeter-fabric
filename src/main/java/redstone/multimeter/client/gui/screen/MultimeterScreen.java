package redstone.multimeter.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.ScrollableList;
import redstone.multimeter.client.gui.element.meter.MeterControlsElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.gui.text.Texts;

public class MultimeterScreen extends RSMMScreen {

	private final boolean isPauseScreen;

	private ScrollableList list;

	public MultimeterScreen() {
		super(Texts.modName(), false);

		this.isPauseScreen = !Screen.hasShiftDown();
	}

	@Override
	public void update() {
		super.update();
		list.updateCoords();
	}

	@Override
	protected void initScreen() {
		list = new ScrollableList(getWidth(), getHeight());
		list.setX(getX());
		list.setY(getY());

		MultimeterHud hud = client.getHud();

		list.add(hud);
		list.add(new MeterControlsElement(0, 0, list.getEffectiveWidth()));

		addChild(list);

		hud.onInitScreen(list.getEffectiveWidth(), list.getHeight());
	}

	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}

	@Override
	protected void renderContent(GuiRenderer renderer, int mouseX, int mouseY) {
		if (client.getHud().hasContent()) {
			super.renderContent(renderer, mouseX, mouseY);
		} else {
			String text;

			if (client.hasSubscription()) {
				text = "Nothing to see here! Add a meter to get started.";
			} else {
				text = "Nothing to see here! Subscribe to a meter group to get started.";
			}

			int textWidth = font.width(text);
			int textHeight = font.height();
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;

			font.drawWithShadow(text, x, y);
		}
	}
}
