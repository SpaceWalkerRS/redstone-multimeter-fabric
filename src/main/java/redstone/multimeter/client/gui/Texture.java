package redstone.multimeter.client.gui;

public class Texture {

	public static final Texture OPTIONS_BACKGROUND = new Texture("/gui/background.png", 16, 16);
	public static final Texture OPTIONS_WIDGETS    = new Texture("/gui/gui.png", 256, 256);

	public final String location;
	public final int width;
	public final int height;

	private Texture(String location, int width, int height) {
		this.location = location;
		this.width = width;
		this.height = height;
	}
}
