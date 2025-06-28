package redstone.multimeter.client.gui.text;

import net.minecraft.client.render.TextRenderer;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.mixin.client.FontAccessor;

public enum Formatting {

	BLACK(net.minecraft.text.Formatting.BLACK),
	DARK_BLUE(net.minecraft.text.Formatting.DARK_BLUE),
	DARK_GREEN(net.minecraft.text.Formatting.DARK_GREEN),
	DARK_AQUA(net.minecraft.text.Formatting.DARK_AQUA),
	DARK_RED(net.minecraft.text.Formatting.DARK_RED),
	DARK_PURPLE(net.minecraft.text.Formatting.DARK_PURPLE),
	GOLD(net.minecraft.text.Formatting.GOLD),
	GRAY(net.minecraft.text.Formatting.GRAY),
	DARK_GRAY(net.minecraft.text.Formatting.DARK_GRAY),
	BLUE(net.minecraft.text.Formatting.BLUE),
	GREEN(net.minecraft.text.Formatting.GREEN),
	AQUA(net.minecraft.text.Formatting.AQUA),
	RED(net.minecraft.text.Formatting.RED),
	LIGHT_PURPLE(net.minecraft.text.Formatting.LIGHT_PURPLE),
	YELLOW(net.minecraft.text.Formatting.YELLOW),
	WHITE(net.minecraft.text.Formatting.WHITE),
	OBFUSCATED(net.minecraft.text.Formatting.OBFUSCATED),
	BOLD(net.minecraft.text.Formatting.BOLD),
	STRIKETHROUGH(net.minecraft.text.Formatting.STRIKETHROUGH),
	UNDERLINED(net.minecraft.text.Formatting.UNDERLINE),
	ITALIC(net.minecraft.text.Formatting.ITALIC),
	RESET(net.minecraft.text.Formatting.RESET);

	public static final char PREFIX = '\u00a7';

	final net.minecraft.text.Formatting resolved;
	final char code;
	final Integer color;

	private Formatting(net.minecraft.text.Formatting formatting) {
		TextRenderer tr = MultimeterClient.MINECRAFT.textRenderer;
		FontAccessor font = (FontAccessor) tr;

		this.resolved = formatting;
		this.code = formatting.toString().charAt(1);

		if (formatting.isColor()) {
			int i = "0123456789abcdefklmnor".indexOf(this.code);

			if (i >= 0 && i < 16) {
				this.color = font.rsmm$getColors()[i];
			} else {
				throw new IllegalStateException("could not resolve color of " + formatting);
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

	public net.minecraft.text.Formatting resolve() {
		return this.resolved;
	}

	public static Formatting resolve(net.minecraft.text.Formatting formatting) {
		for (Formatting f : Formatting.values()) {
			if (f.resolved == formatting) {
				return f;
			}
		}

		throw new IllegalStateException("unknown chat formatting code " + formatting.toString().charAt(1));
	}
}
