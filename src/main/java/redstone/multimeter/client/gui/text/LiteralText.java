package redstone.multimeter.client.gui.text;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class LiteralText extends BaseText {

	private final String value;

	LiteralText(String value) {
		this.value = value;
	}

	@Override
	void buildString(StringBuilder sb, boolean formatted) {
		if (formatted) {
			this.style.apply(sb);
		}

		sb.append(this.value);
	}

	@Override
	MutableComponent buildComponent() {
		return new TextComponent(this.value);
	}
}
