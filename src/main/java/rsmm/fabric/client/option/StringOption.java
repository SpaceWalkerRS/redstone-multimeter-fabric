package rsmm.fabric.client.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.widget.IButton;
import rsmm.fabric.client.gui.widget.TextField;

public class StringOption extends Option<String> {
	
	public StringOption(String name, String description, String defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setFromString(String value) {
		set(value);
	}
	
	@Override
	public IButton createControl(MultimeterClient client, int width, int height) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		TextRenderer font = minecraftClient.textRenderer;
		
		return new TextField(font, 0, 0, width, height, () -> get(), text -> set(text));
	}
}
