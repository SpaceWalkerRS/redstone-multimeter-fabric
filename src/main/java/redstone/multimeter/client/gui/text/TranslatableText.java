package redstone.multimeter.client.gui.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.locale.Language;

import redstone.multimeter.RedstoneMultimeterMod;

public class TranslatableText extends BaseText {

	private static final String BUILT_IN_TRANSLATIONS_PATH = "/assets/redstone_multimeter/lang/en_US.lang";
	private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	private static Map<String, String> builtInTranslations;

	private final String key;
	private final Object[] args;

	private List<Text> resolved;

	private Language resolvedLanguage;

	TranslatableText(String key, Object... args) {
		this.key = key;
		this.args = args;
		if (this.key == null) {
			throw new NullPointerException();
		}
	}

	@Override
	void buildString(StringBuilder sb, boolean formatted) {
		Language language = Language.getInstance();

		if (this.resolved == null || this.resolvedLanguage != language) {
			String translation;

			if (language.contains(this.key)) {
				translation = language.translate(this.key);
			} else {
				translation = builtInTranslate(this.key);
			}

			this.resolvedLanguage = language;
			this.resolve(translation);
		}

		this.buildString(sb, formatted, this.resolved);
	}

	@Override
	net.minecraft.text.Text buildText() {
		return new net.minecraft.text.LiteralText(this.buildFormattedString());
	}

	private void resolve(String translation) {
		this.resolved = new ArrayList<>();

		Matcher matcher = ARG_FORMAT.matcher(translation);

		int nextArgIndex = 0;
		int nextCharIndex = 0;

		while (matcher.find(nextCharIndex)) {
			int matchStart = matcher.start();
			int matchEnd = matcher.end();

			if (matchStart > nextCharIndex) {
				this.resolved.add(Texts.literal(translation.substring(nextCharIndex, matchStart)));
			}

			String formatting = matcher.group(2);
			String partWithFormatting = translation.substring(matchStart, matchEnd);

			if ("%".equals(formatting) && "%%".equals(partWithFormatting)) {
				this.resolved.add(Texts.literal("%"));
			} else {
				if (!"s".equals(formatting)) {
					throw new IllegalStateException("Unsupported format: '" + partWithFormatting + "'");
				}

				String matchedArgIndex = matcher.group(1);
				int argIndex = matchedArgIndex != null ? Integer.parseInt(matchedArgIndex) - 1 : nextArgIndex++;

				if (argIndex < this.args.length) {
					this.resolved.add(this.resolveArg(argIndex));
				}
			}

			nextCharIndex = matchEnd;
		}

		if (nextCharIndex < translation.length()) {
			this.resolved.add(Texts.literal(translation.substring(nextCharIndex)));
		}
	}

	private Text resolveArg(int index) {
		if (index >= this.args.length) {
			throw new IndexOutOfBoundsException("arg " + index + " is out of bounds for 0-" + this.args.length);
		}

		Object arg = this.args[index];

		if (arg == null) {
			return Texts.literal("null");
		}

		return Texts.of(arg);
	}

	private static String builtInTranslate(String key) {
		if (builtInTranslations == null) {
			loadBuiltInTranslations();
		}

		return builtInTranslations.getOrDefault(key, key);
	}

	private static void loadBuiltInTranslations() {
		builtInTranslations = new HashMap<>();

		
	}
}
