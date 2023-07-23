package redstone.multimeter.mixin.common;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.text.Formatting;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import redstone.multimeter.interfaces.mixin.IText;

@Mixin(Text.class)
public interface TextMixin extends IText {

	@Shadow Text setStyle(Style style);
	@Shadow Style getStyle();

	@Override
	default Text withStyle(Consumer<Style> style) {
		style.accept(getStyle());
		return (Text)this;
	}

	@Override
	default Text setFormatting(Formatting... formatting) {
		Style style = getStyle();

		for (Formatting f : formatting) {
			if (f.isColor()) {
				style.setColor(f);
			} else {
				if (f == Formatting.BOLD) {
					style.setBold(true);
					break;
				} else if (f == Formatting.ITALIC) {
					style.setItalic(true);
					break;
				} else if (f == Formatting.OBFUSCATED) {
					style.setObfuscated(true);
					break;
				} else if (f == Formatting.STRIKETHROUGH) {
					style.setStrikethrough(true);
					break;
				} else if (f == Formatting.UNDERLINE) {
					style.setUnderlined(true);
					break;
				} else if (f == Formatting.RESET) {
					setStyle(style = new Style());
					break;
				}
			}
		}

		return (Text)this;
	}
}
