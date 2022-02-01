package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.ButtonFactory;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.button.TransparentToggleButton;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.util.AxisUtils;

public class MeterPropertyElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final FontRenderer font;
	private final TextElement property;
	private final SimpleListElement controls;
	private final int buttonWidth;
	
	private IButton toggle;
	private Consumer<Boolean> listener;
	
	private boolean active;
	
	public MeterPropertyElement(MultimeterClient client, int width, int buttonWidth, String name) {
		this(client, width, buttonWidth, name, () -> Tooltip.EMPTY, t -> false);
	}
	
	public MeterPropertyElement(MultimeterClient client, int width, int buttonWidth, String name, Supplier<Tooltip> tooltip, MousePress<TextElement> onPress) {
		Minecraft minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.fontRenderer;
		this.property = new TextElement(this.client, 0, 0, t -> t.setText(new TextComponentString(name).setStyle(new Style().setColor(this.active ? TextFormatting.WHITE : TextFormatting.GRAY).setItalic(true))).setWithShadow(true), tooltip, onPress);
		this.controls = new SimpleListElement(client, width);
		this.buttonWidth = buttonWidth;
		this.active = true;
		
		addChild(this.property);
		addChild(this.controls);
		
		setWidth(width);
	}
	
	@Override
	protected void onChangedX(int x) {
		if (toggle == null) {
			property.setX(x + 2);
		} else {
			toggle.setX(x + 2);
			property.setX(toggle.getX() + toggle.getWidth() + 4);
		}
		
		controls.setX(x);
	}
	
	@Override
	protected void onChangedY(int y) {
		if (toggle != null) {
			toggle.setY(y + IButton.DEFAULT_HEIGHT - (IButton.DEFAULT_HEIGHT + toggle.getHeight()) / 2);
		}
		property.setY(y + IButton.DEFAULT_HEIGHT - (IButton.DEFAULT_HEIGHT + font.FONT_HEIGHT) / 2);
		controls.setY(y);
	}
	
	public void withToggle(Consumer<Boolean> listener) {
		if (toggle == null) {
			addChild(0, toggle = new TransparentToggleButton(client, 0, 0, 12, 12, on -> new TextComponentString(on ? "\u25A0" : "\u25A1"), () -> isActive(), button -> setActive(!active)));
		}
		
		this.listener = listener;
	}
	
	private void addControl(MeterControlElement control) {
		controls.add(control);
		controls.updateCoords();
		
		setHeight(controls.getHeight());
	}
	
	public void addControl(String name, ButtonFactory factory) {
		addControl(name, UnaryOperator.identity(), factory);
	}
	
	public void addControl(String name, UnaryOperator<Style> formatter, ButtonFactory factory) {
		addControl(new MeterControlElement(name, formatter, factory));
	}
	
	public void addCoordinateControl(Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		addControl(new CoordinateControlElement(axis, getter, setter));
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
		
		if (listener != null) {
			listener.accept(this.active);
		}
		
		update();
	}
	
	private class MeterControlElement extends AbstractParentElement {
		
		protected final TextElement name;
		protected final IButton control;
		
		public MeterControlElement(String name, ButtonFactory factory) {
			this(name, UnaryOperator.identity(), factory);
		}
		
		public MeterControlElement(String name, UnaryOperator<Style> formatter, ButtonFactory factory) {
			this.name = new TextElement(client, 0, 0, t -> t.setText(formatName(name, formatter)).setWithShadow(true));
			this.control = factory.create(client, buttonWidth, IButton.DEFAULT_HEIGHT);
			
			addChild(this.name);
			addChild(this.control);
			
			setWidth(this.name.getWidth() + 4 + this.control.getWidth() + 10);
			setHeight(IButton.DEFAULT_HEIGHT);
		}
		
		@Override
		public void setX(int x) {
			super.setX(x + controls.getWidth() - getWidth());
		}
		
		@Override
		public void update() {
			super.update();
			control.setActive(active);
		}
		
		@Override
		protected void onChangedX(int x) {
			name.setX(x);
			control.setX(x + name.getWidth() + 4);
		}
		
		@Override
		protected void onChangedY(int y) {
			int height = getHeight();
			
			name.setY(y + height - (height + font.FONT_HEIGHT) / 2);
			control.setY(y);
		}
		
		protected ITextComponent formatName(String name, UnaryOperator<Style> formatter) {
			int width = controls.getWidth() - (4 + buttonWidth + 10);
			ITextComponent text = new TextComponentString(font.trimStringToWidth(name, width, true));
			return text.setStyle(active ? formatter.apply(new Style()) : new Style().setColor(TextFormatting.GRAY));
		}
	}
	
	private class CoordinateControlElement extends MeterControlElement {
		
		private final IButton increase;
		private final IButton decrease;
		
		public CoordinateControlElement(Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
			super(axis.getName(), (client, width, height) -> {
				return new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
					try {
						DimPos pos = getter.get();
						BlockPos p = pos.getBlockPos();
						int coord = AxisUtils.choose(axis, p.getX(), p.getY(), p.getZ());
						int newCoord = Integer.valueOf(text);
						DimPos newPos = pos.offset(axis, newCoord - coord);
						
						setter.accept(newPos);
					} catch (NumberFormatException e) {
						
					}
				}, () -> {
					DimPos pos = getter.get();
					BlockPos p = pos.getBlockPos();
					int coord = AxisUtils.choose(axis, p.getX(), p.getY(), p.getZ());
					
					return String.valueOf(coord);
				});
			});
			
			int size = getHeight() / 2 - 1;
			
			this.increase = new TransparentButton(client, 0, 0, size, size, () -> new TextComponentString("+"), () -> Tooltip.EMPTY, button -> {
				int distance = GuiScreen.isShiftKeyDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.offset(axis, distance);
				
				setter.accept(newPos);
				
				return true;
			});
			this.decrease = new TransparentButton(client, 0, 0, size, size, () -> new TextComponentString("-"), () -> Tooltip.EMPTY, button -> {
				int distance = GuiScreen.isShiftKeyDown() ? 10 : 1;
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
	}
}
