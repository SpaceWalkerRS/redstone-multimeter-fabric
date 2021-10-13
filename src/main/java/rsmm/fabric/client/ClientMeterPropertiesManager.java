package rsmm.fabric.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.client.option.Options;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.MeterPropertiesManager;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;

public class ClientMeterPropertiesManager extends MeterPropertiesManager {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private final MultimeterClient client;
	private final File folder;
	private final Map<Identifier, MeterProperties> blockDefaults;
	private final Map<String, MeterProperties> namespaceDefaults;
	
	public ClientMeterPropertiesManager(MultimeterClient multimeterClient) {
		this.client = multimeterClient;
		this.folder = new File(this.client.getConfigFolder(), "default_meter_properties");
		this.blockDefaults = new HashMap<>();
		this.namespaceDefaults = new HashMap<>();
		
		if (!load()) {
			createDefaults();
			save();
		}
	}
	
	@Override
	protected World getWorldOf(WorldPos pos) {
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
		if (properties.getMovable() == null) {
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
			properties = namespaceDefaults.get(Identifier.DEFAULT_NAMESPACE);
		}
		
		return properties;
	}
	
	private boolean load() {
		if (!folder.exists()) {
			folder.mkdirs();
			return false;
		}
		
		for (File subFolder : folder.listFiles()) {
			if (subFolder.isDirectory()) {
				for (File file : subFolder.listFiles()) {
					loadProperties(file, subFolder.getName());
				}
			}
		}
		
		return true;
	}
	
	private void loadProperties(File file, String namespace) {
		if (!file.isFile()) {
			return;
		}
		
		String fileName = file.getName();
		
		if (!fileName.endsWith(".json")) {
			return;
		}
		
		String id = fileName.substring(0, fileName.length() - 5);
		
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
			if (namespaceDefaults.putIfAbsent(namespace, properties) != null) {
				RedstoneMultimeterMod.LOGGER.warn("Tried to register multiple default meter properties for namespace " + namespace);
			}
		} else {
			Identifier blockId = new Identifier(namespace, path);
			
			if (Registry.BLOCK.containsId(blockId)) {
				if (blockDefaults.putIfAbsent(blockId, properties) != null) {
					RedstoneMultimeterMod.LOGGER.warn("Tried to register multiple default meter properties for block " + blockId.toString());
				}
			}
		}
	}
	
	private void createDefaults() {
		MeterProperties properties = new MeterProperties();
		properties.setName("Meter");
		properties.setMovable(true);
		properties.setEventTypes(EventType.POWERED.flag() | EventType.MOVED.flag());
		register(Identifier.DEFAULT_NAMESPACE, "*", properties);
		
		properties = new MeterProperties();
		properties.setEventTypes(EventType.ACTIVE.flag());
		register(properties, Blocks.END_PORTAL_FRAME);
		register(properties, Blocks.LECTERN);
		register(properties, Blocks.LEVER);
		register(properties, Blocks.REPEATER);
		register(properties, Blocks.TRIPWIRE);
		register(properties, Blocks.TRIPWIRE_HOOK);
		
		properties.setName("button");
		register(properties, Blocks.ACACIA_BUTTON);
		register(properties, Blocks.BIRCH_BUTTON);
		register(properties, Blocks.CRIMSON_BUTTON);
		register(properties, Blocks.DARK_OAK_BUTTON);
		register(properties, Blocks.JUNGLE_BUTTON);
		register(properties, Blocks.OAK_BUTTON);
		register(properties, Blocks.POLISHED_BLACKSTONE_BUTTON);
		register(properties, Blocks.SPRUCE_BUTTON);
		register(properties, Blocks.STONE_BUTTON);
		register(properties, Blocks.WARPED_BUTTON);
		
		properties.setName("pressure_plate");
		register(properties, Blocks.ACACIA_PRESSURE_PLATE);
		register(properties, Blocks.BIRCH_PRESSURE_PLATE);
		register(properties, Blocks.CRIMSON_PRESSURE_PLATE);
		register(properties, Blocks.DARK_OAK_PRESSURE_PLATE);
		register(properties, Blocks.JUNGLE_PRESSURE_PLATE);
		register(properties, Blocks.OAK_PRESSURE_PLATE);
		register(properties, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
		register(properties, Blocks.SPRUCE_PRESSURE_PLATE);
		register(properties, Blocks.STONE_PRESSURE_PLATE);
		register(properties, Blocks.WARPED_PRESSURE_PLATE);
		
		properties.setName("redstone_torch");
		register(properties, Blocks.REDSTONE_TORCH);
		register(properties, Blocks.REDSTONE_WALL_TORCH);
		
		
		properties = new MeterProperties();
		properties.setEventTypes(EventType.POWERED.flag());
		register(properties, Blocks.BELL);
		register(properties, Blocks.DISPENSER);
		register(properties, Blocks.DROPPER);
		register(properties, Blocks.HOPPER);
		
		properties = new MeterProperties();
		properties.setEventTypes(EventType.ACTIVE.flag() | EventType.MOVED.flag());
		register(properties, Blocks.DETECTOR_RAIL);
		register(properties, Blocks.LIGHTNING_ROD);
		register(properties, Blocks.OBSERVER);
		register(properties, Blocks.PISTON);
		register(properties, Blocks.STICKY_PISTON);
		
		
		properties = new MeterProperties();
		properties.setEventTypes(EventType.ACTIVE.flag() | EventType.POWER_CHANGE.flag());
		register(properties, Blocks.COMPARATOR);
		register(properties, Blocks.DAYLIGHT_DETECTOR);
		register(properties, Blocks.REDSTONE_WIRE);
		register(properties, Blocks.SCULK_SENSOR);
		register(properties, Blocks.TRAPPED_CHEST);
		
		properties.setName("weighted_pressure_plate");
		register(properties, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		register(properties, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
	}
	
	private void register(MeterProperties properties, Block block) {
		properties = properties.copy();
		Identifier blockId = Registry.BLOCK.getId(block);
		
		if (properties.getName() == null) {
			properties.setName(blockId.getPath());
		}
		
		register(blockId.getNamespace(), blockId.getPath(), properties);
	}
	
	private void save() {
		for (Entry<Identifier, MeterProperties> entry : blockDefaults.entrySet()) {
			Identifier blockId = entry.getKey();
			MeterProperties properties = entry.getValue();
			
			save(blockId.getNamespace(), blockId.getPath(), properties);
		}
		for (Entry<String, MeterProperties> entry : namespaceDefaults.entrySet()) {
			String namespace = entry.getKey();
			MeterProperties properties = entry.getValue();
			
			save(namespace, "*", properties);
		}
	}
	
	private void save(String namespace, String path, MeterProperties properties) {
		File dir = new File(folder, namespace);
		
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File file = new File(dir, path + ".json");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				
			}
		}
		
		JsonObject json = properties.toJson();
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(GSON.toJson(json));
		} catch (IOException | JsonSyntaxException | JsonIOException e) {
			
		}
	}
}
