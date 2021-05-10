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
import rsmm.fabric.client.gui.widget.InvisibleButton;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.listeners.HudChangeDispatcher;
import rsmm.fabric.common.listeners.HudListener;
import rsmm.fabric.common.listeners.MeterChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupListener;
import rsmm.fabric.common.listeners.MeterListener;

public class HudElement extends AbstractParentElement implements HudListener, MeterListener, MeterGroupListener {
	
	private final MultimeterClient client;
	private final MultimeterHudRenderer hudRenderer;
	
	private int x;
	private int y;
	private int width;
	
	private MeterControlsElement meterControls;
	private InvisibleButton playPauseButton;
	private InvisibleButton fastBackwardButton;
	private InvisibleButton fastForwardButton;
	
	private boolean draggingTicksTable;
	private double mouseDragDeltaX;
	private boolean mouseDragged;
	
	public HudElement(MultimeterClient client, int x, int y, int width) {
		this.client = client;
		this.hudRenderer = client.getHudRenderer();
		
		this.x = x;
		this.y = y;
		this.width = width;
		
		this.meterControls = new MeterControlsElement(this.client, x, y + hudRenderer.getTotalHeight(), width);
		this.playPauseButton = new InvisibleButton(client, x, y, 9, 9, () -> new LiteralText(hudRenderer.isPaused() ? "\u23f5" : "\u23f8"), (button) -> {
			client.getHudRenderer().pause();
			button.updateMessage();
		});
		this.fastBackwardButton = new InvisibleButton(client, x, y, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ed" : "\u23e9"), (button) -> {
			client.getHudRenderer().stepBackward(Screen.hasControlDown() ? 10 : 1);
		}) {
			
			@Override
			public void tick() {
				updateMessage();
			}
		};
		this.fastForwardButton = new InvisibleButton(client, x, y, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ee" : "\u23ea"), (button) -> {
			client.getHudRenderer().stepForward(Screen.hasControlDown() ? 10 : 1);
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
		
		HudChangeDispatcher.addListener(this);
		MeterChangeDispatcher.addListener(this);
		MeterGroupChangeDispatcher.addListener(this);
		
		if (!hudRenderer.isPaused()) {
			hudRenderer.pause();
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hudRenderer.render(matrices, x, y);
		hudRenderer.updateHoveredElements(x, y, mouseX, mouseY);
		hudRenderer.renderSelectedMeterIndicator(matrices, x, y, meterControls.getSelectedMeter());
		
		super.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseY >= y && mouseY <= (y + hudRenderer.getTableHeight())) {
			int hoveredTickColumn = hudRenderer.getHoveredTickColumn();
			
			if (hoveredTickColumn >= 0) {
				draggingTicksTable = true;
			}
			
			success = true;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean success = super.mouseRelease(mouseX, mouseY, button);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			draggingTicksTable = false;
			mouseDragDeltaX = 0.0D;
			
			if (!mouseDragged && mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + hudRenderer.getTotalHeight())) {
				int hoveredRow = hudRenderer.getHoveredRow();
				int hoveredNameColumn = hudRenderer.getHoveredNameColumn();
				
				if (hoveredRow >= 0 && hoveredNameColumn >= 0) {
					success |= meterControls.selectMeter(hoveredRow);
				} else if (!success) {
					meterControls.selectMeter(-1);
				}
			}
			
			mouseDragged = false;
		}
		
		return success;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean dragged = super.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (draggingTicksTable) {
				mouseDragDeltaX += deltaX;
				int scrollAmount = (int)Math.round(mouseDragDeltaX / (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE));
				mouseDragDeltaX -= scrollAmount * (HudSettings.COLUMN_WIDTH + HudSettings.GRID_SIZE);
				
				hudRenderer.stepForward(scrollAmount);
				
				dragged = true;
			}
			
			mouseDragged = true;
		}
		
		return dragged;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		
		HudChangeDispatcher.removeListener(this);
		MeterChangeDispatcher.removeListener(this);
		MeterGroupChangeDispatcher.removeListener(this);
		
		if (hudRenderer.isPaused()) {
			hudRenderer.pause();
		}
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
		
		int y0 = y + hudRenderer.getTotalHeight();
		int y1 = y + hudRenderer.getTableHeight() + 2;
		
		meterControls.setY(y0);
		playPauseButton.setY(y1);
		fastForwardButton.setY(y1);
		fastBackwardButton.setY(y1);
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
	public List<List<Text>> getTooltip(double mouseX, double mouseY) {
		List<List<Text>> tooltip = super.getTooltip(mouseX, mouseY);
		
		if (tooltip.isEmpty()) {
			tooltip = hudRenderer.getTextForTooltip();
		}
		
		return tooltip;
	}
	
	@Override
	public void paused() {
		playPauseButton.updateMessage();
		
		boolean paused = hudRenderer.isPaused();
		
		fastForwardButton.active = paused;
		fastBackwardButton.active = paused;
	}
	
	@Override
	public void cleared(MeterGroup meterGroup) {
		updateHudControlsX();
	}
	
	@Override
	public void meterAdded(MeterGroup meterGroup, int index) {
		updateHudControlsX();
	}
	
	@Override
	public void meterRemoved(MeterGroup meterGroup, int index) {
		updateHudControlsX();
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
	public void isMovableChanged(Meter meter) {
		
	}
	
	@Override
	public void meteredEventsChanged(Meter meter) {
		
	}
	
	private void updateHudControlsX() {
		int x = this.x + hudRenderer.getNamesWidth() + HudSettings.TICKS_TABLE_WIDTH;
		
		x -= fastBackwardButton.getWidth();
		fastBackwardButton.setX(x);
		
		x -= playPauseButton.getWidth();
		playPauseButton.setX(x);
		
		x -= fastForwardButton.getWidth();
		fastForwardButton.setX(x);
	}
}
