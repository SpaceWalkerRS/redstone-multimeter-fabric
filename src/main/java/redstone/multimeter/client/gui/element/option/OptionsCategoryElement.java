package redstone.multimeter.client.gui.element.option;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.option.IOption;
import redstone.multimeter.client.option.OptionListener;
import redstone.multimeter.util.TextUtils;

public class OptionsCategoryElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final FontRenderer font;
	private final TextElement category;
	private final SimpleListElement options;
	
	public OptionsCategoryElement(MultimeterClient client, int width, String category, Collection<IOption> options) {
		Minecraft minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.fontRenderer;
		this.category = new TextElement(this.client, 0, 0, t -> t.setText(new TextComponentString(category).setStyle(new Style().setUnderlined(true))).setWithShadow(true));
		this.options = new SimpleListElement(this.client, width);
		
		for (IOption option : options) {
			this.options.add(new OptionElement(option));
		}
		
		this.options.update();
		
		addChild(this.category);
		addChild(this.options);
		
		setWidth(width);
		setHeight(this.category.getHeight() + this.options.getHeight() + 6);
	}
	
	@Override
	protected void onChangedX(int x) {
		category.setX(x + (options.getWidth() - category.getWidth()) / 2);
		options.setX(x);
	}
	
	@Override
	protected void onChangedY(int y) {
		category.setY(y);
		options.setY(y + category.getHeight() + 6);
	}
	
	private class OptionElement extends AbstractParentElement implements OptionListener {
		
		private final IOption option;
		private final TextElement name;
		private final IButton control;
		private final IButton reset;
		
		public OptionElement(IOption option) {
			Tooltip tooltip = Tooltip.of(TextUtils.toLines(font, option.getDescription()));
			
			this.option = option;
			this.name = new TextElement(client, 0, 0, t -> t.setText(this.option.getName()).setWithShadow(true), () -> tooltip, t -> false);
			this.control = this.option.createControl(client, 100, IButton.DEFAULT_HEIGHT);
			this.reset = new Button(client, 0, 0, 50, IButton.DEFAULT_HEIGHT, () -> new TextComponentTranslation("controls.reset"), () -> Tooltip.EMPTY, button -> {
				this.option.reset();
				return true;
			});
			
			addChild(this.name);
			addChild(this.control);
			addChild(this.reset);
			
			setWidth(options.getWidth());
			setHeight(IButton.DEFAULT_HEIGHT);
			
			this.option.setListener(this);
		}
		
		@Override
		public void onRemoved() {
			option.setListener(null);
			super.onRemoved();
		}
		
		@Override
		protected void onChangedX(int x) {
			int mid = x + options.getWidth() / 2;
			
			name.setX(mid - name.getWidth() - 2);
			control.setX(mid + 2);
			reset.setX(mid + 2 + control.getWidth() + 10);
		}
		
		@Override
		protected void onChangedY(int y) {
			int height = getHeight();
			
			name.setY(y + height - (height + font.FONT_HEIGHT) / 2);
			control.setY(y);
			reset.setY(y);
		}
		
		@Override
		public void valueChanged() {
			control.update();
			reset.setActive(!option.isDefault());
		}
	}
}
