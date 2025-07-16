package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public abstract class BaseOption<T> implements Option {

	private final String key;
	// used for parsing config files from older RSMM versions (<1.16)
	private final String legacyKey;
	private final T defaultValue;

	private T value;
	private OptionListener listener;

	protected BaseOption(String key, String legacyKey, T defaultValue) {
		this.key = key;
		this.legacyKey = legacyKey;
		this.defaultValue = defaultValue;

		this.value = this.defaultValue;
	}

	@Override
	public String key() {
		return this.key;
	}

	@Override
	public String legacyKey() {
		return this.legacyKey;
	}

	@Override
	public String translationKey() {
		return "rsmm.option." + this.key;
	}

	@Override
	public Text getName() {
		return Texts.translatable("rsmm.option." + this.key + ".name");
	}

	@Override
	public Text getDescription() {
		return Texts.translatable("rsmm.option." + this.key + ".description");
	}

	@Override
	public boolean isDefault() {
		return this.value.equals(this.defaultValue);
	}

	@Override
	public void reset() {
		this.set(this.defaultValue);
	}

	@Override
	public String getAsString() {
		return value.toString();
	}

	@Override
	public void setListener(OptionListener listener) {
		this.listener = listener;

		if (this.listener != null) {
			this.listener.valueChanged();
		}
	}

	public T getDefault() {
		return this.defaultValue;
	}

	public T get() {
		return this.value;
	}

	public void set(T value) {
		this.value = value;

		if (this.listener != null) {
			this.listener.valueChanged();
		}
	}
}
