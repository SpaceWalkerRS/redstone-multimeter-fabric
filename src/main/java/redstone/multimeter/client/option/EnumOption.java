package redstone.multimeter.client.option;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;

public class EnumOption<T extends Enum<T> & Cyclable<T>> extends Option<T> {

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
	public IButton createControl(MultimeterClient client, int width, int height) {
		return new Button(client, 0, 0, width, height, () -> Text.literal(get().getName()), () -> Tooltip.EMPTY, button -> {
			cycle(!Screen.isShiftDown());
			return true;
		});
	}

	public void cycle(boolean forward) {
		set(forward ? Cyclable.next(type, get()) : Cyclable.prev(type, get()));
	}
}
