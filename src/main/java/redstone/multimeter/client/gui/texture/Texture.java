package redstone.multimeter.client.gui.texture;

import net.minecraft.resources.ResourceLocation;

public class Texture {

	public final ResourceLocation location;
	public final int width;
	public final int height;

	Texture(ResourceLocation location, int width, int height) {
		this.location = location;
		this.width = width;
		this.height = height;
	}
}
