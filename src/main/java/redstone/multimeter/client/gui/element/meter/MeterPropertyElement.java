package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.Label;
import redstone.multimeter.client.gui.element.SimpleList;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.SuggestionsMenu;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.element.button.TransparentToggleButton;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Style;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.DimPos;

public class MeterPropertyElement extends AbstractParentElement {

	private final FontRenderer font;
	private final Label property;
	private final SimpleList controls;
	private final String key;
	private final int buttonWidth;

	private Button toggle;
	private Consumer<Boolean> listener;

	private boolean active;

	public MeterPropertyElement(int width, int buttonWidth, String key) {
		this(width, buttonWidth, key, Tooltips::empty, MousePress::pass);
	}

	public MeterPropertyElement(int width, int buttonWidth, String key, Supplier<Tooltip> tooltip, MousePress<Label> onPress) {
		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.property = new Label(0, 0, t -> t.setLines(Texts.translatable("rsmm.gui.meterControls." + key).format(Formatting.ITALIC, this.active ? Formatting.WHITE : Formatting.GRAY)).setShadow(true), tooltip, onPress);
		this.controls = new SimpleList(width);
		this.key = "rsmm.gui.meterControls." + key;
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
			this.addChild(0, this.toggle = new TransparentToggleButton(0, 0, 12, 12, on -> Texts.literal(on ? "\u25A0" : "\u25A1"), this::isActive, button -> this.setActive(!this.isActive())));
		}

		this.listener = listener;
	}

	private void addControl(MeterControlElement control) {
		control.addChildren();

		this.controls.add(control);
		this.controls.updateCoords();

		this.setHeight(this.controls.getHeight());
	}

	public void addControl(String key, Button control) {
		this.addControl(this.buildControlName(key), UnaryOperator.identity(), control);
	}

	public void addControl(String key, UnaryOperator<Style> formatter, Button control) {
		this.addControl(this.buildControlName(key), formatter, control, Tooltips::empty);
	}

	public void addControl(Text name, Button control) {
		this.addControl(name, UnaryOperator.identity(), control);
	}

	public void addControl(Text name, UnaryOperator<Style> formatter, Button control) {
		this.addControl(name, formatter, control, Tooltips::empty);
	}

	public void addControl(Text name, UnaryOperator<Style> formatter, Button control, Supplier<Tooltip> tooltip) {
		this.addControl(new MeterControlElement(name, formatter, control, tooltip));
	}

	public void addControl(String key, TextField control, SuggestionsProvider suggestions) {
		this.addControl(this.buildControlName(key), UnaryOperator.identity(), control, suggestions);
	}

	public void addControl(Text name, UnaryOperator<Style> formatter, TextField control, SuggestionsProvider suggestions) {
		this.addControl(new TextFieldControlElement(name, formatter, control, suggestions));
	}

	public void addCoordinateControl(Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		this.addCoordinateControl(this.buildControlName(axis.getName()), axis, getter, setter);
	}

	public void addCoordinateControl(Text name, Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
		this.addControl(new CoordinateControlElement(name, axis, getter, setter));
	}

	private Text buildControlName(String key) {
		return key == null || key.isEmpty() ? Texts.literal("") : Texts.translatable(this.key + "." + key);
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

		public MeterControlElement(Text name, Button control) {
			this(name, UnaryOperator.identity(), control);
		}

		public MeterControlElement(Text name, UnaryOperator<Style> formatter, Button control) {
			this(name, formatter, control, Tooltips::empty);
		}

		public MeterControlElement(Text name, UnaryOperator<Style> formatter, Button control, Supplier<Tooltip> tooltip) {
			this.name = new Label(0, 0, t -> {
				if (MeterPropertyElement.this.isActive()) {
					name.format(formatter);
				} else {
					name.format(Formatting.GRAY);
				}

				t.setLines(name);
				t.setShadow(true);
			}, tooltip);
			this.control = control;
			this.control.setWidth(MeterPropertyElement.this.buttonWidth);
			this.control.setHeight(Button.DEFAULT_HEIGHT);

			this.setWidth(this.name.getWidth() + 4 + this.control.getWidth() + 10);
			this.setHeight(Button.DEFAULT_HEIGHT);
		}

		@Override
		public void setX(int x) {
			super.setX(x + MeterPropertyElement.this.controls.getWidth() - this.getWidth());

			this.name.setX(getX());
			this.control.setX(getX() + this.name.getWidth() + 4);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			int height = this.getHeight();

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
	}

	private class TextFieldControlElement extends MeterControlElement {

		private final SuggestionsMenu suggestions;

		public TextFieldControlElement(Text name, UnaryOperator<Style> formatter, TextField control, SuggestionsProvider suggestions) {
			super(name, formatter, control, Tooltips::empty);

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

		public CoordinateControlElement(Text name, Axis axis, Supplier<DimPos> getter, Consumer<DimPos> setter) {
			super(name, new TextField(0, 0, 0, 0, Tooltips::empty, text -> {
					try {
						DimPos pos = getter.get();
						BlockPos p = pos.getBlockPos();
						int coord = axis.choose(p.getX(), p.getY(), p.getZ());
						int newCoord = Integer.valueOf(text);
						DimPos newPos = pos.offset(axis, newCoord - coord);

						setter.accept(newPos);
					} catch (NumberFormatException e) {

					}
				}, () -> {
					DimPos pos = getter.get();
					BlockPos p = pos.getBlockPos();
					int coord = axis.choose(p.getX(), p.getY(), p.getZ());

					return String.valueOf(coord);
				})
			);

			int size = this.getHeight() / 2 - 1;

			this.increase = new TransparentButton(0, 0, size, size, () -> Texts.literal("+"), Tooltips::empty, button -> {
				int distance = Screen.isShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.offset(axis, distance);

				setter.accept(newPos);

				return true;
			});
			this.decrease = new TransparentButton(0, 0, size, size, () -> Texts.literal("-"), Tooltips::empty, button -> {
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
