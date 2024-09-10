package redstone.multimeter.common.meter;

import net.minecraft.world.World;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;

public abstract class MeterPropertiesManager {

	public boolean validate(MutableMeterProperties properties) {
		DimPos pos = properties.getPos();

		if (pos == null) {
			return false;
		}

		World world = getWorld(pos);

		if (world == null) {
			return false;
		}

		postValidation(properties, world, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}

	protected abstract World getWorld(DimPos pos);

	protected abstract void postValidation(MutableMeterProperties properties, World world, int x, int y, int z);

}
