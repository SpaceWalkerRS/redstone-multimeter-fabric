package redstone.multimeter.client.gui.screen;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.Textures;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public abstract class RSMMScreen extends AbstractParentElement {

	protected final MultimeterClient client;
	protected final Minecraft minecraft;
	protected final FontRenderer font;

	private final Text title;
	private final boolean drawTitle;

	protected ScreenWrapper wrapper;

	protected RSMMScreen(Text title, boolean drawTitle) {
		this.client = MultimeterClient.INSTANCE;
		this.minecraft = MultimeterClient.MINECRAFT;
		this.font = client.getFontRenderer();

		this.title = title;
		this.drawTitle = drawTitle;
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		renderBackground(renderer);
		renderContent(renderer, mouseX, mouseY);

		if (drawTitle) {
			int width = font.width(title);
			int x = getX() + (getWidth() - width) / 2;
			int y = getY() + 6;

			font.drawWithShadow(title, x, y);
		}

		Tooltip tooltip = getTooltip(mouseX, mouseY);

		if (!tooltip.isEmpty()) {
			renderer.tooltip(tooltip, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClick(MouseEvent.Click event) {
		return super.mouseClick(event) || client.getInputHandler().mouseClick(this, event);
	}

	@Override
	public boolean keyPress(KeyEvent.Press event) {
		return super.keyPress(event) || client.getInputHandler().keyPress(this, event);
	}

	@Override
	public boolean mouseScroll(MouseEvent.Scroll event) {
		return client.getInputHandler().mouseScroll(this, event) || super.mouseScroll(event);
	}

	@Override
	public final boolean isHovered() {
		return true;
	}

	@Override
	public final void setX(int x) {
	}

	@Override
	public final void setY(int y) {
	}

	public void init(int width, int height) {
		setWidth(width);
		setHeight(height);

		removeChildren();
		initScreen();
		update();

		Window window = minecraft.getWindow();
		MouseHandler mouse = minecraft.mouseHandler;
		double mouseX = (double)mouse.xpos() * window.getGuiScaledWidth() / window.getWidth();
		double mouseY = (double)mouse.ypos() * window.getGuiScaledHeight() / window.getHeight();

		mouseMove(mouseX, mouseY);
	}

	protected abstract void initScreen();

	protected boolean shouldCloseOnEsc() {
		return true;
	}

	public void close() {
		minecraft.setScreen(wrapper.getParent());
	}

	protected boolean hasTransparentBackground() {
		return minecraft.level != null;
	}

	protected void renderBackground(GuiRenderer renderer) {
		if (hasTransparentBackground()) {
			renderer.gradient(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xC0101010, 0xD0101010);
		} else {
			int x0 = getX();
			int y0 = getY();
			int x1 = x0 + getWidth();
			int y1 = y0 + getHeight();

			int u0 = x0 / 2;
			int v0 = y0 / 2;
			int u1 = x1 / 2;
			int v1 = y1 / 2;

			renderer.blit(Textures.OPTIONS_BACKGROUND, x0, y0, x1, y1, u0, v0, u1, v1, 0xFFFFFFFF);
		}
	}

	protected void renderContent(GuiRenderer renderer, int mouseX, int mouseY) {
		super.render(renderer, mouseX, mouseY);
	}

	public Text getTitle() {
		return title;
	}

	public boolean isPauseScreen() {
		return true;
	}
}
