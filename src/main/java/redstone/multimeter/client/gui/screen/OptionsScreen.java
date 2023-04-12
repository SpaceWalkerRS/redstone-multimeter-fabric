package redstone.multimeter.client.gui.screen;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.option.OptionsCategoryElement;
import redstone.multimeter.client.option.IOption;
import redstone.multimeter.client.option.Options;

public class OptionsScreen extends RSMMScreen {

	public OptionsScreen(MultimeterClient client) {
		super(client, Component.literal(String.format("%s Options", RedstoneMultimeterMod.MOD_NAME)), true);
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		Options.validate();
		minecraft.options.save();
		client.getHud().onOptionsChanged();
	}

	@Override
	protected void initScreen() {
		ScrollableListElement list = new ScrollableListElement(client, getWidth(), getHeight(), 52, 36);

		list.setSpacing(IButton.DEFAULT_HEIGHT);
		list.setDrawBackground(true);
		list.setX(getX());
		list.setY(getY());

		int categoryWidth = list.getEffectiveWidth();

		for (Entry<String, List<IOption>> entry : Options.byCategory().entrySet()) {
			String category = entry.getKey();
			List<IOption> options = entry.getValue();

			list.add(new OptionsCategoryElement(client, categoryWidth, category, options));
		}

		int x = getX() + getWidth() / 2;
		int y = getY() + 22;

		IButton properties = new Button(client, x - (4 + IButton.DEFAULT_WIDTH), y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> Component.literal("Default Meter Properties"), () -> Tooltip.EMPTY, button -> {
			client.openScreen(new DefaultMeterPropertiesScreen(client));
			return true;
		});
		IButton controls = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> Component.translatable("options.controls"), () -> Tooltip.EMPTY, button -> {
			minecraft.setScreen(new ControlsScreen(wrapper, minecraft.options));
			return true;
		});

		y = getY() + getHeight() - (IButton.DEFAULT_HEIGHT + 8);

		IButton reset = new Button(client, x - (4 + IButton.DEFAULT_WIDTH), y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> Component.translatable("controls.reset"), () -> Tooltip.EMPTY, button -> {
			for (IOption option : Options.all()) {
				option.reset();
			}
			update();

			return true;
		});
		IButton done = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> CommonComponents.GUI_DONE, () -> Tooltip.EMPTY, button -> {
			close();
			return true;
		});

		addChild(list);
		addChild(properties);
		addChild(controls);
		addChild(reset);
		addChild(done);
	}
}
