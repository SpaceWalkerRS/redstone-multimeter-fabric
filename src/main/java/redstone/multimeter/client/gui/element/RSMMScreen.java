package redstone.multimeter.client.gui.element;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.TextureRegion;

public abstract class RSMMScreen extends AbstractParentElement {
	
	protected final MultimeterClient client;
	protected final MinecraftClient minecraftClient;
	protected final TextRenderer font;
	
	private final Text title;
	private final boolean drawTitle;
	
	protected ScreenWrapper wrapper;
	
	protected RSMMScreen(MultimeterClient client, Text title, boolean drawTitle) {
		this.client = client;
		this.minecraftClient = client.getMinecraftClient();
		this.font = this.minecraftClient.textRenderer;
		
		this.title = title;
		this.drawTitle = drawTitle;
	}
	
	@Override
	public void render(int mouseX, int mouseY) {
		renderBackground();
		renderContent(mouseX, mouseY);
		
		if (drawTitle) {
			int width = getWidth(font, title);
			int x = getX() + (getWidth() - width) / 2;
			int y = getY() + 6;
			
			renderText(font, title, x, y, true, 0xFFFFFFFF);
		}
		
		List<Text> tooltip = getTooltip(mouseX, mouseY);
		
		if (tooltip != null && !tooltip.isEmpty()) {
			drawTooltip(tooltip, mouseX, mouseY);
		}
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		return super.mouseClick(mouseX, mouseY, button) || client.getInputHandler().mouseClick(this, mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPress(int key) {
		return super.keyPress(key) || client.getInputHandler().keyPress(this, key);
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return super.mouseScroll(mouseX, mouseY, scrollX, scrollY) || client.getInputHandler().mouseScroll(this, scrollX, scrollY);
	}
	
	@Override
	public final void setX(int x) {
		
	}
	
	@Override
	public final void setY(int y) {
		
	}
	
	@Override
	protected final void onChangedX(int x) {
		
	}
	
	@Override
	protected final void onChangedY(int y) {
		
	}
	
	protected abstract void initScreen();
	
	protected boolean shouldCloseOnEsc() {
		return true;
	}
	
	public void close() {
		minecraftClient.openScreen(wrapper.getParent());
	}
	
	protected void renderBackground() {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		if (minecraftClient.world == null) {
			renderBackgroundTexture();
		} else {
			renderGradient(getX(), getY(), getWidth(), getHeight(), 0xC0101010, 0xD0101010);
		}
	}
	
	protected void renderBackgroundTexture() {
		renderTextureRegion(TextureRegion.OPTIONS_BACKGROUND, getX(), getY(), getWidth(), getHeight());
	}
	
	protected void renderContent(int mouseX, int mouseY) {
		super.render(mouseX, mouseY);
	}
	
	protected void drawTooltip(List<Text> lines, int mouseX, int mouseY) {
		int lineHeight = font.fontHeight;
		int lineSpacing = 1;
		
		int width = 0;
		int height = (lines.size() - 1) * (lineHeight + lineSpacing) + lineHeight;
		
		for (int index = 0; index < lines.size(); index++) {
			Text text = lines.get(index);
			int lineWidth = getWidth(font, text);
			
			if (lineWidth > width) {
				width = lineWidth;
			}
		}
		
		width += 8;
		height += 8;
		
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
	
	private void drawTooltip(List<Text> lines, int x, int y, int width, int height) {
		int backgroundColor = 0xF0100010;
		int borderColor0    = 0x505000FF;
		int borderColor1    = 0x5028007F;
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(0, 0, 400);
		
		renderRect(bufferBuilder -> {
			// background
			drawRect(bufferBuilder, x    , y + 1         , width    , height - 2, backgroundColor); // center, left/right outer borders
			drawRect(bufferBuilder, x + 1, y             , width - 2, 1         , backgroundColor); // top outer border
			drawRect(bufferBuilder, x + 1, y + height - 1, width - 2, 1         , backgroundColor); // bottom outer border
			
			// inner border
			drawGradient(bufferBuilder, x + 1        , y + 2         , 1        , height - 4, borderColor0, borderColor1); // left
			drawRect    (bufferBuilder, x + 1        , y + height - 2, width - 2, 1         , borderColor1);               // bottom
			drawGradient(bufferBuilder, x + width - 2, y + 2         , 1        , height - 4, borderColor0, borderColor1); // right
			drawRect    (bufferBuilder, x + 1        , y + 1         , width - 2, 1         , borderColor0);               // top
		});
		
		int textX = x + 4;
		int textY = y + 4;
		
		for (int index = 0; index < lines.size(); index++) {
			Text line = lines.get(index);
			renderText(font, line, textX, textY, true, 0xFFFFFFFF);
			
			textY += font.fontHeight + 1;
		}
		
		GlStateManager.popMatrix();
	}
	
	public Text getTitle() {
		return title;
	}
	
	public boolean isPauseScreen() {
		return true;
	}
	
	public static boolean isControlPressed() {
		return Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}
}
