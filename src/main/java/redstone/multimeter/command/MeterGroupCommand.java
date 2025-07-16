package redstone.multimeter.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.CommandNotFoundException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand extends AbstractCommand {

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
	public int compareTo(Object o) {
		return compareTo((Command) o); // needed to keep the compiler happy
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
	public List<String> getSuggestions(CommandSource source, String[] args) {
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
					return suggestMatching(args, server.getMinecraftServer().getPlayerNames());
				case "remove":
					return suggestMatching(args, listMembers(source));
				}
			}

			break;
		}

		return Collections.emptyList();
	}

	@Override
	public void run(CommandSource source, String[] args) throws CommandException {
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
							membersAddPlayer(source, args[2]);
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

	private Set<String> listMembers(CommandSource source) {
		Set<String> names = new HashSet<>();

		command(source, player -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup != null && meterGroup.isOwnedBy(player)) {
				for (String playerName : meterGroup.getMembers()) {
					names.add(playerName);
				}
			}
		});

		return names;
	}

	private void list(CommandSource source) {
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
	}

	private void subscribe(CommandSource source, String name) {
		command(source, player -> {
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

	private void unsubscribe(CommandSource source) {
		command(source, (meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.unsubscribe.success", meterGroup.getName()));
		});
	}

	private void queryPrivate(CommandSource source) {
		command(source, (meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.query.success." + status, meterGroup.getName()));
		});
	}

	private void setPrivate(CommandSource source, boolean isPrivate) {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);

				String status = isPrivate ? "private" : "public";
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.set.success." + status, meterGroup.getName()));
			} else {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.private.set.failure.notOwner"));
			}
		});
	}

	private void membersList(CommandSource source) {
		Set<String> members = listMembers(source);

		commandMembers(source, (meterGroup, owner) -> {
			if (members.isEmpty()) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.list.failure.none", meterGroup.getName()));
			} else {
				Text message = Texts.translatable("rsmm.command.metergroup.members.list.success", meterGroup.getName());

				for (String member : members) {
					message.append("\n  " + member);
				}

				sendSuccess(source, message);
			}
		});
	}

	private void membersAddPlayer(CommandSource source, String playerName) {
		commandMembers(source, (meterGroup, owner) -> {
			ServerPlayerEntity player = multimeter.getServer().getPlayerList().get(playerName);

			if (player == owner) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.self"));
			} else if (meterGroup.hasMember(player)) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.alreadyMember", player.name, meterGroup.getName()));
			} else if (!multimeter.getServer().isMultimeterClient(player)) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.failure.notInstalled", player.name));
			} else {
				multimeter.addMemberToMeterGroup(meterGroup, player.name);
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.add.success", player.name, meterGroup.getName()));
			}
		});
	}

	private void membersRemovePlayer(CommandSource source, String playerName) {
		commandMembers(source, (meterGroup, owner) -> {
			if (!listMembers(source).contains(playerName)) {
				ServerPlayerEntity player = multimeter.getServer().getPlayerList().get(playerName);

				if (player == owner) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.failure.self"));
				} else {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.failure.noSuchMember", meterGroup.getName(), playerName));
				}
			} else {
				multimeter.removeMemberFromMeterGroup(meterGroup, playerName);
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.remove.success", playerName, meterGroup.getName()));
			}
		});
	}

	private void membersClear(CommandSource source) {
		commandMembers(source, (meterGroup, owner) -> {
			multimeter.clearMembersOfMeterGroup(meterGroup);
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.clear.success", meterGroup.getName()));
		});
	}

	private void commandMembers(CommandSource source, MeterGroupCommandExecutor command) {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.run(meterGroup, player);

				if (!meterGroup.isPrivate()) {
					sendSuccess(source, Texts.translatable("rsmm.command.metergroup.members.warning.public"));
				}
			}
		});
	}

	private void clear(CommandSource source) {
		command(source, (meterGroup, player) -> {
			multimeter.clearMeterGroup(meterGroup);
			sendSuccess(source, Texts.translatable("rsmm.command.metergroup.clear.success", meterGroup.getName()));
		});
	}

	private void command(CommandSource source, MeterGroupCommandExecutor command) {
		command(source, player -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);

			if (meterGroup == null) {
				sendSuccess(source, Texts.translatable("rsmm.command.metergroup.warning.notSubscribed"));
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
			return command.apply((ServerPlayerEntity) asPlayer(source));
		} catch (CommandException e) {
			return false;
		}
	}

	private static void sendSuccess(CommandSource source, Text message) {
		sendSuccess(source, message, false);
	}

	private static void sendSuccess(CommandSource source, Text message, boolean actionBar) {
		if (actionBar) {
			throw new UnsupportedOperationException();
		} else {
			source.sendMessage(message.resolve());
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
