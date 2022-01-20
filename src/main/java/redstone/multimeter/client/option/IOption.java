package redstone.multimeter.client.option;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.button.IButton;

public interface IOption {
	
	public String getName();
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public String getAsString();
	
	public void setFromString(String value);
	
	public IButton createControl(MultimeterClient client, int width, int height);
	
	public void setListener(OptionListener listener);
	
}
