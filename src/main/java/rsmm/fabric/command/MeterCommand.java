package rsmm.fabric.command;

import java.util.ArrayList;
import java.util.Collection;
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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
							suggests((context, suggestionsBuilder) -> suggestNames(context.getSource(), suggestionsBuilder, null, BlockPosArgumentType.getBlockPos(context, "pos"))).
							executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "movable"), StringArgumentType.getString(context, "name"), null)).
							then(CommandManager.
								argument("color", StringArgumentType.word()).
								suggests((context, suggestionsBuilder) -> suggestColors(context.getSource(), suggestionsBuilder, null, BlockPosArgumentType.getBlockPos(context, "pos"))).
								executes(context -> addMeter(context.getSource(), BlockPosArgumentType.getBlockPos(context, "pos"), BoolArgumentType.getBool(context, "movable"), StringArgumentType.getString(context, "name"), parseColor(context, "color")))))))).
			then(CommandManager.
				literal("remove").
				then(CommandManager.
					literal("all").
					executes(context -> removeAll(context.getSource()))).
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
					executes(context -> removeMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"))))).
			then(CommandManager.
				literal("name").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
					then(CommandManager.
						argument("name", StringArgumentType.greedyString()).
						suggests((context, suggestionsBuilder) -> suggestNames(context.getSource(), suggestionsBuilder, IntegerArgumentType.getInteger(context, "index"), null)).
						executes(context -> renameMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "name")))))).
			then(CommandManager.
				literal("color").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
					then(CommandManager.
						argument("color", StringArgumentType.word()).
						suggests((context, suggestionsBuilder) -> suggestColors(context.getSource(), suggestionsBuilder, IntegerArgumentType.getInteger(context, "index"), null)).
						executes(context -> recolorMeter(context.getSource(), IntegerArgumentType.getInteger(context, "index"), parseColor(context, "color")))))).
			then(CommandManager.
				literal("event").
				then(CommandManager.
					argument("index", IntegerArgumentType.integer()).
					suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
					executes(context -> listMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index")))).
				then(CommandManager.
					literal("start").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
						then(CommandManager.
							argument("type", StringArgumentType.word()).
							suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(TYPE_NAMES, suggestionsBuilder)).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), parseEventType(context, "type"), true))))).
				then(CommandManager.
					literal("stop").
					then(CommandManager.
						argument("index", IntegerArgumentType.integer()).
						suggests((context, suggestionsBuilder) -> suggestIndices(context.getSource(), suggestionsBuilder, null, null)).
						then(CommandManager.
							argument("type", StringArgumentType.word()).
							suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(TYPE_NAMES, suggestionsBuilder)).
							executes(context -> updateMeteredEvents(context.getSource(), IntegerArgumentType.getInteger(context, "index"), parseEventType(context, "type"), false)))))).
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
			return ColorUtils.fromRGBString(asString);
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
	
	private static int meterCommand(ServerCommandSource source, Integer givenIndex, BlockPos givenPos, CommandTarget target, CommandExecutor command) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			MultimeterServer server = ((IServerCommandSource)source).getMultimeterServer();
			Multimeter multimeter = server.getMultimeter();
			
			if (target == CommandTarget.MULTIMETER) {
				command.execute(server, multimeter, null, player, -1, null);
				return 1;
			}
			
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendFeedback(new LiteralText("You have to subscribe to a meter group first!"), false);
				return 1;
			}
			
			if (target == CommandTarget.METER_GROUP) {
				command.execute(server, multimeter, meterGroup, player, -1, null);
				return 1;
			}
			
			int index = -1;
			WorldPos pos = null;
			
			if (givenIndex == null) {
				World world = player.world;
				BlockPos blockPos = null;
				
				if (givenPos == null) {
					HitResult hitResult = player.raycast(player.interactionManager.getGameMode().isCreative() ? 5.0F : 4.5F, server.getMinecraftServer().getTickTime(), false);
					
					if (hitResult.getType() == HitResult.Type.BLOCK) {
						blockPos = ((BlockHitResult)hitResult).getBlockPos();
					}
				} else {
					blockPos = givenPos;
				}
				
				if (blockPos != null) {
					pos = new WorldPos(world, blockPos);
					index = meterGroup.indexOfMeterAt(pos);
				} else {
					return 1;
				}
			} else {
				int meterCount = meterGroup.getMeterCount();
				
				if (givenIndex >= -meterCount && givenIndex < meterCount) {
					index = givenIndex;
					
					if (index < 0) {
						index += meterCount;
					}
				} else {
					source.sendFeedback(new LiteralText("There is no meter at that index!"), false);
					return 1;
				}
			}
			
			if (target == CommandTarget.METER) {
				command.execute(server, multimeter, meterGroup, player, index, pos);
				return 1;
			}
		} catch (CommandSyntaxException e) {
			
		}
		
		return 0;
	}
	
	private static int addMeter(ServerCommandSource source, BlockPos blockPos, boolean movable, String name, Integer color) {
		return meterCommand(source, null, blockPos, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			if (index >= 0) {
				source.sendFeedback(new LiteralText("There is already a meter at that position!"), false);
			} else {
				multimeter.addMeter(pos, movable, name, color, player);
				source.sendFeedback(new LiteralText(String.format("Added a meter at %s!", pos.toString())), false);
			}
		});
	}
	
	private static int removeMeter(ServerCommandSource source, Integer givenIndex) {
		return meterCommand(source, givenIndex, null, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			multimeter.removeMeter(index, player);
			source.sendFeedback(new LiteralText(String.format("Removed meter #%d", index)), false);
		});
	}
	
	private static int renameMeter(ServerCommandSource source, Integer givenIndex, String name) {
		return meterCommand(source, givenIndex, null, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			multimeter.renameMeter(index, name, player);
			source.sendFeedback(new LiteralText(String.format("Renamed meter #%d to %s", index, name)), false);
		});
	}
	
	private static int recolorMeter(ServerCommandSource source, Integer givenIndex, int color) {
		return meterCommand(source, givenIndex, null, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			multimeter.recolorMeter(index, color, player);
			source.sendFeedback(new LiteralText(String.format("Recolored meter #%d to %s", index, ColorUtils.toRGBString(color))), false);
		});
	}
	
	private static int listMeteredEvents(ServerCommandSource source, Integer givenIndex) {
		return meterCommand(source, givenIndex, null, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			Meter meter = meterGroup.getMeter(index);
			String text = String.format("Meter #%d is metering:", index, meter.getName());
			
			for (EventType type : EventType.TYPES) {
				if (meter.isMetering(type)) {
					text += String.format("\n  %s", type.getName());
				}
			}
			
			source.sendFeedback(new LiteralText(text), false);
		});
	}
	
	private static int updateMeteredEvents(ServerCommandSource source, Integer givenIndex, EventType type, boolean start) {
		return meterCommand(source, givenIndex, null, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter.isMetering(type) != start) {
				multimeter.toggleEventType(index, type, player);
			}
			
			source.sendFeedback(new LiteralText(String.format("Meter #%d %s metering %s", index,start ? "started" : "stopped", type.getName())), false);
		});
	}
	
	private static int removeAll(ServerCommandSource source) {
		return meterCommand(source, null, null, CommandTarget.METER_GROUP, (server, multimeter, meterGroup, player, index, pos) -> {
			multimeter.removeAllMeters(player);
			source.sendFeedback(new LiteralText(String.format("Removed all meters in meter group \'%s\'", meterGroup.getName())), false);
		});
	}
	
	private static int subscribeToGroup(ServerCommandSource source, String name) {
		return meterCommand(source, null, null, CommandTarget.METER_GROUP, (server, multimeter, meterGroup, player, index, pos) -> {
			multimeter.subscribeToMeterGroup(name, player);
			source.sendFeedback(new LiteralText(String.format("Subscribed to meter group \'%s\'", name)), false);
		});
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
	
	private static Collection<String> getSuggestions(ServerCommandSource source, Integer givenIndex, BlockPos givenPos, SuggestionsProvider suggestionsProvider) {
		Collection<String> suggestions = new ArrayList<>();
		
		meterCommand(source, givenIndex, givenPos, CommandTarget.METER, (server, multimeter, meterGroup, player, index, pos) -> {
			suggestionsProvider.addSuggestions(suggestions, server, multimeter, meterGroup, index, pos);
		});
		
		return suggestions;
	}
	
	private static CompletableFuture<Suggestions> suggestIndices(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer givenIndex, BlockPos givenPos) {
		Collection<String> commandSuggestions = getSuggestions(source, givenIndex, givenPos, (suggestions, server, multimeter, meterGroup, index, pos) -> {
			if (index >= 0) {
				suggestions.add(Integer.toString(index));
			}
		});
		
		if (commandSuggestions.isEmpty()) {
			commandSuggestions.add("0");
			commandSuggestions.add("-1");
		}
		
		return CommandSource.suggestMatching(commandSuggestions, suggestionsBuilder);
	}
	
	private static CompletableFuture<Suggestions> suggestNames(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer givenIndex, BlockPos givenPos) {
		Collection<String> commandSuggestions = getSuggestions(source, givenIndex, givenPos, (suggestions, server, multimeter, meterGroup, index, pos) -> {
			if (index >= 0) {
				String name = meterGroup.getMeter(index).getName();
				
				if (givenIndex == null) {
					suggestions.add(String.format("\'%s\'", name));
				} else {
					suggestions.add(name);
				}
			} else if (pos != null) {
				BlockState state = server.getBlockState(pos);
				
				if (state != null) {
					Block block = state.getBlock();
					String name = meterGroup.getNextMeterName(block);
					
					suggestions.add(String.format("\'%s\'", name));
				}
			}
		});
		
		return CommandSource.suggestMatching(commandSuggestions, suggestionsBuilder);
	}
	
	private static CompletableFuture<Suggestions> suggestColors(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder, Integer givenIndex, BlockPos givenPos) {
		Collection<String> commandSuggestions = getSuggestions(source, givenIndex, givenPos, (suggestions, server, multimeter, meterGroup, index, pos) -> {
			if (index >= 0) {
				int color = meterGroup.getMeter(index).getColor();
				String suggestion = ColorUtils.toRGBString(color);
				
				suggestions.add(suggestion);
			}
		});
		
		if (commandSuggestions.isEmpty()) {
			int color = ColorUtils.nextColor(false);
			String suggestion = ColorUtils.toRGBString(color);
			
			commandSuggestions.add(suggestion);
		}
		
		return CommandSource.suggestMatching(commandSuggestions, suggestionsBuilder);
	}
	
	private interface CommandExecutor {
		public void execute(MultimeterServer server, Multimeter multimeter, ServerMeterGroup meterGroup, ServerPlayerEntity player, int index, WorldPos pos);
	}
	
	private interface SuggestionsProvider {
		public void addSuggestions(Collection<String> suggestions, MultimeterServer server, Multimeter multimeter, ServerMeterGroup meterGroup, int targetIndex, WorldPos targetPos);
	}
	
	private enum CommandTarget {
		MULTIMETER, METER_GROUP, METER
	}
}
