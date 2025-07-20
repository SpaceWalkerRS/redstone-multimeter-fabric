package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

@Mixin(Style.class)
public interface StyleAccess {

	@Accessor("color")
	TextColor rsmm$color();

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
