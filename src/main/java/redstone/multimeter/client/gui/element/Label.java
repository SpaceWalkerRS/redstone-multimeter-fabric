package redstone.multimeter.client.gui.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.action.MousePress;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class Label extends AbstractElement {

	private final FontRenderer font;
	private final Consumer<Label> updater;
	private final Supplier<Tooltip> tooltipSupplier;
	private final MousePress<Label> mousePress;

	private List<Text> lines;
	private Alignment alignment;
	private boolean shadow;
	private int spacing;
	private int color;

	public Label(int x, int y, Consumer<Label> updater) {
		this(x, y, updater, Tooltips::empty);
	}

	public Label(int x, int y, Consumer<Label> updater, Supplier<Tooltip> tooltipSupplier) {
		this(x, y, updater, tooltipSupplier, (label, event) -> false);
	}

	public Label(int x, int y, Consumer<Label> updater, Supplier<Tooltip> tooltipSupplier, MousePress<Label> mousePress) {
		super(x, y, 0, 0);

		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.updater = updater;
		this.tooltipSupplier = tooltipSupplier;
		this.mousePress = mousePress;

		this.alignment = Alignment.LEFT;
		this.shadow = false;
		this.spacing = 2;
		this.color = 0xFFFFFFFF;

		this.update();
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		int left = this.getX();
		int right = this.getX() + this.getWidth();

		int lineHeight = this.font.height() + this.spacing;
		int textX;
		int textY = this.getY() - lineHeight;

		for (int index = 0; index < this.lines.size(); index++) {
			Text line = this.lines.get(index);

			switch (this.alignment) {
			case LEFT:
				textX = left;
				break;
			case RIGHT:
				textX = right - this.font.width(line);
				break;
			case CENTER:
				textX = right - this.font.width(line) / 2;
				break;
			default:
				throw new IllegalStateException("unknown label alignment " + this.alignment);
			}
			textY += lineHeight;

			renderer.drawString(line, textX, textY, this.color, this.shadow);
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(MouseEvent.Click event) {
		boolean consumed = super.mouseClick(event);

		if (!consumed && this.mousePress.accept(this, event)) {
			Button.playClickSound();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(MouseEvent.Drag event) {
		return false;
	}

	@Override
	public boolean mouseScroll(MouseEvent.Scroll event) {
		return false;
	}

	@Override
	public boolean keyPress(KeyEvent.Press event) {
		return false;
	}

	@Override
	public boolean keyRelease(KeyEvent.Release event) {
		return false;
	}

	@Override
	public boolean typeChar(CharacterEvent.Type event) {
		return false;
	}

	@Override
	public void tick() {
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		return this.tooltipSupplier.get();
	}

	@Override
	public void update() {
		this.lines = new ArrayList<>();
		this.updater.accept(this);

		this.updateWidth();
		this.updateHeight();
	}

	public Label clearLines() {
		this.lines.clear();
		return this;
	}

	public Label setLines(List<Text> lines) {
		this.lines = lines;
		return this;
	}

	public Label setLines(String line) {
		this.lines = Arrays.asList(Texts.literal(line));
		return this;
	}

	public Label setLines(Text line) {
		this.lines = Arrays.asList(line);
		return this;
	}

	public Label addLine(String line) {
		this.lines.add(Texts.literal(line));
		return this;
	}

	public Label addLine(Text line) {
		this.lines.add(line);
		return this;
	}

	public Label setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public Label setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public Label setSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}

	public Label setColor(int color) {
		this.color = color;
		return this;
	}

	private void updateWidth() {
		int width = 0;

		for (int index = 0; index < this.lines.size(); index++) {
			Text t = this.lines.get(index);
			int textWidth = this.font.width(t);

			if (textWidth > width) {
				width = textWidth;
			}
		}

		this.setWidth(width);
	}

	private void updateHeight() {
		this.setHeight((this.lines.size() - 1) * (this.font.height() + this.spacing) + this.font.height());
	}

	public enum Alignment {

		LEFT, RIGHT, CENTER

	}
}
