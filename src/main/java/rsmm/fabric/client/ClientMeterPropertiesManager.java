package rsmm.fabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.MeterPropertiesManager;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;

public class ClientMeterPropertiesManager extends MeterPropertiesManager {
	
	private final MultimeterClient multimeterClient;
	
	public ClientMeterPropertiesManager(MultimeterClient multimeterClient) {
		super(multimeterClient.getConfigFolder());
		
		this.multimeterClient = multimeterClient;
		
		this.load();
	}
	
	@Override
	protected World getWorldOf(WorldPos pos) {
		MinecraftClient client = multimeterClient.getMinecraftClient();
		return pos.isOf(client.world) ? client.world : null;
	}
	
	@Override
	protected void postValidation(MeterProperties properties) {
		if (properties.getMovable() == null) {
			properties.setMovable(!Screen.hasShiftDown());
		}
		if (properties.getEventTypes() == null) {
			for (int index = 0; index < EventType.ALL.length; index++) {
				KeyBinding keyBind = KeyBindings.TOGGLE_EVENT_TYPES[index];
				
				if (keyBind.isPressed()) {
					EventType type = EventType.ALL[index];
					properties.toggleEventType(type);
				}
			}
		}
	}
}
