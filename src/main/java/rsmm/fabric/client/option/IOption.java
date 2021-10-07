package rsmm.fabric.client.option;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.widget.IButton;

public interface IOption {
	
	public String getName();
	
	default Text getDisplayName() {
		return new LiteralText(getName());
	}
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public String getAsString();
	
	public void setFromString(String value);
	
	public IButton createControl(MultimeterClient client, int width, int height);
	
	public void setListener(OptionListener listener);
	
}
