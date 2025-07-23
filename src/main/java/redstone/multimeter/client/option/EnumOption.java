package redstone.multimeter.client.option;

import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class EnumOption<T extends Enum<T> & Cyclable<T>> extends BaseOption<T> {

	protected final Class<T> type;

	public EnumOption(String key, String legacyKey, Class<T> type, T defaultValue) {
		super(key, legacyKey, defaultValue);

		this.type = type;
	}

	@Override
	public Text getDisplayValue() {
		return Texts.translatable(this.translationKey() + ".value." + this.getAsString());
	}

	@Override
	public String getAsString() {
		return this.get().key();
	}

	@Override
	public void setFromString(String value) {
		T v = Cyclable.byKey(this.type, value);
		if (v == null) {
			v = Cyclable.byLegacyKey(this.type, value);
		}

		if (v == null) {
			throw new IllegalStateException("could not find value " + value + " of type " + this.type.getSimpleName() + " for option " + this.key());
		} else {
			this.set(v);
		}
	}

	@Override
	public Button createControl(int width, int height) {
		return new BasicButton(0, 0, width, height, this::getDisplayValue, Tooltips::empty, button -> {
			this.cycle(!Screen.hasShiftDown());
			return true;
		});
	}

	public void cycle(boolean forward) {
		this.set(forward ? Cyclable.next(this.type, this.get()) : Cyclable.prev(this.type, this.get()));
	}
}
