package redstone.multimeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RedstoneMultimeterMod implements ModInitializer {

	public static final String MOD_ID = "rsmm_fabric";
	public static final String MOD_NAME = "Redstone Multimeter";
	public static final String MOD_VERSION = "1.15.0";
	public static final String NAMESPACE = "redstone_multimeter";
	public static final String CONFIG_PATH = "config/" + NAMESPACE;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static Boolean carpet;
	private static Boolean subtick;

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("%s %s has been initialized!", MOD_NAME, MOD_VERSION));
	}

	public static boolean isCarpetPresent() {
		if (carpet == null) {
			carpet = FabricLoader.getInstance().isModLoaded("carpet");
		}

		return carpet;
	}

	public static boolean isSubtickPresent() {
		if (subtick == null) {
			subtick = FabricLoader.getInstance().isModLoaded("subtick");
		}

		return subtick;
	}
}
