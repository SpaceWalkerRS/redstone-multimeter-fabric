package redstone.multimeter.server.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import net.minecraft.server.MinecraftServer;
import redstone.multimeter.server.MultimeterServer;

public class CarpetCompat {

	private final MultimeterServer server;

	private Supplier<Boolean> frozen = () -> false;

	public CarpetCompat(MultimeterServer server) {
		this.server = server;
	}

	public void init() {
		Method MinecraftServer$getTickRateManager;
		Class<?> ServerTickRateManager;
		Method ServerTickRateManager$runsNormally;

		Supplier<Boolean> frozen = () -> false;

		try {
			MinecraftServer$getTickRateManager = MinecraftServer.class.getMethod("getTickRateManager");
			ServerTickRateManager = Class.forName("carpet.helpers.ServerTickRateManager");
			ServerTickRateManager$runsNormally = ServerTickRateManager.getMethod("runsNormally");

			Object trm = MinecraftServer$getTickRateManager.invoke(server.getMinecraftServer());

			if (ServerTickRateManager$runsNormally.getReturnType() == boolean.class) {
				frozen = () -> {
					try {
						return !(Boolean)ServerTickRateManager$runsNormally.invoke(trm);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						return false;
					}
				};
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}

		this.frozen = frozen;
	}

	public boolean isFrozen() {
		return frozen.get();
	}
}
