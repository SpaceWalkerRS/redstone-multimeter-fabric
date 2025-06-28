package redstone.multimeter.client.gui.text;

import java.util.function.UnaryOperator;

public interface Text {

	Text format(Formatting... formattings);

	Text format(Style style);

	Text format(UnaryOperator<Style> styler);

	Text append(String text);

	Text append(Text text);

	String buildString();

	String buildFormattedString();

	String resolve();

}
