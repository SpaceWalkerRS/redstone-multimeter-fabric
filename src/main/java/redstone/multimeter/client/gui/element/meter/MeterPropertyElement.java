package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleList;
import redstone.multimeter.client.gui.element.Label;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.ButtonFactory;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.SuggestionsMenu;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TextFieldFactory;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.button.TransparentToggleButton;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Style;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.util.Direction.Axis;

public class MeterPropertyElement extends AbstractParentElement {

	private final FontRenderer font;
	private final Label property;
	private final SimpleList controls;
	private final int buttonWidth;

	private Button toggle;
	private Consumer<Boolean> listener;

	private boolean active;

	public MeterPropertyElement(int width, int buttonWidth, String name) {
		this(width, buttonWidth, name, Tooltips::empty, MousePress::pass);
	}

	public MeterPropertyElement(int width, int buttonWidth, String name, Supplier<Tooltip> tooltip, MousePress<Label> onPress) {
		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.property = new Label(0, 0, t -> t.setLines(Texts.literal(name).format(Formatting.ITALIC, this.active ? Formatting.WHITE : Formatting.GRAY)).setShadow(true), tooltip, onPress);
		this.controls = new SimpleList(width);
		this.buttonWidth = buttonWidth;
		this.active = true;

		this.addChild(this.property);
		this.addChild(this.controls);

		this.setWidth(width);
	}

	@Override
	public void setX(int x) {
		super.setX(x);

		if (this.toggle == null) {
			this.property.setX(x + 2);
		} else {
			this.toggle.setX(x + 2);
			this.property.setX(this.toggle.getX() + this.toggle.getWidth() + 4);
		}

		this.controls.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		if (this.toggle != null) {
			this.toggle.setY(y + Button.DEFAULT_HEIGHT - (Button.DEFAULT_HEIGHT + this.toggle.getHeight()) / 2);
		}
		this.property.setY(y + Button.DEFAULT_HEIGHT - (Button.DEFAULT_HEIGHT + this.font.height()) / 2);
		this.controls.setY(y);
	}

	public void withToggle(Consumer<Boolean> listener) {
		if (this.toggle == null) {
			this.addChild(0, this.toggle = new TransparentToggleButton(0, 0, 12, 12, on -> Texts.literal(on ? "\u25A0" : "\u25A1"), () -> this.isActive(), button -> this.setActive(!this.isActive())));
		}

		this.listener = listener;
	}

	private void addControl(MeterControlElement control) {
		control.addChildren();

		this.controls.add(control);
		this.controls.updateCoords();

		this.setHeight(this.controls.getHeight());
	}

	public void addControl(String name, ButtonFactory factory) {
		this.addControl(name, UnaryOperator.identity(), factory);
	}

	public void addControl(String name, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		this.addControl(name, UnaryOperator.identity(), factory, tooltip);
	}

	public void addControl(String name, TextFieldFactory factory, SuggestionsProvider suggestions) {
		this.addControl(name, UnaryOperator.identity(), factory, suggestions);
	}

	public void addControl(String name, UnaryOperator<Style> formatter, ButtonFactory factory) {
		this.addControl(new MeterControlElement(name, formatter, factory));
	}

	public void addControl(String name, UnaryOperator<Style> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		this.addControl(new MeterControlElement(name, formatter, factory, tooltip));
	}

	public void addControl(String name, UnaryOperator<Style> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
		this.addControl(new TextFieldControlElement(name, formatter, factory, suggestions));
	}

	public void addCoordinateControl(Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		this.addControl(new CoordinateControlElement(axis, getter, setter));
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;

		if (this.listener != null) {
			this.listener.accept(this.active);
		}

		this.update();
	}

	private class MeterControlElement extends AbstractParentElement {

		protected final Label name;
		protected final Button control;

		public MeterControlElement(String name, ButtonFactory factory) {
			this(name, UnaryOperator.identity(), factory);
		}

