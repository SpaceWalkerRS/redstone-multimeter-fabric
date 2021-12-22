package redstone.multimeter.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.class_4117;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.IElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.hud.element.MeterListRenderer;
import redstone.multimeter.client.gui.hud.element.PrimaryEventViewer;
import redstone.multimeter.client.gui.hud.element.SecondaryEventViewer;
import redstone.multimeter.client.gui.hud.event.MeterEventRenderDispatcher;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.util.ColorUtils;

public class MultimeterHud extends AbstractParentElement {
	
	public final MultimeterClient client;
	public final TextRenderer font;
	public final HudSettings settings;
	public final HudRenderer renderer;
	public final MeterEventRenderDispatcher eventRenderers;
	public final List<Meter> meters;
	
	private MeterListRenderer names;
	private PrimaryEventViewer ticks;
	private SecondaryEventViewer subticks;
	private TextElement meterGroupName;
	
	private TransparentButton playPauseButton;
	private TransparentButton fastBackwardButton;
	private TransparentButton fastForwardButton;
	private TextElement printIndicator;
	
	private int hudX;
	private int hudY;
	private int hudWidth;
	private int hudHeight;
	
	private boolean paused;
	/** The offset between the last server tick and the first tick to be displayed in the ticks table */
	private int offset;
	private boolean onScreen;
	private Meter selectedMeter;
	
