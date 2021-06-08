package rsmm.fabric.server;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.packet.types.JoinMultimeterServerPacket;
import rsmm.fabric.common.packet.types.ServerTickPacket;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter(this);
	}
	
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public void tickStart() {
		multimeter.tickStart();
	}
	
	public void tickEnd(boolean paused) {
		if (!paused) {
			ServerTickPacket packet = new ServerTickPacket(server.getTicks());
			
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (multimeter.hasSubscription(player)) {
					packetHandler.sendPacketToPlayer(packet, player);
				}
			}
		}
		
		multimeter.tickEnd(paused);
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		JoinMultimeterServerPacket packet = new JoinMultimeterServerPacket(server.getTicks());
		packetHandler.sendPacketToPlayer(packet, player);
		
		multimeter.onPlayerJoin(player);
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.onPlayerLeave(player);
	}
	
	public ServerWorld getWorld(Identifier dimensionId) {
		DimensionType dimensionType = DimensionType.byId(dimensionId);
		return server.getWorld(dimensionType);
	}
	
	public ServerWorld getWorldOf(DimPos pos) {
		return getWorld(pos.getDimensionId());
	}
	
	public BlockState getBlockState(DimPos pos) {
		World world = getWorldOf(pos);
		
		if (world != null) {
			return world.getBlockState(pos.asBlockPos());
		}
		
		return null;
	}
}
