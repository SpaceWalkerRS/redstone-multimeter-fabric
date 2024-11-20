package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.text.Formatting;
import net.minecraft.text.Text;

import redstone.multimeter.interfaces.mixin.IText;

@Mixin(Text.class)
public class TextMixin implements IText {

	@Shadow private Formatting color;
	@Shadow private Boolean bold;
	@Shadow private Boolean italic;
	@Shadow private Boolean underlined;
	@Shadow private Boolean obfuscated;

	@Override
	public Text setFormatting(Formatting... formatting) {
		for (Formatting f : formatting) {
			if (f.isColor()) {
				color = f;
			} else {
				if (f == Formatting.BOLD) {
					bold = true;
					break;
				} else if (f == Formatting.ITALIC) {
					italic = true;
					break;
				} else if (f == Formatting.OBFUSCATED) {
					obfuscated = true;
					break;
				} else if (f == Formatting.STRIKETHROUGH) {
//					rsmm$setStrikethrough(true);
					break;
				} else if (f == Formatting.UNDERLINE) {
					underlined = true;
					break;
				} else if (f == Formatting.RESET) {
					color = null;
					bold = italic = underlined = obfuscated = null;
					break;
				}
			}
		}

		return (Text)(Object)this;
	}
}
