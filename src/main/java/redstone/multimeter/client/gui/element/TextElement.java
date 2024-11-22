package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.IButton;

public class TextElement extends AbstractElement {

	private final MultimeterClient client;
	private final TextRenderer textRenderer;
	private final Consumer<TextElement> updater;
	private final Supplier<Tooltip> tooltipSupplier;
	private final MousePress<TextElement> mousePress;

	private List<String> text;
	private int spacing;
	private boolean rightAligned;
	private boolean withShadow;
	private int color;

	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater) {
		this(client, x, y, updater, () -> Tooltip.EMPTY);
	}

	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater, Supplier<Tooltip> tooltipSupplier) {
		this(client, x, y, updater, tooltipSupplier, textElement -> false);
	}

	public TextElement(MultimeterClient client, int x, int y, Consumer<TextElement> updater, Supplier<Tooltip> tooltipSupplier, MousePress<TextElement> mousePress) {
		super(x, y, 0, 0);

		Minecraft minecraft = client.getMinecraft();

		this.client = client;
		this.textRenderer = minecraft.textRenderer;
		this.updater = updater;
		this.tooltipSupplier = tooltipSupplier;
		this.mousePress = mousePress;

		this.spacing = 2;
		this.rightAligned = false;
		this.withShadow = false;
		this.color = 0xFFFFFFFF;

		this.update();
	}

	@Override
	public void render(int mouseX, int mouseY) {
		int left = getX();
		int right = getX() + getWidth();

		int textY = getY();

		for (int i = 0; i < text.size(); i++) {
			String t = text.get(i);
			int textX = rightAligned ? right - textWidth(textRenderer, t) : left;
			renderText(textRenderer, t, textX, textY, withShadow, color);

			textY += textRenderer.fontHeight + spacing;
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
	public boolean keyPress(int keyCode) {
		return false;
	}

	@Override
	public boolean keyRelease(int keyCode) {
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
	public Tooltip getTooltip(int mouseX, int mouseY) {
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
		this.text.add(text);
		return this;
	}

	public TextElement setText(List<String> text) {
		this.text = text;
		return this;
	}

	public TextElement setText(String text) {
		this.text = Arrays.asList(text);
		return this;
	}

	public TextElement setSpacing(int spacing) {
		this.spacing = spacing;
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

		for (int index = 0; index < text.size(); index++) {
			String t = text.get(index);
			int textWidth = textWidth(textRenderer, t);

			if (textWidth > width) {
				width = textWidth;
			}
		}

		setWidth(width);
	}

	protected void updateHeight() {
		setHeight((text.size() - 1) * (textRenderer.fontHeight + spacing) + textRenderer.fontHeight);
	}
}
