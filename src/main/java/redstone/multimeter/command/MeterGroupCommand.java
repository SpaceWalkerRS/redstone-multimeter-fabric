package redstone.multimeter.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiFunction;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4062;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand {
	
	public static void register(CommandDispatcher<class_3915> dispatcher) {
		LiteralArgumentBuilder<class_3915> builder = CommandManager.
			method_17529("metergroup").
			requires(source -> isMultimeterClient(source)).
			then(CommandManager.
				method_17529("clear").
				executes(context -> clear(context.getSource()))).
			then(CommandManager.
				method_17529("subscribe").
				executes(context -> subscribe(context.getSource(), null)).
				then(CommandManager.
					method_17530("name", StringArgumentType.greedyString()).
					suggests((context, suggestionsBuilder) -> class_3965.method_17571(listMeterGroups(context.getSource()), suggestionsBuilder)).
					executes(context -> subscribe(context.getSource(), StringArgumentType.getString(context, "name"))))).
			then(CommandManager.
				method_17529("unsubscribe").
				executes(context -> unsubscribe(context.getSource()))).
			then(CommandManager.
				method_17529("private").
				requires(source -> isOwnerOfSubscription(source)).
				executes(context -> queryPrivate(context.getSource())).
				then(CommandManager.
					method_17530("private", BoolArgumentType.bool()).
					executes(context -> setPrivate(context.getSource(), BoolArgumentType.getBool(context, "private"))))).
			then(CommandManager.
				method_17529("members").
				requires(source -> isOwnerOfSubscription(source)).
				then(CommandManager.
					method_17529("clear").
					executes(context -> membersClear(context.getSource()))).
				then(CommandManager.
					method_17529("add").
					then(CommandManager.
						method_17530("player", class_4062.method_17904()).
						executes(context -> membersAdd(context.getSource(), class_4062.method_17907(context, "player"))))).
				then(CommandManager.
					method_17529("remove").
					then(CommandManager.
						method_17530("member", StringArgumentType.word()).
						suggests((context, suggestionsBuilder) -> class_3965.method_17571(listMembers(context.getSource()).keySet(), suggestionsBuilder)).
						executes(context -> membersRemovePlayer(context.getSource(), StringArgumentType.getString(context, "member"))))).
				then(CommandManager.
					method_17529("list").
					executes(context -> membersList(context.getSource())))).
			then(CommandManager.
				method_17529("list").
				executes(context -> list(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static boolean isMultimeterClient(class_3915 source) {
		return execute(source, (multimeter, player) -> multimeter.getMultimeterServer().isMultimeterClient(player));
	}
	
	private static boolean isOwnerOfSubscription(class_3915 source) {
		return execute(source, (multimeter, player) -> multimeter.isOwnerOfSubscription(player));
	}
	
	private static Collection<String> listMeterGroups(class_3915 source) {
		List<String> names = new ArrayList<>();
		
		command(source, (multimeter, player) -> {
			for (ServerMeterGroup meterGroup : multimeter.getMeterGroups()) {
				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					names.add(meterGroup.getName());
				}
			}
		});
		
		return names;
	}
	
	private static Map<String, UUID> listMembers(class_3915 source) {
		Map<String, UUID> names = new HashMap<>();
		
		command(source, (multimeter, player) -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup != null && meterGroup.isOwnedBy(player)) {
				for (UUID playerUUID : meterGroup.getMembers()) {
					String playerName = multimeter.getMultimeterServer().getPlayerName(playerUUID);
					
					if (playerName != null) {
						names.put(playerName, playerUUID);
					}
				}
			}
		});
		
		return names;
	}
	
	private static int clear(class_3915 source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.clearMeterGroup(player);
			source.method_17459(new LiteralText(String.format("Removed all meters in meter group \'%s\'", multimeter.getSubscription(player).getName())), false);
		});
	}
	
	private static int subscribe(class_3915 source, String name) {
		return command(source, (multimeter, player) -> {
			if (name == null) {
				multimeter.subscribeToDefaultMeterGroup(player);
				source.method_17459(new LiteralText("Subscribed to default meter group"), false);
			} else if (multimeter.hasMeterGroup(name)) {
				ServerMeterGroup meterGroup = multimeter.getMeterGroup(name);
				
				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					multimeter.subscribeToMeterGroup(meterGroup, player);
					source.method_17459(new LiteralText(String.format("Subscribed to meter group \'%s\'", name)), false);
				} else {
					source.method_17459(new LiteralText("A meter group with that name already exists and it is private!"), false);
				}
			} else {
				if (MeterGroup.isValidName(name)) {
					multimeter.createMeterGroup(player, name);
					source.method_17459(new LiteralText(String.format("Created meter group \'%s\'", name)), false);
				} else {
					source.method_17459(new LiteralText(String.format("\'%s\' is not a valid meter group name!", name)), false);
				}
			}
		});
	}
	
	private static int unsubscribe(class_3915 source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			source.method_17459(new LiteralText(String.format("Unsubscribed from meter group \'%s\'", meterGroup.getName())), false);
		});
	}
	
	private static int queryPrivate(class_3915 source) {
		return command(source, (multimeter, meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			source.method_17459(new LiteralText(String.format("Meter group \'%s\' is %s", meterGroup.getName(), status)), false);
		});
	}
	
	private static int setPrivate(class_3915 source, boolean isPrivate) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);
				source.method_17459(new LiteralText(String.format("Meter group \'%s\' is now %s", meterGroup.getName(), (isPrivate ? "private" : "public"))), false);
			} else {
				source.method_17459(new LiteralText("Only the owner of a meter group can change its privacy!"), false);
			}
		});
	}
	
	private static int membersClear(class_3915 source) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			multimeter.clearMembersOfMeterGroup(meterGroup);
			source.method_17459(new LiteralText(String.format("Removed all members from meter group \'%s\'", meterGroup.getName())), false);
		});
	}
	
	private static int membersAdd(class_3915 source, Collection<ServerPlayerEntity> players) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			for (ServerPlayerEntity player : players) {
				if (player == owner) {
					source.method_17459(new LiteralText("You cannot add yourself as a member!"), false);
				} else if (meterGroup.hasMember(player)) {
					source.method_17459(new LiteralText(String.format("Player \'%s\' is already a member of meter group \'%s\'!", player.method_15586(), meterGroup.getName())), false);
				} else if (!multimeter.getMultimeterServer().isMultimeterClient(player)) {
					source.method_17459(new LiteralText(String.format("You cannot add player \'%s\' as a member; they do not have %s installed!", player.method_15586(), RedstoneMultimeterMod.MOD_NAME)), false);
				} else {
					multimeter.addMemberToMeterGroup(meterGroup, player.getUuid());
					source.method_17459(new LiteralText(String.format("Player \'%s\' is now a member of meter group \'%s\'", player.method_15586(), meterGroup.getName())), false);
				}
			}
		});
	}
	
	private static int membersRemovePlayer(class_3915 source, String playerName) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			Entry<String, UUID> member = findMember(listMembers(source), playerName);
			
			if (member == null) {
				ServerPlayerEntity player = multimeter.getMultimeterServer().getPlayer(playerName);
				
				if (player == owner) {
					source.method_17459(new LiteralText("You cannot remove yourself as a member!"), false);
				} else {
					source.method_17459(new LiteralText(String.format("Meter group \'%s\' has no member with the name \'%s\'!", meterGroup.getName(), playerName)), false);
				}
			} else {
				multimeter.removeMemberFromMeterGroup(meterGroup, member.getValue());
				source.method_17459(new LiteralText(String.format("Player \'%s\' is no longer a member of meter group \'%s\'", member.getKey(), meterGroup.getName())), false);
			}
		});
	}
	
	private static Entry<String, UUID> findMember(Map<String, UUID> members, String playerName) {
		String key = playerName.toLowerCase();
		
		for (Entry<String, UUID> member : members.entrySet()) {
			if (member.getKey().toLowerCase().equals(key)) {
				return member;
			}
		}
		
		return null;
	}
	
	private static int membersList(class_3915 source) {
		Map<String, UUID> members = listMembers(source);
		
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			if (members.isEmpty()) {
				source.method_17459(new LiteralText(String.format("Meter group \'%s\' has no members yet!", meterGroup.getName())), false);
			} else {
				String message = String.format("Members of meter group \'%s\':\n  ", meterGroup.getName()) + String.join("\n  ", members.keySet());
				source.method_17459(new LiteralText(message), false);
			}
		});
	}
	
	private static int commandMembers(class_3915 source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.execute(multimeter, meterGroup, player);
				
				if (!meterGroup.isPrivate()) {
					source.method_17459(new LiteralText("NOTE: this meter group is public; adding/removing members will not have any effect until you make it private!"), false);
				}
			}
		});
	}
	
	private static int list(class_3915 source) {
		Collection<String> names = listMeterGroups(source);
		
		if (names.isEmpty()) {
			source.method_17459(new LiteralText("There are no meter groups yet!"), false);
		} else {
			String message = "Meter groups:\n  " + String.join("\n  ", names);
			source.method_17459(new LiteralText(message), false);
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int command(class_3915 source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, player) -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.method_17459(new LiteralText("Please subscribe to a meter group first!"), false);
			} else {
				command.execute(multimeter, meterGroup, player);
			}
		});
	}
	
	private static int command(class_3915 source, MultimeterCommandExecutor command) {
		return execute(source, (m, p) -> { command.execute(m, p); return true; }) ? Command.SINGLE_SUCCESS : 0;
	}
	
	private static boolean execute(class_3915 source, BiFunction<Multimeter, ServerPlayerEntity, Boolean> command) {
		try {
			ServerPlayerEntity player = source.method_17471();
			MinecraftServer server = source.method_17473();
			MultimeterServer multimeterServer = ((IMinecraftServer)server).getMultimeterServer();
			Multimeter multimeter = multimeterServer.getMultimeter();
			
			return command.apply(multimeter, player);
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
	
	@FunctionalInterface
	private static interface MultimeterCommandExecutor {
		
		public void execute(Multimeter multimeter, ServerPlayerEntity player);
		
	}
	
	@FunctionalInterface
	private static interface MeterGroupCommandExecutor {
		
		public void execute(Multimeter multimeter, ServerMeterGroup meterGroup, ServerPlayerEntity player);
		
	}
}
