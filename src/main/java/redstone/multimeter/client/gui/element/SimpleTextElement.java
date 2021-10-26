package redstone.multimeter.client.gui.element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.action.MouseRelease;

public class SimpleTextElement extends TextElement {
	
	public SimpleTextElement(MultimeterClient client, int x, int y, boolean rightAligned, Supplier<Text> textSupplier) {
		this(client, x, y, rightAligned, textSupplier, () -> Collections.emptyList());
	}
	
	public SimpleTextElement(MultimeterClient client, int x, int y, boolean rightAligned, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier) {
		this(client, x, y, rightAligned, textSupplier, tooltipSupplier, t -> false, t -> false);
	}
	
	public SimpleTextElement(MultimeterClient client, int x, int y, boolean rightAligned, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<TextElement> mousePress, MouseRelease<TextElement> mouseRelease) {
		super(client, x, y, rightAligned, () -> Arrays.asList(textSupplier.get()), tooltipSupplier, mousePress, mouseRelease);
	}
}
