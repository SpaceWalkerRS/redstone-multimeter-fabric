package redstone.multimeter.client.gui.text;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.ChatFormatting;

public class TextColor {

	private static final Map<Formatting, TextColor> BY_FORMATTING = new EnumMap<>(Formatting.class);

	public static final TextColor BLACK = new TextColor(Formatting.BLACK);
	public static final TextColor DARK_BLUE = new TextColor(Formatting.DARK_BLUE);
	public static final TextColor DARK_GREEN = new TextColor(Formatting.DARK_GREEN);
	public static final TextColor DARK_AQUA = new TextColor(Formatting.DARK_AQUA);
	public static final TextColor DARK_RED = new TextColor(Formatting.DARK_RED);
	public static final TextColor DARK_PURPLE = new TextColor(Formatting.DARK_PURPLE);
	public static final TextColor GOLD = new TextColor(Formatting.GOLD);
	public static final TextColor GRAY = new TextColor(Formatting.GRAY);
	public static final TextColor DARK_GRAY = new TextColor(Formatting.DARK_GRAY);
	public static final TextColor BLUE = new TextColor(Formatting.BLUE);
	public static final TextColor GREEN = new TextColor(Formatting.GREEN);
	public static final TextColor AQUA = new TextColor(Formatting.AQUA);
	public static final TextColor RED = new TextColor(Formatting.RED);
	public static final TextColor LIGHT_PURPLE = new TextColor(Formatting.LIGHT_PURPLE);
	public static final TextColor YELLOW = new TextColor(Formatting.YELLOW);
	public static final TextColor WHITE = new TextColor(Formatting.WHITE);

	private final Formatting formatting;
	private final int color;

	private TextColor(Formatting formatting) {
		BY_FORMATTING.put(formatting, this);

		this.formatting = formatting;
		this.color = formatting.color;
	}

	private TextColor(int color) {
		this.formatting = null;
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	public boolean isFormatting() {
		return this.formatting != null;
	}

	public Formatting getFormatting() {
		return this.formatting;
	}

	public static TextColor of(int color) {
		return new TextColor(color);
	}

	public ChatFormatting resolve(){
		return this.isFormatting() ? this.formatting.resolve() : null;
	}

	public static TextColor resolve(ChatFormatting color) {
		return color.isColor() ? resolve(Formatting.resolve(color)) : null;
	}

	static TextColor resolve(Formatting formatting) {
		if (formatting.isColor()) {
			TextColor color = BY_FORMATTING.get(formatting);

			if (color != null) {
				return color;
			}

			throw new IllegalStateException("could not resolve text color " + formatting.name());
		} else {
			return null;
		}
	}
}
