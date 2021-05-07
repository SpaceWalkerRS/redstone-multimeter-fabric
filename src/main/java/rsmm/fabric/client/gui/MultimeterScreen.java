package rsmm.fabric.client.gui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.element.RSMMScreen;
import rsmm.fabric.client.gui.element.meter.HudElement;

public class MultimeterScreen extends RSMMScreen {
	
	// mouse scroll types
	private static final int DRAG = 1;
	private static final int PULL = 2;
	
	private boolean isPauseScreen;
	
	private double scrollAmount;
	private int scrollSpeed = 7;
	private int mouseScrollType;
	
	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarWidth;
	private int scrollBarHeight;
	
	public MultimeterScreen(MultimeterClient client) {
		super(client);
		
		this.isPauseScreen = !Screen.hasShiftDown();
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			mouseScrollType = getScrollType(mouseX, mouseY);
			
			if (mouseScrollType != 0) {
				success = true;
			}
		}
		
		if (!success) {
			success = multimeterClient.getInputHandler().mouseClick(mouseX, mouseY, button);
		}
		
		return true;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean released = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			mouseScrollType = 0;
		}
		
		return released;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean success = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (!success && (mouseScrollType == DRAG)) {
			double scroll = deltaY * (getMaxScrollAmount() + getHeight()) / scrollBarHeight;
			setScrollAmount(scrollAmount + scroll);
		}
		
		return success;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
		if (mouseScrollType != 0) {
			return false;
		}
		
		setScrollAmount(scrollAmount - scrollSpeed * amount);
		
		return true;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (!super.keyPress(keyCode, scanCode, modifiers)) {
			return multimeterClient.getInputHandler().keyPress(keyCode, scanCode, modifiers);
		}
		
		return true;
	}
	
	@Override
	public void onRemoved() {
		minecraft.keyboard.enableRepeatEvents(false);
		super.onRemoved();
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	protected void initScreen() {
		minecraft.keyboard.enableRepeatEvents(true);
		
		scrollBarWidth = 6;
		scrollBarHeight = getHeight() - 8;
		scrollBarX = getWidth() - scrollBarWidth - 2;
		scrollBarY = 4;
		
		int width = scrollBarX - 2 - getX();
		
		addContent(new HudElement(multimeterClient, 0, 0, width));
	}
	
	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}
	
	@Override
	protected void renderContent(int mouseX, int mouseY, float delta) {
		if (multimeterClient.getMeterGroup().getMeterCount() <= 0) {
			String text = "Nothing to see here! Add a meter to get started.";
			
			int textWidth = font.getStringWidth(text);
			int textHeight = font.fontHeight;
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;
			
			font.drawWithShadow(text, x, y, 0xFFFFFF);
			
			return;
		}
		
		int y = getY() - (int)(scrollAmount);
		
		for (IElement element : getChildren()) {
			element.setY(y);
			
			if ((y + element.getHeight()) >= getY() && y <= (getY() + getHeight())) {
				element.render(mouseX, mouseY, delta);
			}
			
			y += element.getHeight();
		}
		
		if (mouseScrollType == PULL) {
			int maxScroll = (int)getMaxScrollAmount();
			
			int screenHeight = getHeight();
			int totalHeight = maxScroll + screenHeight;
			
			int middle = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight() / 2) / totalHeight;
			int margin = 5;
			
			if (mouseY < (middle - margin)) {
				setScrollAmount(scrollAmount - scrollSpeed);
			} else if (mouseY > (middle + margin)) {
				setScrollAmount(scrollAmount + scrollSpeed);
			}
		}
		
		renderScrollBar();
	}
	
	private double getMaxScrollAmount() {
		double amount = -getHeight();
		
		for (IElement element : getChildren()) {
			amount += element.getHeight();
		}
		if (amount < 0) {
			amount = 0;
		}
		
		return amount;
	}
	
	private void setScrollAmount(double amount) {
		scrollAmount = amount;
		
		if (scrollAmount < 0) {
			scrollAmount = 0;
		}
		
		double maxAmount = getMaxScrollAmount();
		
		if (scrollAmount > maxAmount) {
			scrollAmount = maxAmount;
		}
	}
	
	private int getScrollType(double mouseX, double mouseY) {
		int left = scrollBarX;
		int right = scrollBarX + scrollBarWidth;
		int top = scrollBarY;
		int bot = scrollBarY + scrollBarHeight;
		
		if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bot) {
			int maxScroll = (int)getMaxScrollAmount();
			
			int screenHeight = getHeight();
			int totalHeight = maxScroll + screenHeight;
			
			int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
			int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
			
			if (mouseY >= barTop && mouseY <= barBot) {
				return DRAG;
			}
			
			return PULL;
		}
		
		return 0;
	}
	
	private void renderScrollBar() {
		int maxScroll = (int)getMaxScrollAmount();
		
		if (maxScroll > 0) {
			GlStateManager.disableDepthTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ZERO,
				GlStateManager.DestFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.disableTexture();
			
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			
			int screenHeight = getHeight();
			int totalHeight = maxScroll + screenHeight;
			
			int bgLeft = scrollBarX;
			int bgRight = scrollBarX + scrollBarWidth;
			int bgTop = scrollBarY;
			int bgBot = scrollBarY + scrollBarHeight;
			
			int barLeft = bgLeft;
			int barRight = bgRight;
			int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
			int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
			
			bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			
			bufferBuilder.vertex(bgLeft, bgBot, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(bgRight, bgBot, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(bgRight, bgTop, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(bgLeft, bgTop, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			
			bufferBuilder.vertex(barLeft, barBot, 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(barRight, barBot, 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(barRight, barTop, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(barLeft, barTop, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(barLeft, barBot - 1, 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(barRight - 1, barBot - 1, 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(barRight - 1, barTop, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(barLeft, barTop, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
			
			tessellator.draw();
			
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}
}
