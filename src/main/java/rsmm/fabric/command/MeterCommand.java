package rsmm.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.interfaces.mixin.IServerCommandSource;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.ColorUtils;

public class MeterCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("meter").
			then(CommandManager.
				literal("name").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					then(CommandManager.
						argument("name", StringArgumentType.greedyString()).
						executes(context -> renameMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "name"))))).
				then(CommandManager.
					argument("name", StringArgumentType.greedyString()).
					executes(context -> renameMeter(context.getSource(), -1, StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				literal("color").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					then(CommandManager.
						argument("color", IntegerArgumentType.integer(0, ColorUtils.MAX_COLOR)).
						executes(context -> recolorMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), IntegerArgumentType.getInteger(context, "color"))))).
				then(CommandManager.
					argument("color", IntegerArgumentType.integer(0, ColorUtils.MAX_COLOR)).
					executes(context -> recolorMeter(context.getSource(), -1, IntegerArgumentType.getInteger(context, "color"))))).
			then(CommandManager.
				literal("removeAll").
				executes(context -> removeAll(context.getSource()))).
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
	
	private static int renameMeter(ServerCommandSource source, int index, String name) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				
				return 1;
			}
			
			int meterCount = meterGroup.getMeterCount();
			
			if (index < 0) {
				index = meterCount + index;
			}
			
			if (index < 0 || index >= meterCount) {
				source.sendFeedback(new LiteralText("There is no meter at that index!"), false);
				
				return 1;
			} else {
				multimeter.renameMeter(index, name, player);
				source.sendFeedback(new LiteralText(String.format("Renamed meter %d to %s", index, name)), false);
				
				return 1;
			}
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int recolorMeter(ServerCommandSource source, int index, int color) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			MeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				
				return 1;
			}
			
			int meterCount = meterGroup.getMeterCount();
			
			if (index < 0) {
				index = meterCount + index;
			}
			
			if (index < 0 || index >= meterCount) {
				source.sendFeedback(new LiteralText("There is no meter at that index!"), false);
				
				return 1;
			} else {
				multimeter.recolorMeter(index, color, player);
				source.sendFeedback(new LiteralText(String.format("Recolored meter %d to %d", index, color)), false);
				
				return 1;
			}
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int removeAll(ServerCommandSource source) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			if (!multimeter.hasSubscription(player)) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				
				return 1;
			}
			
			multimeter.removeAllMeters(player);
			source.sendFeedback(new LiteralText("Removed all meters"), false);
			
			return 1;
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int subscribeToGroup(ServerCommandSource source, String name) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			multimeter.subscribeToMeterGroup(name, player);
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
