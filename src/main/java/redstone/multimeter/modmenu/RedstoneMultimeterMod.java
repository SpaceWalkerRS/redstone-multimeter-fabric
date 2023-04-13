package redstone.multimeter.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.Minecraft;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.ScreenWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraft;

public class RedstoneMultimeterMod implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			Minecraft minecraft = Minecraft.getInstance();
			MultimeterClient client = ((IMinecraft)minecraft).getMultimeterClient();

			return new ScreenWrapper(parent, new OptionsScreen(client));
		};
	}
}
