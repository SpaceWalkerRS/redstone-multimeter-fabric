package rsmm.fabric.client;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.client.gui.MultimeterHudRenderer;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;

public class MultimeterClient {
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final MeterRenderer meterRenderer;
	private final MultimeterHudRenderer hudRenderer;
	
	private ClientMeterGroup meterGroup;
	private boolean connected; // true if the client is connected to a MultimeterServer
	private boolean renderHud;
	private long currentServerTick;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.meterRenderer = new MeterRenderer(this);
		this.hudRenderer = new MultimeterHudRenderer(this);
		
		this.renderHud = true;
		this.currentServerTick = -1;
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
	
	public boolean renderHud() {
		return renderHud && connected;
	}
	
	public long getCurrentServerTick() {
		return currentServerTick;
	}
	
	public long getSelectedTick() {
		return currentServerTick + hudRenderer.getOffset();
	}
	
	public void onServerTick(long serverTick) {
		currentServerTick = serverTick;
		
		meterGroup.getLogManager().clearOldLogs();
		hudRenderer.tick();
	}
	
	public void onStartup() {
		meterGroup = new ClientMeterGroup(this, client.getSession().getUsername());
	}
	
	public void onShutdown() {
		
	}
	
	/**
	 * Called when this client connects to a MultimeterServer
	 */
	public void onConnect(long serverTick) {
		if (!connected) {
			connected = true;
			currentServerTick = serverTick;
			
			PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
			meterGroup.encode(data);
			
			MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup.getName(), data);
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
	
	public void toggleMeter() {
		HitResult hitResult = client.crosshairTarget;
		
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			World world = client.world;
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			
			WorldPos pos = new WorldPos(world, blockPos);
			
			ToggleMeterPacket packet = new ToggleMeterPacket(pos, !Screen.hasControlDown());
			packetHandler.sendPacket(packet);
		}
	}
	
	public void toggleHud() {
		renderHud = !renderHud;
	}
	
	public void meterGroupDataReceived(String name, PacketByteBuf data) {
		if (!meterGroup.getName().equals(name)) {
			meterGroup = new ClientMeterGroup(this, name);
		}
		
		meterGroup.updateFromData(data);
	}
}
