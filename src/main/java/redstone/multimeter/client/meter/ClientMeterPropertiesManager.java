package redstone.multimeter.client.meter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
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
	
	private static final String RESOURCES_PATH = "meter/default_properties";
	private static final String FILE_EXTENSION = ".json";
	private static final Gson GSON = new Gson();
	
	private final MultimeterClient client;
	private final File folder;
	private final Map<String, MeterProperties> namespaceDefaults;
	private final Map<Identifier, MeterProperties> blockDefaults;
	
	public ClientMeterPropertiesManager(MultimeterClient multimeterClient) {
		this.client = multimeterClient;
		this.folder = new File(this.client.getConfigFolder(), RESOURCES_PATH);
		this.namespaceDefaults = new HashMap<>();
		this.blockDefaults = new HashMap<>();
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
		
		MeterProperties defaults = getDefaults(block);
		
		if (defaults != null) {
			properties.fill(defaults);
		}
		
		if (properties.getName() != null && Options.RedstoneMultimeter.NUMBERED_NAMES.get()) {
			String name = properties.getName();
			name = String.format("%s %d", name, client.getMeterGroup().getNextMeterIndex());
			
			properties.setName(name);
		}
		if (properties.getMovable() == null && Options.RedstoneMultimeter.SHIFTY_METERS.get()) {
			properties.setMovable(!Screen.hasShiftDown());
		}
		if (properties.getEventTypes() == null) {
			for (int index = 0; index < EventType.ALL.length; index++) {
				KeyBinding keyBind = KeyBindings.TOGGLE_EVENT_TYPES[index];
				
				if (keyBind.isPressed()) {
					EventType type = EventType.ALL[index];
					properties.toggleEventType(type);
				}
			}
		}
	}
	
	private MeterProperties getDefaults(Block block) {
		Identifier blockId = Registry.BLOCK.getId(block);
		
		if (blockId == null) {
			return null; // we should never get here
		}
		
		MeterProperties properties = blockDefaults.get(blockId);
		
		if (properties == null) {
			properties = getDefaults(blockId.getNamespace());
		}
		
		return properties;
	}
	
	private MeterProperties getDefaults(String namespace) {
		MeterProperties properties = namespaceDefaults.get(namespace);
		
		if (properties == null) {
			properties = namespaceDefaults.get(RedstoneMultimeterMod.MINECRAFT_NAMESPACE);
		}
		
		return properties;
	}
	
	public void reload() {
		namespaceDefaults.clear();
		blockDefaults.clear();
		
		loadDefaultProperties();
		loadUserProperties();
	}
	
	private void loadDefaultProperties() {
		String namespace = RedstoneMultimeterMod.NAMESPACE;
		String path = String.format("/assets/%s/%s", namespace, RESOURCES_PATH);
		URL resource = getClass().getResource(path);
		
		if (resource == null) {
			return;
		}
		
		try {
			load(new File(resource.toURI()), false);
		} catch (URISyntaxException e) {
			
		}
		
	}
	
	private void loadUserProperties() {
		load(folder, true);
	}
	
	private void load(File folder, boolean createFolder) {
		if (!folder.exists()) {
			if (createFolder) {
				folder.mkdirs();
			}
			
			return;
		}
		
		for (File subFolder : folder.listFiles()) {
			if (subFolder.isDirectory()) {
				for (File file : subFolder.listFiles()) {
					loadProperties(file, subFolder.getName());
				}
			}
		}
	}
	
	private void loadProperties(File file, String namespace) {
		if (!file.isFile()) {
			return;
		}
		
		String fileName = file.getName();
		
		if (!fileName.endsWith(FILE_EXTENSION)) {
			return;
		}
		
		String id = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
		
		try (FileReader fr = new FileReader(file)) {
			JsonElement rawJson = GSON.fromJson(fr, JsonElement.class);
			
			if (!rawJson.isJsonObject()) {
				return;
			}
			
			JsonObject json = rawJson.getAsJsonObject();
			MeterProperties properties = MeterProperties.fromJson(json);
			
			register(namespace, id, properties);
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
	
	private void register(String namespace, String path, MeterProperties properties) {
		if (path.equals("*")) {
			namespaceDefaults.put(namespace, properties);
		} else {
			Identifier blockId = new Identifier(namespace, path);
			
			if (Registry.BLOCK.containsId(blockId)) {
				blockDefaults.put(blockId, properties);
			} else {
				RedstoneMultimeterMod.LOGGER.info(String.format("Unable to load default meter properties for %s: that block does not exist!", blockId.toString()));
			}
		}
	}
}
