package redstone.multimeter.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.NotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.PlayerSelector;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupCommand extends AbstractCommand {
	
	private static final String COMMAND_NAME = "metergroup";
	
	private static final String USAGE_CLEAR             = singleUsage("clear");
	
	private static final String USAGE_SUBSCRIBE_DEFAULT = singleUsage("subscribe");
	private static final String USAGE_SUBSCRIBE_NAME    = singleUsage("subscribe <name>");
	private static final String USAGE_SUBSCRIBE         = buildUsage(USAGE_SUBSCRIBE_DEFAULT, USAGE_SUBSCRIBE_NAME);
	
	private static final String USAGE_UNSUBSCRIBE       = singleUsage("unsubscribe");
	
	private static final String USAGE_PRIVATE_QUERY     = singleUsage("private");
	private static final String USAGE_PRIVATE_SET       = singleUsage("private <private true|false>");
	private static final String USAGE_PRIVATE           = buildUsage(USAGE_PRIVATE_QUERY, USAGE_PRIVATE_SET);
	
	private static final String USAGE_MEMBERS_CLEAR     = singleUsage("members clear");
	private static final String USAGE_MEMBERS_ADD       = singleUsage("members add <player>");
	private static final String USAGE_MEMBERS_REMOVE    = singleUsage("members remove <player>");
	private static final String USAGE_MEMBERS_LIST      = singleUsage("members list");
	private static final String USAGE_MEMBERS           = buildUsage(USAGE_MEMBERS_CLEAR, USAGE_MEMBERS_ADD, USAGE_MEMBERS_REMOVE, USAGE_MEMBERS_LIST);
	
	private static final String USAGE_LIST              = singleUsage("list");
	
	private static final String TOTAL_USAGE_MEMBER = buildUsage(USAGE_CLEAR, USAGE_SUBSCRIBE, USAGE_UNSUBSCRIBE, USAGE_LIST);
	private static final String TOTAL_USAGE_OWNER  = buildUsage(USAGE_CLEAR, USAGE_SUBSCRIBE, USAGE_UNSUBSCRIBE, USAGE_PRIVATE, USAGE_MEMBERS, USAGE_LIST);
	
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
	public String getCommandName() {
		return "metergroup";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		try {
			if (isOwnerOfSubscription(source)) {
				return TOTAL_USAGE_OWNER;
			}
		} catch (CommandException e) {
			
		}
		
		return TOTAL_USAGE_MEMBER;
	}
	
	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		boolean isOwner = false;
		
		try {
			isOwner = isOwnerOfSubscription(source);
		} catch (CommandException e) {
			
		}
		
		switch (args.length) {
		case 1:
			if (isOwner) {
				return method_2894(args, "clear", "subscribe", "unsubscribe", "private", "members", "list");
			} else {
				return method_2894(args, "clear", "subscribe", "unsubscribe", "list");
			}
		case 2:
			switch (args[0]) {
			case "subscribe":
				try {
					return method_10708(args, listMeterGroups(source));
				} catch (CommandException e) {
					
				}
				
				break;
			case "private":
				if (isOwner) {
					return method_2894(args, "true", "false");
				}
				
				break;
			case "members":
				if (isOwner) {
					return method_2894(args, "clear", "add", "remove", "list");
				}
				
				break;
			}
			
			break;
		case 3:
			if (isOwner && args[0].equals("members")) {
				switch (args[1]) {
				case "add":
					return method_2894(args, server.getMinecraftServer().getPlayerNames());
				case "remove":
					try {
						return method_10708(args, listMembers(source).keySet());
					} catch (CommandException e) {
						
					}
					
					break;
				}
			}
			
			break;
		}
		
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (!isMultimeterClient(source)) {
			throw new NotFoundException();
		}
		
		if (args.length > 0) {
			switch (args[0]) {
			case "clear":
				if (args.length == 1) {
					clear(source);
					return;
				}
				
				throw new IncorrectUsageException(USAGE_CLEAR);
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
					case "clear":
						if (args.length == 2) {
							membersClear(source);
							return;
						}
						
						throw new IncorrectUsageException(USAGE_MEMBERS_CLEAR);
					case "add":
						if (args.length == 3) {
							membersAdd(source, findMatchingPlayers(server.getMinecraftServer(), source, args[2]));
							return;
						}
						
						throw new IncorrectUsageException(USAGE_MEMBERS_ADD);
					case "remove":
						if (args.length == 3) {
							membersRemove(source, args[2]);
							return;
						}
						
						throw new IncorrectUsageException(USAGE_MEMBERS_REMOVE);
					case "list":
						if (args.length == 2) {
							membersList(source);
							return;
						}
						
						throw new IncorrectUsageException(USAGE_MEMBERS_LIST);
					}
				}
				
				throw new IncorrectUsageException(USAGE_MEMBERS);
			case "list":
				if (args.length == 1) {
					list(source);
					return;
				}
				
				throw new IncorrectUsageException(USAGE_LIST);
			}
		}
		
		throw new IncorrectUsageException(getUsageTranslationKey(source));
	}
	
	private boolean isMultimeterClient(CommandSource source) throws CommandException {
		return execute(source, player -> multimeter.getMultimeterServer().isMultimeterClient(player));
	}
	
	private boolean isOwnerOfSubscription(CommandSource source) throws CommandException {
		return execute(source, player -> multimeter.isOwnerOfSubscription(player));
	}
	
	private Collection<String> listMeterGroups(CommandSource source) throws CommandException {
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
	
	private Map<String, UUID> listMembers(CommandSource source) throws CommandException {
		Map<String, UUID> names = new HashMap<>();
		
		command(source, player -> {
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
	
	private void clear(CommandSource source) throws CommandException {
		command(source, (meterGroup, player) -> {
			multimeter.clearMeterGroup(player);
			source.sendMessage(new LiteralText(String.format("Removed all meters in meter group \'%s\'", multimeter.getSubscription(player).getName())));
		});
	}
	
	private void subscribe(CommandSource source, String name) throws CommandException {
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
					source.sendMessage(new LiteralText("A meter group with that name already exists and it is private!"));
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
	
	private void unsubscribe(CommandSource source) throws CommandException {
		command(source, (meterGroup, player) -> {
			multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			source.sendMessage(new LiteralText(String.format("Unsubscribed from meter group \'%s\'", meterGroup.getName())));
		});
	}
	
	private void queryPrivate(CommandSource source) throws CommandException {
		command(source, (meterGroup, player) -> {
			String status = meterGroup.isPrivate() ? "private" : "public";
			source.sendMessage(new LiteralText(String.format("Meter group \'%s\' is %s", meterGroup.getName(), status)));
		});
	}
	
	private void setPrivate(CommandSource source, boolean isPrivate) throws CommandException {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				meterGroup.setPrivate(isPrivate);
				source.sendMessage(new LiteralText(String.format("Meter group \'%s\' is now %s", meterGroup.getName(), (isPrivate ? "private" : "public"))));
			} else {
				source.sendMessage(new LiteralText("Only the owner of a meter group can change its privacy!"));
			}
		});
	}
	
	private void membersClear(CommandSource source) throws CommandException {
		commandMembers(source, (meterGroup, owner) -> {
			multimeter.clearMembersOfMeterGroup(meterGroup);
			source.sendMessage(new LiteralText(String.format("Removed all members from meter group \'%s\'", meterGroup.getName())));
		});
	}
	
	private void membersAdd(CommandSource source, Collection<ServerPlayerEntity> players) throws CommandException {
		commandMembers(source, (meterGroup, owner) -> {
			for (ServerPlayerEntity player : players) {
				if (player == owner) {
					source.sendMessage(new LiteralText("You cannot add yourself as a member!"));
				} else if (meterGroup.hasMember(player)) {
					source.sendMessage(new LiteralText(String.format("Player \'%s\' is already a member of meter group \'%s\'!", player.getName(), meterGroup.getName())));
				} else if (!multimeter.getMultimeterServer().isMultimeterClient(player)) {
					source.sendMessage(new LiteralText(String.format("You cannot add player \'%s\' as a member; they do not have %s installed!", player.getName(), RedstoneMultimeterMod.MOD_NAME)));
				} else {
					multimeter.addMemberToMeterGroup(meterGroup, player.getUuid());
					source.sendMessage(new LiteralText(String.format("Player \'%s\' is now a member of meter group \'%s\'", player.getName(), meterGroup.getName())));
				}
			}
		});
	}
	
	private void membersRemove(CommandSource source, String playerName) throws CommandException {
		commandMembers(source, (meterGroup, owner) -> {
			Entry<String, UUID> member = findMember(listMembers(source), playerName);
			
			if (member == null) {
				ServerPlayerEntity player = multimeter.getMultimeterServer().getPlayer(playerName);
				
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
	
	private void membersList(CommandSource source) throws CommandException {
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
	
	private void commandMembers(CommandSource source, MeterGroupCommandExecutor command) throws CommandException {
		command(source, (meterGroup, player) -> {
			if (meterGroup.isOwnedBy(player)) {
				command.execute(meterGroup, player);
				
				if (!meterGroup.isPrivate()) {
					source.sendMessage(new LiteralText("NOTE: this meter group is public; adding/removing members will not have any effect until you make it private!"));
				}
			}
		});
	}
	
	private void list(CommandSource source) throws CommandException {
		Collection<String> names = listMeterGroups(source);
		
		if (names.isEmpty()) {
			source.sendMessage(new LiteralText("There are no meter groups yet!"));
		} else {
			String message = "Meter groups:\n  " + String.join("\n  ", names);
			source.sendMessage(new LiteralText(message));
		}
	}
	
	private void command(CommandSource source, MeterGroupCommandExecutor command) throws CommandException {
		command(source, player -> {
			ServerMeterGroup meterGroup = multimeter.getSubscription(player);
			
			if (meterGroup == null) {
				source.sendMessage(new LiteralText("Please subscribe to a meter group first!"));
			} else {
				command.execute(meterGroup, player);
			}
		});
	}
	
	private void command(CommandSource source, MultimeterCommandExecutor command) throws CommandException {
		execute(source, player -> { command.execute(player); return true; });
	}
	
	private boolean execute(CommandSource source, CommandExecutor command) throws CommandException {
		return command.execute(getAsPlayer(source));
	}
	
	@FunctionalInterface
	private interface MultimeterCommandExecutor {
		
		public void execute(ServerPlayerEntity player) throws CommandException;
		
	}
	
	@FunctionalInterface
	private interface MeterGroupCommandExecutor {
		
		public void execute(ServerMeterGroup meterGroup, ServerPlayerEntity player) throws CommandException;
		
	}
	
	@FunctionalInterface
	private interface CommandExecutor {
		
		public boolean execute(ServerPlayerEntity player) throws CommandException;
		
	}
	
	private static List<ServerPlayerEntity> findMatchingPlayers(MinecraftServer server, CommandSource source, String arg) throws CommandException {
		List<ServerPlayerEntity> list = PlayerSelector.method_10866(source, arg, ServerPlayerEntity.class);
		
		if (list.isEmpty()) {
			return Arrays.asList(getPlayer(source, arg));
		}
		
		return list;
	}
}
