package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.TextureRegion;

public class TransparentToggleButton extends ToggleButton {

	public TransparentToggleButton(int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<BasicButton> toggle) {
		super(x, y, width, height, getter, toggle);
	}

	public TransparentToggleButton(int x, int y, int width, int height, Function<Boolean, Text> text, Supplier<Boolean> getter, Consumer<BasicButton> toggle) {
		super(x, y, width, height, text, getter, toggle);
	}

	@Override
	protected TextureRegion getButtonTexture() {
		return null;
	}

	@Override
	protected int getMessageColor() {
		return this.isActive() ? (this.isHovered() ? 0xFFC0C0C0 : 0xFFFFFFFF) : 0xFF909090;
	}
}
