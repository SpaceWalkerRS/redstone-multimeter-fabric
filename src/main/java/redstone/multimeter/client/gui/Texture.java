package redstone.multimeter.client.gui;

import net.minecraft.resources.ResourceLocation;

public class Texture {

	public static final Texture OPTIONS_BACKGROUND = new Texture(new ResourceLocation("textures/gui/options_background.png"), 16, 16);
	public static final Texture OPTIONS_WIDGETS    = new Texture(new ResourceLocation("textures/gui/widgets.png"), 256, 256);

	public final ResourceLocation location;
	public final int width;
	public final int height;

	private Texture(ResourceLocation location, int width, int height) {
		this.location = location;
		this.width = width;
		this.height = height;
	}
}
