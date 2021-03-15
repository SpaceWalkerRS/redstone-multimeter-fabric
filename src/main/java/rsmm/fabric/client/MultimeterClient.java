package rsmm.fabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;

public class MultimeterClient {
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final MeterRenderer meterRenderer;
	
	private MeterGroup meterGroup;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.meterRenderer = new MeterRenderer(this);
	}
	
	public MinecraftClient getMinecraftClient() {
		return client;
	}
	
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public MeterRenderer getMeterRenderer() {
		return meterRenderer;
	}
	
	public MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	public void subscribeToMeterGroup(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	public void onConnect() {
		subscribeToMeterGroup(new MeterGroup(client.player.getEntityName()));
	}
	
	public void onShutdown() {
		subscribeToMeterGroup(null);
	}
	
	public void toggleMeter() {
		HitResult hitResult = client.crosshairTarget;
		
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
			
			meterGroup.toggleMeter(pos);
			
			ToggleMeterPacket packet = new ToggleMeterPacket(pos);
			packetHandler.sendPacket(packet);
		}
	}
}
