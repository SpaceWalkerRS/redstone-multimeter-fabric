package rsmm.fabric.client;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.RecolorMeterPacket;
import rsmm.fabric.common.packet.types.RemoveAllMetersPacket;
import rsmm.fabric.common.packet.types.RenameMeterPacket;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;

public class MultimeterClient {
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final MeterRenderer meterRenderer;
	private final MultimeterHudRenderer hudRenderer;
	
	private MeterGroup meterGroup;
	private boolean connected; // true if the client is connected to a MultimeterServer
	private boolean renderHud;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.meterRenderer = new MeterRenderer(this);
		this.hudRenderer = new MultimeterHudRenderer(this);
		
		this.renderHud = true;
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
	
	public MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	public boolean renderHud() {
		return renderHud;
	}
	
	public void onStartup() {
		meterGroup = new MeterGroup(client.getSession().getUsername());
	}
	
	public void onShutdown() {
		
	}
	
	/**
	 * Called when this client connects to a MultimeterServer
	 */
	public void onConnect() {
		connected = true;
		
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		meterGroup.encode(data);
		
		MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup.getName(), data);
		packetHandler.sendPacket(packet);
	}
	
	public void onDisconnect() {
		if (connected) {
			connected = false;
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
	
	public void pauseMeters() {
		if (getMeterGroup() == null) {
			return;
		}
	}
	
	public void stepForward() {
		if (getMeterGroup() == null) {
			return;
		}
	}
	
	public void stepBackward() {
		if (getMeterGroup() == null) {
			return;
		}
	}
	
	public void toggleHud() {
		renderHud = !renderHud;
	}
	
	public void renameMeter(int index, String name) {
		RenameMeterPacket packet = new RenameMeterPacket(index, name);
		packetHandler.sendPacket(packet);
	}
	
	public void recolorMeter(int index, int color) {
		RecolorMeterPacket packet = new RecolorMeterPacket(index, color);
		packetHandler.sendPacket(packet);
	}
	
	public void removeMeters() {
		RemoveAllMetersPacket packet = new RemoveAllMetersPacket();
		packetHandler.sendPacket(packet);
	}
	
	public void meterGroupDataReceived(String name, PacketByteBuf data) {
		if (!meterGroup.getName().equals(name)) {
			meterGroup = new MeterGroup(name);
		}
		
		meterGroup.updateFromData(data);
	}
	
	public void meterDataReceived() {
		
	}
}
