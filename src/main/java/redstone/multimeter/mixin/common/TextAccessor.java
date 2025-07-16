package redstone.multimeter.mixin.common;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.text.Text;

@Mixin(Text.class)
public interface TextAccessor {

	@Accessor("text")
	String rsmm$getText();

	@Accessor("translated")
	String rsmm$getKey();

	@Accessor("siblings")
	List<String> rsmm$getUsing();

}
