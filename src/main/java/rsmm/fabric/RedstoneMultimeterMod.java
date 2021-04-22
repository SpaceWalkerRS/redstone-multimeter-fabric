package rsmm.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import rsmm.fabric.command.argument.RSMMCommandArgumentTypes;

public class RedstoneMultimeterMod implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("Redstone Multimeter");
	
	@Override
	public void onInitialize() {
		RSMMCommandArgumentTypes.register();
		
		LOGGER.info("Redstone Multimeter has been initialized!");
	}
}
