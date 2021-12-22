package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.IButton;

public class TextElement extends AbstractElement {
	
	private static final int SPACING = 2;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final Consumer<TextElement> updater;
	private final Supplier<List<Text>> tooltipSupplier;
	private final MousePress<TextElement> mousePress;
	
	private List<Text> text;
	private boolean rightAligned;
	private boolean withShadow;
	private int color;
	
	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater) {
		this(client, x, y, updater, () -> null);
	}
	
	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater, Supplier<List<Text>> tooltipSupplier) {
		this(client, x, y, updater, tooltipSupplier, textElement -> false);
	}
	
	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater, Supplier<List<Text>> tooltipSupplier, MousePress<TextElement> mousePress) {
		super(x, y, 0, 0);
		
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.updater = updater;
		this.tooltipSupplier = tooltipSupplier;
		this.mousePress = mousePress;
		
		this.rightAligned = false;
		this.withShadow = false;
		this.color = 0xFFFFFFFF;
		
		this.update();
	}
	
	@Override
	public void render(int mouseX, int mouseY) {
		int left = getX();
		int right = getX() + getWidth();
		
		int textX;
		int textY = getY();
		
		for (int index = 0; index < text.size(); index++) {
			Text t = text.get(index);
			textX = rightAligned ? right - getWidth(font, t) : left;
			renderText(font, t, textX, textY, withShadow, color);
			
			textY += font.fontHeight + SPACING;
		}
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (!consumed && mousePress.accept(this)) {
			IButton.playClickSound(client);
			consumed = true;
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}
	
	@Override
	public boolean keyPress(int key) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int key) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr) {
		return false;
	}
	
	@Override
	public void onRemoved() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public List<Text> getTooltip(int mouseX, int mouseY) {
		return tooltipSupplier.get();
	}
	
	@Override
	public void update() {
		text = new ArrayList<>();
		updater.accept(this);
		
		updateWidth();
		updateHeight();
	}
	
	public TextElement add(String text) {
		return add(new LiteralText(text));
	}
	
	public TextElement add(Text text) {
		this.text.add(text);
		return this;
	}
	
	public TextElement setText(List<Text> text) {
		this.text = text;
		return this;
	}
	
	public TextElement setRightAligned(boolean rightAligned) {
		this.rightAligned = rightAligned;
		return this;
	}
	
	public TextElement setWithShadow(boolean withShadow) {
		this.withShadow = withShadow;
		return this;
	}
	
	public TextElement setColor(int color) {
		this.color = color;
		return this;
	}
	
	protected void updateWidth() {
		int width = 0;
		
		for (Text t : text) {
			int textWidth = getWidth(font, t);
			
			if (textWidth > width) {
				width = textWidth;
			}
		}
		
		setWidth(width);
	}
	
	protected void updateHeight() {
		setHeight((text.size() - 1) * (font.fontHeight + SPACING) + font.fontHeight);
	}
}
