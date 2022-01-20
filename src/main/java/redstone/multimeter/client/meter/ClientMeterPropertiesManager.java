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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.KeyBindings;
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
	private static final String DEFAULT_ID = "block";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private final MultimeterClient client;
	private final File folder;
	private final Map<Identifier, MeterProperties> defaults;
	private final Map<Identifier, MeterProperties> overrides;
	private final Map<Identifier, MeterProperties> cache;
	
	public ClientMeterPropertiesManager(MultimeterClient multimeterClient) {
		this.client = multimeterClient;
		this.folder = new File(this.client.getConfigFolder(), PROPERTIES_PATH);
		this.defaults = new HashMap<>();
		this.overrides = new HashMap<>();
		this.cache = new HashMap<>();
		
		initDefaults();
		
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	
	@Override
	protected World getWorldOf(DimPos pos) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		return pos.isOf(minecraftClient.world) ? minecraftClient.world : null;
	}
	
	@Override
	protected void postValidation(MutableMeterProperties properties, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
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
		if (Options.RedstoneMultimeter.SHIFTY_METERS.get()) {
			properties.setMovable(!Screen.hasShiftDown());
		}
		for (int index = 0; index < EventType.ALL.length; index++) {
			KeyBinding keyBind = KeyBindings.TOGGLE_EVENT_TYPES[index];
			
			if (keyBind.isPressed()) {
				EventType type = EventType.ALL[index];
				properties.toggleEventType(type);
			}
		}
		if (Options.RedstoneMultimeter.AUTO_RANDOM_TICKS.get() && state.hasRandomTicks()) {
			if (!properties.hasEventType(EventType.RANDOM_TICK)) {
				properties.toggleEventType(EventType.RANDOM_TICK);
			}
		}
	}
	
	public Map<Identifier, MeterProperties> getDefaults() {
		return Collections.unmodifiableMap(defaults);
	}
	
	public Map<Identifier, MeterProperties> getOverrides() {
		return Collections.unmodifiableMap(overrides);
	}
	
	public <T extends MeterProperties> void update(Map<Identifier, T> newOverrides) {
		Map<Identifier, MeterProperties> prev = new HashMap<>(overrides);
		
		overrides.clear();
		cache.clear();
		
		for (Entry<Identifier, T> entry : newOverrides.entrySet()) {
			Identifier blockId = entry.getKey();
			MeterProperties properties = entry.getValue();
			
			prev.remove(blockId);
			overrides.put(blockId, properties.toImmutable());
		}
		
		save();
		
		for (Identifier blockId : prev.keySet()) {
			deleteOverrideFile(blockId);
		}
	}
	
	private MeterProperties getDefaultProperties(Block block) {
		Identifier blockId = Registry.BLOCK.getId(block);
		
		if (blockId == null) {
			return null; // we should never get here
		}
		
		return cache.computeIfAbsent(blockId, key -> {
			String namespace = blockId.getNamespace();
			Identifier defaultId = new Identifier(namespace, DEFAULT_ID);
			
			return new MutableMeterProperties().
				fill(overrides.get(blockId)).
				fill(defaults.get(blockId)).
				fill(overrides.get(defaultId)).
				fill(defaults.get(defaultId)).
				toImmutable();
		});
	}
	
	private void initDefaults() {
		Set<String> namespaces = new HashSet<>();
		
		for (Identifier blockId : Registry.BLOCK.getIds()) {
			loadDefaultProperties(blockId);
			
			if (namespaces.add(blockId.getNamespace())) {
				loadDefaultProperties(new Identifier(blockId.getNamespace(), DEFAULT_ID));
			}
		}
	}
	
	private void loadDefaultProperties(Identifier blockId) {
		String path = String.format("%s/%s/%s%s", RESOURCES_PATH, blockId.getNamespace(), blockId.getPath(), FILE_EXTENSION);
		InputStream resource = getClass().getResourceAsStream(path);
		
		if (resource == null) {
			return;
		}
		
		try (InputStreamReader isr = new InputStreamReader(resource)) {
			loadProperties(defaults, blockId, isr);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	public void reload() {
		overrides.clear();
		cache.clear();
		
		for (File subFolder : folder.listFiles()) {
			if (subFolder.isDirectory()) {
				String namespace = subFolder.getName();
				
				for (File file : subFolder.listFiles()) {
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
			loadProperties(overrides, new Identifier(namespace, path), fr);
		} catch (InvalidIdentifierException | IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	private static void loadProperties(Map<Identifier, MeterProperties> map, Identifier blockId, Reader reader) {
		JsonElement rawJson = GSON.fromJson(reader, JsonElement.class);
		
		if (rawJson.isJsonObject()) {
			JsonObject json = rawJson.getAsJsonObject();
			MeterProperties properties = MeterProperties.fromJson(json);
			
			map.put(blockId, properties);
		}
	}
	
	public void save() {
		for (Entry<Identifier, MeterProperties> entry : overrides.entrySet()) {
			Identifier blockId = entry.getKey();
			MeterProperties properties = entry.getValue();
			
			saveUserOverrides(blockId, properties);
		}
	}
	
	private void saveUserOverrides(Identifier blockId, MeterProperties properties) {
		String namespace = blockId.getNamespace();
		String path = blockId.getPath();
		
		File subFolder = new File(folder, namespace);
		
		if (!subFolder.exists()) {
			subFolder.mkdirs();
		}
		if (!subFolder.isDirectory()) {
			RedstoneMultimeterMod.LOGGER.warn("Unable to save properties for \'" + blockId.toString() + "\' - the \'" + namespace + "\' folder does not exist and cannot be created!");
			return;
		}
		
		File file = new File(subFolder, String.format("%s%s", path, FILE_EXTENSION));
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				
			}
		}
		if (!file.isFile()) {
			RedstoneMultimeterMod.LOGGER.warn("Unable to save properties for \'" + blockId.toString() + "\' - the \'" + path + "\' file does not exist and cannot be created!");
			return;
		}
		
		JsonObject json = properties.toJson();
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(GSON.toJson(json));
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	private void deleteOverrideFile(Identifier blockId) {
		String namespace = blockId.getNamespace();
		String path = blockId.getPath();
		
		File subFolder = new File(folder, namespace);
		
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
