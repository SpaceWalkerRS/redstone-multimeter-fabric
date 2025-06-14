package redstone.multimeter.client.gui.screen;

import java.util.List;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

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
	public void render(PoseStack poses, int mouseX, int mouseY) {
		renderBackground(poses);
		renderContent(poses, mouseX, mouseY);

		if (drawTitle) {
			int width = font.width(title);
			int x = getX() + (getWidth() - width) / 2;
			int y = getY() + 6;

			renderText(font, poses, title, x, y, true, 0xFFFFFFFF);
		}

		Tooltip tooltip = getTooltip(mouseX, mouseY);

		if (!tooltip.isEmpty()) {
			drawTooltip(poses, tooltip, mouseX, mouseY);
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

	protected void renderBackground(PoseStack poses) {
		if (hasTransparentBackground()) {
			renderGradient(poses, getX(), getY(), getWidth(), getHeight(), 0xC0101010, 0xD0101010);
		} else {
			renderBackgroundTexture(poses);
		}
	}

	protected boolean hasTransparentBackground() {
		return minecraft.level != null;
	}

	protected void renderBackgroundTexture(PoseStack poses) {
		int x0 = getX();
		int y0 = getY();
		int x1 = x0 + getWidth();
		int y1 = y0 + getHeight();

		int tx0 = x0 / 2;
		int ty0 = y0 / 2;
		int tx1 = x1 / 2;
		int ty1 = y1 / 2;

		renderTextureColor(poses, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
	}

	protected void renderContent(PoseStack poses, int mouseX, int mouseY) {
		super.render(poses, mouseX, mouseY);
	}

	protected void drawTooltip(PoseStack poses, Tooltip tooltip, int mouseX, int mouseY) {
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

		drawTooltip(poses, lines, x, y, width, height);
	}

	private void drawTooltip(PoseStack poses, List<Component> lines, int x, int y, int width, int height) {
		int backgroundColor = 0xF0100010;
		int borderColor0 = 0x505000FF;
		int borderColor1 = 0x5028007F;

		poses.pushPose();
		poses.translate(0, 0, 400);

		renderRect(poses, (bufferBuilder, pose) -> {
			// background
			drawRect(bufferBuilder, pose, x    , y + 1         , width    , height - 2, backgroundColor); // center, left/right outer borders
			drawRect(bufferBuilder, pose, x + 1, y             , width - 2, 1         , backgroundColor); // top outer border
			drawRect(bufferBuilder, pose, x + 1, y + height - 1, width - 2, 1         , backgroundColor); // bottom outer border

			// inner border
			drawGradient(bufferBuilder, pose, x + 1        , y + 2         , 1        , height - 4, borderColor0, borderColor1); // left
			drawRect    (bufferBuilder, pose, x + 1        , y + height - 2, width - 2, 1         , borderColor1);               // bottom
			drawGradient(bufferBuilder, pose, x + width - 2, y + 2         , 1        , height - 4, borderColor0, borderColor1); // right
			drawRect    (bufferBuilder, pose, x + 1        , y + 1         , width - 2, 1         , borderColor0);               // top
		});

		renderText(poses, (immediate, model) -> {
			int textX = x + 4;
			int textY = y + 4;

			for (int index = 0; index < lines.size(); index++) {
				Component line = lines.get(index);
				drawText(immediate, model, font, line, textX, textY, true);

				textY += font.lineHeight + 1;
			}
		});

		poses.popPose();
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
