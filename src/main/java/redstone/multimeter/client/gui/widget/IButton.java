package redstone.multimeter.client.gui.widget;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.IElement;

public interface IButton extends IElement {
	
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	
	public boolean isActive();
	
	public void setActive(boolean active);
	
	public static void playClickSound(MultimeterClient client) {
		SoundManager soundManager = client.getMinecraftClient().getSoundManager();
		SoundInstance sound = PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F);
		
		soundManager.play(sound);
	}
}
