package redstone.multimeter.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.CommandNotFoundException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand extends Command {

	private static final String COMMAND_NAME = "metergroup";
	
	private static final String USAGE_LIST              = singleUsage("list");

	private static final String USAGE_SUBSCRIBE_DEFAULT = singleUsage("subscribe");
	private static final String USAGE_SUBSCRIBE_NAME    = singleUsage("subscribe <name>");
	private static final String USAGE_SUBSCRIBE         = buildUsage(USAGE_SUBSCRIBE_DEFAULT, USAGE_SUBSCRIBE_NAME);

	private static final String USAGE_UNSUBSCRIBE       = singleUsage("unsubscribe");

	private static final String USAGE_PRIVATE_QUERY     = singleUsage("private");
	private static final String USAGE_PRIVATE_SET       = singleUsage("private <private true|false>");
	private static final String USAGE_PRIVATE           = buildUsage(USAGE_PRIVATE_QUERY, USAGE_PRIVATE_SET);

	private static final String USAGE_MEMBERS_LIST      = singleUsage("members list");
	private static final String USAGE_MEMBERS_ADD       = singleUsage("members add <player>");
	private static final String USAGE_MEMBERS_REMOVE    = singleUsage("members remove <player>");
	private static final String USAGE_MEMBERS_CLEAR     = singleUsage("members clear");
	private static final String USAGE_MEMBERS           = buildUsage(USAGE_MEMBERS_LIST, USAGE_MEMBERS_ADD, USAGE_MEMBERS_REMOVE, USAGE_MEMBERS_CLEAR);

	private static final String USAGE_CLEAR             = singleUsage("clear");

	private static final String TOTAL_USAGE_MEMBER      = buildUsage(USAGE_LIST, USAGE_SUBSCRIBE, USAGE_UNSUBSCRIBE, USAGE_CLEAR);
	private static final String TOTAL_USAGE_OWNER       = buildUsage(USAGE_LIST, USAGE_SUBSCRIBE, USAGE_UNSUBSCRIBE, USAGE_PRIVATE, USAGE_MEMBERS, USAGE_CLEAR);

	private static String singleUsage(String usage) {
		return String.format("/%s %s", COMMAND_NAME, usage);
	}

	private static String buildUsage(String... usages) {
		return String.join(" OR ", usages);
	}

	private final MultimeterServer server;
	private final Multimeter multimeter;

	public MeterGroupCommand(MinecraftServer server) {
		this.server = ((IMinecraftServer)server).getMultimeterServer();
		this.multimeter = this.server.getMultimeter();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

	@Override
	public String getUsage(CommandSource source) {
		return isOwnerOfSubscription(source) ? TOTAL_USAGE_OWNER : TOTAL_USAGE_MEMBER;
	}

	@Override
	public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos pos) {
		boolean isOwner = isOwnerOfSubscription(source);

		switch (args.length) {
		case 1:
			if (isOwner) {
				return suggestMatching(args, "clear", "subscribe", "unsubscribe", "private", "members", "list");
			} else {
				return suggestMatching(args, "clear", "subscribe", "unsubscribe", "list");
			}
		case 2:
			switch (args[0]) {
			case "subscribe":
				return suggestMatching(args, listMeterGroups(source));
			case "private":
				if (isOwner) {
					return suggestMatching(args, "true", "false");
				}

				break;
			case "members":
				if (isOwner) {
					return suggestMatching(args, "clear", "add", "remove", "list");
				}

				break;
			}

			break;
		case 3:
			if (isOwner && args[0].equals("members")) {
				switch (args[1]) {
				case "add":
					return suggestMatching(args, server.getPlayerNames());
				case "remove":
					return suggestMatching(args, listMembers(source).keySet());
				}
			}

			break;
		}

		return Collections.emptyList();
	}

	@Override
	public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
		if (!isMultimeterClient(source)) {
			throw new CommandNotFoundException();
		}

		if (args.length > 0) {
			switch (args[0]) {
			case "list":
				if (args.length == 1) {
					list(source);
					return;
				}

				throw new IncorrectUsageException(USAGE_LIST);
			case "subscribe":
				if (args.length == 1) {
					subscribe(source, null);
					return;
				}

				String name = "";

				for (int index = 1; index < args.length; index++) {
					name += args[index] + " ";
				}

				subscribe(source, name);
				return;
			case "unsubscribe":
				if (args.length == 1) {
					unsubscribe(source);
					return;
				}

				throw new IncorrectUsageException(USAGE_UNSUBSCRIBE);
			case "private":
				if (!isOwnerOfSubscription(source)) {
					break;
				}

				switch (args.length) {
				case 1:
					queryPrivate(source);
					return;
				case 2:
					switch (args[1]) {
					case "true":
						setPrivate(source, true);
						return;
					case "false":
						setPrivate(source, false);
						return;
					}

					throw new IncorrectUsageException(USAGE_PRIVATE_SET);
				}

				throw new IncorrectUsageException(USAGE_PRIVATE);
			case "members":
				if (!isOwnerOfSubscription(source)) {
					break;
				}

				if (args.length > 1) {
					switch (args[1]) {
					case "list":
						if (args.length == 2) {
							membersList(source);
							return;
						}

						throw new IncorrectUsageException(USAGE_MEMBERS_LIST);
					case "add":
						if (args.length == 3) {
							membersAdd(source, parsePlayers(server, source, args[2]));
							return;
						}

						throw new IncorrectUsageException(USAGE_MEMBERS_ADD);
					case "remove":
						if (args.length == 3) {
							membersRemovePlayer(source, args[2]);
							return;
						}

						throw new IncorrectUsageException(USAGE_MEMBERS_REMOVE);
					case "clear":
						if (args.length == 2) {
							membersClear(source);
							return;
						}

						throw new IncorrectUsageException(USAGE_MEMBERS_CLEAR);
					}
				}

				throw new IncorrectUsageException(USAGE_MEMBERS);
			case "clear":
				if (args.length == 1) {
					clear(source);
					return;
				}

				throw new IncorrectUsageException(USAGE_CLEAR);
			}
		}

		throw new IncorrectUsageException(getUsage(source));
	}

	private boolean isMultimeterClient(CommandSource source) {
		return run(source, player -> server.isMultimeterClient(player));
	}

	private boolean isOwnerOfSubscription(CommandSource source) {
		return run(source, player -> multimeter.isOwnerOfSubscription(player));
	}

	private Collection<String> listMeterGroups(CommandSource source) {
		List<String> names = new ArrayList<>();

		command(source, player -> {
			for (ServerMeterGroup meterGroup : multimeter.getMeterGroups()) {
				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					names.add(meterGroup.getName());
				}
			}
		});

		return names;
	}

	private Map<String, UUID> listMembers(CommandSource source) {
		Map<String, UUID> names = new HashMap<>();

		command(source, player -> {
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

	private void list(CommandSource source) {
		Collection<String> names = listMeterGroups(source);

		if (names.isEmpty()) {
			source.sendMessage(new LiteralText("There are no meter groups yet!"));
		} else {
			String message = "Meter groups:\n  " + String.join("\n  ", names);
			source.sendMessage(new LiteralText(message));
		}
	}

	private void subscribe(CommandSource source, String name) {
		command(source, player -> {
			if (name == null) {
				multimeter.subscribeToDefaultMeterGroup(player);
				source.sendMessage(new LiteralText("Subscribed to default meter group"));
			} else if (multimeter.hasMeterGroup(name)) {
				ServerMeterGroup meterGroup = multimeter.getMeterGroup(name);

				if (!meterGroup.isPrivate() || meterGroup.hasMember(player) || meterGroup.isOwnedBy(player)) {
					multimeter.subscribeToMeterGroup(meterGroup, player);
					source.sendMessage(new LiteralText(String.format("Subscribed to meter group \'%s\'", name)));
				} else {
					source.sendMessage(new LiteralText("That meter group is private!"));
				}
			} else {
				if (MeterGroup.isValidName(name)) {
					multimeter.createMeterGroup(player, name);
					source.sendMessage(new LiteralText(String.format("Created meter group \'%s\'", name)));
				} else {
					source.sendMessage(new LiteralText(String.format("\'%s\' is not a valid meter group name!", name)));
				}
			}
		});
	}

	private void unsubscribe(CommandSource source) {
		command(source, (meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			source.sendMessage(new LiteralText(String.format("Unsubscribed from meter group \'%s\'", meterGroup.getName())));
		});
	}

	private void queryPrivate(CommandSource source) {
		command(source, (meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			source.sendMessage(new LiteralText(String.format("Meter group \'%s\' is %s", meterGroup.getName(), status)));
		});
	}

	private void setPrivate(CommandSource source, boolean isPrivate) {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);
				source.sendMessage(new LiteralText(String.format("Meter group \'%s\' is now %s", meterGroup.getName(), (isPrivate ? "private" : "public"))));
			} else {
				source.sendMessage(new LiteralText("Only the owner of a meter group can change its privacy!"));
			}
		});
	}

	private void membersList(CommandSource source) {
		Map<String, UUID> members = listMembers(source);

		commandMembers(source, (meterGroup, owner) -> {
			if (members.isEmpty()) {
				source.sendMessage(new LiteralText(String.format("Meter group \'%s\' has no members yet!", meterGroup.getName())));
			} else {
				String message = String.format("Members of meter group \'%s\':\n  ", meterGroup.getName()) + String.join("\n  ", members.keySet());
				source.sendMessage(new LiteralText(message));
			}
		});
	}

	private void membersAdd(CommandSource source, Collection<ServerPlayerEntity> players) {
		commandMembers(source, (meterGroup, owner) -> {
			for (ServerPlayerEntity player : players) {
				if (player == owner) {
					source.sendMessage(new LiteralText("You cannot add yourself as a member!"));
				} else if (meterGroup.hasMember(player)) {
					source.sendMessage(new LiteralText(String.format("Player \'%s\' is already a member of meter group \'%s\'!", player.getScoreboardName(), meterGroup.getName())));
				} else if (!multimeter.getServer().isMultimeterClient(player)) {
					source.sendMessage(new LiteralText(String.format("You cannot add player \'%s\' as a member; they do not have %s installed!", player.getScoreboardName(), RedstoneMultimeterMod.MOD_NAME)));
				} else {
					multimeter.addMemberToMeterGroup(meterGroup, player.getUuid());
					source.sendMessage(new LiteralText(String.format("Player \'%s\' is now a member of meter group \'%s\'", player.getScoreboardName(), meterGroup.getName())));
				}
			}
		});
	}

	private void membersRemovePlayer(CommandSource source, String playerName) {
		commandMembers(source, (meterGroup, owner) -> {
			Entry<String, UUID> member = findMember(listMembers(source), playerName);

			if (member == null) {
				ServerPlayerEntity player = multimeter.getServer().getPlayerList().get(playerName);

				if (player == owner) {
					source.sendMessage(new LiteralText("You cannot remove yourself as a member!"));
				} else {
					source.sendMessage(new LiteralText(String.format("Meter group \'%s\' has no member with the name \'%s\'!", meterGroup.getName(), playerName)));
				}
			} else {
				multimeter.removeMemberFromMeterGroup(meterGroup, member.getValue());
				source.sendMessage(new LiteralText(String.format("Player \'%s\' is no longer a member of meter group \'%s\'", member.getKey(), meterGroup.getName())));
			}
		});
	}

	private Entry<String, UUID> findMember(Map<String, UUID> members, String playerName) {
		String key = playerName.toLowerCase();

		for (Entry<String, UUID> member : members.entrySet()) {
			if (member.getKey().toLowerCase().equals(key)) {
				return member;
			}
		}

		return null;
	}

	private void membersClear(CommandSource source) {
		commandMembers(source, (meterGroup, owner) -> {
			multimeter.clearMembersOfMeterGroup(meterGroup);
			source.sendMessage(new LiteralText(String.format("Removed all members from meter group \'%s\'", meterGroup.getName())));
		});
	}

	private void commandMembers(CommandSource source, MeterGroupCommandExecutor command) {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.run(meterGroup, player);

				if (!meterGroup.isPrivate()) {
					source.sendMessage(new LiteralText("NOTE: this meter group is public; adding/removing members will not have any effect until you make it private!"));
				}
			}
		});
	}

	private void clear(CommandSource source) {
		command(source, (meterGroup, player) -> {
			multimeter.clearMeterGroup(meterGroup);
			source.sendMessage(new LiteralText(String.format("Removed all meters in meter group \'%s\'", meterGroup.getName())));
		});
	}

	private void command(CommandSource source, MeterGroupCommandExecutor command) {
		command(source, player -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup == null) {
				source.sendMessage(new LiteralText("Please subscribe to a meter group first!"));
			} else {
				command.run(meterGroup, player);
			}
		});
	}

	private void command(CommandSource source, MultimeterCommandExecutor command) {
		run(source, p -> { command.run(p); return true; });
	}

	private boolean run(CommandSource source, Function<ServerPlayerEntity, Boolean> command) {
		try {
			return command.apply(asPlayer(source));
		} catch (CommandException e) {
			return false;
		}
	}

	@FunctionalInterface
	private static interface MultimeterCommandExecutor {

		public void run(ServerPlayerEntity player);

	}

	@FunctionalInterface
	private static interface MeterGroupCommandExecutor {

		public void run(ServerMeterGroup meterGroup, ServerPlayerEntity player);

	}
}