	public MultimeterHud(MultimeterClient client) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.settings = new HudSettings(this);
		this.renderer = new HudRenderer(this);
		this.eventRenderers = new MeterEventRenderDispatcher(this);
		this.meters = new ArrayList<>();
	}
	
	@Override
	public void render(int mouseX, int mouseY) {
		if (!hasContent()) {
			return;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(0, 0, 100);
		
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement child = children.get(index);
			
			if (child.isVisible()) {
				renderer.render(child, mouseX, mouseY);
			}
		}
		
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (!consumed) {
			selectMeter(null);
		}
		
		return consumed;
	}
	
	@Override
	public boolean isHovered(double mouseX, double mouseY) {
		int minX = Math.max(getX(), hudX);
		int maxX = Math.min(getX() + getAvailableWidth(), hudX + hudWidth);
		int minY = Math.max(getY(), hudY);
		int maxY = Math.max(getY() + getAvailableHeight(), hudY + hudHeight);
		
		return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		
		onScreen = false;
		
		settings.forceFullOpacity = false;
		settings.ignoreHiddenMeters = true;
		
		meterGroupName.update();
		
		if (!Options.HUD.PAUSE_INDICATOR.get()) {
			playPauseButton.setVisible(false);
		}
		playPauseButton.update();
		fastBackwardButton.setVisible(false);
		fastForwardButton.setVisible(false);
		
		setX(0);
		setY(0);
		resetSize();
		
		updateMeterList();
		
		if (paused && Options.HUD.AUTO_UNPAUSE.get()) {
			pause();
		}
	}
	
	@Override
	public int getWidth() {
		return hudWidth;
	}
	
	@Override
	public int getHeight() {
		return hudHeight;
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	protected void onChangedX(int x) {
		int effectiveWidth = names.getWidth() + ticks.getWidth();
		int range = getAvailableWidth() - effectiveWidth;
		float rawPos = getScreenPosX();
		int pos = Math.round(range * rawPos);
		int w;
		
		switch (getDirectionalityX()) {
		default:
		case LEFT_TO_RIGHT:
			hudX = x + pos;
			
			x = hudX;
			names.setX(x);
			meterGroupName.setX(x + settings.gridSize + 1);
			
			x += names.getWidth();
			ticks.setX(x);
			
			x += ticks.getWidth();
			w = playPauseButton.getWidth();
			playPauseButton.setX(x - 2 * w);
			fastBackwardButton.setX(x - 3 * w);
			fastForwardButton.setX(x - w);
			printIndicator.setX(x - 4 * w);
			
			x += settings.columnWidth + settings.gridSize;
			subticks.setX(x);
			
			break;
		case RIGHT_TO_LEFT:
			hudX = x + pos + effectiveWidth - getWidth();
			
			x = hudX + (getWidth() - names.getWidth());
			names.setX(x);
			meterGroupName.setX(x + names.getWidth() - (meterGroupName.getWidth() + settings.gridSize + 1));
			
			x -= ticks.getWidth();
			ticks.setX(x);
			
			w = playPauseButton.getWidth();
			playPauseButton.setX(x + w);
			fastBackwardButton.setX(x + 2 * w);
			fastForwardButton.setX(x);
			printIndicator.setX(x + 3 * w);
			
			x -= (settings.columnWidth + settings.gridSize + subticks.getWidth());
			subticks.setX(x);
			
			break;
		}
	}
	
	@Override
	protected void onChangedY(int y) {
		int range = getAvailableHeight() - getHeight();
		float position = getScreenPosY();
		hudY = y + Math.round(position * range);
		
		switch (getDirectionalityY()) {
		default:
		case TOP_TO_BOTTOM:
			y = hudY;
			names.setY(y);
			ticks.setY(y);
			subticks.setY(y);
			
			y += names.getHeight();
			meterGroupName.setY(y + settings.gridSize);
			
			y += 1;
			playPauseButton.setY(y);
			fastBackwardButton.setY(y);
			fastForwardButton.setY(y);
			
			break;
		case BOTTOM_TO_TOP:
			y = hudY + settings.rowHeight + settings.gridSize;
			names.setY(y);
			ticks.setY(y);
			subticks.setY(y);
			meterGroupName.setY(y - meterGroupName.getHeight());
			
			y -= playPauseButton.getHeight();
			playPauseButton.setY(y);
			fastBackwardButton.setY(y);
			fastForwardButton.setY(y);
			
			break;
		}
		
		y = fastBackwardButton.getMessageY();
		printIndicator.setY(y);
	}
	
	public void init() {
		this.names = new MeterListRenderer(this);
		this.ticks = new PrimaryEventViewer(this);
		this.subticks = new SecondaryEventViewer(this);
		this.meterGroupName = new TextElement(this.client, 0, 0, t -> {
			String text = this.client.getMeterGroup().getName();
			
			int a = Math.round(0xFF * settings.opacity() / 100.0F);
			int rgb = onScreen ? 0xD0D0D0 : 0x202020;
			int color = ColorUtils.setAlpha(rgb, a);
			
			if (paused && !Options.HUD.HIDE_HIGHLIGHT.get() && !Options.HUD.PAUSE_INDICATOR.get()) {
				text += " (Paused)";
			}
			
			t.add(text).setColor(color);
		});
		
		this.playPauseButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(!onScreen ^ paused ? "\u23f5" : "\u23f8"), () -> null, button -> {
			pause();
			return true;
		});
		this.fastBackwardButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(getStepSymbol(false, Screen.hasControlDown())), () -> null, button -> {
			stepBackward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		this.fastForwardButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(getStepSymbol(true, Screen.hasControlDown())), () -> null, button -> {
			stepForward(Screen.hasControlDown() ? 10 : 1);
			return true;
		}) {
			
			@Override
			public void tick() {
				update();
			}
		};
		this.printIndicator = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText("P").method_20170(Formatting.BOLD)).setWithShadow(true));
		
		if (!Options.HUD.PAUSE_INDICATOR.get()) {
			this.playPauseButton.setVisible(false);
		}
		this.fastBackwardButton.setVisible(false);
		this.fastForwardButton.setVisible(false);
		this.printIndicator.setVisible(false);
		
		addChild(this.names);
		addChild(this.ticks);
		addChild(this.subticks);
		addChild(this.meterGroupName);
		addChild(this.playPauseButton);
		addChild(this.fastBackwardButton);
		addChild(this.fastForwardButton);
		addChild(this.printIndicator);
		
		resetSize();
	}
	
	private String getStepSymbol(boolean forward, boolean fast) {
		boolean leftToRight = (getDirectionalityX() == Directionality.X.LEFT_TO_RIGHT);
		
		if (forward == leftToRight) {
			return fast ? "\u23ed" : "\u23e9";
		} else {
			return fast ? "\u23ee" : "\u23ea";
		}
	}
	
	public void render() {
		render(-1, -1);
	}
	
	public float getScreenPosX() {
		return Options.HUD.SCREEN_POS_X.get() / 100.0F;
	}
	
	public float getScreenPosY() {
		return Options.HUD.SCREEN_POS_Y.get() / 100.0F;
	}
	
	public Directionality.X getDirectionalityX() {
		return Options.HUD.DIRECTIONALITY_X.get();
	}
	
	public Directionality.Y getDirectionalityY() {
		return Options.HUD.DIRECTIONALITY_Y.get();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		if (!hasContent()) {
			return;
		}
		
		paused = !paused;
		
		meterGroupName.update();
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
	
	public void stepBackward(int amount) {
		if (paused) {
			setOffset(offset - amount);
		}
	}
	
	public void stepForward(int amount) {
		if (paused) {
			setOffset(offset + amount);
		}
	}
	
	public boolean hasContent() {
		return !meters.isEmpty();
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
	
	public boolean selectMeter(Meter meter) {
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
		int max = meters.size() - 1;
		int row = Math.min(max, (int)((mouseY - names.getY()) / (settings.rowHeight + settings.gridSize)));
		
		if (getDirectionalityY() == Directionality.Y.BOTTOM_TO_TOP) {
			row = max - row;
		}
		
		return row;
	}
	
	public int getSelectedRow() {
		return selectedMeter == null ? -1 : meters.indexOf(selectedMeter);
	}
	
	public long getSelectedTick() {
		return client.getPrevServerTime() + offset + Options.HUD.SELECTED_COLUMN.get();
	}
	
	public int getAvailableWidth() {
		return super.getWidth();
	}
	
	public int getAvailableHeight() {
		return onScreen ? hudHeight : super.getHeight();
	}
	
	public void resize(int width, int height) {
		super.setWidth(width);
		super.setHeight(height);
		
		onResized();
	}
	
	public void onResized() {
		updateWidth();
		updateHeight();
		
		if (onScreen) {
			client.getScreen().update();
		}
	}
	
	public void resetSize() {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		class_4117 window = minecraftClient.field_19944;
		int width = window.method_18321();
		int height = window.method_18322();
		
		resize(width, height);
	}
	
	public void updateWidth() {
		names.updateWidth();
		ticks.updateWidth();
		subticks.updateWidth();
		onWidthUpdated();
	}
	
	public void updateMeterListWidth() {
		names.updateWidth();
		onWidthUpdated();
	}
	
	public void updateEventViewersWidth() {
		ticks.updateWidth();
		subticks.updateWidth();
		onWidthUpdated();
	}
	
	private void onWidthUpdated() {
		hudWidth = names.getWidth() + ticks.getWidth();
		
		if (subticks.getWidth() > 0) {
			hudWidth += subticks.getWidth() + settings.columnWidth + settings.gridSize;
		}
		
		validateHudWidth();
		onChangedX(getX());
	}
	
	private void validateHudWidth() {
		hudWidth = Math.min(hudWidth, getAvailableWidth());
	}
	
	public void updateHeight() {
		names.updateHeight();
		ticks.updateHeight();
		subticks.updateHeight();
		onHeightUpdated();
	}
	
	private void onHeightUpdated() {
		hudHeight = names.getHeight() + settings.rowHeight + settings.gridSize;
		
		validateHudHeight();
		onChangedY(getY());
	}
	
	private void validateHudHeight() {
		hudHeight = Math.min(hudHeight, getAvailableHeight());
	}
	
	public void updateMeterList() {
		meters.clear();
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			if (!settings.ignoreHiddenMeters || !meter.isHidden()) {
				meters.add(meter);
			}
		}
		
		meterGroupName.update();
		onResized();
		
		if (selectedMeter != null && !client.getMeterGroup().hasMeter(selectedMeter)) {
			selectMeter(null);
		}
		
		if (paused && !hasContent()) {
			pause();
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
			setOffset(offset - 1);
		}
	}
	
	public void onInitScreen(int width, int height) {
		if (!client.hasMultimeterScreenOpen()) {
			return; // we should never get here
		}
		
		onScreen = true;
		
		settings.forceFullOpacity = true;
		settings.ignoreHiddenMeters = false;
		
		meterGroupName.update();
		playPauseButton.setVisible(true);
		playPauseButton.update();
		fastBackwardButton.setVisible(true);
		fastForwardButton.setVisible(true);
		
		resize(width - 2, height);
		updateMeterList();
		
		if (hasContent() && !paused && Options.HUD.AUTO_PAUSE.get()) {
			pause();
		}
	}
	
	public void onOptionsChanged() {
		meterGroupName.update();
		playPauseButton.setVisible(onScreen || Options.HUD.PAUSE_INDICATOR.get());
		onResized();
	}
	
	public void onTogglePrinter() {
		printIndicator.setVisible(client.getMeterGroup().getLogManager().getPrinter().isPrinting());
	}
}
