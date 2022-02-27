package redstone.multimeter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod(
		modid   = RedstoneMultimeterMod.MOD_ID,
		name    = RedstoneMultimeterMod.MOD_NAME,
		version = RedstoneMultimeterMod.MOD_VERSION
)
public class RedstoneMultimeterMod {
	
	public static final String MOD_ID = "rsmm_forge";
	public static final String MOD_NAME = "Redstone Multimeter";
	public static final String MOD_VERSION = "1.6.1";
	public static final String NAMESPACE = "redstone_multimeter";
	public static final String MINECRAFT_NAMESPACE = "minecraft";
	public static final String CONFIG_PATH = "config/" + NAMESPACE;
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	
}
