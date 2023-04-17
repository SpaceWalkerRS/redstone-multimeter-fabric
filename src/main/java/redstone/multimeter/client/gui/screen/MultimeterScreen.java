package redstone.multimeter.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.meter.MeterControlsElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;

public class MultimeterScreen extends RSMMScreen {

	private final boolean isPauseScreen;

	private ScrollableListElement list;

	public MultimeterScreen(MultimeterClient client) {
		super(client, new TextComponent(RedstoneMultimeterMod.MOD_NAME), false);

		this.isPauseScreen = !Screen.hasShiftDown();
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void update() {
		super.update();
		list.updateCoords();
	}

	@Override
	protected void initScreen() {
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		list = new ScrollableListElement(client, getWidth(), getHeight());
		list.setX(getX());
		list.setY(getY());

		MultimeterHud hud = client.getHud();

		list.add(hud);
		list.add(new MeterControlsElement(client, 0, 0, list.getEffectiveWidth()));

		addChild(list);

		hud.onInitScreen(list.getEffectiveWidth(), list.getHeight());
	}

	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}

	@Override
	protected void renderContent(int mouseX, int mouseY) {
		if (client.getHud().hasContent()) {
			super.renderContent(mouseX, mouseY);
		} else {
			String text;

			if (client.hasSubscription()) {
				text = "Nothing to see here! Add a meter to get started.";
			} else {
				text = "Nothing to see here! Subscribe to a meter group to get started.";
			}

			int textWidth = font.width(text);
			int textHeight = font.lineHeight;
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;

			renderText(font, text, x, y, true, 0xFFFFFFFF);
		}
	}
}
