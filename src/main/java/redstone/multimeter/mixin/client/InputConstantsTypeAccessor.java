package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.InputConstants;

@Mixin(InputConstants.Type.class)
public interface InputConstantsTypeAccessor {

	@Accessor("defaultPrefix")
	String rsmm$getDefaultPrefix();

}
