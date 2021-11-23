package redstone.multimeter.client.meter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.BlockState;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.MeterPropertiesManager;
import redstone.multimeter.common.meter.event.EventType;

public class ClientMeterPropertiesManager extends MeterPropertiesManager {
	
	private static final String PROPERTIES_PATH = "meter/default_properties";
	private static final String RESOURCES_PATH = String.format("/assets/%s/%s", RedstoneMultimeterMod.NAMESPACE, PROPERTIES_PATH);
	private static final String FILE_EXTENSION = "json";
	private static final String DEFAULT_ID = "block";
	private static final Gson GSON = new Gson();
	
	private final MultimeterClient client;
	private final File folder;
	private final Map<String, MeterProperties> namespaceDefaults;
	private final Map<Identifier, MeterProperties> blockDefaults;
	
	public ClientMeterPropertiesManager(MultimeterClient multimeterClient) {
		this.client = multimeterClient;
		this.folder = new File(this.client.getConfigFolder(), PROPERTIES_PATH);
		this.namespaceDefaults = new HashMap<>();
		this.blockDefaults = new HashMap<>();
		
		if (!this.folder.exists()) {
			this.folder.mkdirs();
		}
	}
	
	@Override
	protected World getWorldOf(DimPos pos) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		return pos.isOf(minecraftClient.world) ? minecraftClient.world : null;
	}
	
	@Override
	protected void postValidation(MeterProperties properties, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		
		MeterProperties defaultProperties = getPropertiesForBlock(block);
		
		if (defaultProperties != null) {
			properties.fill(defaultProperties);
		}
		
		if (properties.getName() != null && Options.RedstoneMultimeter.NUMBERED_NAMES.get()) {
			String name = properties.getName();
			int number = client.getMeterGroup().getNextMeterIndex();
			
			properties.setName(String.format("%s %d", name, number));
		}
		if (Options.RedstoneMultimeter.SHIFTY_METERS.get()) {
			properties.setMovable(!Screen.method_2223());
		}
		for (int index = 0; index < EventType.ALL.length; index++) {
			KeyBinding keyBind = KeyBindings.TOGGLE_EVENT_TYPES[index];
			
			if (keyBind.isPressed()) {
				EventType type = EventType.ALL[index];
				properties.toggleEventType(type);
			}
		}
		if (Options.RedstoneMultimeter.AUTO_RANDOM_TICKS.get() && state.method_73312()) {
			if (!properties.hasEventType(EventType.RANDOM_TICK)) {
				properties.toggleEventType(EventType.RANDOM_TICK);
			}
		}
	}
	
	private MeterProperties getPropertiesForBlock(Block block) {
		Identifier blockId = Registry.Registry.getId(block);
		
		if (blockId == null) {
			return null; // we should never get here
		}
		
		MeterProperties properties = blockDefaults.get(blockId);
		
		if (properties == null) {
			properties = getPropertiesForNamespace(blockId.getNamespace());
		}
		
		return properties;
	}
	
	private MeterProperties getPropertiesForNamespace(String namespace) {
		MeterProperties properties = namespaceDefaults.get(namespace);
		
		if (properties == null) {
			properties = namespaceDefaults.get(RedstoneMultimeterMod.MINECRAFT_NAMESPACE);
		}
		
		return properties;
	}
	
	public void reload() {
		namespaceDefaults.clear();
		blockDefaults.clear();
		
		Set<String> namespaces = new HashSet<>();
		
		for (Identifier blockId : Registry.Registry.getIds()) {
			String namespace = blockId.getNamespace();
			String id = blockId.getPath();
			
			loadDefaultProperties(namespace, id);
			loadUserOverrides(namespace, id);
			
			namespaces.add(namespace);
		}
		for (String namespace : namespaces) {
			loadDefaultProperties(namespace, DEFAULT_ID);
			loadUserOverrides(namespace, DEFAULT_ID);
		}
	}
	
	private void loadDefaultProperties(String namespace, String id) {
		String path = String.format("%s/%s/%s.%s", RESOURCES_PATH, namespace, id, FILE_EXTENSION);
		InputStream resource = getClass().getResourceAsStream(path);
		
		if (resource == null) {
			return;
		}
		
		try (InputStreamReader isr = new InputStreamReader(resource)) {
			loadProperties(namespace, id, isr);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	private void loadUserOverrides(String namespace, String id) {
		String path = String.format("%s/%s.%s", namespace, id, FILE_EXTENSION);
		File file = new File(folder, path);
		
		if (!file.exists() || !file.isFile()) {
			return;
		}
		
		try (FileReader fr = new FileReader(file)) {
			loadProperties(namespace, id, fr);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	private void loadProperties(String namespace, String id, Reader reader) {
		JsonElement rawJson = GSON.fromJson(reader, JsonElement.class);
		
		if (rawJson.isJsonObject()) {
			JsonObject json = rawJson.getAsJsonObject();
			MeterProperties properties = MeterProperties.fromJson(json);
			
			register(namespace, id, properties);
		}
	}
	
	private void register(String namespace, String path, MeterProperties properties) {
		if (path.equals(DEFAULT_ID)) {
			namespaceDefaults.put(namespace, properties);
		} else {
			blockDefaults.put(new Identifier(namespace, path), properties);
		}
	}
}
