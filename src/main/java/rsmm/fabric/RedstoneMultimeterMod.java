package rsmm.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class RedstoneMultimeterMod implements ModInitializer {
	
	public static final String MOD_ID = "rsmm-fabric";
	public static final String MOD_VERSION = "1.3.0";
	public static final Logger LOGGER = LogManager.getLogger("Redstone Multimeter");
	
	@Override
	public void onInitialize() {
		LOGGER.info("Redstone Multimeter has been initialized!");
	}
}
