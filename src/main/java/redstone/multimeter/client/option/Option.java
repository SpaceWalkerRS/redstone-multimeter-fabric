package redstone.multimeter.client.option;

import redstone.multimeter.client.gui.element.button.Button;

public interface Option {

	public String getName();

	public String getDescription();

	public boolean isDefault();

	public void reset();

	public String getAsString();

	public void setFromString(String value);

	public Button createControl(int width, int height);

	public void setListener(OptionListener listener);

}
