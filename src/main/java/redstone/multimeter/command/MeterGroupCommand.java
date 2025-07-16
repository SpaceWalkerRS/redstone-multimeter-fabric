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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.list.failure.none"));
		} else {
			Text message = Texts.translatable("rsmm.command.metergroup.list.success");

			for (String name : names) {
				message.append("\n  " + name);
			}

			sendSuccess(source, message);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int subscribe(CommandSourceStack source, String name) {
		return command(source, (multimeter, player) -> {
			if (name == null) {
				multimeter.subscribeToDefaultMeterGroup(player);
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.subscribe.success.default"));
			} else if (multimeter.hasMeterGroup(name)) {
				ServerMeterGroup meterGroup = multimeter.getMeterGroup(name);

				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					multimeter.subscribeToMeterGroup(meterGroup, player);
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.subscribe.success.joined", name));
				} else {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.subscribe.failure.private"));
				}
			} else {
				if (MeterGroup.isValidName(name)) {
					multimeter.createMeterGroup(player, name);
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.subscribe.success.created", name));
				} else {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.subscribe.failure.invalid", name));
				}
			}
		});
	}

	private static int unsubscribe(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.unsubscribe.success", meterGroup.getName()));
		});
	}

	private static int queryPrivate(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.query.success." + status, meterGroup.getName()));
		});
	}

	private static int setPrivate(CommandSourceStack source, boolean isPrivate) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);

				String status = isPrivate ? "private" : "public";
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.set.success." + status, meterGroup.getName()));
			} else {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.set.failure.notOwner"));
			}
		});
	}

	private static int membersList(CommandSourceStack source) {
		Map<String, UUID> members = listMembers(source);

		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			if (members.isEmpty()) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.list.failure.none", meterGroup.getName()));
			} else {
				Text message = Texts.translatable("rsmm.command.metergroup.members.list.success", meterGroup.getName());

				for (Map.Entry<String, UUID> member : members.entrySet()) {
					message.append("\n  " + member.getKey());
				}

				sendSuccess(source, message);
			}
		});
	}

	private static int membersAdd(CommandSourceStack source, Collection<ServerPlayer> players) {
		return commandMembers(source, (multimeter, meterGroup, owner) -> {
			for (ServerPlayer player : players) {
				if (player == owner) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.self"));
				} else if (meterGroup.hasMember(player)) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.alreadyMember", player.getScoreboardName(), meterGroup.getName()));
				} else if (!multimeter.getServer().isMultimeterClient(player)) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.notInstalled", player.getScoreboardName()));
				} else {
					multimeter.addMemberToMeterGroup(meterGroup, player.getUUID());
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.success", player.getScoreboardName(), meterGroup.getName()));
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
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.failure.self"));
				} else {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.failure.noSuchMember", meterGroup.getName(), playerName));
				}
			} else {
				multimeter.removeMemberFromMeterGroup(meterGroup, member.getValue());
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.success", member.getKey(), meterGroup.getName()));
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
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.clear.success", meterGroup.getName()));
		});
	}

	private static int commandMembers(CommandSourceStack source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.run(multimeter, meterGroup, player);

				if (!meterGroup.isPrivate()) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.warning.public"));
				}
			}
		});
	}

	private static int clear(CommandSourceStack source) {
		return command(source, (multimeter, meterGroup, player) -> {
			multimeter.clearMeterGroup(meterGroup);
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.clear.success", meterGroup.getName()));
		});
	}

	private static int command(CommandSourceStack source, MeterGroupCommandExecutor command) {
		return command(source, (multimeter, player) -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup == null) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.warning.notSubscribed"));
			} else {
				command.run(multimeter, meterGroup, player);
			}
		});
	}

	private static int command(CommandSourceStack source, MultimeterCommandExecutor command) {
		return run(source, (m, p) -> { command.run(m, p); return true; }) ? Command.SINGLE_SUCCESS : 0;
	}

	private static boolean run(CommandSourceStack source, BiFunction<Multimeter, ServerPlayer, Boolean> command) {
		try {
			ServerPlayer player = source.getPlayerOrException();
			MinecraftServer server = source.getServer();
			MultimeterServer multimeterServer = ((IMinecraftServer)server).getMultimeterServer();
			Multimeter multimeter = multimeterServer.getMultimeter();

			return command.apply(multimeter, player);
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	private static void sendSuccess(CommandSourceStack source, Text message) {
		sendSuccess(source, message, false);
	}

	private static void sendSuccess(CommandSourceStack source, Text message, boolean actionBar) {
		source.sendSuccess(message.resolve(), actionBar);
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
