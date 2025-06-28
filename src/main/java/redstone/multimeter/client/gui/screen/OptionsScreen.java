package redstone.multimeter.client.gui.screen;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.element.ScrollableList;
import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.option.OptionsCategoryElement;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.client.option.Option;
import redstone.multimeter.client.option.Options;

public class OptionsScreen extends RSMMScreen {

	public OptionsScreen() {
		super(Texts.literal("%s Options", RedstoneMultimeterMod.MOD_NAME), true);
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		Options.validate();
		minecraft.options.save();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
		client.getHud().optionsChanged();
	}

	@Override
	protected void initScreen() {
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		ScrollableList list = new ScrollableList(getWidth(), getHeight(), 52, 36);

		list.setSpacing(Button.DEFAULT_HEIGHT);
		list.setDrawBackground(true);
		list.setX(getX());
		list.setY(getY());

		int categoryWidth = list.getEffectiveWidth();

		for (Entry<String, List<Option>> entry : Options.byCategory().entrySet()) {
			String category = entry.getKey();
			List<Option> options = entry.getValue();

			list.add(new OptionsCategoryElement(categoryWidth, category, options));
		}

		int x = getX() + getWidth() / 2;
		int y = getY() + 22;

		Button properties = new BasicButton(x - (4 + Button.DEFAULT_WIDTH), y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, () -> Texts.literal("Default Meter Properties"), Tooltips::empty, button -> {
			client.openScreen(new DefaultMeterPropertiesScreen());
			return true;
		});
		Button controls = new BasicButton(x + 4, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiControls, Tooltips::empty, button -> {
			minecraft.openScreen(new ControlsOptionsScreen(wrapper, minecraft.options));
			return true;
		});

		y = getY() + getHeight() - (Button.DEFAULT_HEIGHT + 8);

		Button reset = new BasicButton(x - (4 + Button.DEFAULT_WIDTH), y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiReset, Tooltips::empty, button -> {
			for (Option option : Options.all()) {
				option.reset();
			}
			update();

			return true;
		});
		Button done = new BasicButton(x + 4, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiDone, Tooltips::empty, button -> {
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
