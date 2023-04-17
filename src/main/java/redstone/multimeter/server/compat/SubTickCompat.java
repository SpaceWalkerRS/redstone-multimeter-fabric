package redstone.multimeter.server.compat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import redstone.multimeter.server.MultimeterServer;

public class SubTickCompat {

	private final MultimeterServer server;

	private Supplier<Boolean> frozen = () -> false;

	public SubTickCompat(MultimeterServer server) {
		this.server = server;
	}

	public void init() {
		Class<?> SubTick;
		Class<?> TickHandler;
		Method SubTick$getTickHandler;
		Field TickHandler$frozen;

		Supplier<Boolean> frozen = () -> false;

		try {
			SubTick = Class.forName("subtick.SubTick");
			SubTick$getTickHandler = SubTick.getMethod("getTickHandler", Level.class);
			TickHandler = Class.forName("subtick.TickHandler");
			TickHandler$frozen = TickHandler.getField("frozen");

			for (ServerLevel level : server.getLevels()) {
				Object tickHandler = SubTick$getTickHandler.invoke(null, level);

				Supplier<Boolean> frozenChain = frozen;

				frozen = () -> {
					try {
						return TickHandler$frozen.getBoolean(tickHandler);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						return frozenChain.get();
					}
				};
			}
		} catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}

		this.frozen = frozen;
	}

	public boolean isFrozen() {
		return frozen.get();
	}
}
