package redstone.multimeter.server.meter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.ColorPicker;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.MeterPropertiesManager;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.server.Multimeter;

public class ServerMeterPropertiesManager extends MeterPropertiesManager {

	private final Multimeter multimeter;

	public ServerMeterPropertiesManager(Multimeter multimeter) {
		this.multimeter = multimeter;
	}

	@Override
	protected Level getLevel(DimPos pos) {
		return multimeter.getServer().getLevel(pos);
	}

	@Override
	protected void postValidation(MutableMeterProperties properties, Level level, BlockPos pos) {
		// These are the backup values for if the saved defaults
		// do not fully populate the meter settings.

		if (properties.getName() == null) {
			properties.setName("Meter");
		}
		if (properties.getColor() == null) {
			properties.setColor(ColorPicker.RANDOM.next());
		}
		if (properties.getMovable() == null) {
			properties.setMovable(true);
		}
		if (properties.getEventTypes() == null) {
			properties.setEventTypes(EventType.POWERED.flag() | EventType.MOVED.flag());
		}
	}
}
