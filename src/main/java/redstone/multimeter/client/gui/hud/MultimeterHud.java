package redstone.multimeter.client.gui.hud;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.TransparentButton;
import redstone.multimeter.client.gui.hud.element.MeterEventDetails;
import redstone.multimeter.client.gui.hud.element.MeterListRenderer;
import redstone.multimeter.client.gui.hud.element.PrimaryEventViewer;
import redstone.multimeter.client.gui.hud.element.SecondaryEventViewer;
import redstone.multimeter.client.gui.hud.event.MeterEventRenderDispatcher;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.util.ColorUtils;
import redstone.multimeter.util.TextUtils;

public class MultimeterHud extends AbstractParentElement {

	public final MultimeterClient client;
	public final TextRenderer textRenderer;
	public final HudSettings settings;
	public final HudRenderer renderer;
	public final MeterEventRenderDispatcher eventRenderers;
	public final List<Meter> meters;

	private MeterListRenderer names;
	private PrimaryEventViewer ticks;
	private SecondaryEventViewer subticks;
	private MeterEventDetails details;
	private TextElement meterGroupSlot;
	private TextElement meterGroupName;
	private TextElement tickMarkerCounter;

	private TransparentButton playPauseButton;
	private TransparentButton fastBackwardButton;
	private TransparentButton fastForwardButton;
	private TextElement printIndicator;

	private int hudX;
	private int hudY;
	private int hudWidth;
	private int hudHeight;

	private boolean paused;
	private boolean wasPaused;
	/** The offset between the last server tick and the first tick to be displayed in the ticks table. */
	private int offset;
	private boolean onScreen;
	private Meter selectedMeter;
	private boolean focusMode;
	private Meter focussedMeter;
	private EventLog focussedEvent;
	private long tickMarker;

	public MultimeterHud(MultimeterClient client) {
		Minecraft minecraft = client.getMinecraft();

		this.client = client;
		this.textRenderer = minecraft.textRenderer;
		this.settings = new HudSettings(this);
		this.renderer = new HudRenderer(this);
		this.eventRenderers = new MeterEventRenderDispatcher(this);
		this.meters = new ArrayList<>();

		this.tickMarker = -1L;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		if (!hasContent()) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 100);

		List<Element> children = getChildren();

		for (int index = 0; index < children.size(); index++) {
			Element child = children.get(index);

			if (child.isVisible()) {
				renderer.render(child, mouseX, mouseY);
			}
		}

		GL11.glPopMatrix();
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
	public boolean isMouseOver(double mouseX, double mouseY) {
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
			togglePaused();
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
	public void setX(int x) {
		super.setX(x);

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

			x += settings.gridSize + 1;
			if (meterGroupSlot.isVisible()) {
				meterGroupSlot.setX(x);
				x += meterGroupSlot.getWidth() + settings.gridSize + 1;
			}
			meterGroupName.setX(x);

			x = hudX;
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
			tickMarkerCounter.setX(x);

			x += subticks.getWidth() + settings.columnWidth + settings.gridSize;
			details.setX(x);

			break;
		case RIGHT_TO_LEFT:
			hudX = x + pos + effectiveWidth - getWidth();

			x = hudX + (getWidth() - names.getWidth());
			names.setX(x);

			x += names.getWidth();
			if (meterGroupSlot.isVisible()) {
				x -= meterGroupSlot.getWidth() + settings.gridSize + 1;
				meterGroupSlot.setX(x + 1);
			}
			x -= meterGroupName.getWidth() + settings.gridSize;
			meterGroupName.setX(x);

			x = hudX + (getWidth() - names.getWidth());
			x -= ticks.getWidth();
			ticks.setX(x);

			w = playPauseButton.getWidth();
			playPauseButton.setX(x + w);
			fastBackwardButton.setX(x + 2 * w);
			fastForwardButton.setX(x);
			printIndicator.setX(x + 3 * w);

			x -= settings.columnWidth;
			tickMarkerCounter.setX(x);

			x -= (settings.gridSize + subticks.getWidth());
			subticks.setX(x);

			x -= (settings.columnWidth + settings.gridSize + details.getWidth());
			details.setX(x);

			break;
		}
	}

	@Override
	public void setY(int y) {
		super.setY(y);

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
			details.setY(y);

			y += names.getHeight();
			meterGroupSlot.setY(y + settings.gridSize);
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
			details.setY(y - (details.getHeight() - names.getHeight()));
			meterGroupSlot.setY(y - meterGroupSlot.getHeight());
			meterGroupName.setY(y - meterGroupName.getHeight());

			y -= playPauseButton.getHeight();
			playPauseButton.setY(y);
			fastBackwardButton.setY(y);
			fastForwardButton.setY(y);

			break;
		}

