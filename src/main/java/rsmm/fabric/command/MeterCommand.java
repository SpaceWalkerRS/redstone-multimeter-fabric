package rsmm.fabric.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.interfaces.mixin.IServerCommandSource;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.server.ServerMeterGroup;
import rsmm.fabric.util.ColorUtils;

public class MeterCommand {
	
	private static final Collection<String> TYPE_NAMES = new ArrayList<>();
	
	static {
		for (EventType type : EventType.TYPES) {
			TYPE_NAMES.add(type.getName());
		}
	}
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("meter").
			then(CommandManager.
				literal("add").
				then(CommandManager.
					argument("pos", BlockPosArgumentType.blockPos()).
					executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), true, null, null)).
					then(CommandManager.
						argument("movable", BoolArgumentType.bool()).
						executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "movable"), null, null)).
						then(CommandManager.
							argument("name", StringArgumentType.string()).
							suggests((context, suggestionsBuilder) -> nameSuggestions(context.getSource(), suggestionsBuilder, null)).
							executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "movable"), StringArgumentType.getString(context, "name"), null)).
							then(CommandManager.
								argument("color", StringArgumentType.word()).
								suggests((context, suggestionsBuilder) -> colorSuggestions(context.getSource(), suggestionsBuilder, null)).
								executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "movable"), StringArgumentType.getString(context, "name"), parseColor(context, "color")))))))).
			then(CommandManager.
				literal("remove").
				then(CommandManager.
					literal("all").
					executes(context -> removeAll(context.getSource()))).
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
					executes(context -> removeMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null)))).
			then(CommandManager.
				literal("name").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
					then(CommandManager.
						argument("name", StringArgumentType.greedyString()).
						suggests((context, suggestionsBuilder) -> nameSuggestions(context.getSource(), suggestionsBuilder, IntegerArgumentType.getInteger(context, "index"))).
						executes(context -> renameMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null, StringArgumentType.getString(context, "name")))))).
			then(CommandManager.
				literal("color").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
					then(CommandManager.
						argument("color", StringArgumentType.word()).
						suggests((context, suggestionsBuilder) -> colorSuggestions(context.getSource(), suggestionsBuilder, IntegerArgumentType.getInteger(context, "index"))).
						executes(context -> recolorMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null, parseColor(context, "color")))))).
			then(CommandManager.
				literal("event").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
					executes(context -> listMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null))).
				then(CommandManager.
					literal("start").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
						then(CommandManager.
							argument("type", StringArgumentType.word()).
							suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(TYPE_NAMES, suggestionsBuilder)).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null, parseEventType(context, "type"), true))))).
				then(CommandManager.
					literal("stop").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						suggests((context, suggestionsBuilder) -> indexSuggestions(context.getSource(), suggestionsBuilder, null)).
						then(CommandManager.
							argument("type", StringArgumentType.word()).
							suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(TYPE_NAMES, suggestionsBuilder)).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), null, parseEventType(context, "type"), false)))))).
			then(CommandManager.
				literal("group").
				then(CommandManager.
					argument("name", StringArgumentType.greedyString()).
					suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(((IMinecraftServer)context.getSource().getMinecraftServer()).getMultimeterServer().getMultimeter().getMeterGroupNames(), suggestionsBuilder)).
					executes(context -> subscribeToGroup(context.getSource(), StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				literal("listGroups").
				executes(context -> listGroups(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static <S> int parseColor(CommandContext<S> context, String name) throws CommandSyntaxException {
		String asString = StringArgumentType.getString(context, name);
		
		if (asString.length() > 6) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
		}
		
		try {
			return ColorUtils.fromString(asString);
		} catch (Exception e) {
			throw new DynamicCommandExceptionType(value -> new LiteralMessage(String.format("Invalid color \'%s\'", value))).create(asString);
		}
	}
	
	private static <S> EventType parseEventType(CommandContext<S> context, String name) throws CommandSyntaxException {
		String typeName = StringArgumentType.getString(context, name);
		EventType type = EventType.fromName(typeName);
		
		if (type == null) {
			throw new DynamicCommandExceptionType(value -> new LiteralMessage(String.format("Unknown event type \'%s\'", value))).create(typeName);
		}
		
		return type;
	}
	
	private static int updateMeter(ServerCommandSource source, Integer index, BlockPos blockPos, UpdateMeter update) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				return 1;
			}
			
			int meterCount = meterGroup.getMeterCount();
			
			if (index == null) {
				if (blockPos == null) {
					HitResult hitResult = player.raycast(player.interactionManager.getGameMode().isCreative() ? 5.0F : 4.5F, server.getMinecraftServer().getTickTime(), false);
					
					if (hitResult.getType() == HitResult.Type.BLOCK) {
						blockPos = ((BlockHitResult)hitResult).getBlockPos();
					}
				}
				
				if (blockPos == null) {
					index = -1;
				} else {
					World world = player.world;
					WorldPos pos = new WorldPos(world, blockPos);
					
					index = meterGroup.indexOfMeterAt(pos);
				}
				
				if (index < 0) {
					index = meterCount + index;
				}
				if (index < 0 || index >= meterCount) {
					source.sendFeedback(new LiteralText("There is no meter at that position!"), false);
					return 1;
				}
			} else {
				if (index < 0) {
					index = meterCount + index;
				}
				if (index < 0 || index >= meterCount) {
					source.sendFeedback(new LiteralText("There is no meter at that index!"), false);
					return 1;
				}
			}
			
			update.accept(multimeter, index, player);
			
			return 1;
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int addMeter(ServerCommandSource source, BlockPos blockPos, boolean movable, String name, Integer color) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				return 1;
			}
			
			WorldPos pos = new WorldPos(player.world, blockPos);
			
			if (meterGroup.hasMeterAt(pos)) {
				source.sendFeedback(new LiteralText("There is already a meter at that position!"), false);
				return 1;
			}
			
			multimeter.addMeter(pos, movable, name, color, player);
			source.sendFeedback(new LiteralText(String.format("Added a meter at %s!", pos.toString())), false);
			
			return 1;
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int removeMeter(ServerCommandSource source, Integer meterIndex, BlockPos pos) {
		return updateMeter(source, meterIndex, pos, (multimeter, index, player) -> {
			multimeter.removeMeter(index, player);
			source.sendFeedback(new LiteralText(String.format("Removed meter #%d", index)), false);
		});
	}
	
	private static int renameMeter(ServerCommandSource source, Integer meterIndex, BlockPos pos, String name) {
		return updateMeter(source, meterIndex, pos, (multimeter, index, player) -> {
			multimeter.renameMeter(index, name, player);
			source.sendFeedback(new LiteralText(String.format("Renamed meter #%d to %s", index, name)), false);
		});
	}
	
	private static int recolorMeter(ServerCommandSource source, Integer meterIndex, BlockPos pos, int color) {
		return updateMeter(source, meterIndex, pos, (multimeter, index, player) -> {
			multimeter.recolorMeter(index, color, player);
			source.sendFeedback(new LiteralText(String.format("Recolored meter #%d to %s", index, ColorUtils.toHexString(color))), false);
		});
	}
	
	private static int listMeteredEvents(ServerCommandSource source, Integer meterIndex, BlockPos pos) {
		return updateMeter(source, meterIndex, pos, (multimeter, index, player) -> {
			Meter meter = multimeter.getSubscription(player).getMeter(index);
			String text = String.format("Meter #%d is metering:", index);
			
			for (EventType type : EventType.TYPES) {
				if (meter.isMetering(type)) {
					text += String.format("\n  %s", type.getName());
				}
			}
			
			source.sendFeedback(new LiteralText(text), false);
		});
	}
	
	private static int updateMeteredEvents(ServerCommandSource source, Integer meterIndex, BlockPos pos, EventType type, boolean start) {
		return updateMeter(source, meterIndex, pos, (multimeter, index, player) -> {
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
	
	private static CompletableFuture<Suggestions> getSuggestionsForMeter(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer index, AddSuggestions addSuggestions) {
		List<String> suggestions = new ArrayList<>();
		
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null) {
				int meterCount = meterGroup.getMeterCount();
				
				if (index == null) {
					HitResult hitResult = player.raycast(player.interactionManager.getGameMode().isCreative() ? 5.0F : 4.5F, server.getMinecraftServer().getTickTime(), false);
					
					if (hitResult.getType() == HitResult.Type.BLOCK) {
						World world = player.world;
						BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
						WorldPos pos = new WorldPos(world, blockPos);
						
						index = meterGroup.indexOfMeterAt(pos);
					} else {
						index = -1;
					}
				} else if (index < 0) {
					index = meterCount + index;
				}
				if (index >= meterCount) {
					index = -1;
				}
				
				addSuggestions.accept(suggestions, meterGroup, index);
			}
		} catch (CommandSyntaxException e) {
			
		}
		
		return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
	}
	
	private static CompletableFuture<Suggestions> indexSuggestions(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer meterIndex) {
		return getSuggestionsForMeter(source, suggestionsBuilder, meterIndex, (suggestions, meterGroup, index) -> {
			if (index >= 0) {
				suggestions.add(String.valueOf(index));
			} else {
				suggestions.add("0");
				suggestions.add("-1");
			}
		});
	}
	
	private static CompletableFuture<Suggestions> nameSuggestions(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer meterIndex) {
		return getSuggestionsForMeter(source, suggestionsBuilder, meterIndex, (suggestions, meterGroup, index) -> {
			if (index >= 0) {
				suggestions.add(meterGroup.getMeter(index).getName());
			} else {
				suggestions.add(String.format("\"%s\"", meterGroup.getNextMeterName()));
			}
		});
	}
	
	private static CompletableFuture<Suggestions> colorSuggestions(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer meterIndex) {
		return getSuggestionsForMeter(source, suggestionsBuilder, meterIndex, (suggestions, meterGroup, index) -> {
			int intColor;
			
			if (index >= 0) {
				intColor = meterGroup.getMeter(index).getColor();
			} else {
				intColor = ColorUtils.nextColor(false);
			}
			
			suggestions.add(ColorUtils.toHexString(intColor));
		});
	}
	
	private interface UpdateMeter {
		public void accept(Multimeter multimeter, int index, ServerPlayerEntity player);
	}
	
	private interface AddSuggestions {
		public void accept(List<String> suggestions, ServerMeterGroup meterGroup, int index);
	}
}
