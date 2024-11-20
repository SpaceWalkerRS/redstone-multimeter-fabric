package redstone.multimeter.interfaces.mixin;

import net.minecraft.text.Formatting;
import net.minecraft.text.Text;

public interface IText {

	default Text setFormatting(Formatting... formatting) {
		throw new UnsupportedOperationException();
	}
}
