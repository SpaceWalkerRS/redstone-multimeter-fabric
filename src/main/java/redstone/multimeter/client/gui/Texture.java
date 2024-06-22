package redstone.multimeter.client.gui;

import net.minecraft.resources.ResourceLocation;

public class Texture {

	public static final Texture OPTIONS_BACKGROUND    = new Texture(new ResourceLocation("textures/gui/inworld_menu_list_background.png"), 16, 16);
	public static final Texture BASIC_BUTTON_INACTIVE = new Texture(new ResourceLocation("textures/gui/sprites/widget/button_disabled.png"), 200, 20);
	public static final Texture BASIC_BUTTON          = new Texture(new ResourceLocation("textures/gui/sprites/widget/button.png"), 200, 20);
	public static final Texture BASIC_BUTTON_HOVERED  = new Texture(new ResourceLocation("textures/gui/sprites/widget/button_highlighted.png"), 200, 20);

	public final ResourceLocation location;
	public final int width;
	public final int height;

	private Texture(ResourceLocation location, int width, int height) {
		this.location = location;
		this.width = width;
		this.height = height;
	}
}
