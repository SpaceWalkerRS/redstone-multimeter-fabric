package redstone.multimeter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RedstoneMultimeterMod implements ModInitializer {

	public static final String MOD_ID = "rsmm_fabric";
	public static final String MOD_NAME = "Redstone Multimeter";
	public static final String MOD_VERSION = "1.15.0";
	public static final String NAMESPACE = "redstone_multimeter";
	public static final String CONFIG_PATH = "config/" + NAMESPACE;
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	private static Boolean carpet;

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("%s %s has been initialized!", MOD_NAME, MOD_VERSION));
	}

	public static boolean isCarpetPreset() {
		if (carpet == null) {
			carpet = FabricLoader.getInstance().isModLoaded("carpet");
		}

		return carpet;
	}
}
