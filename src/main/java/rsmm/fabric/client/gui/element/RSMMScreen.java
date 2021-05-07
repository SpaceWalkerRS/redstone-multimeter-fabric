package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;

public abstract class RSMMScreen extends Screen implements IParentElement {
	
	public final MultimeterClient multimeterClient;
	private final List<IElement> content;
	
	private IElement focused;
	private boolean dragging;
	
	protected RSMMScreen(MultimeterClient multimeterClient) {
		super(new LiteralText(""));
		
		this.multimeterClient = multimeterClient;
		this.content = new ArrayList<>();
	}
	
	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		mouseMove(mouseX, mouseY);
	}
	
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		return mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		return mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return mouseScroll(mouseX, mouseY, amount);
	}
	
	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return keyPress(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean charTyped(char chr, int modifiers) {
		return typeChar(chr, modifiers);
	}

	@Override
	public boolean isDraggingMouse() {
		return dragging;
	}
	
	@Override
	public void setDraggingMouse(boolean dragging) {
		this.dragging = dragging;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		renderContent(mouseX, mouseY, delta);
		
		List<List<Text>> tooltip = getTooltip(mouseX, mouseY);
		
		if (!tooltip.isEmpty()) {
			drawTooltip(tooltip, mouseX, mouseY + 15);
		}
	}
	
	@Override
	protected final void init() {
		clearChildren();
		initScreen();
	}
	
	protected abstract void initScreen();
	
	@Override
	public void tick() {
		IParentElement.super.tick();
	}
	
	@Override
	public void removed() {
		onRemoved();
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (IParentElement.super.keyPress(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (shouldCloseOnEsc() && (keyCode == GLFW.GLFW_KEY_ESCAPE)) {
			onClose();
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<IElement> getChildren() {
		return content;
	}
	
	@Override
	public IElement getFocusedElement() {
		return focused;
	}
	
	@Override
	public void setFocusedElement(IElement element) {
		IElement focused = getFocusedElement();
		
		if (element == focused) {
			return;
		}
		
		if (focused != null) {
			focused.unfocus();
		}
		
		this.focused = element;
		
		if (element != null) {
			element.focus();
		}
	}
	
	@Override
	public final int getX() {
		return 0;
	}
	
	@Override
	public final void setX(int x) {
		
	}
	
	@Override
	public final int getY() {
		return 0;
	}
	
	@Override
	public final void setY(int y) {
		
	}
	
	@Override
	public final int getWidth() {
		return width;
	}
	
	@Override
	public final void setWidth(int width) {
		
	}
	
	@Override
	public final int getHeight() {
		return height;
	}
	
	@Override
	public final void setHeight(int height) {
		
	}
	
	protected void addContent(IElement element) {
		content.add(element);
	}
	
	protected void renderContent(int mouseX, int mouseY, float delta) {
		IParentElement.super.render(mouseX, mouseY, delta);
	}
	
	public void drawTooltip(List<List<Text>> lines, int x, int y) {
		if (lines.isEmpty()) {
			return;
		}
		
		int width = 0;
		
		for (List<Text> line : lines) {
			int lineWidth = 0;
			
			for (Text text : line) {
				lineWidth += font.getStringWidth(text.asFormattedString());
			}
			
			if (lineWidth > width) {
				width = lineWidth;
			}
		}
		
		int left = x + 12;
		int top = y - 12;
		
		int height = 8;
		
		if (lines.size() > 1) {
			height += 2 + 10 * (lines.size() - 1);
		}
		
		if (left + width > getX() + getWidth()) {
			left -= (28 + width);
		}
		if (top + height + 6 > getY() + getHeight()) {
			top = (getY() + getHeight()) - height - 6;
		}
		
		int backgroundColor  = 0xF0100010;
		int borderColorStart = 0x505000FF;
		int borderColorEnd   = 0x5028007F;
		
		blitOffset = 300;
        itemRenderer.zOffset = 300.0F;
		
		GlStateManager.disableRescaleNormal();
        DiffuseLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
		
		fillGradient(left - 3        , top - 4         , left + width + 3, top - 3         , backgroundColor, backgroundColor);
		fillGradient(left - 3        , top + height + 3, left + width + 3, top + height + 4, backgroundColor, backgroundColor);
		fillGradient(left - 3        , top - 3         , left + width + 3, top + height + 3, backgroundColor, backgroundColor);
		fillGradient(left - 4        , top - 3         , left - 3        , top + height + 3, backgroundColor, backgroundColor);
		fillGradient(left + width + 3, top - 3         , left + width + 4, top + height + 3, backgroundColor, backgroundColor);
		fillGradient(left - 3        , top - 2         , left - 2        , top + height + 2, borderColorStart, borderColorEnd);
		fillGradient(left + width + 2, top - 2         , left + width + 3, top + height + 2, borderColorStart, borderColorEnd);
		fillGradient(left - 3        , top - 3         , left + width + 3, top - 2         , borderColorStart, borderColorStart);
		fillGradient(left - 3        , top + height + 2, left + width + 3, top + height + 3, borderColorEnd, borderColorEnd);
		
		int textX;
		int textY = top;
		
		for (List<Text> line : lines) {
			textX = left;
			
			for (Text text : line) {
				String string = text.asFormattedString();
				font.drawWithShadow(string, textX, textY, 0xFFFFFF);
				
				textX += font.getStringWidth(string);
			}
			
			textY += 10;
		}
		
		GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        DiffuseLighting.enable();
        GlStateManager.enableRescaleNormal();
		
		blitOffset = 0;
        itemRenderer.zOffset = 0.0F;
	}
}
