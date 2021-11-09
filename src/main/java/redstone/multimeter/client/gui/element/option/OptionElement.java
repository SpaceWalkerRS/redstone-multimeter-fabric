package redstone.multimeter.client.gui.element.option;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.controls.ControlElement;
import redstone.multimeter.client.option.IOption;
import redstone.multimeter.client.option.OptionListener;
import redstone.multimeter.util.TextUtils;

public class OptionElement extends ControlElement implements OptionListener {
	
	private final IOption option;
	
	public OptionElement(MultimeterClient client, int midpoint, int controlWidth, IOption option) {
		super(client, midpoint, controlWidth, () -> option.getDisplayName(), getTooltipSupplier(client, option), (_client, width, height) -> option.createControl(_client, width, height), () -> option.isDefault(), () -> option.reset());
		
		this.option = option;
		this.option.setListener(this);
	}
	
	@Override
	public void onRemoved() {
		option.setListener(null);
		super.onRemoved();
	}

	@Override
	public void valueChanged() {
		update();
	}
	
	private static Supplier<List<Text>> getTooltipSupplier(MultimeterClient client, IOption option) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		TextRenderer font = minecraftClient.textRenderer;
		
		String description = option.getDescription();
		List<Text> tooltip = TextUtils.toLines(font, description);
		
		return () -> tooltip;
	}
}
