package redstone.multimeter.client.gui.hud;

import redstone.multimeter.client.option.Options;
import redstone.multimeter.util.ColorUtils;

public class HudSettings {

	public final int colorBackground;
	public final int colorGridMain;
	public final int colorGridInterval;
	public final int colorGridMarker;
	public final int colorHighlightHovered;
	public final int colorHighlightSelected;
	public final int colorTextOn;
	public final int colorTextOff;

	public int columnWidth;
	public int rowHeight;
	public int gridSize;
	public int scale;
	public int wparity;
	public int hparity;
	public int colorHighlightTickMarker;
	public boolean forceFullOpacity;
	public boolean ignoreHiddenMeters;

	public HudSettings(MultimeterHud hud) {
		this.colorBackground = 0xFF202020;
		this.colorGridMain = 0xFF404040;
		this.colorGridInterval = 0xFF606060;
		this.colorGridMarker = 0xFFC0C0C0;
		this.colorHighlightHovered = 0xFF808080;
		this.colorHighlightSelected = 0xFFFFFFFF;
		this.colorTextOn = 0xFF000000;
		this.colorTextOff = 0xFF707070;
	}

	public int opacity() {
		return forceFullOpacity ? 100 : Options.HUD.OPACITY.get();
	}

	public void optionsChanged() {
		this.scale = Options.HUD.CELL_SCALE.get();
		this.columnWidth = Options.HUD.COLUMN_WIDTH.get();
		this.rowHeight = Options.HUD.ROW_HEIGHT.get();
		this.gridSize = Options.HUD.GRID_SIZE.get();

		this.wparity = 1 - this.columnWidth % 2;
		this.hparity = 1 - this.rowHeight % 2;

		this.columnWidth *= this.scale;
		this.rowHeight *= this.scale;

		if (!this.setTickMarkerColor(Options.HUD.TICK_MARKER_COLOR.get())) {
			this.setTickMarkerColor(Options.HUD.TICK_MARKER_COLOR.getDefault());
		}
	}

	public boolean setTickMarkerColor(String hexColor) {
		try {
			int color = ColorUtils.fromRGBString(hexColor);
			this.colorHighlightTickMarker = ColorUtils.setAlpha(color, 0xFF);

			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
