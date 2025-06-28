package redstone.multimeter.client.gui.texture;

public class TextureRegion {

	public final Texture texture;
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	TextureRegion(Texture texture) {
		this(texture, 0, 0, texture.width, texture.height);
	}

	TextureRegion(Texture texture, int x, int y, int width, int height) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
