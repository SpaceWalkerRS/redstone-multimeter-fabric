package redstone.multimeter.client.gui.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class BaseText implements Text {

	private final List<Text> siblings;

	Style style;

	BaseText() {
		this.siblings = new ArrayList<>();

		this.style = Style.EMPTY;
	}

	@Override
	public Text format(Formatting... formattings) {
		this.style = this.style.applyFormattings(formattings);
		return this;
	}

	@Override
	public Text format(Style style) {
		this.style = style;
		return this;
	}

	@Override
	public Text format(UnaryOperator<Style> styler) {
		this.style = styler.apply(this.style);
		return this;
	}

	@Override
	public Text append(String text) {
		this.siblings.add(Texts.literal(text));
		return this;
	}

	@Override
	public Text append(Text text) {
		this.siblings.add(text);
		return this;
	}

	@Override
	public String buildString() {
		return this.buildString(false);
	}

	@Override
	public String buildFormattedString() {
		return this.buildString(true);
	}

	private String buildString(boolean formatted) {
		StringBuilder sb = new StringBuilder();

		this.buildString(sb, formatted);
		this.buildString(sb, formatted, this.siblings);

		if (formatted) {
			sb.append(Formatting.RESET);
		}

		return sb.toString();
	}

	abstract void buildString(StringBuilder sb, boolean formatted);

	final void buildString(StringBuilder sb, boolean formatted, List<Text> texts) {
		for (Text text : texts) {
			if (formatted) {
				this.style.apply(sb);
			}

			sb.append(formatted
				? text.buildFormattedString()
				: text.buildString());
		}
	}

	@Override
	public net.minecraft.text.Text resolve() {
		net.minecraft.text.Text text = this.buildText();
		this.style.apply(text);

		for (Text sibling : this.siblings) {
			text.append(sibling.resolve());
		}

		return text;
	}

	abstract net.minecraft.text.Text buildText();

}
