package rsmm.fabric.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.hud.event.MeterEventRenderDispatcher;
import rsmm.fabric.client.gui.widget.TransparentButton;
import rsmm.fabric.client.option.Options;
import rsmm.fabric.common.Meter;

public class MultimeterHud extends AbstractParentElement implements HudRenderer {
	
	public final MultimeterClient client;
	public final TextRenderer font;
	public final HudSettings settings;
	public final MeterEventRenderDispatcher eventRenderers;
	public final List<Meter> meters;
	
	public final MeterListRenderer names;
	public final PrimaryEventViewer ticks;
	public final SecondaryEventViewer subticks;
	
	private final TransparentButton playPauseButton;
	private final TransparentButton fastBackwardButton;
	private final TransparentButton fastForwardButton;
	
	private boolean paused;
	/** The offset between the last server tick and the first tick to be displayed in the ticks table */
	private int offset;
	private boolean onScreen;
	private Meter selectedMeter;
	
	public MultimeterHud(MultimeterClient client) {
		super(0, 0, 0, 0);
		
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.settings = new HudSettings(this);
		this.eventRenderers = new MeterEventRenderDispatcher(this);
		this.meters = new ArrayList<>();
		
		this.names = new MeterListRenderer(this);
		this.ticks = new PrimaryEventViewer(this);
		this.subticks = new SecondaryEventViewer(this);
		
		this.playPauseButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(!onScreen ^ paused ? "\u23f5" : "\u23f8"), button -> {
			pause();
			return true;
		});
		this.fastBackwardButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ed" : "\u23e9"), button -> {
			stepBackward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		this.fastForwardButton = new TransparentButton(client, 0, 0, 9, 9, () -> new LiteralText(Screen.hasControlDown() ? "\u23ee" : "\u23ea"), button -> {
			stepForward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		
		if (!Options.HUD.PAUSE_INDICATOR.get()) {
			this.playPauseButton.setVisible(false);
		}
		this.fastBackwardButton.setVisible(false);
		this.fastForwardButton.setVisible(false);
		
		addChild(this.names);
		addChild(this.ticks);
		addChild(this.subticks);
		addChild(this.playPauseButton);
		addChild(this.fastBackwardButton);
		addChild(this.fastForwardButton);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (meters.isEmpty()) {
			return;
		}
		
		matrices.push();
		matrices.translate(0, 0, 100);
		
		super.render(matrices, mouseX, mouseY, delta);
		drawMeterGroup(matrices);
		
		matrices.pop();
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success) {
			selectMeter(null);
		}
		
		return success;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		
		onScreen = false;
		
		settings.forceFullOpacity = false;
		settings.ignoreHiddenMeters = true;
		
		if (!Options.HUD.PAUSE_INDICATOR.get()) {
			playPauseButton.setVisible(false);
		}
		playPauseButton.update();
		fastBackwardButton.setVisible(false);
		fastForwardButton.setVisible(false);
		
		setX(0);
		setY(0);
		
		updateMeterList();
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	protected void onChangedX(int x) {
		int w;
		
		switch (getPos()) {
		default:
		case TOP_LEFT:
			names.setX(x);
			
			x += names.getWidth();
			ticks.setX(x);
			
			x += ticks.getWidth();
			w = playPauseButton.getWidth();
			playPauseButton.setX(x - 2 * w);
			fastBackwardButton.setX(x - w);
			fastForwardButton.setX(x - 3 * w);
			
			x += settings.columnWidth + settings.gridSize;
			subticks.setX(x);
			
			break;
		case TOP_RIGHT:
			x += (getWidth() - names.getWidth());
			names.setX(x);
			
			w = playPauseButton.getWidth();
			playPauseButton.setX(x - 2 * w);
			fastBackwardButton.setX(x - w);
			fastForwardButton.setX(x - 3 * w);
			
			x -= ticks.getWidth();
			ticks.setX(x);
			
			x -= (settings.columnWidth + settings.gridSize + subticks.getWidth());
			subticks.setX(x);
			
			break;
		}
	}
	
	@Override
	protected void onChangedY(int y) {
		names.setY(y);
		ticks.setY(y);
		subticks.setY(y);
		
		y += ticks.getHeight() + 1;
		playPauseButton.setY(y);
		fastBackwardButton.setY(y);
		fastForwardButton.setY(y);
	}
	
	public void render(MatrixStack matrices) {
		render(matrices, -1, -1, 0);
	}
	
	private void drawMeterGroup(MatrixStack matrices) {
		String text = client.getMeterGroup().getName();
		
		if (paused && !Options.HUD.HIDE_HIGHLIGHT.get() && !Options.HUD.PAUSE_INDICATOR.get()) {
			text += " (Paused)";
		}
		
		int nameX = getMeterGroupNameX(text);
		int nameY = getY() + settings.gridSize + getTableHeight();
		int color = onScreen ? 0xD0D0D0 : 0x202020;
		
		drawText(this, matrices, new LiteralText(text), nameX, nameY, color);
	}
	
	private int getMeterGroupNameX(String name) {
		switch (getPos()) {
		default:
		case TOP_LEFT:
			return getX() + settings.gridSize;
		case TOP_RIGHT:
			return (getX() + getWidth()) - (font.getWidth(name) + settings.gridSize);
		}
	}
	
	public ScreenPos getPos() {
		return onScreen ? ScreenPos.TOP_LEFT : Options.HUD.SCREEN_POS.get();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		if (meters.isEmpty()) {
			return;
		}
		
		paused = !paused;
		
		playPauseButton.update();
		fastBackwardButton.setActive(paused);
		fastForwardButton.setActive(paused);
		
		if (paused) {
			updateEventViewersWidth();
		} else {
			resetOffset();
		}
	}
	
	private void setOffset(int offset) {
		this.offset = offset;
		updateEventViewersWidth();
	}
	
	public void resetOffset() {
		setOffset(1 - Options.HUD.COLUMN_COUNT.get());
	}
	
	public void stepForward(int amount) {
		if (paused) {
			setOffset(offset - amount);
		}
	}
	
	public void stepBackward(int amount) {
		if (paused) {
			setOffset(offset + amount);
		}
	}
	
	public boolean isOnScreen() {
		return onScreen;
	}
	
	public Meter getSelectedMeter() {
		return selectedMeter;
	}
	
	public boolean selectMeter(int row) {
		if (row >= 0 && row < meters.size()) {
			return selectMeter(meters.get(row));
		}
		
		return selectMeter(null);
	}
	
	private boolean selectMeter(Meter meter) {
		if (meter == selectedMeter) {
			return false;
		}
		
		selectedMeter = meter;
		
		if (onScreen) {
			client.getScreen().update();
		}
		
		return true;
	}
	
	public int getHoveredRow(double mouseY) {
		return (int)((mouseY - getY()) / (settings.rowHeight + settings.gridSize));
	}
	
	public int getSelectedRow() {
		return selectedMeter == null ? -1 : meters.indexOf(selectedMeter);
	}
	
	public long getSelectedTick() {
		return client.getLastServerTick() + offset + Options.HUD.SELECTED_COLUMN.get();
	}
	
	public int getTableHeight() {
		return getHeight() - (settings.rowHeight + settings.gridSize);
	}
	
	public void updateDimensions() {
		updateWidth();
		updateHeight();
		
		if (onScreen) {
			client.getScreen().update();
		}
	}
	
	public void updateWidth() {
		setWidth(client.getMinecraftClient().getWindow().getScaledWidth());
		names.updateWidth();
		ticks.updateWidth();
		subticks.updateWidth();
		onChangedX(getX());
	}
	
	public void updateMeterListWidth() {
		names.updateWidth();
		onChangedX(getX());
	}
	
	public void updateEventViewersWidth() {
		ticks.updateWidth();
		subticks.updateWidth();
		onChangedX(getX());
	}
	
	public void updateHeight() {
		setHeight((meters.size() + 1) * (settings.rowHeight + settings.gridSize) + settings.gridSize);
		names.updateHeight();
		ticks.updateHeight();
		subticks.updateHeight();
		onChangedY(getY());
	}
	
	public void updateMeterList() {
		meters.clear();
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			if (!settings.ignoreHiddenMeters || !meter.isHidden()) {
				meters.add(meter);
			}
		}
		
		updateDimensions();
		
		if (selectedMeter != null && !client.getMeterGroup().hasMeter(selectedMeter)) {
			selectMeter(null);
		}
	}
	
	public void reset() {
		paused = false;
		meters.clear();
		resetOffset();
		setX(0);
		setY(0);
	}
	
	public void onServerTick() {
		if (paused) {
			offset--;
		}
	}
	
	public void onInitScreen() {
		if (!client.hasMultimeterScreenOpen()) {
			return;
		}
		
		onScreen = true;
		
		settings.forceFullOpacity = true;
		settings.ignoreHiddenMeters = false;
		
		playPauseButton.setVisible(true);
		playPauseButton.update();
		fastBackwardButton.setVisible(true);
		fastForwardButton.setVisible(true);
		
		updateMeterList();
		
		if (!meters.isEmpty() && !paused && Options.HUD.AUTO_PAUSE.get()) {
			pause();
		}
	}
	
	public void onOptionsChanged() {
		playPauseButton.setVisible(onScreen || Options.HUD.PAUSE_INDICATOR.get());
		updateDimensions();
	}
}
