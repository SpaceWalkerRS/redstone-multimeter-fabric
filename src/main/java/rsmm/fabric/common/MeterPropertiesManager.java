package rsmm.fabric.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import rsmm.fabric.RedstoneMultimeterMod;

public abstract class MeterPropertiesManager {
	
	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	protected final File folder;
	protected final Map<Identifier, MeterProperties> blockDefaults;
	protected final Map<String, MeterProperties> namespaceDefaults;
	
	protected MeterPropertiesManager(File dir) {
		this.folder = new File(dir, "default_meter_properties");
		this.blockDefaults = new HashMap<>();
		this.namespaceDefaults = new HashMap<>();
	}
	
	public boolean validate(MeterProperties properties) {
		WorldPos pos = properties.getPos();
		
		if (pos == null) {
			return false;
		}
		
		World world = getWorldOf(pos);
		
		if (world == null) {
			return false;
		}
		
		BlockPos blockPos = pos.getBlockPos();
		BlockState state = world.getBlockState(blockPos);
		Block block = state.getBlock();
		
		MeterProperties defaults = getDefaults(block);
		
		if (defaults != null) {
			properties.fill(defaults);
		}
		
		postValidation(properties);
		
		return true;
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
	
	protected abstract World getWorldOf(WorldPos pos);
	
	protected abstract void postValidation(MeterProperties properties);
	
	protected boolean load() {
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
	
	protected void register(String namespace, String path, MeterProperties properties) {
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
}
