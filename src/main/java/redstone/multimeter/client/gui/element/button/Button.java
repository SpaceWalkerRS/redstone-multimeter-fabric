package redstone.multimeter.client.gui.element.button;

import net.minecraft.client.sound.instance.SimpleSoundInstance;
import net.minecraft.client.sound.instance.SoundInstance;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.resource.Identifier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public interface Button extends Element {

	Identifier CLICK_SOUND = new Identifier("gui.button.press");

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
		SoundManager soundManager = MultimeterClient.MINECRAFT.getSoundManager();
		SoundInstance sound = SimpleSoundInstance.of(CLICK_SOUND, 1.0F);

		soundManager.play(sound);
	}
}
