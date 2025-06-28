package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Texture;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.texture.TextureRegions;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public abstract class AbstractButton extends AbstractElement implements Button {

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
			this.renderButtonTexture(renderer, texture, getX(), this.getY(), this.getWidth(), this.getHeight());
		}
	}

	protected void renderButtonTexture(GuiRenderer renderer, TextureRegion region, int x, int y, int width, int height) {
		boolean matchWidth = (width == region.width);
		boolean matchHeight = (height == region.height);

		if (matchWidth && matchHeight) {
			renderer.blit(region, x, y, x + width, y + height);
		} else {
			Texture texture = region.texture;

			int border = 3;

			int leftWidth = Math.min(region.width, width) - border;
			int rightWidth = width - leftWidth;
			int topHeight = Math.min(region.height, height) - border;
			int bottomHeight = height - topHeight;

			int x0 = x;
			int x1 = x + leftWidth;
			int x2 = x + width - rightWidth;
			int x3 = x + width;
			int y0 = y;
			int y1 = y + topHeight;
			int y2 = y + height - bottomHeight;
			int y3 = y + height;

			int u0 = region.x;
			int u1 = region.x + leftWidth;
			int u2 = region.x + region.width - rightWidth;
			int u3 = region.x + region.width;
			int v0 = region.y;
			int v1 = region.y + topHeight;
			int v2 = region.y + region.height - bottomHeight;
			int v3 = region.y + region.height;

			renderer.blit(texture, x0, y0, x1, y1, u0, v0, u1, v1);
			renderer.blit(texture, x0, y2, x1, y3, u0, v2, u1, v3);
			renderer.blit(texture, x2, y2, x3, y3, u2, v2, u3, v3);
			renderer.blit(texture, x2, y0, x3, y1, u2, v0, u3, v1);
		}
	}

	protected void renderButtonMessage(GuiRenderer renderer) {
		Text message = this.getMessage();

		if (message != null) {
			int x = this.getMessageX(message);
			int y = this.getMessageY();
			int color = this.getMessageColor();

			renderer.drawStringWithShadow(message, x, y, color);
		}
	}

	protected int getMessageX(Text message) {
		return this.getX() + this.getWidth() - (this.getWidth() + this.font.width(message)) / 2;
	}

	public int getMessageY() {
		return this.getY() + this.getHeight() - (this.getHeight() + this.font.height()) / 2;
	}

	protected int getMessageColor() {
		return this.isActive() ? 0xFFFFFFFF : 0xFFA0A0A0;
	}

}
