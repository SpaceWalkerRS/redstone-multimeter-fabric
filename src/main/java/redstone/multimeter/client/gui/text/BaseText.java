package redstone.multimeter.client.gui.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

abstract class BaseText implements Text {

	private final List<Text> siblings;

	Style style;

	BaseText() {
		this.siblings = new ArrayList<>();

		this.style = Style.EMPTY;
	}

	@Override
	public Style getStyle() {
		return this.style;
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
	public Component resolve() {
		MutableComponent component = this.buildComponent().setStyle(this.style.resolve());

		for (Text sibling : this.siblings) {
			component.append(sibling.resolve());
		}

		return component;
	}

	abstract MutableComponent buildComponent();

}
