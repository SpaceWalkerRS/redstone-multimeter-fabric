package redstone.multimeter.client.gui.element.button;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.TextureRegion;
import redstone.multimeter.client.gui.element.action.MousePress;

public class TransparentButton extends Button {
	
	public TransparentButton(MultimeterClient client, int x, int y, Supplier<Text> message, Supplier<List<Text>> tooltip, MousePress<Button> onPress) {
		super(client, x, y, message, tooltip, onPress);
	}
	
	public TransparentButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> message, Supplier<List<Text>> tooltip, MousePress<Button> onPress) {
		super(client, x, y, width, height, message, tooltip, onPress);
	}
	
	@Override
	protected TextureRegion getBackgroundTexture() {
		return null;
	}
}
