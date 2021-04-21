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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rsmm.fabric.command.argument.MeterEventArgumentType;
import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.event.EventType;
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
					executes(context -> renameMeter(context.getSource(), null, StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				literal("color").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					then(CommandManager.
						argument("color", IntegerArgumentType.integer(0, ColorUtils.MAX_COLOR)).
						executes(context -> recolorMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), IntegerArgumentType.getInteger(context, "color"))))).
				then(CommandManager.
					argument("color", IntegerArgumentType.integer(0, ColorUtils.MAX_COLOR)).
					executes(context -> recolorMeter(context.getSource(), null, IntegerArgumentType.getInteger(context, "color"))))).
			then(CommandManager.
				literal("event").
				executes(context -> listMeteredEvents(context.getSource(), null)).
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					executes(context -> listMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index")))).
				then(CommandManager.
					literal("start").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						then(CommandManager.
							argument("type", MeterEventArgumentType.eventType()).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), MeterEventArgumentType.getEventType(context, "type"), true)))).
					then(CommandManager.
						argument("type", MeterEventArgumentType.eventType()).
						executes(context -> updateMeteredEvents(context.getSource(), null, MeterEventArgumentType.getEventType(context, "type"), true)))).
				then(CommandManager.
					literal("stop").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						then(CommandManager.
							argument("type", MeterEventArgumentType.eventType()).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), MeterEventArgumentType.getEventType(context, "type"), false)))).
					then(CommandManager.
						argument("type", MeterEventArgumentType.eventType()).
						executes(context -> updateMeteredEvents(context.getSource(), null, MeterEventArgumentType.getEventType(context, "type"), false))))).
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
	
	private static int updateMeter(ServerCommandSource source, Integer index, UpdateMeter update) {
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
			
			if (index == null) {
				HitResult hitResult = player.rayTrace(player.interactionManager.getGameMode().isCreative() ? 5.0F : 4.5F, server.getMinecraftServer().getTickTime(), false);
				
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					World world = player.world;
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					
					DimPos pos = new DimPos(world, blockPos);
					
					index = meterGroup.indexOfMeterAt(pos);
				} else {
					index = -1;
				}
			}
			
			if (index < 0) {
				index = meterCount + index;
			}
			
			if (index < 0 || index >= meterCount) {
				source.sendFeedback(new LiteralText("There is no meter at that index!"), false);
				
				return 1;
			} else {
				update.accept(multimeter, index, player);
				
				return 1;
			}
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int renameMeter(ServerCommandSource source, Integer meterIndex, String name) {
		return updateMeter(source, meterIndex, (multimeter, index, player) -> {
			multimeter.renameMeter(index, name, player);
			source.sendFeedback(new LiteralText(String.format("Renamed meter %d to %s", index, name)), false);
		});
	}
	
	private static int recolorMeter(ServerCommandSource source, Integer meterIndex, int color) {
		return updateMeter(source, meterIndex, (multimeter, index, player) -> {
			multimeter.recolorMeter(index, color, player);
			source.sendFeedback(new LiteralText(String.format("Recolored meter %d to %d", index, color)), false);
		});
	}
	
	private static int listMeteredEvents(ServerCommandSource source, Integer meterIndex) {
		return updateMeter(source, meterIndex, (multimeter, index, player) -> {
			Meter meter = multimeter.getSubscription(player).getMeter(index);
			String text = String.format("%s is metering:", meter.getName());
			
			for (EventType type : EventType.TYPES) {
				if (meter.isMetering(type)) {
					text += "\n  " + type.getName();
				}
			}
			
			source.sendFeedback(new LiteralText(text), false);
		});
	}
	
	private static int updateMeteredEvents(ServerCommandSource source, Integer meterIndex, EventType type, boolean start) {
		return updateMeter(source, meterIndex, (multimeter, index, player) -> {
			multimeter.updateMeteredEvents(index, type, start, player);
			source.sendFeedback(new LiteralText(String.format("%s %s metering %s", multimeter.getSubscription(player).getMeter(index).getName(),start ? "started" : "stopped", type.getName())), false);
		});
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
	
	private interface UpdateMeter {
		public void accept(Multimeter multimeter, int index, ServerPlayerEntity player);
	}
}
