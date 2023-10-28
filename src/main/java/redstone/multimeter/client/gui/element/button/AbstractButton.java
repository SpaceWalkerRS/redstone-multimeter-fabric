package redstone.multimeter.client.gui.element.button;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.TextureRegion;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractElement;

public abstract class AbstractButton extends AbstractElement implements IButton {

	protected static final WidgetSprites SPRITES = new WidgetSprites(new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted"));

	protected final MultimeterClient client;
	protected final Font font;
	private final Supplier<Component> messageSupplier;
	private final Supplier<Tooltip> tooltipSupplier;

	private boolean active;
	private boolean hovered;
	private Component message;

	private boolean moved;

	protected AbstractButton(MultimeterClient client, int x, int y, Supplier<Component> message, Supplier<Tooltip> tooltip) {
		this(client, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip);
	}

	protected AbstractButton(MultimeterClient client, int x, int y, int width, int height, Supplier<Component> message, Supplier<Tooltip> tooltip) {
		super(x, y, width, height);

		Minecraft minecraft = client.getMinecraft();

		this.client = client;
		this.font = minecraft.font;
		this.messageSupplier = message;
		this.tooltipSupplier = tooltip;

		this.active = true;
		this.hovered = false;
		this.message = message.get();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		if (moved) {
			moved = false;
			updateHovered(mouseX, mouseY);
		}

		renderButton(graphics);
		renderButtonMessage(graphics);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		updateHovered(mouseX, mouseY);
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		moved = true;
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		moved = true;
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		return tooltipSupplier.get();
	}

	@Override
	public void update() {
		updateMessage();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isHovered() {
		return hovered;
	}

	@Override
	public Component getMessage() {
		return message;
	}

	@Override
	public void setMessage(Component message) {
		this.message = message;
	}

	protected void playClickSound() {
		IButton.playClickSound(client);
	}

	protected void updateHovered(double mouseX, double mouseY) {
		hovered = isActive() && isHovered(mouseX, mouseY);
	}

	protected void updateMessage() {
		setMessage(messageSupplier.get());
	}

	protected void renderButton(GuiGraphics graphics) {
		TextureRegion texture = getBackgroundTexture();

		if (texture != null) {
			drawTexturedButton(graphics, texture, getX(), getY(), getWidth(), getHeight());
		}
	}

	protected TextureRegion getBackgroundTexture() {
		return getButtonTexture();
	}

	protected TextureRegion getButtonTexture() {
		if (!isActive()) {
			return TextureRegion.BASIC_BUTTON_INACTIVE;
		}
		if (isHovered()) {
			return TextureRegion.BASIC_BUTTON_HOVERED;
		}

		return TextureRegion.BASIC_BUTTON;
	}

	protected void drawTexturedButton(GuiGraphics graphics, TextureRegion region, int x, int y, int width, int height) {
		boolean matchWidth = (width == region.width);
		boolean matchHeight = (height == region.height);

		if (matchWidth && matchHeight) {
			renderTextureRegion(graphics, region, x, y, width, height);
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

			int tx0 = region.x;
			int tx1 = region.x + leftWidth;
			int tx2 = region.x + region.width - rightWidth;
			int tx3 = region.x + region.width;
			int ty0 = region.y;
			int ty1 = region.y + topHeight;
			int ty2 = region.y + region.height - bottomHeight;
			int ty3 = region.y + region.height;

			renderTexture(graphics, texture, (bufferBuilder, pose) -> {
				drawTexture(bufferBuilder, pose, texture, x0, y0, x1, y1, tx0, ty0, tx1, ty1);
				drawTexture(bufferBuilder, pose, texture, x0, y2, x1, y3, tx0, ty2, tx1, ty3);
				drawTexture(bufferBuilder, pose, texture, x2, y2, x3, y3, tx2, ty2, tx3, ty3);
				drawTexture(bufferBuilder, pose, texture, x2, y0, x3, y1, tx2, ty0, tx3, ty1);
			});
		}
	}

	protected void renderButtonMessage(GuiGraphics graphics) {
		Component message = getMessage();

		if (message != null) {
			int x = getMessageX(message);
			int y = getMessageY();
			int color = getMessageColor();

			renderText(font, graphics, message, x, y, true, color);
		}
	}

	protected int getMessageX(Component message) {
		return getX() + getWidth() - (getWidth() + font.width(message)) / 2;
	}

	public int getMessageY() {
		return getY() + getHeight() - (getHeight() + font.lineHeight) / 2;
	}

	protected int getMessageColor() {
		return isActive() ? 0xFFFFFFFF : 0xFFA0A0A0;
	}
}
