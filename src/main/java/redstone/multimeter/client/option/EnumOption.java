package redstone.multimeter.client.option;

import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class EnumOption<T extends Enum<T> & Cyclable<T>> extends BaseOption<T> {

	protected final Class<T> type;

	public EnumOption(String name, String description, Class<T> type, T defaultValue) {
		super(name, description, defaultValue);

		this.type = type;
	}

	@Override
	public void setFromString(String value) {
		try {
			set(Enum.valueOf(type, value));
		} catch (IllegalArgumentException e) {

		}
	}

	@Override
	public Button createControl(int width, int height) {
		return new BasicButton(0, 0, width, height, () -> Texts.literal(get().getName()), Tooltips::empty, button -> {
			cycle(!Screen.hasShiftDown());
			return true;
		});
	}

	public void cycle(boolean forward) {
		set(forward ? Cyclable.next(type, get()) : Cyclable.prev(type, get()));
	}
}
