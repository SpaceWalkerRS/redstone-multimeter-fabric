package rsmm.fabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;

public class MultimeterClient {
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final Multimeter multimeter;
	private final MeterRenderer meterRenderer;
	private final MultimeterHudRenderer multimeterHudRenderer;
	
	private boolean renderHud;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.multimeter = new Multimeter();
		this.meterRenderer = new MeterRenderer(this);
		this.multimeterHudRenderer = new MultimeterHudRenderer(this);
		
		this.renderHud = true;
	}
	
	public MinecraftClient getMinecraftClient() {
		return client;
	}
	
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
	
	public MeterRenderer getMeterRenderer() {
		return meterRenderer;
	}
	
	public MultimeterHudRenderer getMultimeterHudRenderer() {
		return multimeterHudRenderer;
	}
	
	public boolean renderHud() {
		return renderHud;
	}
	
	// Return the MeterGroup this client is subscribed to
	public MeterGroup getMeterGroup() {
		return multimeter.getSubscription(client.player);
	}
	
	public void onStartup() {
		
	}
	
	public void onShutdown() {
		
	}
	
	public void onConnect() {
		
	}
	
	public void onDisconnect() {
		
	}
	
	public void toggleMeter() {
		HitResult hitResult = client.crosshairTarget;
		
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			World world = client.world;
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			
			WorldPos pos = new WorldPos(world, blockPos);
			
			ToggleMeterPacket packet = new ToggleMeterPacket(pos);
			packetHandler.sendPacket(packet);
		}
	}
	
	public void pauseMeters() {
		
	}
	
	public void stepForward() {
		
	}
	
	public void stepBackward() {
		
	}
	
	public void toggleHud() {
		renderHud = !renderHud;
	}
}
