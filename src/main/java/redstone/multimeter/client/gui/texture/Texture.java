package redstone.multimeter.client.gui.texture;

import net.minecraft.resource.Identifier;

public class Texture {

	public final Identifier location;
	public final int width;
	public final int height;

	Texture(Identifier location, int width, int height) {
		this.location = location;
		this.width = width;
		this.height = height;
	}
}
