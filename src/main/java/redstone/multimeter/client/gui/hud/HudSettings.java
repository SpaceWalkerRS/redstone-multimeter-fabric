package redstone.multimeter.client.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import redstone.multimeter.client.option.Options;

public class HudSettings {
	
	public final int columnWidth;
	public final int rowHeight;
	public final int gridSize;
	
	public final int colorBackground;
	public final int colorGridMain;
	public final int colorGridInterval;
	public final int colorGridMarker;
	public final int colorHighlightHovered;
	public final int colorHighlightSelected;
	public final int colorTextOn;
	public final int colorTextOff;
	
	public boolean forceFullOpacity;
	public boolean ignoreHiddenMeters;
	
	public HudSettings(MultimeterHud hud) {
		MinecraftClient minecraftClient = hud.client.getMinecraftClient();
		TextRenderer font = minecraftClient.textRenderer;
		
		this.columnWidth = 3;
		this.rowHeight = font.lineHeight;
		this.gridSize = 1;
		
		this.colorBackground = 0x202020;
		this.colorGridMain = 0x404040;
		this.colorGridInterval = 0x606060;
		this.colorGridMarker = 0xC0C0C0;
		this.colorHighlightHovered = 0x808080;
		this.colorHighlightSelected = 0xFFFFFF;
		this.colorTextOn = 0x000000;
		this.colorTextOff = 0x707070;
	}
	
	public int opacity() {
		return forceFullOpacity ? 100 : Options.HUD.OPACITY.get();
	}
}
