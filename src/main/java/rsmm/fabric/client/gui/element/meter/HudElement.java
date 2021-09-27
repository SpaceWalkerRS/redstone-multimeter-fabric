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
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.gui.widget.TransparentButton;
import rsmm.fabric.client.listeners.HudListener;
import rsmm.fabric.client.listeners.MeterGroupListener;
import rsmm.fabric.client.listeners.MeterListener;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;

public class HudElement extends AbstractParentElement implements HudListener, MeterListener, MeterGroupListener {
	
	private final MultimeterClient client;
	private final MultimeterHudRenderer hudRenderer;
	
	private int x;
	private int y;
	private int width;
	
	private MeterControlsElement meterControls;
	private TransparentButton playPauseButton;
	private TransparentButton fastBackwardButton;
	private TransparentButton fastForwardButton;
	
	private boolean draggingTicksTable;
	private double mouseDragDeltaX;
	
	public HudElement(MultimeterClient client, int x, int y, int width) {
		this.client = client;
		this.hudRenderer = client.getHudRenderer();
		
		this.x = x;
		this.y = y;
		this.width = width;
		
		hudRenderer.ignoreHiddenMeters(false);
		hudRenderer.forceFullOpacity(true);
		
		this.meterControls = new MeterControlsElement(this.client, x, y + hudRenderer.getTotalHeight(), width);
		this.playPauseButton = new TransparentButton(client, x, y, 9, 9, () -> new LiteralText(hudRenderer.isPaused() ? "\u23f5" : "\u23f8"), (button) -> {
			hudRenderer.pause();
			return true;
		});
		this.fastBackwardButton = new TransparentButton(client, x, y, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ed" : "\u23e9"), (button) -> {
			hudRenderer.stepBackward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				updateMessage();
			}
		};
		this.fastForwardButton = new TransparentButton(client, x, y, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ee" : "\u23ea"), (button) -> {
			hudRenderer.stepForward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				updateMessage();
			}
		};
		
		addChild(meterControls);
		addChild(playPauseButton);
		addChild(fastBackwardButton);
		addChild(fastForwardButton);
		
		updateHudControlsX();
		
		hudRenderer.addListener(this);
		this.client.getMeterGroup().addMeterListener(this);
		this.client.getMeterGroup().addMeterGroupListener(this);
		
		if (this.client.getMeterGroup().hasMeters() && !hudRenderer.isPaused()) {
			hudRenderer.pause();
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hudRenderer.render(matrices, x, y);
		hudRenderer.updateHoveredElements(x, y, mouseX, mouseY);
		hudRenderer.renderSelectedMeterIndicator(matrices, x, y, meterControls.getSelectedMeterId());
		
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
					success = meterControls.selectMeter(hudRenderer.getIdAtRow(hoveredRow));
					Button.playClickSound(client);
				} else if (mouseY < (y + hudRenderer.getTotalHeight())) {
					success = meterControls.selectMeter(-1);
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
		
		hudRenderer.removeListener(this);
		client.getMeterGroup().removeMeterListener(this);
		client.getMeterGroup().removeMeterGroupListener(this);
		
		hudRenderer.ignoreHiddenMeters(true);
		hudRenderer.forceFullOpacity(false);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
		
		meterControls.setX(x);
		updateHudControlsX();
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
		updateControlsY();
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
		meterControls.setWidth(width);
	}
	
	@Override
	public int getHeight() {
		return hudRenderer.getTotalHeight() + meterControls.getHeight();
	}
	
	@Override
	public void setHeight(int height) {
		
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
	public void hudPaused() {
		playPauseButton.updateMessage();
		
		boolean paused = hudRenderer.isPaused();
		
		fastForwardButton.active = paused;
		fastBackwardButton.active = paused;
	}
	
	@Override
	public void meterGroupCleared(MeterGroup meterGroup) {
		updateHudControlsX();
		updateControlsY();
	}
	
	@Override
	public void meterAdded(MeterGroup meterGroup, long id) {
		updateHudControlsX();
		updateControlsY();
	}
	
	@Override
	public void meterRemoved(MeterGroup meterGroup, long id) {
		updateHudControlsX();
		updateControlsY();
	}
	
	@Override
	public void posChanged(Meter meter) {
		
	}
	
	@Override
	public void nameChanged(Meter meter) {
		updateHudControlsX();
	}
	
	@Override
	public void colorChanged(Meter meter) {
		
	}
	
	@Override
	public void movableChanged(Meter meter) {
		
	}
	
	@Override
	public void eventTypesChanged(Meter meter) {
		
	}
	
	@Override
	public void hiddenChanged(Meter meter) {
		
	}
	
	private void updateHudControlsX() {
		int x = this.x + hudRenderer.getNamesWidth() + HudSettings.ticksOverviewWidth();
		
		x -= fastBackwardButton.getWidth();
		fastBackwardButton.setX(x);
		
		x -= playPauseButton.getWidth();
		playPauseButton.setX(x);
		
		x -= fastForwardButton.getWidth();
		fastForwardButton.setX(x);
	}
	
	private void updateControlsY() {
		int y0 = y + hudRenderer.getTotalHeight();
		int y1 = y + hudRenderer.getTableHeight() + 2;
		
		meterControls.setY(y0);
		playPauseButton.setY(y1);
		fastForwardButton.setY(y1);
		fastBackwardButton.setY(y1);
	}
}
