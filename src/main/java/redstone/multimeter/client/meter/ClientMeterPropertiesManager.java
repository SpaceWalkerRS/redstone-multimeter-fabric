package redstone.multimeter.client.meter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.MeterPropertiesManager;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.Blocks;

public class ClientMeterPropertiesManager extends MeterPropertiesManager {

	private static final String PROPERTIES_PATH = "meter/default_properties";
	private static final String RESOURCES_PATH = String.format("/assets/%s/%s", RedstoneMultimeterMod.NAMESPACE, PROPERTIES_PATH);
	private static final String FILE_EXTENSION = ".json";
	private static final String DEFAULT_NAMESPACE = "minecraft";
	private static final String DEFAULT_KEY = "block";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final MultimeterClient client;
	private final Path dir;
	private final Map<String, MeterProperties> defaults;
	private final Map<String, MeterProperties> overrides;
	private final Map<String, MeterProperties> cache;

	public ClientMeterPropertiesManager(MultimeterClient client) {
		this.client = client;
		this.dir = this.client.getConfigDirectory().resolve(PROPERTIES_PATH);
		this.defaults = new HashMap<>();
		this.overrides = new HashMap<>();
		this.cache = new HashMap<>();

		initDefaults();

		if (!Files.exists(this.dir)) {
			try {
				Files.createDirectories(this.dir);
			} catch (IOException e) {
				throw new RuntimeException("unable to create parent directories of meter properties file", e);
			}
		}
	}

	@Override
	protected World getWorld(DimPos pos) {
		Minecraft minecraft = client.getMinecraft();
		return pos.is(minecraft.world) ? minecraft.world : null;
	}

	@Override
	protected void postValidation(MutableMeterProperties properties, World world, int x, int y, int z) {
		int block = world.getBlock(x, y, z);

		MeterProperties defaultProperties = getDefaultProperties(block);

		if (defaultProperties != null) {
			properties.fill(defaultProperties);
		}

		if (properties.getName() != null && Options.RedstoneMultimeter.NUMBERED_NAMES.get()) {
			String name = properties.getName();
			int number = client.getMeterGroup().getNextMeterIndex();

			properties.setName(String.format("%s %d", name, number));
		}
		if (properties.getColor() == null) {
			properties.setColor(Options.RedstoneMultimeter.COLOR_PICKER.get().next());
		}
		if (Options.RedstoneMultimeter.SHIFTY_METERS.get()) {
			properties.setMovable(!Screen.isShiftDown());
		}
		for (int index = 0; index < EventType.ALL.length; index++) {
			KeyBinding keybind = Keybinds.TOGGLE_EVENT_TYPES[index];

			if (keybind.pressed) {
				EventType type = EventType.ALL[index];
				properties.toggleEventType(type);
			}
		}
		if (Options.RedstoneMultimeter.AUTO_RANDOM_TICKS.get() && block != 0 && Block.BY_ID[block].ticksRandomly()) {
			if (!properties.hasEventType(EventType.RANDOM_TICK)) {
				properties.toggleEventType(EventType.RANDOM_TICK);
			}
		}
	}

	public Map<String, MeterProperties> getDefaults() {
		return Collections.unmodifiableMap(defaults);
	}

	public Map<String, MeterProperties> getOverrides() {
		return Collections.unmodifiableMap(overrides);
	}

	public <T extends MeterProperties> void update(Map<String, T> newOverrides) {
		Map<String, MeterProperties> prev = new HashMap<>(overrides);

		overrides.clear();
		cache.clear();

		for (Entry<String, T> entry : newOverrides.entrySet()) {
			String key = entry.getKey();
			MeterProperties properties = entry.getValue();

			prev.remove(key);
			overrides.put(key, properties.immutable());
		}

		save();

		for (String key : prev.keySet()) {
			deleteOverrideFile(key);
		}
	}

	public MeterProperties getDefaultProperties(int block) {
		String key = Blocks.REGISTRY.getKey(block);

		if (key == null) {
			return null; // we should never get here
		}

		return cache.computeIfAbsent(key, _key -> {
			String defaultKey = DEFAULT_KEY;

			return new MutableMeterProperties().
				fill(overrides.get(key)).
				fill(defaults.get(key)).
				fill(overrides.get(defaultKey)).
				fill(defaults.get(defaultKey)).
				immutable();
		});
	}