		public MeterControlElement(String name, UnaryOperator<Style> formatter, ButtonFactory factory) {
			this(name, formatter, factory, Tooltips::empty);
		}

		public MeterControlElement(String name, UnaryOperator<Style> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
			this.name = new Label(0, 0, t -> t.setLines(formatName(name, formatter)).setShadow(true), tooltip);
			this.control = factory.create(MeterPropertyElement.this.buttonWidth, Button.DEFAULT_HEIGHT);

			this.setWidth(this.name.getWidth() + 4 + this.control.getWidth() + 10);
			this.setHeight(Button.DEFAULT_HEIGHT);
		}

		@Override
		public void setX(int x) {
			super.setX(x + MeterPropertyElement.this.controls.getWidth() - getWidth());

			this.name.setX(getX());
			this.control.setX(getX() + this.name.getWidth() + 4);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			int height = getHeight();

			this.name.setY(y + height - (height + MeterPropertyElement.this.font.height()) / 2);
			this.control.setY(y);
		}

		@Override
		public void update() {
			super.update();
			this.control.setActive(MeterPropertyElement.this.isActive());
		}

		protected void addChildren() {
			this.addChild(this.name);
			this.addChild(this.control);
		}

		protected Text formatName(String name, UnaryOperator<Style> formatter) {
			int width = MeterPropertyElement.this.controls.getWidth() - (4 + MeterPropertyElement.this.buttonWidth + 10);
			Text text = Texts.literal(MeterPropertyElement.this.font.trim(name, width, true));
			return MeterPropertyElement.this.isActive() ? text.format(formatter) : text.format(Formatting.GRAY);
		}
	}

	private class TextFieldControlElement extends MeterControlElement {

		private final SuggestionsMenu suggestions;

		public TextFieldControlElement(String name, UnaryOperator<Style> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
			super(name, formatter, factory, Tooltips::empty);

			this.suggestions = ((TextField) this.control).setSuggestions(suggestions);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return super.isMouseOver(mouseX, mouseY) || this.suggestions.isMouseOver(mouseX, mouseY);
		}

		@Override
		protected void addChildren() {
			this.addChild(this.suggestions);

			super.addChildren();
		}
	}

	private class CoordinateControlElement extends MeterControlElement {

		private final Button increase;
		private final Button decrease;

		public CoordinateControlElement(Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
			super(axis.getName(), (width, height) -> {
				return new TextField(0, 0, width, height, Tooltips::empty, text -> {
					try {
						DimPos pos = getter.get();
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						int coord = axis.choose(x, y, z);
						int newCoord = Integer.valueOf(text);
						DimPos newPos = pos.offset(axis, newCoord - coord);

						setter.accept(newPos);
					} catch (NumberFormatException e) {

					}
				}, () -> {
					DimPos pos = getter.get();
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					int coord = axis.choose(x, y, z);

					return String.valueOf(coord);
				});
			});

			int size = this.getHeight() / 2 - 1;

			this.increase = new TransparentButton(0, 0, size, size, Texts::addition, Tooltips::empty, button -> {
				int distance = Screen.isShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.offset(axis, distance);

				setter.accept(newPos);

				return true;
			});
			this.decrease = new TransparentButton(0, 0, size, size, Texts::subtraction, Tooltips::empty, button -> {
				int distance = Screen.isShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.offset(axis, -distance);

				setter.accept(newPos);

				return true;
			});
		}

		@Override
		public void setX(int x) {
			super.setX(x);

			x = this.control.getX() + this.control.getWidth();

			this.increase.setX(x);
			this.decrease.setX(x);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			y = this.control.getY() + 1;

			this.increase.setY(y);
			this.decrease.setY(y + this.increase.getHeight());
		}

		@Override
		protected void addChildren() {
			super.addChildren();

			this.addChild(this.increase);
			this.addChild(this.decrease);
		}
	}
}
