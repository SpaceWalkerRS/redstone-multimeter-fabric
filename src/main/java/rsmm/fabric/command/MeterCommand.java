package rsmm.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rsmm.fabric.common.Multimeter;
import rsmm.fabric.interfaces.mixin.IServerCommandSource;
import rsmm.fabric.server.MultimeterServer;

public class MeterCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("meter").
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
				then(CommandManager.
					argument("name", StringArgumentType.greedyString()).
					executes(context -> subscribeToGroup(context.getSource(), StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				literal("listGroups").
				executes(context -> listGroups(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static int subscribeToGroup(ServerCommandSource source, String name) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			
			server.subscribeToMeterGroup(name, player);
			
			source.sendFeedback(new LiteralText(String.format("Subscribed to meter group \'%s\'", name)), false);
			
			return 1;
		} catch (Exception e) {
			
		}
		
		return 0;
	}
	
	private static int listGroups(ServerCommandSource source) {
		MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		String message = "Meter Groups:";
		
		for (String name : multimeter.getMeterGroupNames()) {
			message += "\n  " + name;
		}
		
		source.sendFeedback(new LiteralText(message), false);
		
		return 1;
	}
}
