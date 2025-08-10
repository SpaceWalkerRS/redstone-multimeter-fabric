package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.texture.TextureRegions;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public abstract class AbstractButton extends AbstractElement implements Button {

	protected static final int EDGE = 3;

	private final FontRenderer font;
	private final Supplier<Text> messageSupplier;
	private final Supplier<Tooltip> tooltipSupplier;

	private Text message;
	private boolean active;

	protected AbstractButton(int x, int y, Supplier<Text> message, Supplier<Tooltip> tooltip) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip);
	}

	protected AbstractButton(int x, int y, int width, int height, Supplier<Text> message, Supplier<Tooltip> tooltip) {
		super(x, y, width, height);

		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.messageSupplier = message;
		this.tooltipSupplier = tooltip;

		this.message = message.get();
		this.active = true;
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		this.renderButton(renderer);
		this.renderButtonMessage(renderer);
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		return this.tooltipSupplier.get();
	}

	@Override
	public void update() {
		this.updateMessage();
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Text getMessage() {
		return this.message;
	}

	@Override
	public void setMessage(Text message) {
		this.message = message;
	}

	protected void updateMessage() {
		this.setMessage(this.messageSupplier.get());
	}

	protected TextureRegion getButtonTexture() {
		if (!this.isActive()) {
			return TextureRegions.BASIC_BUTTON_INACTIVE;
		}
		if (this.isHovered()) {
			return TextureRegions.BASIC_BUTTON_HOVERED;
		}

		return TextureRegions.BASIC_BUTTON;
	}

	protected void renderButton(GuiRenderer renderer) {
		TextureRegion texture = this.getButtonTexture();

		if (texture != null) {
			int x0 = this.getX();
			int y0 = this.getY();
			int x1 = x0 + this.getWidth();
			int y1 = y0 + this.getHeight();

			renderer.blitSpliced(texture, x0, y0, x1, y1, EDGE);
		}
	}

	protected void renderButtonMessage(GuiRenderer renderer) {
		Text message = this.getMessage();

		if (message != null) {
			this.renderScrollingText(renderer, message);
		}
	}

	protected void renderScrollingText(GuiRenderer renderer, Text message) {
		int buttonWidth = this.getWidth();
		int availableWidth = buttonWidth - 2 * EDGE;
		int messageWidth = renderer.width(message);

		if (messageWidth > availableWidth) {
			int x = this.getX() + buttonWidth - (buttonWidth + availableWidth) / 2;
			int y = this.getMessageY();
			int color = this.getMessageColor();

			double time = System.nanoTime() / 1000000000.0D;
			double progress = 0.5D + Math.sin(time) / 2;
			int maxScroll = messageWidth - availableWidth;
			int scroll = (int) (maxScroll * progress);

			renderer.pushScissor(x, y, x + availableWidth, y + this.font.height());
			renderer.drawStringWithShadow(message, x - scroll, y, color);
			renderer.popScissor();
		} else {
			int x = this.getX() + buttonWidth - (buttonWidth + messageWidth) / 2;
			int y = this.getMessageY();
			int color = this.getMessageColor();

			renderer.drawStringWithShadow(message, x, y, color);
		}
	}

	public int getMessageY() {
		return this.getY() + this.getHeight() - (this.getHeight() + this.font.height()) / 2;
	}

	protected int getMessageColor() {
		return this.isActive() ? 0xFFFFFFFF : 0xFFA0A0A0;
	}
}
