package rsmm.fabric.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.MeterPropertiesManager;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.util.ColorUtils;

public class ServerMeterPropertiesManager extends MeterPropertiesManager {
	
	private final Multimeter multimeter;
	
	public ServerMeterPropertiesManager(Multimeter multimeter) {
		super(multimeter.getMultimeterServer().getWorldSaveDataFolder());
		
		this.multimeter = multimeter;
		
		if (!this.load()) {
			createDefaults();
			save();
		}
	}
	
	@Override
	protected World getWorldOf(WorldPos pos) {
		return multimeter.getMultimeterServer().getWorldOf(pos);
	}
	
	@Override
	protected void postValidation(MeterProperties properties) {
		// These are the backup values for if the saved defaults
		// do not fully populate the meter settings.
		
		if (properties.getName() == null) {
			properties.setName("Meter");
		}
		if (properties.getColor() == null) {
			properties.setColor(ColorUtils.nextColor());
		}
		if (properties.getMovable() == null) {
			properties.setMovable(true);
		}
		if (properties.getEventTypes() == null) {
			properties.setEventTypes(EventType.POWERED.flag() | EventType.MOVED.flag());
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
		for (Block block : BlockTags.BUTTONS.values()) {
			register(properties, block);
		}
		
		properties.setName("pressure_plate");
		for (Block block : BlockTags.STONE_PRESSURE_PLATES.values()) {
			register(properties, block);
		}
		for (Block block : BlockTags.WOODEN_PRESSURE_PLATES.values()) {
			register(properties, block);
		}
		
		properties.setName("redstone_torch");
		register(properties, Blocks.REDSTONE_TORCH);
		register(properties, Blocks.REDSTONE_WALL_TORCH);
		
		
		properties = new MeterProperties();
		properties.setEventTypes(EventType.POWERED.flag());
		register(properties, Blocks.BELL);
		register(properties, Blocks.DISPENSER);
		register(properties, Blocks.DROPPER);
		register(properties, Blocks.HOPPER);
		
		properties.setName("door");
		for (Block block : BlockTags.DOORS.values()) {
			register(properties, block);
		}
		
		
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
