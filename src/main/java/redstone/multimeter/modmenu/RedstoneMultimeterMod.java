package redstone.multimeter.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import redstone.multimeter.client.gui.OptionsScreen;

public class RedstoneMultimeterMod implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new OptionsScreen(parent);
	}
}
