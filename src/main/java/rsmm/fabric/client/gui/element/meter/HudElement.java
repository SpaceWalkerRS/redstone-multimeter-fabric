package rsmm.fabric.client.gui.element.meter;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.HudSettings;
import rsmm.fabric.client.gui.MultimeterHudRenderer;
import rsmm.fabric.client.gui.Selector;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.gui.widget.TransparentButton;

public class HudElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final Selector selector;
	private final MultimeterHudRenderer hudRenderer;
	
	private TransparentButton playPauseButton;
	private TransparentButton fastBackwardButton;
	private TransparentButton fastForwardButton;
	
	private boolean draggingTicksTable;
	private double mouseDragDeltaX;
	
	public HudElement(MultimeterClient client, Selector selector, int x, int y, int width) {
		super(x, y, width, 0);
		
		this.client = client;
		this.selector = selector;
		this.hudRenderer = client.getHudRenderer();
		
		this.playPauseButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(hudRenderer.isPaused() ? "\u23f5" : "\u23f8"), (button) -> {
			hudRenderer.pause();
			return true;
		});
		this.fastBackwardButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ed" : "\u23e9"), (button) -> {
			hudRenderer.stepBackward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		this.fastForwardButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ee" : "\u23ea"), (button) -> {
			hudRenderer.stepForward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		
		addChild(playPauseButton);
		addChild(fastBackwardButton);
		addChild(fastForwardButton);
		
		updateControlsX();
		updateControlsY();
		
		hudRenderer.ignoreHiddenMeters(false);
		hudRenderer.forceFullOpacity(true);
		
		if (this.client.getMeterGroup().hasMeters() && !hudRenderer.isPaused()) {
			hudRenderer.pause();
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = getX();
		int y = getY();
		
		hudRenderer.render(matrices, x, y);
		hudRenderer.updateHoveredElements(x, y, mouseX, mouseY);
		hudRenderer.renderSelectedMeterIndicator(matrices, x, y, selector.get());
		
		super.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			int hoveredTickColumn = hudRenderer.getHoveredTickColumn();
			
			if (hoveredTickColumn >= 0) {
				draggingTicksTable = true;
				success = true;
			} else {
				int hoveredRow = hudRenderer.getHoveredRow();
				int hoveredNameColumn = hudRenderer.getHoveredNameColumn();
				
				if (hoveredNameColumn >= 0) {
					success = selector.select(hudRenderer.getIdAtRow(hoveredRow));
					Button.playClickSound(client);
				} else if (mouseY < (getY() + hudRenderer.getTotalHeight())) {
					success = selector.select(-1);
				}
			}
		}
		
		return success;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean success = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			draggingTicksTable = false;
			mouseDragDeltaX = 0.0D;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean dragged = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (isDraggingMouse() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (draggingTicksTable) {
				mouseDragDeltaX += deltaX;
				int scrollAmount = (int)Math.round(mouseDragDeltaX / (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE));
				mouseDragDeltaX -= scrollAmount * (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE);
				
				hudRenderer.stepForward(scrollAmount);
				
				dragged = true;
			}
		}
		
		return dragged;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		
		hudRenderer.ignoreHiddenMeters(true);
		hudRenderer.forceFullOpacity(false);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public void onChangedX(int x) {
		updateControlsX();
	}
	
	@Override
	public void onChangedY(int y) {
		updateControlsY();
	}
	
	@Override
	public int getHeight() {
		return hudRenderer.getTotalHeight();
	}
	
	@Override
	public List<Text> getTooltip(double mouseX, double mouseY) {
		List<Text> tooltip = super.getTooltip(mouseX, mouseY);
		
		if (tooltip.isEmpty() && !draggingTicksTable) {
			tooltip = hudRenderer.getTextForTooltip();
		}
		
		return tooltip;
	}
	
	@Override
	public void update() {
		hudRenderer.updateRowCount();
		
		super.update();
		
		updateControlsX();
		updateControlsY();
		
		boolean paused = hudRenderer.isPaused();
		
		fastForwardButton.active = paused;
		fastBackwardButton.active = paused;
	}
	
	private void updateControlsX() {
		int x = getX() + hudRenderer.getNamesWidth() + HudSettings.ticksOverviewWidth();
		
		x -= fastBackwardButton.getWidth();
		fastBackwardButton.setX(x);
		
		x -= playPauseButton.getWidth();
		playPauseButton.setX(x);
		
		x -= fastForwardButton.getWidth();
		fastForwardButton.setX(x);
	}
	
	private void updateControlsY() {
		int y = getY() + hudRenderer.getTableHeight() + 2;
		
		playPauseButton.setY(y);
		fastForwardButton.setY(y);
		fastBackwardButton.setY(y);
	}
}
