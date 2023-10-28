package redstone.multimeter.client.gui;

public class TextureRegion {

	public static final TextureRegion OPTIONS_BACKGROUND    = new TextureRegion(Texture.OPTIONS_BACKGROUND);
	public static final TextureRegion BASIC_BUTTON_INACTIVE = new TextureRegion(Texture.BASIC_BUTTON_INACTIVE);
	public static final TextureRegion BASIC_BUTTON          = new TextureRegion(Texture.BASIC_BUTTON);
	public static final TextureRegion BASIC_BUTTON_HOVERED  = new TextureRegion(Texture.BASIC_BUTTON_HOVERED);

	public final Texture texture;
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	private TextureRegion(Texture texture) {
		this(texture, 0, 0, texture.width, texture.height);
	}

	private TextureRegion(Texture texture, int x, int y, int width, int height) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
