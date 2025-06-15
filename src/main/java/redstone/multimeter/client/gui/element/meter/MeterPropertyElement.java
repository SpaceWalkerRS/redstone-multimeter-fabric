package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.ButtonFactory;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.SuggestionsMenu;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TextFieldFactory;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.button.TransparentToggleButton;
import redstone.multimeter.common.DimPos;

public class MeterPropertyElement extends AbstractParentElement {

	private final MultimeterClient client;
	private final Font font;
	private final TextElement property;
	private final SimpleListElement controls;
	private final int buttonWidth;

	private IButton toggle;
	private Consumer<Boolean> listener;

	private boolean active;

	public MeterPropertyElement(MultimeterClient client, int width, int buttonWidth, String name) {
		this(client, width, buttonWidth, name, () -> Tooltip.EMPTY, t -> false);
	}

	public MeterPropertyElement(MultimeterClient client, int width, int buttonWidth, String name, Supplier<Tooltip> tooltip,
		MousePress<TextElement> onPress) {
		Minecraft minecraft = client.getMinecraft();

		this.client = client;
		this.font = minecraft.font;
		this.property = new TextElement(this.client, 0, 0, t -> t.setText(Component.literal(name).withStyle(ChatFormatting.ITALIC, this.active ? ChatFormatting.WHITE : ChatFormatting.GRAY)).setWithShadow(true), tooltip, onPress);
		this.controls = new SimpleListElement(client, width);
		this.buttonWidth = buttonWidth;
		this.active = true;

		addChild(this.property);
		addChild(this.controls);

		setWidth(width);
	}

	@Override
	public void setX(int x) {
		super.setX(x);

		if (toggle == null) {
			property.setX(x + 2);
		} else {
			toggle.setX(x + 2);
			property.setX(toggle.getX() + toggle.getWidth() + 4);
		}

		controls.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		if (toggle != null) {
			toggle.setY(y + IButton.DEFAULT_HEIGHT - (IButton.DEFAULT_HEIGHT + toggle.getHeight()) / 2);
		}
		property.setY(y + IButton.DEFAULT_HEIGHT - (IButton.DEFAULT_HEIGHT + font.lineHeight) / 2);
		controls.setY(y);
	}

	public void withToggle(Consumer<Boolean> listener) {
		if (toggle == null) {
			addChild(0, toggle = new TransparentToggleButton(client, 0, 0, 12, 12, on -> Component.literal(on ? "\u25A0" : "\u25A1"), () -> isActive(), button -> setActive(!active)));
		}

		this.listener = listener;
	}

	private void addControl(MeterControlElement control) {
		control.addChildren();

		controls.add(control);
		controls.updateCoords();

		setHeight(controls.getHeight());
	}

	public void addControl(String name, ButtonFactory factory) {
		addControl(name, UnaryOperator.identity(), factory);
	}

	public void addControl(String name, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		addControl(name, UnaryOperator.identity(), factory, tooltip);
	}

	public void addControl(String name, TextFieldFactory factory, SuggestionsProvider suggestions) {
		addControl(name, UnaryOperator.identity(), factory, suggestions);
	}

	public void addControl(String name, UnaryOperator<Style> formatter, ButtonFactory factory) {
		addControl(new MeterControlElement(name, formatter, factory));
	}

	public void addControl(String name, UnaryOperator<Style> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		addControl(new MeterControlElement(name, formatter, factory, tooltip));
	}

	public void addControl(String name, UnaryOperator<Style> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
		addControl(new TextFieldControlElement(name, formatter, factory, suggestions));
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
			this(name, formatter, factory, () -> Tooltip.EMPTY);
		}

		public MeterControlElement(String name, UnaryOperator<Style> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
			this.name = new TextElement(client, 0, 0, t -> t.setText(formatName(name, formatter)).setWithShadow(true), tooltip);
			this.control = factory.create(client, buttonWidth, IButton.DEFAULT_HEIGHT);

			setWidth(this.name.getWidth() + 4 + this.control.getWidth() + 10);
			setHeight(IButton.DEFAULT_HEIGHT);
		}

		@Override
		public void setX(int x) {
			super.setX(x + controls.getWidth() - getWidth());

			name.setX(getX());
			control.setX(getX() + name.getWidth() + 4);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			int height = getHeight();

			name.setY(y + height - (height + font.lineHeight) / 2);
			control.setY(y);
		}

		@Override
		public void update() {
			super.update();
			control.setActive(active);
		}

		protected void addChildren() {
			addChild(name);
			addChild(control);
		}

		protected Component formatName(String name, UnaryOperator<Style> formatter) {
			int width = controls.getWidth() - (4 + buttonWidth + 10);
			MutableComponent text = Component.literal(font.plainSubstrByWidth(name, width, true));
			return active ? text.withStyle(formatter) : text.withStyle(ChatFormatting.GRAY);
		}
	}

	private class TextFieldControlElement extends MeterControlElement {

		private final SuggestionsMenu suggestions;

		public TextFieldControlElement(String name, UnaryOperator<Style> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
			super(name, formatter, factory, () -> Tooltip.EMPTY);

			this.suggestions = ((TextField) this.control).setSuggestions(suggestions);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return super.isMouseOver(mouseX, mouseY) || suggestions.isMouseOver(mouseX, mouseY);
		}

		@Override
		protected void addChildren() {
			addChild(suggestions);

			super.addChildren();
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
						int coord = axis.choose(p.getX(), p.getY(), p.getZ());
						int newCoord = Integer.valueOf(text);
						DimPos newPos = pos.relative(axis, newCoord - coord);

						setter.accept(newPos);
					} catch (NumberFormatException e) {

					}
				}, () -> {
					DimPos pos = getter.get();
					BlockPos p = pos.getBlockPos();
					int coord = axis.choose(p.getX(), p.getY(), p.getZ());

					return String.valueOf(coord);
				});
			});

			int size = getHeight() / 2 - 1;

			this.increase = new TransparentButton(client, 0, 0, size, size, () -> Component.literal("+"), () -> Tooltip.EMPTY, button -> {
				int distance = Screen.hasShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.relative(axis, distance);

				setter.accept(newPos);

				return true;
			});
			this.decrease = new TransparentButton(client, 0, 0, size, size, () -> Component.literal("-"), () -> Tooltip.EMPTY, button -> {
				int distance = Screen.hasShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.relative(axis, -distance);

				setter.accept(newPos);

				return true;
			});
		}

		@Override
		public void setX(int x) {
			super.setX(x);

			x = control.getX() + control.getWidth();

			increase.setX(x);
			decrease.setX(x);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			y = control.getY() + 1;

			increase.setY(y);
			decrease.setY(y + increase.getHeight());
		}

		@Override
		protected void addChildren() {
			super.addChildren();

			addChild(increase);
			addChild(decrease);
		}
	}
}
