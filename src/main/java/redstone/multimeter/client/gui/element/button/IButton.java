package redstone.multimeter.client.gui.element.button;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.IElement;

public interface IButton extends IElement {
	
	public static final int DEFAULT_WIDTH = 150;
	public static final int DEFAULT_HEIGHT = 20;
	
	public boolean isActive();
	
	public void setActive(boolean active);
	
	public boolean isHovered();
	
	public ITextComponent getMessage();
	
	public void setMessage(ITextComponent message);
	
	default void setMessage(String message) {
		setMessage(new TextComponentString(message));
	}
	
	public static void playClickSound(MultimeterClient client) {
		SoundHandler soundManager = client.getMinecraftClient().getSoundHandler();
		PositionedSoundRecord sound = PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F);
		
		soundManager.playSound(sound);
	}
}
