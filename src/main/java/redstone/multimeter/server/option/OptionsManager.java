package redstone.multimeter.server.option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.event.EventType;

public class OptionsManager {

	private static final String FILE_NAME = "options.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static Options load(Path dir) {
		Path file = dir.resolve(FILE_NAME);
		return validate(Files.exists(file) ? read(file) : write(file));
	}

	private static Options read(Path file) {
		try (BufferedReader br = Files.newBufferedReader(file)) {
			return GSON.fromJson(br, Options.class);
		} catch (IOException e) {
			return new Options();
		}
	}

	private static Options write(Path file) {
		Options options = new Options();

		try (BufferedWriter bw = Files.newBufferedWriter(file)) {
			bw.write(GSON.toJson(options));
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving options", e);
		}

		return options;
	}

	private static Options validate(Options options) {
		switch (options.event_types.allowed) {
		case "all":
			break;
		case "blacklist":
			for (String name : options.event_types.blacklist) {
				EventType type = EventType.byName(name);

				if (type != null) {
					options.enabledEventTypes &= ~type.flag();
				}
			}

			break;
		case "whitelist":
			options.enabledEventTypes = 0;

			for (String name : options.event_types.whitelist) {
				EventType type = EventType.byName(name);

				if (type != null) {
					options.enabledEventTypes |= type.flag();
				}
			}

			break;
		default:
			throw new IllegalStateException("unknown event types filter " + options.event_types.allowed);
		}

		return options;
	}
}
