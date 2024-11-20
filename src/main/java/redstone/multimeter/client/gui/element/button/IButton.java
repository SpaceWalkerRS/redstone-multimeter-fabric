package redstone.multimeter.client.gui.element.button;

import net.minecraft.client.resource.Identifier;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.Element;

public interface IButton extends Element {

	public static final Identifier CLICK_SOUND = new Identifier("gui.button.press");

	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;

	public boolean isActive();

	public void setActive(boolean active);

	public boolean isHovered();

	public Text getMessage();

	public void setMessage(Text message);

	default void setMessage(String message) {
		setMessage(Text.literal(message));
	}

	public static void playClickSound(MultimeterClient client) {
		client.getMinecraft().soundSystem.play("random.click", 1.0F, 1.0F);
	}
}
