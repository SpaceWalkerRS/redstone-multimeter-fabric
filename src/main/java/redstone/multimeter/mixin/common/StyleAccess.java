package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;

@Mixin(Style.class)
public interface StyleAccess {

	@Accessor("color")
	Formatting rsmm$color();

	@Accessor("bold")
	Boolean rsmm$bold();

	@Accessor("italic")
	Boolean rsmm$italic();

	@Accessor("underlined")
	Boolean rsmm$underlined();

	@Accessor("strikethrough")
	Boolean rsmm$strikethrough();

	@Accessor("obfuscated")
	Boolean rsmm$obfuscated();

	@Accessor("clickEvent")
	ClickEvent rsmm$clickEvent();

	@Accessor("hoverEvent")
	HoverEvent rsmm$hoverEvent();

}
