package rsmm.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import rsmm.fabric.common.packet.types.ToggleMeterPacket;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.server.MultimeterServer;

public class MeterCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("meter").
			then(CommandManager.
				literal("toggle").
				executes(context -> toggleMeter(context.getSource()))).
			then(CommandManager.
				literal("name").
				executes(context -> 1)).
			then(CommandManager.
				literal("color").
				executes(context -> 1)).
			then(CommandManager.
				literal("removeAll").
				executes(context -> 1)).
			then(CommandManager.
				literal("group").
				executes(context -> 1)).
			then(CommandManager.
				literal("listGroups").
				executes(context -> 1));
		
		dispatcher.register(builder);
	}
	
	private static int toggleMeter(ServerCommandSource source) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			
			if (player != null) {
				MultimeterServer server = ((IMinecraftServer)player.getServer()).getMultimeterServer();
				
				ToggleMeterPacket packet = new ToggleMeterPacket(player.getBlockPos());
				server.getPacketHandler().sendPacketToPlayer(packet, player);
			}
			
			return 1;
		} catch(Exception e) {
			
		}
		
		return 0;
	}
}
