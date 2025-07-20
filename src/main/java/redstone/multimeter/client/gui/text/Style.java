package redstone.multimeter.client.gui.text;

import java.util.Objects;

import net.minecraft.ChatFormatting;

import redstone.multimeter.mixin.common.StyleAccess;

public class Style {
	
	public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null);

	private final TextColor color;
	private final Boolean bold;
	private final Boolean italic;
	private final Boolean underlined;
	private final Boolean strikethrough;
	private final Boolean obfuscated;
	private final ClickEvent clickEvent;
	private final HoverEvent hoverEvent;

	private Style(
		TextColor color,
		Boolean bold,
		Boolean italic,
		Boolean underlined,
		Boolean strikethrough,
		Boolean obfuscated,
		ClickEvent clickEvent,
		HoverEvent hoverEvent
	) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Style)) {
			return false;
		}
		Style style = (Style) o;
		return this.color == style.color
			&& this.bold == style.bold
			&& this.italic == style.italic
			&& this.obfuscated == style.obfuscated
			&& this.strikethrough == style.strikethrough
			&& this.underlined == style.underlined
			&& Objects.equals(this.clickEvent, style.clickEvent)
			&& Objects.equals(this.hoverEvent, style.hoverEvent);
	}

	public TextColor getColor() {
		return this.color;
	}

	public boolean isBold() {
		return this.bold == Boolean.TRUE;
	}

	public boolean isItalic() {
		return this.italic == Boolean.TRUE;
	}

	public boolean isStrikethrough() {
		return this.strikethrough == Boolean.TRUE;
	}

	public boolean isUnderlined() {
		return this.underlined == Boolean.TRUE;
	}

	public boolean isObfuscated() {
		return this.obfuscated == Boolean.TRUE;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public ClickEvent getClickEvent() {
		return this.clickEvent;
	}

	public HoverEvent getHoverEvent() {
		return this.hoverEvent;
	}

	private static <T> Style checkEmptyAfterChange(Style style, T oldValue, T newValue) {
		return oldValue != null && newValue == null && style.equals(EMPTY) ? EMPTY : style;
	}

	public Style withColor(int color) {
		return this.withColor(TextColor.of(color));
	}

	public Style withColor(Formatting formatting) {
		return this.withColor(formatting.getColor());
	}

	public Style withColor(TextColor color) {
		return Objects.equals(this.color, color)
			? this
			: checkEmptyAfterChange(
				new Style(
					color,
					this.bold,
					this.italic,
					this.underlined,
					this.strikethrough,
					this.obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.color,
				color
			);
	}

	public Style withBold(Boolean bold) {
		return Objects.equals(this.bold, bold)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					bold,
					this.italic,
					this.underlined,
					this.strikethrough,
					this.obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.bold,
				bold
			);
	}

	public Style withItalic(Boolean italic) {
		return Objects.equals(this.italic, italic)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					italic,
					this.underlined,
					this.strikethrough,
					this.obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.italic,
				italic
			);
	}

	public Style withUnderlined(Boolean underlined) {
		return Objects.equals(this.underlined, underlined)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					this.italic,
					underlined,
					this.strikethrough,
					this.obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.underlined,
				underlined
			);
	}

	public Style withStrikethrough(Boolean strikethrough) {
		return Objects.equals(this.strikethrough, strikethrough)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					this.italic,
					this.underlined,
					strikethrough,
					this.obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.strikethrough,
				strikethrough
			);
	}

	public Style withObfuscated(Boolean obfuscated) {
		return Objects.equals(this.obfuscated, obfuscated)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					this.italic,
					this.underlined,
					this.strikethrough,
					obfuscated,
					this.clickEvent,
					this.hoverEvent
				),
				this.obfuscated,
				obfuscated
			);
	}

	public Style withClickEvent(ClickEvent clickEvent) {
		return Objects.equals(this.clickEvent, clickEvent)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					this.italic,
					this.underlined,
					this.strikethrough,
					this.obfuscated,
					clickEvent,
					this.hoverEvent
				),
				this.clickEvent,
				clickEvent
			);
	}

	public Style withHoverEvent(HoverEvent hoverEvent) {
		return Objects.equals(this.hoverEvent, hoverEvent)
			? this
			: checkEmptyAfterChange(
				new Style(
					this.color,
					this.bold,
					this.italic,
					this.underlined,
					this.strikethrough,
					this.obfuscated,
					this.clickEvent,
					hoverEvent
				),
				this.hoverEvent,
				hoverEvent
			);
	}

	public Style applyFormattings(Formatting... formattings) {
		TextColor color = this.color;
		Boolean bold = this.bold;
		Boolean italic = this.italic;
		Boolean strikethrough = this.strikethrough;
		Boolean underlined = this.underlined;
		Boolean obfuscated = this.obfuscated;

		for (Formatting formatting : formattings) {
			switch (formatting) {
			case RESET:
				return EMPTY;
			case OBFUSCATED:
				obfuscated = true;
				break;
			case BOLD:
				bold = true;
				break;
			case STRIKETHROUGH:
				strikethrough = true;
				break;
			case UNDERLINED:
				underlined = true;
				break;
			case ITALIC:
				italic = true;
				break;
			default:
				if (formatting.isColor()) {
					color = formatting.getColor();
				}
			}
		}

		return new Style(
			color,
			bold,
			italic,
			underlined,
			strikethrough,
			obfuscated,
			this.clickEvent,
			this.hoverEvent
		);
	}

	public Style applyStyle(Style style) {
		if (this == EMPTY) {
			return style;
		}
		if (style == EMPTY) {
			return this;
		}
		return new Style(
			this.color != null ? this.color : style.color,
			this.bold != null ? this.bold : style.bold,
			this.italic != null ? this.italic : style.italic,
			this.underlined != null ? this.underlined : style.underlined,
			this.strikethrough != null ? this.strikethrough : style.strikethrough,
			this.obfuscated != null ? this.obfuscated : style.obfuscated,
			this.clickEvent != null ? this.clickEvent : style.clickEvent,
			this.hoverEvent != null ? this.hoverEvent : style.hoverEvent
		);
	}

	public void apply(StringBuilder sb) {
		if (this.color != null) {
			Formatting formatting = this.color.getFormatting();

			if (formatting != null) {
				sb.append(formatting);
			}
		}
		if (this.bold != null) {
			sb.append(Formatting.BOLD);
		}
		if (this.italic != null) {
			sb.append(Formatting.ITALIC);
		}
		if (this.underlined != null) {
			sb.append(Formatting.UNDERLINED);
		}
		if (this.strikethrough != null) {
			sb.append(Formatting.STRIKETHROUGH);
		}
		if (this.obfuscated != null) {
			sb.append(Formatting.OBFUSCATED);
		}
	}

	public net.minecraft.network.chat.Style resolve() {
		net.minecraft.network.chat.Style style = new net.minecraft.network.chat.Style();

		if (this.color != null) {
			style = style.setColor(this.color.resolve());
		}
		if (this.bold != null) {
			style = style.setBold(this.bold);
		}
		if (this.italic != null) {
			style = style.setItalic(this.italic);
		}
		if (this.underlined != null) {
			style = style.setUnderlined(this.underlined);
		}
		if (this.strikethrough != null) {
			style = style.setStrikethrough(this.strikethrough);
		}
		if (this.obfuscated != null) {
			style = style.setObfuscated(this.obfuscated);
		}
		if (this.clickEvent != null) {
			style = style.setClickEvent(this.clickEvent.resolve());
		}
		if (this.hoverEvent != null) {
			style = style.setHoverEvent(this.hoverEvent.resolve());
		}

		return style;
	}

	public static Style resolve(net.minecraft.network.chat.Style style) {
		StyleAccess styleAccess = (StyleAccess) style;

		ChatFormatting color = styleAccess.rsmm$color();
		Boolean bold = styleAccess.rsmm$bold();
		Boolean italic = styleAccess.rsmm$italic();
		Boolean underlined = styleAccess.rsmm$underlined();
		Boolean strikethrough = styleAccess.rsmm$strikethrough();
		Boolean obfuscated = styleAccess.rsmm$obfuscated();
		net.minecraft.network.chat.ClickEvent clickEvent = styleAccess.rsmm$clickEvent();
		net.minecraft.network.chat.HoverEvent hoverEvent = styleAccess.rsmm$hoverEvent();

		Style s = EMPTY;

		if (color != null) {
			s = s.withColor(TextColor.resolve(color));
		}
		if (bold != null) {
			s = s.withBold(bold);
		}
		if (italic != null) {
			s = s.withItalic(italic);
		}
		if (underlined != null) {
			s = s.withUnderlined(underlined);
		}
		if (strikethrough != null) {
			s = s.withStrikethrough(strikethrough);
		}
		if (obfuscated != null) {
			s = s.withObfuscated(obfuscated);
		}
		if (clickEvent != null) {
			s = s.withClickEvent(ClickEvent.resolve(clickEvent));
		}
		if (hoverEvent != null) {
			s = s.withHoverEvent(HoverEvent.resolve(hoverEvent));
		}

		return s;
	}
}
