package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.server.MeterableBlock;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin implements IBlock, MeterableBlock {
	
private static final int MAX_POWER = 15;
	
	@Shadow private boolean wiresGivePower = true;
	
	@Inject(
			method = "updateLogic",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "FIELD",
					ordinal = 1,
					target = "Lnet/minecraft/block/RedstoneWireBlock;POWER:Lnet/minecraft/state/property/IntProperty;"
			)
	)
	private void onUpdateLogicInjectBeforeWith(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir, BlockState oldState, int currentPower, int nonWirePower, int wirePower, int receivedPower) {
		logPowered(world, pos, receivedPower > 0);
	}
	
	@Override
	public int getDefaultMeteredEvents() {
		return EventType.ACTIVE.flag();
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return getReceivedRedstonePower(world, pos) > 0;
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWER) > 0;
	}
	
	private int getReceivedRedstonePower(World world, BlockPos pos) {
		int nonWirePower = getNonWirePower(world, pos);
		
		if (nonWirePower >= MAX_POWER) {
			return MAX_POWER;
		}
		
		int wirePower = getWirePower(world, pos);
		
		if (wirePower > nonWirePower) {
			return wirePower;
		}
		
		return nonWirePower;
	}
	
	private int getNonWirePower(World world, BlockPos pos) {
		wiresGivePower = false;
		int power = world.getReceivedRedstonePower(pos);
		wiresGivePower = true;
		
		return power;
	}
	
	private int getWirePower(World world, BlockPos pos) {
		int power = 0;
		
		Block wire = (Block)(Object)this;
		
		BlockPos above = pos.up();
		BlockState aboveState = world.getBlockState(above);
		boolean aboveIsSolid = aboveState.isSimpleFullBlock(world, above);
		
		for (Direction dir : Direction.Type.HORIZONTAL) {
			BlockPos side = pos.offset(dir);
			BlockState sideState = world.getBlockState(side);
			
			if (sideState.getBlock() == wire) {
				power = Math.max(power, sideState.get(Properties.POWER) - 1);
				
				continue;
			}
			
			if (sideState.isSimpleFullBlock(world, side)) {
				if (!aboveIsSolid) {
					BlockPos aboveSide = side.up();
					BlockState aboveSideState = world.getBlockState(aboveSide);
					
					if (aboveSideState.getBlock() == wire) {
						power = Math.max(power, aboveSideState.get(Properties.POWER) - 1);
					}
				}
			} else {
				BlockPos belowSide = side.down();
				BlockState belowSideState = world.getBlockState(belowSide);
				
				if (belowSideState.getBlock() == wire) {
					power = Math.max(power, belowSideState.get(Properties.POWER) - 1);
				}
			}
		}
		
		return power;
	}
}
