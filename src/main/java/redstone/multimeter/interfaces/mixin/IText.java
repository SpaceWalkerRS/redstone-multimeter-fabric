package redstone.multimeter.interfaces.mixin;

import java.util.function.Consumer;

import net.minecraft.text.Formatting;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public interface IText {

	default Text withStyle(Consumer<Style> style) {
		throw new UnsupportedOperationException();
	}

	default Text setFormatting(Formatting... formatting) {
		throw new UnsupportedOperationException();
	}
}
