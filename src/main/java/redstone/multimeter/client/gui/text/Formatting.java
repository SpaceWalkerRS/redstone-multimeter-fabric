package redstone.multimeter.client.gui.text;

import net.minecraft.ChatFormatting;

public enum Formatting {

	BLACK(ChatFormatting.BLACK),
	DARK_BLUE(ChatFormatting.DARK_BLUE),
	DARK_GREEN(ChatFormatting.DARK_GREEN),
	DARK_AQUA(ChatFormatting.DARK_AQUA),
	DARK_RED(ChatFormatting.DARK_RED),
	DARK_PURPLE(ChatFormatting.DARK_PURPLE),
	GOLD(ChatFormatting.GOLD),
	GRAY(ChatFormatting.GRAY),
	DARK_GRAY(ChatFormatting.DARK_GRAY),
	BLUE(ChatFormatting.BLUE),
	GREEN(ChatFormatting.GREEN),
	AQUA(ChatFormatting.AQUA),
	RED(ChatFormatting.RED),
	LIGHT_PURPLE(ChatFormatting.LIGHT_PURPLE),
	YELLOW(ChatFormatting.YELLOW),
	WHITE(ChatFormatting.WHITE),
	OBFUSCATED(ChatFormatting.OBFUSCATED),
	BOLD(ChatFormatting.BOLD),
	STRIKETHROUGH(ChatFormatting.STRIKETHROUGH),
	UNDERLINED(ChatFormatting.UNDERLINE),
	ITALIC(ChatFormatting.ITALIC),
	RESET(ChatFormatting.RESET);

	public static final char PREFIX = ChatFormatting.PREFIX_CODE;

	final ChatFormatting resolved;
	final char code;
	final Integer color;

	private Formatting(ChatFormatting formatting) {
		this.resolved = formatting;
		this.code = formatting.getChar();
		this.color = formatting.getColor();
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

	public ChatFormatting resolve() {
		return this.resolved;
	}

	public static Formatting resolve(ChatFormatting formatting) {
		for (Formatting f : Formatting.values()) {
			if (f.resolved == formatting) {
				return f;
			}
		}

		throw new IllegalStateException("unknown chat formatting code " + formatting.getChar());
	}
}