		y = fastBackwardButton.getMessageY();
		tickMarkerCounter.setY(y);
		printIndicator.setY(y);
	}

	public void init() {
		this.names = new MeterListRenderer(this);
		this.ticks = new PrimaryEventViewer(this);
		this.subticks = new SecondaryEventViewer(this);
		this.details = new MeterEventDetails(this);
		this.meterGroupSlot = new TextElement(this.client, 0, 0, t -> {
			int slot = this.client.isPreviewing()
				? this.client.getMeterGroupPreview().getSlot()
				: this.client.getMeterGroup().getSlot();

			if (slot < 0) {
				t.setVisible(false);
			} else {
				t.setVisible(true);

				String text = "[" + slot + "]";
				int a = Math.round(0xFF * settings.opacity() / 100.0F);
				int rgb = onScreen ? 0xF0F0F0 : 0x404040;
				int color = ColorUtils.setAlpha(rgb, a);

				if (!this.client.isPreviewing() && this.client.getMeterGroup().isDirty()) {
					text = Formatting.BOLD + text;
				}

				t.setText(text);
				t.setColor(color);
			}
		}, () -> {
			return this.client.isPreviewing() || !this.client.getMeterGroup().isDirty()
				? Tooltip.EMPTY
				: Tooltip.of("There are unsaved changes to this saved meter group!");
		});
		this.meterGroupName = new TextElement(this.client, 0, 0, t -> {
			String text = this.client.isPreviewing()
				? this.client.getMeterGroupPreview().getName()
				: this.client.getMeterGroup().getName();

			int a = Math.round(0xFF * settings.opacity() / 100.0F);
			int rgb = onScreen ? 0xD0D0D0 : 0x202020;
			int color = ColorUtils.setAlpha(rgb, a);

			if (paused && !Options.HUD.HIDE_HIGHLIGHT.get() && !Options.HUD.PAUSE_INDICATOR.get()) {
				text += " (Paused)";
			}

			t.add(text).setColor(color);
		});
		this.tickMarkerCounter = new TextElement(this.client, 0, 0, t -> {
			if (hasTickMarker()) {
				long tick = paused ? getSelectedTick() : getCurrentTick();
				String text = String.valueOf(tick - tickMarker);

				int a = Math.round(0xFF * settings.opacity() / 100.0F);
				int rgb = settings.colorHighlightTickMarker;
				int color = ColorUtils.setAlpha(rgb, a);

				t.add(text).setColor(color).setVisible(true);
			} else {
				t.setVisible(false);
			}
		}, () -> Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.TOGGLE_MARKER)));

		this.playPauseButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(!onScreen ^ paused ? "\u23f5" : "\u23f8"), () -> Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.PAUSE_METERS)), button -> {
			togglePaused();
			return true;
		});
		this.fastBackwardButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(getStepSymbol(false, Screen.isControlDown())), () -> Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.STEP_BACKWARD, new Object[] { Keybinds.SCROLL_HUD, "scroll" })), button -> {
			stepBackward(Screen.isControlDown());
			return true;
		}) {

			@Override
			public void tick() {
				update();
			}
		};
		this.fastForwardButton = new TransparentButton(this.client, 0, 0, 9, 9, () -> new LiteralText(getStepSymbol(true, Screen.isControlDown())), () -> Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.STEP_FORWARD, new Object[] { Keybinds.SCROLL_HUD, "scroll" })), button -> {
			stepForward(Screen.isControlDown());
			return true;
		}) {

			@Override
			public void tick() {
				update();
			}
		};
		this.printIndicator = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText("P").setFormatting(Formatting.BOLD)).setWithShadow(true), () -> Tooltip.of(TextUtils.formatKeybindInfo(Keybinds.PRINT_LOGS)));

		if (!Options.HUD.PAUSE_INDICATOR.get()) {
			this.playPauseButton.setVisible(false);
		}
		this.fastBackwardButton.setVisible(false);
		this.fastForwardButton.setVisible(false);
		this.printIndicator.setVisible(false);

		addChild(this.names);
		addChild(this.ticks);
		addChild(this.subticks);
		addChild(this.details);
		addChild(this.meterGroupSlot);
		addChild(this.meterGroupName);
		addChild(this.tickMarkerCounter);
		addChild(this.playPauseButton);
		addChild(this.fastBackwardButton);
		addChild(this.fastForwardButton);
		addChild(this.printIndicator);

		optionsChanged();
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

	public void togglePaused() {
		if (!client.isPreviewing() && !focusMode && hasContent()) {
			boolean pause = !paused;

			if (setPaused(pause)) {
				client.getTutorial().onPauseHud(pause);
			}
		}
	}

	public boolean setPaused(boolean pause) {
		if (paused == pause) {
			return false;
		}

		paused = pause;

		meterGroupSlot.update();
		meterGroupName.update();
		playPauseButton.update();
		fastBackwardButton.setActive(paused);
		fastForwardButton.setActive(paused);

		if (paused) {
			updateEventViewersWidth();
		} else {
			resetOffset();

			if (hasTickMarker() && Options.HUD.AUTO_REMOVE_MARKER.get()) {
				toggleTickMarker(false);
			}
		}

		return true;
	}

	public int getColumn(long tick) {
		return getColumn(tick, false);
	}

	public int getColumn(long tick, boolean allowRightEdge) {
		long first = client.getPrevGameTime() + offset;
		long last = first + Options.HUD.COLUMN_COUNT.get();

		if (!allowRightEdge) {
			last--;
		}

		return tick < first || tick > last ? -1 : (int)(tick - first);
	}

	private void setOffset(int offset) {
		this.offset = offset;

		updateTickMarkerCounter();
		updateEventViewersWidth();
	}

	public void resetOffset() {
		setOffset(1 - Options.HUD.COLUMN_COUNT.get());
	}

	public void stepBackward(boolean jump) {
		if (!client.isPreviewing()) {
			if (focusMode) {
				moveFocus(focussedEvent.getTick(), jump ? 0 : focussedEvent.getSubtick(), false);
			} else if (paused) {
				scroll(jump ? 10 : 1, false);
			}
		}
	}

	public void stepForward(boolean jump) {
		if (!client.isPreviewing()) {
			if (focusMode) {
				moveFocus(focussedEvent.getTick(), jump ? client.getMeterGroup().getLogManager().getSubtickCount(focussedEvent.getTick()) : focussedEvent.getSubtick(), true);
			} else if (paused) {
				scroll(jump ? 10 : 1, true);
			}
		}
	}

	public void scroll(int amount, boolean forward) {
		if (!forward) amount *= -1;
		setOffset(offset + amount);
		client.getTutorial().onScrollHud(amount);
	}

	public boolean moveFocus(long tick, int subtick, boolean forward) {
		Meter closestMeter = null;
		EventLog closestEvent = null;

		for (Meter meter : meters) {
			EventLog event = forward
				? meter.getLogs().getFirstLogAfter(tick, subtick)
				: meter.getLogs().getLastLogBefore(tick, subtick);

			if (event != null && (closestEvent == null || (forward ? event.isBefore(closestEvent) : event.isAfter(closestEvent)))) {
				closestMeter = meter;
				closestEvent = event;
			}
		}

		if (closestEvent != null) {
			focussedMeter = closestMeter;
			focussedEvent = closestEvent;

			setOffset(offset + (int)(focussedEvent.getTick() - getSelectedTick()));
		}

		return focussedEvent != null;
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

	public boolean hasSelectedMeter() {
		return selectedMeter != null;
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

	public boolean isFocusMode() {
		return focusMode;
	}

	public void toggleFocusMode() {
		if (!client.isPreviewing() && hasContent()) {
			boolean enable = !focusMode;

			if (setFocusMode(enable)) {
				String message = String.format("%s Focus Mode", enable ? "Enabled" : "Disabled");
				client.sendMessage(new LiteralText(message), true);

				client.getTutorial().onToggleFocusMode(enable);
			} else {
				String message = "No meter event logs available to focus on...";
				client.sendMessage(new LiteralText(message), true);
			}
		}
	}

	public boolean setFocusMode(boolean enabled) {
		if (focusMode == enabled) {
			return false;
		}
		if (enabled) {
			// first attempt to find focus point after the selected tick
			// then look prior to the selected tick
			if (!moveFocus(getSelectedTick(), 0, true) && !moveFocus(getSelectedTick(), Integer.MAX_VALUE, false)) {
				return false;
			}
		}

		focusMode = enabled;

		if (focusMode) {
			wasPaused = paused;

			if (!wasPaused) {
				setPaused(true);
			}
		} else {
			if (!wasPaused) {
				setPaused(false);
			}

			focussedMeter = null;
			focussedEvent = null;
		}

		return true;
	}

	public Meter getFocussedMeter() {
		return focussedMeter;
	}

	public EventLog getFocussedEvent() {
		return focussedEvent;
	}

	public boolean hasTickMarker() {
		return tickMarker >= 0L;
	}

	public long getTickMarker() {
		return tickMarker;
	}

	public void toggleTickMarker(boolean force) {
		if (tickMarker < 0L || force) {
			tickMarker = paused ? getSelectedTick() : getCurrentTick();
		} else {
			tickMarker = -1;
		}

		updateTickMarkerCounter();
	}

	private void updateTickMarkerCounter() {
		tickMarkerCounter.update();

		if (tickMarkerCounter.isVisible()) {
			int x;

			switch (getDirectionalityX()) {
			default:
			case LEFT_TO_RIGHT:
				x = subticks.getX();
				break;
			case RIGHT_TO_LEFT:
				x = subticks.getX() + subticks.getWidth() - tickMarkerCounter.getWidth();
				break;
			}

			tickMarkerCounter.setX(x);
		}
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

	public int getFocussedRow() {
		return focussedMeter == null ? -1 : meters.indexOf(focussedMeter);
	}

	public long getSelectedTick() {
		return client.getPrevGameTime() + offset + Options.HUD.SELECTED_COLUMN.get();
	}

	public long getCurrentTick() {
		return client.getPrevGameTime() + 1;
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
		Minecraft minecraft = client.getMinecraft();
		Window window = new Window(minecraft.options, minecraft.width, minecraft.height);

		int width = window.getWidth();
		int height = window.getHeight();

		resize(width, height);
	}

	public void updateWidth() {
		names.updateWidth();
		ticks.updateWidth();
		subticks.updateWidth();
		details.updateWidth();
		onWidthUpdated();
	}

	public void updateMeterListWidth() {
		names.updateWidth();
		onWidthUpdated();
	}

	public void updateEventViewersWidth() {
		ticks.updateWidth();
		subticks.updateWidth();
		details.updateWidth();
		onWidthUpdated();
	}

	private void onWidthUpdated() {
		hudWidth = names.getWidth() + ticks.getWidth();

		if (subticks.getWidth() > 0) {
			hudWidth += subticks.getWidth() + settings.columnWidth + settings.gridSize;
			hudWidth += details.getWidth() + settings.columnWidth + settings.gridSize;
		}

		validateHudWidth();
		setX(getX());
	}

	private void validateHudWidth() {
		hudWidth = Math.min(hudWidth, getAvailableWidth());
	}

	public void updateHeight() {
		names.updateHeight();
		ticks.updateHeight();
		subticks.updateHeight();
		details.updateHeight();
		onHeightUpdated();
	}

	private void onHeightUpdated() {
		hudHeight = Math.max(names.getHeight(), details.getHeight()) + settings.rowHeight + settings.gridSize;

		validateHudHeight();
		setY(getY());
	}

	private void validateHudHeight() {
		hudHeight = Math.min(hudHeight, getAvailableHeight());
	}

	public void updateMeterList() {
		meters.clear();

		ClientMeterGroup meterGroup = client.isPreviewing()
			? client.getMeterGroupPreview()
			: client.getMeterGroup();

		for (Meter meter : meterGroup.getMeters()) {
			if (!settings.ignoreHiddenMeters || !meter.isHidden()) {
				meters.add(meter);
			}
		}

		meterGroupSlot.update();
		meterGroupName.update();

		onResized();

		if (selectedMeter != null && !meterGroup.hasMeter(selectedMeter)) {
			selectMeter(null);
		}
		if (focusMode && focussedMeter != null && !meterGroup.hasMeter(focussedMeter)) {
			toggleFocusMode();
		}

		if (paused && !hasContent()) {
			togglePaused();
		}
	}

	public void reset() {
		paused = false;
		tickMarker = -1L;
		meters.clear();
		resetOffset();
		setX(0);
		setY(0);
	}

	public void tickTime() {
		if (paused) {
			setOffset(offset - 1);
		} else {
			updateTickMarkerCounter();
		}
	}

	public void onInitScreen(int width, int height) {
		if (!client.hasMultimeterScreenOpen()) {
			return; // we should never get here
		}
		if (client.isPreviewing()) {
			client.getSavedMeterGroupsManager().setIdle();
		}

		onScreen = true;

		if (settings.rowHeight < textRenderer.fontHeight) {
			settings.rowHeight = textRenderer.fontHeight;
		}

		settings.forceFullOpacity = true;
		settings.ignoreHiddenMeters = false;

		meterGroupSlot.update();
		meterGroupName.update();
		playPauseButton.setVisible(true);
		playPauseButton.update();
		fastBackwardButton.setVisible(true);
		fastForwardButton.setVisible(true);

		resize(width - 2, height);
		updateMeterList();

		if (hasContent() && !paused && Options.HUD.AUTO_PAUSE.get()) {
			togglePaused();
		}
	}

	public void optionsChanged() {
		settings.optionsChanged();

		meterGroupSlot.update();
		meterGroupName.update();
		playPauseButton.setVisible(onScreen || Options.HUD.PAUSE_INDICATOR.get());
		onResized();

		if (!paused) {
			resetOffset();
		}
	}

	public void onTogglePrinter() {
		printIndicator.setVisible(client.getMeterGroup().getLogManager().getPrinter().isPrinting());
	}
}