	private void initDefaults() {
		loadDefaultProperties(DEFAULT_KEY);

		for (Block block : Block.BY_ID) {
			if (block != null) {
				String key = Blocks.REGISTRY.getKey(block.id);
				loadDefaultProperties(key);
			}
		}
	}

	private void loadDefaultProperties(String key) {
		String path = String.format("%s/%s/%s%s", RESOURCES_PATH, DEFAULT_NAMESPACE, key, FILE_EXTENSION);
		InputStream resource = getClass().getResourceAsStream(path);

		if (resource == null) {
			return;
		}

		try (InputStreamReader isr = new InputStreamReader(resource)) {
			loadProperties(defaults, key, isr);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while loading default meter properties", e);
		}
	}

	public void reload() {
		overrides.clear();
		cache.clear();

		try {
			for (Path dirForNamespace : Files.newDirectoryStream(this.dir, f -> Files.isDirectory(f))) {
				String namespace = dirForNamespace.getFileName().toString();

				for (Path file : Files.newDirectoryStream(dirForNamespace, f -> Files.isRegularFile(f))) {
					loadUserOverrides(namespace, file);
				}
			}
		} catch (Exception e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while reloading meter properties", e);
		}
	}

	private void loadUserOverrides(String namespace, Path file) throws Exception {
		String path = file.getFileName().toString();

		if (!path.endsWith(FILE_EXTENSION)) {
			return;
		}

		String key = path.substring(0, path.length() - FILE_EXTENSION.length());

		try (BufferedReader br = Files.newBufferedReader(file)) {
			loadProperties(overrides, key, br);
		}

		if (!DEFAULT_NAMESPACE.equals(namespace)) {
			RedstoneMultimeterMod.LOGGER.warn("loaded user meter properties override for \'" + key + "\' from non-default namespace \'" + namespace + "\' - it will be saved to the default namespace!");
		}
	}

	private static void loadProperties(Map<String, MeterProperties> map, String key, Reader reader) {
		JsonElement rawJson = GSON.fromJson(reader, JsonElement.class);

		if (rawJson.isJsonObject()) {
			JsonObject json = rawJson.getAsJsonObject();
			MeterProperties properties = MeterProperties.fromJson(json);

			map.put(key, properties);
		}
	}

	public void save() {
		try {
			for (Entry<String, MeterProperties> entry : overrides.entrySet()) {
				String key = entry.getKey();
				MeterProperties properties = entry.getValue();

				saveUserOverrides(key, properties);
			}
		} catch (Exception e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while saving meter properties", e);
		}
	}

	private void saveUserOverrides(String key, MeterProperties properties) throws Exception {
		String namespace = DEFAULT_NAMESPACE;
		String path = key;

		Path dirForNamespace = dir.resolve(namespace);

		if (!Files.exists(dirForNamespace)) {
			Files.createDirectories(dirForNamespace);
		}
		if (!Files.isDirectory(dirForNamespace)) {
			throw new IOException("Unable to save properties for \'" + key + "\' - the \'" + namespace + "\' folder does not exist and cannot be created!");
		}

		Path file = dirForNamespace.resolve(String.format("%s%s", key, FILE_EXTENSION));

		if (Files.exists(file) && !Files.isRegularFile(file)) {
			RedstoneMultimeterMod.LOGGER.warn("Unable to save properties for \'" + key + "\' - the \'" + path + "\' file does not exist and cannot be created!");
			return;
		}

		JsonObject json = properties.toJson();

		try (BufferedWriter bw = Files.newBufferedWriter(file)) {
			bw.write(GSON.toJson(json));
		}
	}

	private void deleteOverrideFile(String key) {
		String namespace = DEFAULT_NAMESPACE;
		String path = key;

		try {
			Path folder = dir.resolve(namespace);
			Path file = folder.resolve(String.format("%s%s", path, FILE_EXTENSION));

			Files.deleteIfExists(file);
		} catch (IOException e) {
			RedstoneMultimeterMod.LOGGER.warn("exception while deleting meter properties override file", e);
		}
	}
}
