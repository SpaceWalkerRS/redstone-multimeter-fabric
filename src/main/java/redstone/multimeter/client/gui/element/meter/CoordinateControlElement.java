package redstone.multimeter.client.gui.element.meter;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.controls.ControlElement;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.util.Direction.Axis;

public class CoordinateControlElement extends ControlElement {
	
	private final Button increase;
	private final Button decrease;
	
	public CoordinateControlElement(MultimeterClient client, int spacing, int controlWidth, Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		super(client, spacing, controlWidth, () -> new LiteralText(axis.getName().toLowerCase()), () -> Collections.emptyList(), (_client, width, height) -> createControl(_client, width, height, axis, getter, setter), null, null);
		
		int size = getHeight() / 2 - 1;
		
		this.increase = new TransparentButton(client, 0, 0, size, size, () -> new LiteralText("+"), () -> null, button -> {
			int distance = Screen.hasShiftDown() ? 10 : 1;
			DimPos pos = getter.get();
			DimPos newPos = pos.offset(axis, distance);
			
			setter.accept(newPos);
			
			return true;
		});
		this.decrease = new TransparentButton(client, 0, 0, size, size, () -> new LiteralText("-"), () -> null, button -> {
			int distance = Screen.hasShiftDown() ? 10 : 1;
			DimPos pos = getter.get();
			DimPos newPos = pos.offset(axis, -distance);
			
			setter.accept(newPos);
			
			return true;
		});
		
		addChild(this.increase);
		addChild(this.decrease);
	}
	
	@Override
	protected void onChangedX(int x) {
		super.onChangedX(x);
		
		x = control.getX() + control.getWidth();
		
		increase.setX(x);
		decrease.setX(x);
	}
	
	@Override
	protected void onChangedY(int y) {
		super.onChangedY(y);
		
		y = control.getY() + 1;
		
		increase.setY(y);
		decrease.setY(y + increase.getHeight());
	}
	
	private static IButton createControl(MultimeterClient client, int width, int height, Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		return new TextField(client, 0, 0, width, height, () -> null, text -> {
			try {
				DimPos pos = getter.get();
				int coord = axis.choose(pos.getX(), pos.getY(), pos.getZ());
				int newCoord = Integer.valueOf(text);
				DimPos newPos = pos.offset(axis, newCoord - coord);
				
				setter.accept(newPos);
			} catch (NumberFormatException e) {
				
			}
		}, () -> {
			DimPos pos = getter.get();
			int coord = axis.choose(pos.getX(), pos.getY(), pos.getZ());
			
			return String.valueOf(coord);
		});
	}
}
