package redstone.multimeter.client.meter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.MeterPropertiesManager;
import redstone.multimeter.common.meter.event.EventType;

public class ClientMeterPropertiesManager extends MeterPropertiesManager {

	private static final String PROPERTIES_PATH = "meter/default_properties";
	private static final String RESOURCES_PATH = String.format("/assets/%s/%s", RedstoneMultimeterMod.NAMESPACE, PROPERTIES_PATH);
	private static final String FILE_EXTENSION = ".json";
	private static final String DEFAULT_KEY = "block";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final MultimeterClient client;
	private final File dir;
	private final Map<ResourceLocation, MeterProperties> defaults;
	private final Map<ResourceLocation, MeterProperties> overrides;
	private final Map<ResourceLocation, MeterProperties> cache;

	public ClientMeterPropertiesManager(MultimeterClient client) {
		this.client = client;
		this.dir = new File(this.client.getConfigDirectory(), PROPERTIES_PATH);
		this.defaults = new HashMap<>();
		this.overrides = new HashMap<>();
		this.cache = new HashMap<>();

		initDefaults();

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	@Override
	protected Level getLevel(DimPos pos) {
		Minecraft minecraft = client.getMinecraft();
		return pos.is(minecraft.level) ? minecraft.level : null;
	}

	@Override
	protected void postValidation(MutableMeterProperties properties, Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

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
			properties.setMovable(!Screen.hasShiftDown());
		}
		for (int index = 0; index < EventType.ALL.length; index++) {
			KeyMapping keybind = Keybinds.TOGGLE_EVENT_TYPES[index];

			if (keybind.isDown()) {
				EventType type = EventType.ALL[index];
				properties.toggleEventType(type);
			}
		}
		if (Options.RedstoneMultimeter.AUTO_RANDOM_TICKS.get() && state.isRandomlyTicking()) {
			if (!properties.hasEventType(EventType.RANDOM_TICK)) {
				properties.toggleEventType(EventType.RANDOM_TICK);
			}
		}
	}

	public Map<ResourceLocation, MeterProperties> getDefaults() {
		return Collections.unmodifiableMap(defaults);
	}

	public Map<ResourceLocation, MeterProperties> getOverrides() {
		return Collections.unmodifiableMap(overrides);
	}

	public <T extends MeterProperties> void update(Map<ResourceLocation, T> newOverrides) {
		Map<ResourceLocation, MeterProperties> prev = new HashMap<>(overrides);

		overrides.clear();
		cache.clear();

		for (Entry<ResourceLocation, T> entry : newOverrides.entrySet()) {
			ResourceLocation key = entry.getKey();
			MeterProperties properties = entry.getValue();

			prev.remove(key);
			overrides.put(key, properties.immutable());
		}

		save();

		for (ResourceLocation key : prev.keySet()) {
			deleteOverrideFile(key);
		}
	}

	private MeterProperties getDefaultProperties(Block block) {
		ResourceLocation key = Registry.BLOCK.getKey(block);

		if (key == null) {
			return null; // we should never get here
		}

		return cache.computeIfAbsent(key, _key -> {
			String namespace = key.getNamespace();
			ResourceLocation defaultKey = new ResourceLocation(namespace, DEFAULT_KEY);

			return new MutableMeterProperties().
				fill(overrides.get(key)).
				fill(defaults.get(key)).
				fill(overrides.get(defaultKey)).
				fill(defaults.get(defaultKey)).
				immutable();
		});
	}

	private void initDefaults() {
		Set<String> namespaces = new HashSet<>();

		for (ResourceLocation key : Registry.BLOCK.keySet()) {
			loadDefaultProperties(key);

			if (namespaces.add(key.getNamespace())) {
				loadDefaultProperties(new ResourceLocation(key.getNamespace(), DEFAULT_KEY));
			}
		}
	}

	private void loadDefaultProperties(ResourceLocation key) {
		String path = String.format("%s/%s/%s%s", RESOURCES_PATH, key.getNamespace(), key.getPath(), FILE_EXTENSION);
		InputStream resource = getClass().getResourceAsStream(path);

		if (resource == null) {
			return;
		}

		try (InputStreamReader isr = new InputStreamReader(resource)) {
			loadProperties(defaults, key, isr);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
		}
	}

	public void reload() {
		overrides.clear();
		cache.clear();

		for (File dirForNamespace : dir.listFiles()) {
			if (dirForNamespace.isDirectory()) {
				String namespace = dirForNamespace.getName();

				for (File file : dirForNamespace.listFiles()) {
					if (file.isFile()) {
						loadUserOverrides(namespace, file);
					}
				}
			}
		}
	}

	private void loadUserOverrides(String namespace, File file) {
		String path = file.getName();

		if (!path.endsWith(FILE_EXTENSION)) {
			return;
		}

		path = path.substring(0, path.length() - FILE_EXTENSION.length());

		try (FileReader fr = new FileReader(file)) {
			loadProperties(overrides, new ResourceLocation(namespace, path), fr);
		} catch (ResourceLocationException | IOException | JsonSyntaxException | JsonIOException e) {
		}
	}

	private static void loadProperties(Map<ResourceLocation, MeterProperties> map, ResourceLocation key, Reader reader) {
		JsonElement rawJson = GSON.fromJson(reader, JsonElement.class);

		if (rawJson.isJsonObject()) {
			JsonObject json = rawJson.getAsJsonObject();
			MeterProperties properties = MeterProperties.fromJson(json);

			map.put(key, properties);
		}
	}

	public void save() {
		for (Entry<ResourceLocation, MeterProperties> entry : overrides.entrySet()) {
			ResourceLocation key = entry.getKey();
			MeterProperties properties = entry.getValue();

			saveUserOverrides(key, properties);
		}
	}

	private void saveUserOverrides(ResourceLocation key, MeterProperties properties) {
		String namespace = key.getNamespace();
		String path = key.getPath();

		File dirForNamespace = new File(dir, namespace);

		if (!dirForNamespace.exists()) {
			dirForNamespace.mkdirs();
		}
		if (!dirForNamespace.isDirectory()) {
			RedstoneMultimeterMod.LOGGER.warn("Unable to save properties for \'" + key.toString() + "\' - the \'" + namespace + "\' folder does not exist and cannot be created!");
			return;
		}

		File file = new File(dirForNamespace, String.format("%s%s", path, FILE_EXTENSION));

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		if (!file.isFile()) {
			RedstoneMultimeterMod.LOGGER.warn("Unable to save properties for \'" + key.toString() + "\' - the \'" + path + "\' file does not exist and cannot be created!");
			return;
		}

		JsonObject json = properties.toJson();

		try (FileWriter fw = new FileWriter(file)) {
			fw.write(GSON.toJson(json));
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
		}
	}

	private void deleteOverrideFile(ResourceLocation key) {
		String namespace = key.getNamespace();
		String path = key.getPath();

		File subFolder = new File(dir, namespace);

		if (!subFolder.exists() || !subFolder.isDirectory()) {
			return;
		}

		File file = new File(subFolder, String.format("%s%s", path, FILE_EXTENSION));

		if (!file.exists() || !file.isFile()) {
			return;
		}

		file.delete();
	}
}
