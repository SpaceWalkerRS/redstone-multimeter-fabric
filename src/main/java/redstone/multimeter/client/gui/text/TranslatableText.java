package redstone.multimeter.client.gui.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TranslatableText extends BaseText {

	private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	private final String key;
	private final Object[] args;

	private List<Text> resolved;

	private Language resolvedLanguage;

	TranslatableText(String key, Object... args) {
		this.key = key;
		this.args = args;
	}

	@Override
	void buildString(StringBuilder sb, boolean formatted) {
		Language language = Language.getInstance();

		if (this.resolved == null || this.resolvedLanguage != language) {
			String translation = language.getOrDefault(this.key);

			this.resolvedLanguage = language;
			this.resolve(translation);
		}

		this.buildString(sb, formatted, this.resolved);
	}

	@Override
	MutableComponent buildComponent() {
		Object[] args = new Object[this.args.length];

		for (int i = 0; i < args.length; i++) {
			Object arg = this.args[i];

			if (arg instanceof Text) {
				arg = ((Text) arg).resolve();
			}

			args[i] = arg;
		}

		return new TranslatableComponent(this.key, args);
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
}
