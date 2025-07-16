package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.text.Text;

public interface Option {

	String key();

	String legacyKey();

	String translationKey();

	Text getName();

	Text getDescription();

	Text getDisplayValue();

	boolean isDefault();

	void reset();

	String getAsString();

	void setFromString(String value);

	Button createControl(int width, int height);

	void setListener(OptionListener listener);

}
