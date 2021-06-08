package rsmm.fabric.client;

import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundTag;
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
import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;
import rsmm.fabric.util.NBTUtils;

public class MultimeterClient {
	
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
				client.player.addChatMessage(warning, false);
			}
			
			connected = true;
			lastServerTick = serverTick;
			
			hudRenderer.reset();
			
			MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup);
			packetHandler.sendPacket(packet);
		}
	}
	
	public void onDisconnect() {
		if (connected) {
			connected = false;
			
			hudRenderer.reset();
			meterGroup.getLogManager().clearLogs();
		}
	}
	
	/**
	 * Request the server to add a meter at the position
	 * the player is looking at or remove it if there
	 * already is one.
	 */
	public void toggleMeter() {
		HitResult hitResult = client.crosshairTarget;
		
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			World world = client.world;
			BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
			
			CompoundTag properties = new CompoundTag();
			
			properties.put("pos", NBTUtils.dimPosToTag(new DimPos(world, pos)));
			properties.putBoolean("movable", !Screen.hasControlDown());
			
			ToggleMeterPacket packet = new ToggleMeterPacket(properties);
			packetHandler.sendPacket(packet);
		}
	}
	
	public void toggleHud() {
		renderHud = !renderHud;
		
		Text text = new LiteralText(String.format("%s Multimeter HUD", renderHud ? "Enabled" : "Disabled"));
		client.player.addChatMessage(text, true);
	}
	
	public boolean hasMultimeterScreenOpen() {
		return client.currentScreen != null && client.currentScreen instanceof MultimeterScreen;
	}
	
	/**
	 * Whenever a player subscribes to a meter group,
	 * the server sends all the data pertaining to
	 * meters in that meter group to that client.
	 */
	public void meterGroupDataReceived(String name, CompoundTag data) {
		if (!meterGroup.getName().equals(name)) {
			meterGroup = new ClientMeterGroup(this, name);
		}
		
		meterGroup.updateFromTag(data);
	}
}
