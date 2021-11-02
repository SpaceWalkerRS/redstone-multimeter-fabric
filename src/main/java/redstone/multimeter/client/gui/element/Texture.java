package redstone.multimeter.client.gui.element;

import net.minecraft.util.Identifier;

public class Texture {
	
	public static final Texture OPTIONS_BACKGROUND = new Texture(new Identifier("textures/gui/options_background.png"), 16, 16);
	public static final Texture OPTIONS_WIDGETS    = new Texture(new Identifier("textures/gui/widgets.png"), 256, 256);
	
	public final Identifier id;
	public final int width;
	public final int height;
	
	private Texture(Identifier id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}
}
