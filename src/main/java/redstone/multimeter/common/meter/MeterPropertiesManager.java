package redstone.multimeter.common.meter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;

public abstract class MeterPropertiesManager {

	public boolean validate(MutableMeterProperties properties) {
		DimPos pos = properties.getPos();

		if (pos == null) {
			return false;
		}

		Level level = getLevel(pos);

		if (level == null) {
			return false;
		}

		postValidation(properties, level, pos.getBlockPos());

		return true;
	}

	protected abstract Level getLevel(DimPos pos);

	protected abstract void postValidation(MutableMeterProperties properties, Level level, BlockPos pos);

}
