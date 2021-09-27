package rsmm.fabric.client;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.client.gui.MultimeterHudRenderer;
import rsmm.fabric.client.gui.MultimeterScreen;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.network.packets.AddMeterPacket;
import rsmm.fabric.common.network.packets.MeterGroupDataPacket;
import rsmm.fabric.common.network.packets.MeterUpdatePacket;
import rsmm.fabric.common.network.packets.RemoveMeterPacket;
import rsmm.fabric.common.network.packets.ResetMeterPacket;

public class MultimeterClient {
	
	public static final String CONFIG_PATH = "config/" + RedstoneMultimeterMod.MOD_ID;
	private static final Function<String, String> VERSION_WARNING = (modVersion) -> {
		String warning;
		
		if (modVersion.isEmpty()) {
			warning = "WARNING: the server is running an unknown version of Redstone Multimeter. If you are experiencing issues, ask the server operator for the correct version of Redstone Multimeter to install.";
		} else {
			warning = "WARNING: the server is running a different version of Redstone Multimeter. If you are experiencing issues, install version " + modVersion + " of Redstone Multimeter.";
		}
		
		return warning;
	};
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final MeterRenderer meterRenderer;
	private final MultimeterHudRenderer hudRenderer;
	private final ClientMeterPropertiesManager meterPropertiesManager;
	
	private ClientMeterGroup meterGroup;
	private boolean connected; // true if the client is connected to a MultimeterServer
	private boolean renderHud;
	private long lastServerTick;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.meterRenderer = new MeterRenderer(this);
		this.hudRenderer = new MultimeterHudRenderer(this);
		this.meterPropertiesManager = new ClientMeterPropertiesManager(this);
		
		this.renderHud = true;
		this.lastServerTick = -1;
	}
	
	public MinecraftClient getMinecraftClient() {
		return client;
	}
	
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
	
	public MeterRenderer getMeterRenderer() {
		return meterRenderer;
	}
	
	public MultimeterHudRenderer getHudRenderer() {
		return hudRenderer;
	}
	
	public ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	/**
	 * Check if this client is connected to a Multimeter server
	 */
	public boolean isConnected() {
		return connected;
	}
	
	public boolean renderHud() {
		return renderHud && connected && !hasMultimeterScreenOpen();
	}
	
	public long getLastServerTick() {
		return lastServerTick;
	}
	
	public File getConfigFolder() {
		return new File(client.runDirectory, CONFIG_PATH);
	}
	
	/**
	 * At the end of each server tick, the server sends a packet
	 * to clients with the current server time.
	 */
	public void onServerTick(long serverTick) {
		lastServerTick = serverTick;
		
		meterGroup.getLogManager().clearOldLogs();
		hudRenderer.tick();
	}
	
	/**
	 * Called after the MinecraftClient has been initialized
	 */
	public void onStartup() {
		meterGroup = new ClientMeterGroup(this);
		hudRenderer.onStartup();
	}
	
	public void onShutdown() {
		hudRenderer.onShutdown();
	}
	
	/**
	 * Called when this client connects to a Multimeter server
	 */
	public void onConnect(String modVersion, long serverTick) {
		if (!connected) {
			if (!RedstoneMultimeterMod.MOD_VERSION.equals(modVersion)) {
				Text warning = new LiteralText(VERSION_WARNING.apply(modVersion)).formatted(Formatting.RED);
				client.player.sendMessage(warning, false);
			}
			
			connected = true;
			lastServerTick = serverTick;
			
			hudRenderer.reset();
			refreshMeterGroup();
		}
	}
	
	public void onDisconnect() {
		if (connected) {
			connected = false;
			
			hudRenderer.reset();
			meterGroup.reset();
		}
	}
	
	public void refreshMeterGroup() {
		MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup);
		packetHandler.sendPacket(packet);
	}
	
	/**
	 * Request the server to add a meter at the position
	 * the player is looking at or remove it if there
	 * already is one.
	 */
	public void toggleMeter() {
		onTargetBlock(pos -> {
			Meter meter = meterGroup.getMeterAt(pos);
			
			if (meter == null) {
				addMeter(pos);
			} else {
				RemoveMeterPacket packet = new RemoveMeterPacket(meter.getId());
				packetHandler.sendPacket(packet);
			}
		});
	}
	
	private void addMeter(WorldPos pos) {
		MeterProperties properties = new MeterProperties();
		properties.setPos(pos);
		
		if (meterPropertiesManager.validate(properties)) {
			AddMeterPacket packet = new AddMeterPacket(properties);
			packetHandler.sendPacket(packet);
		}
	}
	
	public void resetMeter() {
		onTargetMeter(meter -> {
			MeterProperties newProperties = new MeterProperties();
			newProperties.setPos(meter.getPos());
			
			if (meterPropertiesManager.validate(newProperties)) {
				ResetMeterPacket packet = new ResetMeterPacket(meter.getId(), newProperties);
				packetHandler.sendPacket(packet);
			}
		});
	}
	
	public void toggleEventType(EventType type) {
		onTargetMeter(meter -> {
			MeterProperties newProperties = new MeterProperties();
			newProperties.setEventTypes(meter.getEventTypes());
			newProperties.toggleEventType(type);
			
			MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
			packetHandler.sendPacket(packet);
		});
	}
	
	private void onTargetMeter(Consumer<Meter> consumer) {
		onTargetBlock(pos -> {
			Meter meter = meterGroup.getMeterAt(pos);
			
			if (meter != null) {
				consumer.accept(meter);
			}
		});
	}
	
	private void onTargetBlock(Consumer<WorldPos> consumer) {
		HitResult hitResult = client.crosshairTarget;
		
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			World world = client.world;
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			
			consumer.accept(new WorldPos(world, blockPos));
		}
	}
	
	public void toggleHud() {
		renderHud = !renderHud;
		
		Text text = new LiteralText(String.format("%s Multimeter HUD", renderHud ? "Enabled" : "Disabled"));
		client.player.sendMessage(text, true);
	}
	
	public boolean hasMultimeterScreenOpen() {
		return client.currentScreen != null && client.currentScreen instanceof MultimeterScreen;
	}
}
