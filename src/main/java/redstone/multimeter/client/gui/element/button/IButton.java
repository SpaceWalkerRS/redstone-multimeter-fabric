package redstone.multimeter.client.gui.element.button;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.Element;

public interface IButton extends Element {

	public static final String CLICK_SOUND = "random.click";

	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;

	public boolean isActive();

	public void setActive(boolean active);

	public boolean isHovered();

	public String getMessage();

	public void setMessage(String message);

	public static void playClickSound(MultimeterClient client) {
		client.getMinecraft().soundSystem.play(CLICK_SOUND, 1.0F, 1.0F);
	}
}
