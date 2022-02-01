package redstone.multimeter.common.meter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;

public abstract class MeterPropertiesManager {
	
	public boolean validate(MutableMeterProperties properties) {
		WorldPos pos = properties.getPos();
		
		if (pos == null) {
			return false;
		}
		
		World world = getWorldOf(pos);
		
		if (world == null) {
			return false;
		}
		
		postValidation(properties, world, pos.getBlockPos());
		
		return true;
	}
	
	protected abstract World getWorldOf(WorldPos pos);
	
	protected abstract void postValidation(MutableMeterProperties properties, World world, BlockPos pos);
	
}
