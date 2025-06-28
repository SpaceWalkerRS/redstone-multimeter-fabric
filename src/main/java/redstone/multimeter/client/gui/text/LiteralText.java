package redstone.multimeter.client.gui.text;

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
	net.minecraft.text.Text buildText() {
		return new net.minecraft.text.LiteralText(this.value);
	}
}
