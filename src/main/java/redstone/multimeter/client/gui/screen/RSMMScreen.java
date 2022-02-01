package redstone.multimeter.client.gui.screen;

import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;

public abstract class RSMMScreen extends AbstractParentElement {
	
	protected final MultimeterClient client;
	protected final Minecraft minecraftClient;
	protected final FontRenderer font;
	
	private final ITextComponent title;
	private final boolean drawTitle;
	
	protected ScreenWrapper wrapper;
	
	protected RSMMScreen(MultimeterClient client, ITextComponent title, boolean drawTitle) {
		this.client = client;
		this.minecraftClient = client.getMinecraftClient();
		this.font = this.minecraftClient.fontRenderer;
		
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
	public boolean keyPress(int keyCode) {
		return super.keyPress(keyCode) || client.getInputHandler().keyPress(this, keyCode);
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
 		
		double mouseX = Mouse.getX() * width / minecraftClient.displayWidth;
		double mouseY = height - 1 - Mouse.getY() * height / minecraftClient.displayHeight;
 		
 		updateHoveredElement(mouseX, mouseY);
 	}
	
	protected abstract void initScreen();
	
	protected boolean shouldCloseOnEsc() {
		return true;
	}
	
	public void close() {
		minecraftClient.displayGuiScreen(wrapper.getParent());
	}
	
	protected void renderBackground() {
		if (hasTransparentBackground()) {
			renderGradient(getX(), getY(), getWidth(), getHeight(), 0xC0101010, 0xD0101010);
		} else {
			renderBackgroundTexture();
		}
	}
	
	protected boolean hasTransparentBackground() {
		return minecraftClient.world != null;
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
		List<ITextComponent> lines = tooltip.getLines();
		
		int lineHeight = font.FONT_HEIGHT;
		int lineSpacing = 1;
		
		int width = 0;
		int height = (lines.size() - 1) * (lineHeight + lineSpacing) + lineHeight;
		
		for (int index = 0; index < lines.size(); index++) {
			ITextComponent text = lines.get(index);
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
	
	private void drawTooltip(List<ITextComponent> lines, int x, int y, int width, int height) {
		int backgroundColor = 0xF0100010;
		int borderColor0    = 0x505000FF;
		int borderColor1    = 0x5028007F;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 400);
		
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
			ITextComponent line = lines.get(index);
			renderText(font, line, textX, textY, true, 0xFFFFFFFF);
			
			textY += font.FONT_HEIGHT + 1;
		}
		
		GlStateManager.popMatrix();
	}
	
	public ITextComponent getTitle() {
		return title;
	}
	
	public boolean isPauseScreen() {
		return true;
	}
	
	public static boolean isControlPressed() {
		return GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
	}
}
