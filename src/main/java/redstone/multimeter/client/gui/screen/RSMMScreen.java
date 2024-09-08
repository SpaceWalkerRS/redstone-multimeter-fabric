package redstone.multimeter.client.gui.screen;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;

public abstract class RSMMScreen extends AbstractParentElement {

	protected final MultimeterClient client;
	protected final Minecraft minecraft;
	protected final Font font;

	private final Component title;
	private final boolean drawTitle;

	protected ScreenWrapper wrapper;

	protected RSMMScreen(MultimeterClient client, Component title, boolean drawTitle) {
		this.client = client;
		this.minecraft = client.getMinecraft();
		this.font = this.minecraft.font;

		this.title = title;
		this.drawTitle = drawTitle;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		renderBackground();
		renderContent(mouseX, mouseY);

		if (drawTitle) {
			int width = textWidth(font, title);
			int x = getX() + (getWidth() - width) / 2;
			int y = getY() + 6;

			renderText(font, title, x, y, true, 0xFFFFFFFF);
		}

		Tooltip tooltip = getTooltip(mouseX, mouseY);

		if (!tooltip.isEmpty()) {
			drawTooltip(tooltip, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		return super.mouseClick(mouseX, mouseY, button) || client.getInputHandler().mouseClick(this, mouseX, mouseY, button);
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return super.keyPress(keyCode, scanCode, modifiers) || client.getInputHandler().keyPress(this, keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return client.getInputHandler().mouseScroll(this, scrollX, scrollY) || super.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public final void setX(int x) {
	}

	@Override
	public final void setY(int y) {
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
	}

	@Override
	protected final void onChangedX(int x) {
	}

	@Override
	protected final void onChangedY(int y) {
	}

	public void init(int width, int height) {
		setWidth(width);
		setHeight(height);

		removeChildren();
		initScreen();
		update();

		Window window = minecraft.window;
		MouseHandler mouse = minecraft.mouseHandler;
		double mouseX = (double)mouse.xpos() * window.getGuiScaledWidth() / window.getWidth();
		double mouseY = (double)mouse.ypos() * window.getGuiScaledHeight() / window.getHeight();

		updateHoveredElement(mouseX, mouseY);
	}

	protected abstract void initScreen();

	protected boolean shouldCloseOnEsc() {
		return true;
	}

	public void close() {
		minecraft.setScreen(wrapper.getParent());
	}

	protected void renderBackground() {
		if (hasTransparentBackground()) {
			renderGradient(getX(), getY(), getWidth(), getHeight(), 0xC0101010, 0xD0101010);
		} else {
			renderBackgroundTexture();
		}
	}

	protected boolean hasTransparentBackground() {
		return minecraft.level != null;
	}

	protected void renderBackgroundTexture() {
		int x0 = getX();
		int y0 = getY();
		int x1 = x0 + getWidth();
		int y1 = y0 + getHeight();

		int tx0 = x0 / 2;
		int ty0 = y0 / 2;
		int tx1 = x1 / 2;
		int ty1 = y1 / 2;

		renderTextureColor(Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
	}

	protected void renderContent(int mouseX, int mouseY) {
		super.render(mouseX, mouseY);
	}

	protected void drawTooltip(Tooltip tooltip, int mouseX, int mouseY) {
		List<Component> lines = tooltip.getLines();

		int width = tooltip.getWidth(font) + 8;
		int height = tooltip.getHeight(font) + 8;

		int x = mouseX + 15;
		int y = mouseY;

		if (x + width > getX() + getWidth()) {
			x = mouseX - 15 - width;
		}
		if (y + height > getY() + getHeight()) {
			y = mouseY - height;
		}

		drawTooltip(lines, x, y, width, height);
	}

	private void drawTooltip(List<Component> lines, int x, int y, int width, int height) {
		int backgroundColor = 0xF0100010;
		int borderColor0 = 0x505000FF;
		int borderColor1 = 0x5028007F;

		GlStateManager.pushMatrix();
		GlStateManager.translated(0, 0, 400);

		renderRect(bufferBuilder -> {
			// background
			drawRect(bufferBuilder, x    , y + 1         , width    , height - 2, backgroundColor); // center, left/right outer borders
			drawRect(bufferBuilder, x + 1, y             , width - 2, 1         , backgroundColor); // top outer border
			drawRect(bufferBuilder, x + 1, y + height - 1, width - 2, 1         , backgroundColor); // bottom outer border

			// inner border
			drawGradient(bufferBuilder, x + 1        , y + 2         , 1         , height - 4, borderColor0, borderColor1); // left
			drawRect    (bufferBuilder, x + 1        , y + height - 2, width - 2 , 1         , borderColor1);               // bottom
			drawGradient(bufferBuilder, x + width - 2, y + 2         , 1         , height - 4, borderColor0, borderColor1); // right
			drawRect    (bufferBuilder, x + 1        , y + 1         , width - 2 , 1         , borderColor0);               // top
		});

		int textX = x + 4;
		int textY = y + 4;

		for (int i = 0; i < lines.size(); i++) {
			Component line = lines.get(i);
			renderText(font, line, textX, textY, true, 0xFFFFFFFF);

			textY += font.lineHeight + 1;
		}

		GlStateManager.popMatrix();
	}

	public Component getTitle() {
		return title;
	}

	public boolean isPauseScreen() {
		return true;
	}

	public static boolean isControlPressed() {
		return Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}
}
