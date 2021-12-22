package redstone.multimeter.client.gui;

import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.option.OptionsListBuilder;
import redstone.multimeter.client.option.IOption;
import redstone.multimeter.client.option.Options;

public class OptionsScreen extends RSMMScreen {
	
	public OptionsScreen(MultimeterClient client) {
		super(client, new LiteralText(String.format("%s Options", RedstoneMultimeterMod.MOD_NAME)), true);
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		Options.validate();
		Keyboard.enableRepeatEvents(false);
		minecraftClient.options.save();
		client.getHUD().onOptionsChanged();
	}
	
	@Override
	protected void initScreen() {
		Keyboard.enableRepeatEvents(true);
		
		ScrollableListElement list = new ScrollableListElement(client, getWidth(), getHeight(), 52, 36);
		OptionsListBuilder builder = new OptionsListBuilder(client, getWidth());
		
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
		
		IButton controls = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("options.controls"), () -> null, button -> {
			minecraftClient.openScreen(new ControlsOptionsScreen(wrapper, minecraftClient.options));
			return true;
		});
		
		y = getY() + getHeight() - (IButton.DEFAULT_HEIGHT + 8);
		
		IButton reset = new Button(client, x - (4 + IButton.DEFAULT_WIDTH), y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("controls.reset"), () -> null, button -> {
			for (IOption option : Options.all()) {
				option.reset();
			}
			update();
			
			return true;
		});
		IButton done = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("gui.done"), () -> null, button -> {
			close();
			return true;
		});
		
		addChild(list);
		addChild(controls);
		addChild(reset);
		addChild(done);
	}
}
