package redstone.multimeter.client.gui.element.button;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public interface Button extends Element {

	String CLICK_SOUND = "random.click";

	int DEFAULT_WIDTH = 150;
	int DEFAULT_HEIGHT = 20;

	boolean isActive();

	void setActive(boolean active);

	boolean isHovered();

	Text getMessage();

	void setMessage(Text message);

	default void setMessage(String message) {
		this.setMessage(Texts.literal(message));
	}

	static void playClickSound() {
		MultimeterClient.MINECRAFT.soundSystem.play(CLICK_SOUND, 1.0F, 1.0F);
	}
}
