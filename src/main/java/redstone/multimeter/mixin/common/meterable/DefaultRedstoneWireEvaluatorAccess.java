package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;

@Mixin(DefaultRedstoneWireEvaluator.class)
public interface DefaultRedstoneWireEvaluatorAccess {

	@Invoker("calculateTargetStrength")
	int rsmm$calculateTargetStrength(Level world, BlockPos pos);

}
