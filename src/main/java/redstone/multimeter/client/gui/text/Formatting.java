package redstone.multimeter.client.gui.text;

import net.minecraft.client.render.TextRenderer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.mixin.client.FontAccessor;

public enum Formatting {

	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	OBFUSCATED('k'),
	BOLD('l'),
	STRIKETHROUGH('m'),
	UNDERLINED('n'),
	ITALIC('o'),
	RESET('r');

	public static final char PREFIX = '\u00a7';

	final char code;
	final Integer color;

	private Formatting(char code) {
		TextRenderer tr = MultimeterClient.MINECRAFT.textRenderer;
		FontAccessor font = (FontAccessor) tr;

		this.code = code;

		if ((this.code >= '0' && this.code <= '9') || (this.code >= 'a' && this.code <= 'f')) {
			int i = "0123456789abcdefklmnor".indexOf(this.code);

			if (i >= 0 && i < 16) {
				this.color = font.rsmm$getColors()[i];
			} else {
				throw new IllegalStateException("could not resolve color of " + this.code);
			}
		} else {
			this.color = null;
		}
	}

	@Override
	public String toString() {
		return "" + PREFIX + this.code;
	}

	public char getCode() {
		return this.code;
	}

	public boolean isColor() {
		return this.color != null;
	}

	public TextColor getColor() {
		return TextColor.resolve(this);
	}
}
