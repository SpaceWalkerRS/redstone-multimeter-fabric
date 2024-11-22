package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.TextureRegion;

public class TransparentToggleButton extends ToggleButton {

	public TransparentToggleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, getter, toggle);
	}

	public TransparentToggleButton(MultimeterClient client, int x, int y, int width, int height, Function<Boolean, String> text, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, text, getter, toggle);
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
