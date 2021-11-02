package redstone.multimeter.client.gui.element;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.IButton;

public class TextElement extends AbstractElement {
	
	private static final int SPACING = 2;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final Supplier<List<Text>> textSupplier;
	private final Supplier<List<Text>> tooltipSupplier;
	private final MousePress<TextElement> mousePress;
	
	private List<Text> text;
	private boolean rightAligned;
	
	public TextElement(MultimeterClient client, int x, int y, boolean rightAligned, Supplier<List<Text>> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<TextElement> mousePress) {
		super(x, y, 0, 0);
		
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.textSupplier = textSupplier;
		this.tooltipSupplier = tooltipSupplier;
		this.mousePress = mousePress;
		
		this.rightAligned = rightAligned;
		
		this.update();
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int left = getX();
		int right = getX() + getWidth();
		int textY = getY();
		
		for (Text t : text) {
			int textX = rightAligned ? right - font.getWidth(t) : left;
			drawText(matrices, textX, textY, t);
			
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
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
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
		text = textSupplier.get();
		
		updateWidth();
		updateHeight();
	}
	
	protected void updateWidth() {
		int width = 0;
		
		for (Text t : text) {
			int textWidth = font.getWidth(t);
			
			if (textWidth > width) {
				width = textWidth;
			}
		}
		
		setWidth(width);
	}
	
	protected void updateHeight() {
		setHeight((text.size() - 1) * (font.fontHeight + SPACING) + font.fontHeight);
	}
	
	protected void drawText(MatrixStack matrices, int x, int y, Text text) {
		font.draw(matrices, text, x, y, 0xFFFFFFFF);
	}
}
