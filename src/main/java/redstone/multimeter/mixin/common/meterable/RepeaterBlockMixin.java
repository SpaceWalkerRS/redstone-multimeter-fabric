package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.material.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends Block implements MeterableBlock, PowerSource {

	@Shadow @Final private boolean powered;

	@Shadow private boolean shouldBePowered(World world, int x, int y, int z, int metadata) { return false; }

	private RepeaterBlockMixin(int id, Material material) {
		super(id, material);
	}

	@Inject(
		method = "shouldBePowered",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, int x, int y, int z, int metadata, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, x, y, z, cir.getReturnValue());
	}

	@Inject(
		method = "isLocked",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(WorldView world, int x, int y, int z, int metadata, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && world instanceof ServerWorld) {
			rsmm$logPowered((ServerWorld)world, x, y, z, ((Block)(Object)this).id, metadata);
		}
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, int x, int y, int z, int metadata) {
		return shouldBePowered(world, x, y, z, metadata);
	}

	@Override
	public boolean rsmm$isActive(World world, int x, int y, int z, int metadata) {
		return powered;
	}

	@Override
	public int rsmm$getPowerLevel(World world, int x, int y, int z, int metadata) {
		return powered ? MAX_POWER : MIN_POWER;
	}
}
