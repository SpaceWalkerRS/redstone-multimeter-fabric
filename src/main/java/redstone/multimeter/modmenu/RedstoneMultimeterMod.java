package redstone.multimeter.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.ScreenWrapper;

public class RedstoneMultimeterMod implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ScreenWrapper(parent, new OptionsScreen());
	}
}
