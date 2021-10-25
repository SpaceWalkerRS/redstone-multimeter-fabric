package rsmm.fabric.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rsmm.fabric.common.meter.event.EventType;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.interfaces.mixin.IServerCommandSource;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

public class MeterCommand {
	
	private static final Collection<String> TYPE_NAMES = new ArrayList<>();
	
	static {
		for (EventType type : EventType.ALL) {
			TYPE_NAMES.add(type.getName());
		}
	}
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("meter").
			then(CommandManager.
				literal("removeAll").
				executes(context -> removeAll(context.getSource()))).
			then(CommandManager.
				literal("group").
				then(CommandManager.
					argument("name", StringArgumentType.greedyString()).
					suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(((IMinecraftServer)context.getSource().getServer()).getMultimeterServer().getMultimeter().getMeterGroupNames(), suggestionsBuilder)).
					executes(context -> subscribeToGroup(context.getSource(), StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				literal("listGroups").
				executes(context -> listGroups(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static int removeAll(ServerCommandSource source) {
		return meterCommand(source, (multimeter, player) -> {
			if (multimeter.hasSubscription(player)) {
				multimeter.removeAllMeters(player);
				source.sendFeedback(new LiteralText(String.format("Removed all meters in meter group \'%s\'", multimeter.getSubscription(player).getName())), false);
				
				return 1;
			} else {
				source.sendFeedback(new LiteralText("Please subscribe to a meter group first!"), false);
				return 0;
			}
		});
	}
	
	private static int subscribeToGroup(ServerCommandSource source, String name) {
		return meterCommand(source, (multimeter, player) -> {
			multimeter.subscribeToMeterGroup(name, player);
			source.sendFeedback(new LiteralText(String.format("Subscribed to meter group \'%s\'", name)), false);
			
			return 1;
		});
	}
	
	private static int meterCommand(ServerCommandSource source, BiFunction<Multimeter, ServerPlayerEntity, Integer> command) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MinecraftServer server = source.getServer();
			MultimeterServer multimeterServer = ((IMinecraftServer)server).getMultimeterServer();
			Multimeter multimeter = multimeterServer.getMultimeter();
			
			return command.apply(multimeter, player);
		} catch (CommandSyntaxException e) {
			return 0;
		}
	}
	
	private static int listGroups(ServerCommandSource source) {
		MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		String message = "Meter groups:";
		
		for (String name : multimeter.getMeterGroupNames()) {
			message += "\n  " + name;
		}
		
		source.sendFeedback(new LiteralText(message), false);
		
		return 1;
	}
}
