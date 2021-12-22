package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RedstoneComponentBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends RedstoneComponentBlock implements MeterableBlock, PowerSource {
	
	protected RepeaterBlockMixin(boolean active) {
		super(active);
	}
	
	@Inject(
			method = "method_4758",
			at = @At(
					value = "RETURN"
			)
	)
	private void onIsLocked(WorldView worldView, int x, int y, int z, int metadata, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && worldView instanceof World) {
			World world = (World)worldView;
			
			if (!world.isClient) {
				logPowered(world, x, y, z, method_4754(world, x, y, z, metadata));
			}
		}
	}
	
	@Override
	public int getPowerLevel(World world, int x, int y, int z, int metadata) {
		return field_5539 ? MAX_POWER : MIN_POWER;
	}
}
