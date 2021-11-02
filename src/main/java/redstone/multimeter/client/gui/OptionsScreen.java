package redstone.multimeter.client.gui;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.option.OptionsListBuilder;
import redstone.multimeter.client.option.IOption;
import redstone.multimeter.client.option.Options;

public class OptionsScreen extends RSMMScreen {
	
	private final Screen parent;
	
	public OptionsScreen(Screen parent) {
		super(new LiteralText(String.format("%s Options", RedstoneMultimeterMod.MOD_NAME)), true);
		
		this.parent = parent;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		client.keyboard.setRepeatEvents(false);
		Options.validate();
		multimeterClient.getHUD().onOptionsChanged();
	}
	
	@Override
	public void onClose() {
		client.setScreen(parent);
		client.options.write();
	}
	
	@Override
	protected void initScreen() {
		client.keyboard.setRepeatEvents(true);
		
		ScrollableListElement list = new ScrollableListElement(multimeterClient, getWidth(), getHeight(), 52, 36);
		OptionsListBuilder builder = new OptionsListBuilder(multimeterClient, getWidth());
		
		for (Entry<String, List<IOption>> entry : Options.byCategory().entrySet()) {
			String category = entry.getKey();
			List<IOption> options = entry.getValue();
			
			builder.addOptions(category, options);
		}
		
		builder.setMidpoint(getWidth() / 2);
		builder.setControlWidth(100);
		builder.build(list);
		
		list.setSpacing(IButton.DEFAULT_HEIGHT);
		list.setDrawBackground(true);
		list.setX(getX());
		list.setY(getY());
		
		int x = getX() + getWidth() / 2;
		int y = getY() + 22;
		
		IButton controls = new Button(multimeterClient, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("options.controls"), () -> null, button -> {
			client.setScreen(new ControlsOptionsScreen(this, client.options));
			return true;
		});
		
		y = getY() + getHeight() - (IButton.DEFAULT_HEIGHT + 8);
		
		IButton reset = new Button(multimeterClient, x - (4 + IButton.DEFAULT_WIDTH), y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("controls.reset"), () -> null, button -> {
			for (IOption option : Options.all()) {
				option.reset();
			}
			update();
			
			return true;
		});
		IButton done = new Button(multimeterClient, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> ScreenTexts.DONE, () -> null, button -> {
			client.setScreen(parent);
			client.options.write();
			
			return true;
		});
		
		addContent(list);
		addContent(controls);
		addContent(reset);
		addContent(done);
	}
}
