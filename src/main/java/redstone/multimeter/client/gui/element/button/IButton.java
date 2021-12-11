package redstone.multimeter.client.gui.element.button;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.IElement;

public interface IButton extends IElement {
	
	public static final Identifier CLICK_SOUND = new Identifier("gui.button.press");
	
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	
	public boolean isActive();
	
	public void setActive(boolean active);
	
	public boolean isHovered();
	
	public Text getMessage();
	
	public void setMessage(Text message);
	
	default void setMessage(String message) {
		setMessage(new LiteralText(message));
	}
	
	public static void playClickSound(MultimeterClient client) {
		SoundManager soundManager = client.getMinecraftClient().getSoundManager();
		soundManager.play(PositionedSoundInstance.master(CLICK_SOUND, 1.0F));
	}
}
