package rsmm.fabric.server;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.MeterPropertiesManager;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.util.ColorUtils;

public class ServerMeterPropertiesManager extends MeterPropertiesManager {
	
	private final Multimeter multimeter;
	
	public ServerMeterPropertiesManager(Multimeter multimeter) {
		this.multimeter = multimeter;
	}
	
	@Override
	protected World getWorldOf(WorldPos pos) {
		return multimeter.getMultimeterServer().getWorldOf(pos);
	}
	
	@Override
	protected void postValidation(MeterProperties properties, World world, BlockPos pos) {
		// These are the backup values for if the saved defaults
		// do not fully populate the meter settings.
		
		if (properties.getName() == null) {
			properties.setName("Meter");
		}
		if (properties.getColor() == null) {
			properties.setColor(ColorUtils.nextColor());
		}
		if (properties.getMovable() == null) {
			properties.setMovable(true);
		}
		if (properties.getEventTypes() == null) {
			properties.setEventTypes(EventType.POWERED.flag() | EventType.MOVED.flag());
		}
	}
}
