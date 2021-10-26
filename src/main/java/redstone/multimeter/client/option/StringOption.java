package redstone.multimeter.client.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.widget.IButton;
import redstone.multimeter.client.gui.widget.TextField;

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
