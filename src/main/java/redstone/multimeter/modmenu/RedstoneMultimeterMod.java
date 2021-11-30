package redstone.multimeter.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.MinecraftClient;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.OptionsScreen;
import redstone.multimeter.client.gui.element.ScreenWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

public class RedstoneMultimeterMod implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			MultimeterClient client = ((IMinecraftClient)minecraftClient).getMultimeterClient();
			
			return new ScreenWrapper(parent, new OptionsScreen(client));
		};
	}
}
