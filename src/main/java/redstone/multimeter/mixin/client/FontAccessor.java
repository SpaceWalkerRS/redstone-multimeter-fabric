package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.TextRenderer;

@Mixin(TextRenderer.class)
public interface FontAccessor {

	@Accessor("colors")
	int[] rsmm$getColors();

}
