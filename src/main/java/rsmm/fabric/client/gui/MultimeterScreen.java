package rsmm.fabric.client.gui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.element.RSMMScreen;
import rsmm.fabric.client.gui.element.meter.HudElement;

public class MultimeterScreen extends RSMMScreen {
	
	// mouse scroll types
	private static final int DRAG = 1;
	private static final int PULL = 2;
	
	private static double lastScrollAmount;
	
	private final boolean isPauseScreen;
	
	private double scrollAmount = -1;
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
		client.keyboard.setRepeatEvents(false);
		super.onRemoved();
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	protected void initScreen() {
		client.keyboard.setRepeatEvents(true);
		
		scrollBarWidth = 6;
		scrollBarHeight = getHeight() - 8;
		scrollBarX = getWidth() - scrollBarWidth - 2;
		scrollBarY = 4;
		
		int width = scrollBarX - 2 - getX();
		
		addContent(new HudElement(multimeterClient, 0, 0, width));
		
		setScrollAmount(lastScrollAmount);
	}
	
	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}
	
	@Override
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!multimeterClient.getMeterGroup().hasMeters()) {
			String text = "Nothing to see here! Add a meter to get started.";
			
			int textWidth = textRenderer.getWidth(text);
			int textHeight = textRenderer.fontHeight;
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;
			
			textRenderer.drawWithShadow(matrices, text, x, y, 0xFFFFFF);
			
			return;
		}
		
		if (mouseScrollType == PULL) {
			int screenHeight = getHeight();
			int totalHeight = screenHeight + (int)getMaxScrollAmount();
			
			int middle = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight() / 2) / totalHeight;
			int margin = 5;
			
			if (mouseY < (middle - margin)) {
				setScrollAmount(scrollAmount - scrollSpeed);
			} else if (mouseY > (middle + margin)) {
				setScrollAmount(scrollAmount + scrollSpeed);
			}
		}
		
		for (IElement element : getChildren()) {
			int y = element.getY();
			
			if ((y + element.getHeight()) >= getY() && y <= (getY() + getHeight())) {
				element.render(matrices, mouseX, mouseY, delta);
			}
		}
		
		if (getMaxScrollAmount() > 0.0D) {
			renderScrollBar(matrices);
		}
	}
	
	private double getMaxScrollAmount() {
		double amount = -getHeight();
		
		for (IElement element : getChildren()) {
			amount += element.getHeight();
		}
		if (amount < 0.0D) {
			amount = 0.0D;
		}
		
		return amount;
	}
	
	private void setScrollAmount(double amount) {
		double oldScrollAmount = scrollAmount;
		scrollAmount = amount;
		
		if (scrollAmount < 0.0D) {
			scrollAmount = 0.0D;
		}
		
		double maxAmount = getMaxScrollAmount();
		
		if (scrollAmount > maxAmount) {
			scrollAmount = maxAmount;
		}
		
		lastScrollAmount = scrollAmount;
		
		if (scrollAmount != oldScrollAmount) {
			int y = getY() - (int)scrollAmount;
			
			for (IElement element : getChildren()) {
				element.setY(y);
				y += element.getHeight();
			}
		}
	}
	
	private int getScrollType(double mouseX, double mouseY) {
		int left = scrollBarX;
		int right = scrollBarX + scrollBarWidth;
		int top = scrollBarY;
		int bot = scrollBarY + scrollBarHeight;
		
		if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bot) {
			int screenHeight = getHeight();
			int totalHeight = screenHeight + (int)getMaxScrollAmount();
			
			int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
			int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
			
			if (mouseY >= barTop && mouseY <= barBot) {
				return DRAG;
			}
			
			return PULL;
		}
		
		return 0;
	}
	
	private void renderScrollBar(MatrixStack matrices) {
		RenderSystem.disableTexture();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		int screenHeight = getHeight();
		int totalHeight = screenHeight + (int)getMaxScrollAmount();
		
		int bgLeft = scrollBarX;
		int bgRight = scrollBarX + scrollBarWidth;
		int bgTop = scrollBarY;
		int bgBot = scrollBarY + scrollBarHeight;
		
		int barLeft = bgLeft;
		int barRight = bgRight;
		int barTop = scrollBarY + scrollBarHeight * (int)scrollAmount / totalHeight;
		int barBot = scrollBarY + scrollBarHeight * ((int)scrollAmount + getHeight()) / totalHeight;
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		bufferBuilder.vertex(bgLeft, bgBot, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgRight, bgBot, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgRight, bgTop, 0.0D).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(bgLeft, bgTop, 0.0D).color(0, 0, 0, 255).next();
		
		bufferBuilder.vertex(barLeft, barBot, 0.0D).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(barRight, barBot, 0.0D).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(barRight, barTop, 0.0D).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(barLeft, barTop, 0.0D).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(barLeft, barBot - 1, 0.0D).color(192, 192, 192, 255).next();
		bufferBuilder.vertex(barRight - 1, barBot - 1, 0.0D).color(192, 192, 192, 255).next();
		bufferBuilder.vertex(barRight - 1, barTop, 0.0D).color(192, 192, 192, 255).next();
		bufferBuilder.vertex(barLeft, barTop, 0.0D).color(192, 192, 192, 255).next();
		
		tessellator.draw();
		
		RenderSystem.enableTexture();
	}
}
