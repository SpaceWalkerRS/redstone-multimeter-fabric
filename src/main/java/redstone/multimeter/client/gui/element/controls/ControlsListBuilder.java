package redstone.multimeter.client.gui.element.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RenderHelper2D;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.IButton;

public class ControlsListBuilder extends RenderHelper2D {
	
	protected final MultimeterClient client;
	protected final TextRenderer font;
	protected final Map<String, TextElement> categories;
	protected final Map<String, List<ControlElementFactory>> factories;
	protected final int width;
	
	protected int margin;
	protected int midpoint;
	protected int controlWidth;
	
	public ControlsListBuilder(MultimeterClient client, int width) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.categories = new LinkedHashMap<>();
		this.factories = new HashMap<>();
		this.width = width;
		
		this.margin = 0;
		this.midpoint = this.width / 2;
		this.controlWidth = IButton.DEFAULT_WIDTH;
	}
	
	public void addCategory(String name) {
		addCategory(name, () -> null, t -> false);
	}
	
	public void addCategory(String name, Supplier<List<Text>> tooltip, MousePress<TextElement> onPress) {
		if (categories.containsKey(name)) {
			throw new IllegalStateException("This options list already contains a \'" + name + "\' category!");
		}
		
		categories.put(name, new TextElement(client, 0, 0, t -> t.add(new LiteralText(name).setStyle(new Style().setItalic(true))).setWithShadow(true), tooltip, onPress));
		factories.put(name, new ArrayList<>());
	}
	
	public void addControl(String category, ControlElementFactory factory) {
		if (!categories.containsKey(category)) {
			addCategory(category);
		}
		
		factories.get(category).add(factory);
	}
	
	public void setMargin(int width) {
		margin = width;
	}
	
	public void setMidpoint(int width) {
		midpoint = width;
	}
	
	public void setControlWidth(int width) {
		controlWidth = width;
	}
	
	public SimpleListElement build() {
		SimpleListElement list = new SimpleListElement(client, width);
		build(list);
		return list;
	}
	
	public void build(SimpleListElement list) {
		list.clear();
		
		for (Entry<String, TextElement> entry : categories.entrySet()) {
			String name = entry.getKey();
			TextElement category = entry.getValue();
			List<ControlElementFactory> controls = factories.get(name);
			
			list.add(buildSection(category, controls));
		}
	}
	
	private ControlsSectionElement buildSection(TextElement category, List<ControlElementFactory> factories) {
		ControlsSectionElement section = createSection(category);
		
		for (ControlElementFactory factory : factories) {
			section.addControl(factory.create(client, midpoint, controlWidth));
		}
		
		return section;
	}
	
	protected ControlsSectionElement createSection(TextElement category) {
		return new ControlsSectionElement(client, width, margin, midpoint, category);
	}
}
