package redstone.multimeter.client.gui.element.button;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.Element;

public interface IButton extends Element {

	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;

	public boolean isActive();

	public void setActive(boolean active);

	public boolean isHovered();

	public Component getMessage();

	public void setMessage(Component message);

	default void setMessage(String message) {
		setMessage(new TextComponent(message));
	}

	public static void playClickSound() {
		SoundManager soundManager = MultimeterClient.MINECRAFT.getSoundManager();
		SoundInstance sound = SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F);

		soundManager.play(sound);
	}
}
