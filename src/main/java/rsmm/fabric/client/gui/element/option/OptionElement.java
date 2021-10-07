package rsmm.fabric.client.gui.element.option;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.controls.ControlElement;
import rsmm.fabric.client.option.IOption;
import rsmm.fabric.client.option.OptionListener;
import rsmm.fabric.util.TextUtils;

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
