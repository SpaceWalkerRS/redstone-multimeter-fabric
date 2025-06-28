package redstone.multimeter.client.gui.element.option;

import java.util.Collection;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleList;
import redstone.multimeter.client.gui.element.Label;
import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.client.option.Option;
import redstone.multimeter.client.option.OptionListener;

public class OptionsCategoryElement extends AbstractParentElement {

	private final FontRenderer font;
	private final Label category;
	private final SimpleList options;

	public OptionsCategoryElement(int width, String category, Collection<Option> options) {
		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.category = new Label(0, 0, t -> t.setLines(Texts.literal(category).format(Formatting.ITALIC)).setShadow(true));
		this.options = new SimpleList(width);

		for (Option option : options) {
			this.options.add(new OptionElement(option));
		}

		this.options.update();

		addChild(this.category);
		addChild(this.options);

		setWidth(width);
		setHeight(this.category.getHeight() + this.options.getHeight() + 6);
	}

	@Override
	public void setX(int x) {
		super.setX(x);

		category.setX(x + (options.getWidth() - category.getWidth()) / 2);
		options.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		category.setY(y);
		options.setY(y + category.getHeight() + 6);
	}

	private class OptionElement extends AbstractParentElement implements OptionListener {

		private final Option option;
		private final Label name;
		private final Button control;
		private final Button reset;

		public OptionElement(Option option) {
			Tooltip tooltip = Tooltips.split(font, option.getDescription());

			this.option = option;
			this.name = new Label(0, 0, t -> t.setLines(this.option.getName()).setShadow(true), () -> tooltip, t -> false);
			this.control = this.option.createControl(100, Button.DEFAULT_HEIGHT);
			this.reset = new BasicButton(0, 0, 50, Button.DEFAULT_HEIGHT, Texts::guiReset, Tooltips::empty, button -> {
				this.option.reset();
				return true;
			});

			addChild(this.name);
			addChild(this.control);
			addChild(this.reset);

			setWidth(options.getWidth());
			setHeight(Button.DEFAULT_HEIGHT);

			this.option.setListener(this);
		}

		@Override
		public void setX(int x) {
			super.setX(x);

			int mid = x + options.getWidth() / 2;

			name.setX(mid - name.getWidth() - 2);
			control.setX(mid + 2);
			reset.setX(mid + 2 + control.getWidth() + 10);
		}

		@Override
		public void setY(int y) {
			super.setY(y);

			int height = getHeight();

			name.setY(y + height - (height + font.height()) / 2);
			control.setY(y);
			reset.setY(y);
		}

		@Override
		public void onRemoved() {
			option.setListener(null);
			super.onRemoved();
		}

		@Override
		public void valueChanged() {
			control.update();
			reset.setActive(!option.isDefault());
		}
	}
}
