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

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.
			literal("metergroup").
			requires(source -> isMultimeterClient(source)).
			then(Commands.
				literal("list").
				executes(context -> list(context.getSource()))).
			then(Commands.
				literal("subscribe").
				executes(context -> subscribe(context.getSource(), null)).
				then(Commands.
					argument("name", StringArgumentType.greedyString()).
					suggests((context, suggestionsBuilder) -> SharedSuggestionProvider.suggest(listMeterGroups(context.getSource()), suggestionsBuilder)).
					executes(context -> subscribe(context.getSource(), StringArgumentType.getString(context, "name"))))).
			then(Commands.
				literal("unsubscribe").
				executes(context -> unsubscribe(context.getSource()))).
			then(Commands.
				literal("private").
				requires(source -> isOwnerOfSubscription(source)).
				executes(context -> queryPrivate(context.getSource())).
				then(Commands.
					argument("private", BoolArgumentType.bool()).
					executes(context -> setPrivate(context.getSource(), BoolArgumentType.getBool(context, "private"))))).
			then(Commands.
				literal("members").
				requires(source -> isOwnerOfSubscription(source)).
				then(Commands.
					literal("list").
					executes(context -> membersList(context.getSource()))).
				then(Commands.
					literal("add").
					then(Commands.
						argument("player", EntityArgument.players()).
						executes(context -> membersAdd(context.getSource(), EntityArgument.getPlayers(context, "player"))))).
				then(Commands.
					literal("remove").
					then(Commands.
						argument("member", StringArgumentType.word()).
						suggests((context, suggestionsBuilder) -> SharedSuggestionProvider.suggest(listMembers(context.getSource()).keySet(), suggestionsBuilder)).
						executes(context -> membersRemovePlayer(context.getSource(), StringArgumentType.getString(context, "member"))))).
				then(Commands.
					literal("clear").
					executes(context -> membersClear(context.getSource())))).
			then(Commands.
				literal("clear").
				executes(context -> clear(context.getSource())));

		dispatcher.register(builder);
	}

	private static boolean isMultimeterClient(CommandSourceStack source) {
		return run(source, (multimeter, player) -> multimeter.getServer().isMultimeterClient(player));
	}

	private static boolean isOwnerOfSubscription(CommandSourceStack source) {
		return run(source, (multimeter, player) -> multimeter.isOwnerOfSubscription(player));
	}

	private static Collection<String> listMeterGroups(CommandSourceStack source) {
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

	private static Map<String, UUID> listMembers(CommandSourceStack source) {
		Map<String, UUID> names = new HashMap<>();

		command(source, (multimeter, player) -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup != null && meterGroup.isOwnedBy(player)) {
				for (UUID playerUUID : meterGroup.getMembers()) {
					String playerName = multimeter.getServer().getPlayerList().getName(playerUUID);

					if (playerName != null) {
						names.put(playerName, playerUUID);
					}
				}
			}
		});

		return names;
	}

	private static int list(CommandSourceStack source) {
		Collection<String> names = listMeterGroups(source);

		if (names.isEmpty()) {
			source.sendSuccess(() -> Component.literal("There are no meter groups yet!"), false);
		} else {
			String message = "Meter groups:\n  " + String.join("\n  ", names);
			source.sendSuccess(() -> Component.literal(message), false);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int subscribe(CommandSourceStack source, String name) {
		return command(source, (multimeter, player) -> {
			if (name == null) {
				multimeter.subscribeToDefaultMeterGroup(player);
				source.sendSuccess(() -> Component.literal("Subscribed to default meter group"), false);
			} else if (multimeter.hasMeterGroup(name)) {
				ServerMeterGroup meterGroup = multimeter.getMeterGroup(name);

				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					multimeter.subscribeToMeterGroup(meterGroup, player);
					source.sendSuccess(() -> Component.literal(String.format("Subscribed to meter group \'%s\'", name)), false);
				} else {
					source.sendSuccess(() -> Component.literal("That meter group is private!"), false);
				}
			} else {
				if (MeterGroup.isValidName(name)) {
					multimeter.createMeterGroup(player, name);
					source.sendSuccess(() -> Component.literal(String.format("Created meter group \'%s\'", name)), false);
				} else {
					source.sendSuccess(() -> Component.literal(String.format("\'%s\' is not a valid meter group name!", name)), false);
				}
			}
		});
	}

	private static int unsubscribe(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			source.sendSuccess(() -> Component.literal(String.format("Unsubscribed from meter group \'%s\'", meterGroup.getName())), false);
		});
	}

	private static int queryPrivate(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			source.sendSuccess(() -> Component.literal(String.format("Meter group \'%s\' is %s", meterGroup.getName(), status)), false);
		});
	}

	private static int setPrivate(CommandSourceStack source, boolean isPrivate) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);
				source.sendSuccess(() -> Component.literal(String.format("Meter group \'%s\' is now %s", meterGroup.getName(), (isPrivate ? "private" : "public"))), false);
			} else {
				source.sendSuccess(() -> Component.literal("Only the owner of a meter group can change its privacy!"), false);
			}
		});
	}

	private static int membersList(CommandSourceStack source) {
		Map<String, UUID> members = listMembers(source);

		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			if (members.isEmpty()) {
				source.sendSuccess(() -> Component.literal(String.format("Meter group \'%s\' has no members yet!", meterGroup.getName())), false);
			} else {
				String message = String.format("Members of meter group \'%s\':\n  ", meterGroup.getName()) + String.join("\n  ", members.keySet());
				source.sendSuccess(() -> Component.literal(message), false);
			}
		});
	}

	private static int membersAdd(CommandSourceStack source, Collection<ServerPlayer> players) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			for (ServerPlayer player : players) {
				if (player == owner) {
					source.sendSuccess(() -> Component.literal("You cannot add yourself as a member!"), false);
				} else if (meterGroup.hasMember(player)) {
					source.sendSuccess(() -> Component.literal(String.format("Player \'%s\' is already a member of meter group \'%s\'!", player.getScoreboardName(), meterGroup.getName())), false);
				} else if (!multimeter.getServer().isMultimeterClient(player)) {
					source.sendSuccess(() -> Component.literal(String.format("You cannot add player \'%s\' as a member; they do not have %s installed!", player.getScoreboardName(), RedstoneMultimeterMod.MOD_NAME)), false);
				} else {
					multimeter.addMemberToMeterGroup(meterGroup, player.getUUID());
					source.sendSuccess(() -> Component.literal(String.format("Player \'%s\' is now a member of meter group \'%s\'", player.getScoreboardName(), meterGroup.getName())), false);
				}
			}
		});
	}

	private static int membersRemovePlayer(CommandSourceStack source, String playerName) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			Entry<String, UUID> member = findMember(listMembers(source), playerName);

			if (member == null) {
				ServerPlayer player = multimeter.getServer().getPlayerList().get(playerName);

				if (player == owner) {
					source.sendSuccess(() -> Component.literal("You cannot remove yourself as a member!"), false);
				} else {
					source.sendSuccess(() -> Component.literal(String.format("Meter group \'%s\' has no member with the name \'%s\'!", meterGroup.getName(), playerName)), false);
				}
			} else {
				multimeter.removeMemberFromMeterGroup(meterGroup, member.getValue());
				source.sendSuccess(() -> Component.literal(String.format("Player \'%s\' is no longer a member of meter group \'%s\'", member.getKey(), meterGroup.getName())), false);
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

	private static int membersClear(CommandSourceStack source) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			multimeter.clearMembersOfMeterGroup(meterGroup);
			source.sendSuccess(() -> Component.literal(String.format("Removed all members from meter group \'%s\'", meterGroup.getName())), false);
		});
	}

	private static int commandMembers(CommandSourceStack source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.run(multimeter, meterGroup, player);

				if (!meterGroup.isPrivate()) {
					source.sendSuccess(() -> Component.literal("NOTE: this meter group is public; adding/removing members will not have any effect until you make it private!"), false);
				}
			}
		});
	}

	private static int clear(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.clearMeterGroup(meterGroup);
			source.sendSuccess(() -> Component.literal(String.format("Removed all meters in meter group \'%s\'", meterGroup.getName())), false);
		});
	}

	private static int command(CommandSourceStack source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, player) -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup == null) {
				source.sendSuccess(() -> Component.literal("Please subscribe to a meter group first!"), false);
			} else {
				command.run(multimeter, meterGroup, player);
			}
		});
	}

	private static int command(CommandSourceStack source, MultimeterCommandExecutor command) {
		return run(source, (m, p) -> { command.run(m, p); return true; }) ? Command.SINGLE_SUCCESS : 0;
	}

	private static boolean run(CommandSourceStack source, BiFunction<Multimeter, ServerPlayer, Boolean> command) {
		ServerPlayer player = source.getPlayer();
		MinecraftServer server = source.getServer();
		MultimeterServer multimeterServer = ((IMinecraftServer)server).getMultimeterServer();
		Multimeter multimeter = multimeterServer.getMultimeter();

		return command.apply(multimeter, player);
	}

	@FunctionalInterface
	private static interface MultimeterCommandExecutor {

		public void run(Multimeter multimeter, ServerPlayer player);

	}

	@FunctionalInterface
	private static interface MeterGroupCommandExecutor {

		public void run(Multimeter multimeter, ServerMeterGroup meterGroup, ServerPlayer player);

	}
}
