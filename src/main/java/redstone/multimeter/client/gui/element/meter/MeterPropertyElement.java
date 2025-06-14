package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Formatting;

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
import redstone.multimeter.util.Direction.Axis;

public class MeterPropertyElement extends AbstractParentElement {

	private final MultimeterClient client;
	private final TextRenderer textRenderer;
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
		this.textRenderer = minecraft.textRenderer;
		this.property = new TextElement(this.client, 0, 0, t -> t.setText("" + Formatting.ITALIC + (this.active ? Formatting.WHITE : Formatting.GRAY) + name).setWithShadow(true), tooltip, onPress);
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
		property.setY(y + IButton.DEFAULT_HEIGHT - (IButton.DEFAULT_HEIGHT + textRenderer.fontHeight) / 2);
		controls.setY(y);
	}

	public void withToggle(Consumer<Boolean> listener) {
		if (toggle == null) {
			addChild(0, toggle = new TransparentToggleButton(client, 0, 0, 12, 12, on -> on ? "\u25A0" : "\u25A1", () -> isActive(), button -> setActive(!active)));
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
		addControl(name, text -> text, factory);
	}

	public void addControl(String name, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		addControl(name, text -> text, factory, tooltip);
	}

	public void addControl(String name, TextFieldFactory factory, SuggestionsProvider suggestions) {
		addControl(name, text -> text, factory, suggestions);
	}

	public void addControl(String name, Function<String, String> formatter, ButtonFactory factory) {
		addControl(new MeterControlElement(name, formatter, factory));
	}

	public void addControl(String name, Function<String, String> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
		addControl(new MeterControlElement(name, formatter, factory, tooltip));
	}

	public void addControl(String name, Function<String, String> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
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
			this(name, text -> text, factory);
		}

		public MeterControlElement(String name, Function<String, String> formatter, ButtonFactory factory) {
			this(name, formatter, factory, () -> Tooltip.EMPTY);
		}

		public MeterControlElement(String name, Function<String, String> formatter, ButtonFactory factory, Supplier<Tooltip> tooltip) {
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

			name.setY(y + height - (height + textRenderer.fontHeight) / 2);
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

		protected String formatName(String name, Function<String, String> formatter) {
			int width = controls.getWidth() - (4 + buttonWidth + 10);
			String text = textRenderer.trim(name, width, true);
			return active ? formatter.apply(text) : Formatting.GRAY + text;
		}
	}

	private class TextFieldControlElement extends MeterControlElement {

		private final SuggestionsMenu suggestions;

		public TextFieldControlElement(String name, Function<String, String> formatter, TextFieldFactory factory, SuggestionsProvider suggestions) {
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

			int size = getHeight() / 2 - 1;

			this.increase = new TransparentButton(client, 0, 0, size, size, () -> "+", () -> Tooltip.EMPTY, button -> {
				int distance = Screen.isShiftDown() ? 10 : 1;
				DimPos pos = getter.get();
				DimPos newPos = pos.offset(axis, distance);

				setter.accept(newPos);

				return true;
			});
			this.decrease = new TransparentButton(client, 0, 0, size, size, () -> "-", () -> Tooltip.EMPTY, button -> {
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
