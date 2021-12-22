package redstone.multimeter.client.gui.element.meter;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.ButtonFactory;
import redstone.multimeter.client.gui.element.controls.ControlElement;
import redstone.multimeter.client.gui.element.controls.ControlsListBuilder;
import redstone.multimeter.client.gui.element.controls.ControlsSectionElement;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.util.Direction.Axis;

public class MeterControlsListBuilder extends ControlsListBuilder {
	
	public MeterControlsListBuilder(MultimeterClient client, int width) {
		super(client, width);
	}
	
	@Override
	public void addCategory(String name, Supplier<List<Text>> tooltip, MousePress<TextElement> onPress) {
		super.addCategory(name, tooltip, onPress);
		updateMargin(name);
	}
	
	@Override
	protected ControlsSectionElement createSection(TextElement category) {
		return new MeterControlsCategory(client, width, margin, midpoint, category);
	}
	
	public void addControl(String category, String name, ButtonFactory control) {
		addControl(category, () -> new LiteralText(name), control);
	}
	
	public void addControl(String category, Supplier<Text> name, ButtonFactory control) {
		addControl(category, (client, midpoint, controlWidth) -> new ControlElement(client, midpoint, controlWidth, name, () -> Collections.emptyList(), control));
		updateMidpoint(name.get());
	}
	
	public void addCoordinateControl(String category, Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		addControl(category, (client, midpoint, controlWidth) -> new CoordinateControlElement(client, midpoint, controlWidth, axis, getter, setter));
	}
	
	private void updateMargin(String category) {
		int width = font.getStringWidth(category) + 30;
		
		if (width > margin) {
			margin = width;
		}
	}
	
	private void updateMidpoint(Text name) {
		int width = getWidth(font, name) + 2;
		
		if (width > midpoint) {
			midpoint = width;
		}
	}
}
