package rsmm.fabric.client.gui.element;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import rsmm.fabric.client.gui.MultimeterHudRenderer;

public class HudElement implements IElement {
	
	private final MultimeterHudRenderer hudRenderer;
	
	private int x;
	private int y;
	private int width;
	
	private int selectedRow;
	private int selectedNameColumn;
	private int selectedTickColumn;
	private int selectedSubTickColumn;
	
	public HudElement(MultimeterHudRenderer hudRenderer, int x, int y, int width) {
		this.hudRenderer = hudRenderer;
		
		this.x = x;
		this.y = y;
		this.width = width;
		
		this.selectedRow = -1;
		this.selectedNameColumn = -1;
		this.selectedTickColumn = -1;
		this.selectedSubTickColumn = -1;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hudRenderer.render(matrices, x, y);
		hudRenderer.updateHoveredElement(x, y, mouseX, mouseY);
		hudRenderer.renderSelectionIndicators(matrices, x, y, selectedRow, selectedNameColumn, selectedTickColumn, selectedSubTickColumn);
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		selectedRow = hudRenderer.getHoveredRow();
		selectedNameColumn = hudRenderer.getHoveredNameColumn();
		selectedTickColumn = hudRenderer.getHoveredTickColumn();
		selectedSubTickColumn = hudRenderer.getHoveredSubTickColumn();
		
		return selectedRow >= 0 && (selectedNameColumn >= 0 || selectedTickColumn >= 0 || selectedSubTickColumn >= 0);
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		return false;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double amount) {
		return false;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
	}
	
	@Override
	public boolean isDragging() {
		return false;
	}
	
	@Override
	public void setDragging(boolean dragging) {
		
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public void unfocus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return hudRenderer.getHeight();
	}
	
	@Override
	public void setHeight(int height) {
		
	}
	
	@Override
	public List<Text> getTooltip(double mouseX, double mouseY) {
		return hudRenderer.getTextForTooltip();
	}
}
