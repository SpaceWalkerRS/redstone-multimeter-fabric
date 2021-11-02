package redstone.multimeter.client.gui.element.meter;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.controls.ControlElement;
import redstone.multimeter.common.WorldPos;

public class CoordinateControlElement extends ControlElement {
	
	private final Button increase;
	private final Button decrease;
	
	public CoordinateControlElement(MultimeterClient client, int spacing, int controlWidth, Axis axis, Supplier<WorldPos> getter, Consumer<WorldPos> setter) {
		super(client, spacing, controlWidth, () -> new LiteralText(axis.getName().toLowerCase()), () -> Collections.emptyList(), (_client, width, height) -> createControl(_client, width, height, axis, getter, setter), null, null);
		
		int size = getHeight() / 2 - 1;
		
		this.increase = new TransparentButton(client, 0, 0, size, size, () -> new LiteralText("+"), () -> null, button -> {
			int distance = Screen.hasShiftDown() ? 10 : 1;
			WorldPos pos = getter.get();
			WorldPos newPos = pos.offset(axis, distance);
			
			setter.accept(newPos);
			
			return true;
		});
		this.decrease = new TransparentButton(client, 0, 0, size, size, () -> new LiteralText("-"), () -> null, button -> {
			int distance = Screen.hasShiftDown() ? 10 : 1;
			WorldPos pos = getter.get();
			WorldPos newPos = pos.offset(axis, -distance);
			
			setter.accept(newPos);
			
			return true;
		});
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
	
	private static IButton createControl(MultimeterClient client, int width, int height, Axis axis, Supplier<WorldPos> getter, Consumer<WorldPos> setter) {
		return new TextField(client, 0, 0, width, height, () -> null, text -> {
			try {
				WorldPos pos = getter.get();
				BlockPos p = pos.getBlockPos();
				int coord = axis.choose(p.getX(), p.getY(), p.getZ());
				int newCoord = Integer.valueOf(text);
				WorldPos newPos = pos.offset(axis, newCoord - coord);
				
				setter.accept(newPos);
			} catch (NumberFormatException e) {
				
			}
		}, () -> {
			WorldPos pos = getter.get();
			BlockPos p = pos.getBlockPos();
			int coord = axis.choose(p.getX(), p.getY(), p.getZ());
			
			return String.valueOf(coord);
		});
	}
}
