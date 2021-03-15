package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.player.PlayerEntity;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.interfaces.mixin.IPlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntity {
	
	private MeterGroup meterGroup;
	
	@Override
	public MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	public void subscribeToMeterGroup(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
}
