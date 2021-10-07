package rsmm.fabric.client.gui.widget;

import java.util.function.Supplier;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import rsmm.fabric.client.MultimeterClient;

public class ToggleButton extends Button {
	
	public ToggleButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Boolean> getter, Consumer<Button> toggle) {
		super(client, x, y, width, height, () -> {
			Boolean value = getter.get();
			Formatting color = value ? Formatting.GREEN : Formatting.RED;
			
			return new LiteralText(value.toString()).formatted(color);
		}, button -> { toggle.accept(button); return true; });
	}
}
