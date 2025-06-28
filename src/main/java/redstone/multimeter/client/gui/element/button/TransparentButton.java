package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public class TransparentButton extends BasicButton {

	public TransparentButton(int x, int y, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<BasicButton> onPress) {
		super(x, y, message, tooltip, onPress);
	}

	public TransparentButton(int x, int y, int width, int height, Supplier<Text> message, Supplier<Tooltip> tooltip, MousePress<BasicButton> onPress) {
		super(x, y, width, height, message, tooltip, onPress);
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
