package redstone.multimeter.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.TextColor;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.util.ColorUtils;

public class FontRenderer {

	final Font font;

	public FontRenderer(MultimeterClient client) {
		Minecraft minecraft = client.getMinecraft();

		this.font = minecraft.font;
	}

	public int height() {
		return this.font.lineHeight;
	}

	public void draw(String s, int x, int y) {
		this.font.draw(s, x, y, 0xFFFFFFFF);
	}

	public void draw(Text t, int x, int y) {
		this.font.draw(t.buildFormattedString(), x, y, this.resolveColor(t, 0xFFFFFFFF));
	}

	public void draw(String s, int x, int y, int color) {
		this.font.draw(s, x, y, color);
	}

	public void draw(Text t, int x, int y, int color) {
		this.font.draw(t.buildFormattedString(), x, y, this.resolveColor(t, color));
	}

	public void drawWithShadow(String s, int x, int y) {
		this.font.drawShadow(s, x, y, 0xFFFFFFFF);
	}

	public void drawWithShadow(Text t, int x, int y) {
		this.font.drawShadow(t.buildFormattedString(), x, y, this.resolveColor(t, 0xFFFFFFFF));
	}

	public void drawWithShadow(String s, int x, int y, int color) {
		this.font.drawShadow(s, x, y, color);
	}

	public void drawWithShadow(Text t, int x, int y, int color) {
		this.font.drawShadow(t.buildFormattedString(), x, y, this.resolveColor(t, color));
	}

	public void draw(String s, int x, int y, int color, boolean shadow) {
		if (shadow) {
			this.drawWithShadow(s, x, y, color);
		} else {
			this.draw(s, x, y, color);
		}
	}

	public void draw(Text t, int x, int y, int color, boolean shadow) {
		if (shadow) {
			this.drawWithShadow(t, x, y, color);
		} else {
			this.draw(t, x, y, color);
		}
	}

	private int resolveColor(Text t, int color) {
		TextColor textColor = t.getStyle().getColor();

		if (textColor != null) {
			int rgb = textColor.getColor();
			int alpha = ColorUtils.getAlpha(color);

			color = ColorUtils.setAlpha(rgb, alpha);
		}

		return color;
	}

	public String trim(String s, int width) {
		return this.font.substrByWidth(s, width);
	}

	public String trim(String s, int width, boolean rightToLeft) {
		return this.font.substrByWidth(s, width, rightToLeft);
	}

	public List<String> split(String s, int width) {
		List<String> lines = new ArrayList<>();

		while (!s.isEmpty()) {
			int lastSpace = -1;
			int length = 0;

			while (++length < s.length()) {
				int index = length - 1;

				if (s.charAt(index) == ' ') {
					lastSpace = index;
				}

				String substring = s.substring(0, length);

				if (this.width(substring) > width) {
					if (lastSpace >= 0) {
						substring = s.substring(0, lastSpace);
						length = lastSpace + 1;
					}

					lines.add(substring);

					break;
				}
			}

			if (length == s.length()) {
				if (length > 0) {
					lines.add(s);
				}

				break;
			}

			s = s.substring(length);
		}

		return lines;
	}

	public int width(String s) {
		return this.font.width(s);
	}

	public int width(Text t) {
		return this.font.width(t.buildString());
	}

	public int width(Tooltip t) {
		int width = 0;

		for (Text line : t) {
			int w = this.width(line);

			if (w > width) {
				width = w;
			}
		}

		return width;
	}

	public int height(Tooltip t) {
		int height = -1;

		for (Text line : t) {
			height += this.height() + 1;
		}

		return height;
	}
}
