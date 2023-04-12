package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import net.minecraft.network.chat.Component;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.action.MousePress;

public class TransparentButton extends Button {

	public TransparentButton(MultimeterClient client, int x, int y, Supplier<Component> message, Supplier<Tooltip> tooltip, MousePress<Button> onPress) {
		super(client, x, y, message, tooltip, onPress);
	}

	public TransparentButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Component> message, Supplier<Tooltip> tooltip, MousePress<Button> onPress) {
		super(client, x, y, width, height, message, tooltip, onPress);
	}

	@Override
	protected TextureRegion getBackgroundTexture() {
		return null;
	}

	@Override
	protected int getMessageColor() {
		return isActive() ? (isHovered() ? 0xFFC0C0C0 : 0xFFFFFFFF) : 0xFF909090;
	}
}
