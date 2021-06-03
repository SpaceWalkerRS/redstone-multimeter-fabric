package rsmm.fabric.server;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import rsmm.fabric.block.Meterable;
import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.packet.types.AddMeterPacket;
import rsmm.fabric.common.packet.types.MeterChangesPacket;
import rsmm.fabric.common.packet.types.MeterGroupDataPacket;
import rsmm.fabric.common.packet.types.MeterLogsPacket;
import rsmm.fabric.common.packet.types.RemoveAllMetersPacket;
import rsmm.fabric.common.packet.types.RemoveMeterPacket;
import rsmm.fabric.interfaces.mixin.IBlock;
import rsmm.fabric.util.ColorUtils;
import rsmm.fabric.util.NBTUtils;

public class Multimeter {
	
	private final MultimeterServer server;
	private final Map<String, ServerMeterGroup> meterGroups;
	private final Map<ServerPlayerEntity, ServerMeterGroup> subscriptions;
	
	private TickPhase currentTickPhase = TickPhase.UNKNOWN;
	
	public Multimeter(MultimeterServer server) {
		this.server = server;
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
	}
	
	public MultimeterServer getMultimeterServer() {
		return server;
	}
	
	public Collection<ServerMeterGroup> getMeterGroups() {
		return Collections.unmodifiableCollection(meterGroups.values());
	}
	
	public Set<String> getMeterGroupNames() {
		return Collections.unmodifiableSet(meterGroups.keySet());
	}
	
	public ServerMeterGroup getMeterGroup(String name) {
		return meterGroups.get(name);
	}
	
	public ServerMeterGroup getSubscription(ServerPlayerEntity player) {
		return subscriptions.get(player);
	}
	
	public boolean hasSubscription(ServerPlayerEntity player) {
		return subscriptions.containsKey(player);
	}
	
	public TickPhase getCurrentTickPhase() {
		return currentTickPhase;
	}
	
	public void onTickPhase(TickPhase tickPhase) {
		currentTickPhase = tickPhase;
	}
	
	public void tickStart() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			meterGroup.getLogManager().tick();
		}
	}
	
	public void tickEnd(boolean paused) {
		broadcastMeterData();
		
		if (!paused) {
			broadcastMeterLogs();
		}
		
		onTickPhase(TickPhase.UNKNOWN);
	}
	
	/**
	 * This is called at the end of every server tick,
	 * and sends meter changes of the past tick to
	 * clients.
	 */
	private void broadcastMeterData() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.isDirty()) {
				if (meterGroup.hasSubscribers()) {
					CompoundTag data = meterGroup.collectMeterChanges();
					MeterChangesPacket packet = new MeterChangesPacket(data);
					server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
				}
				
				meterGroup.cleanUp();
			}
		}
	}
	
	/**
	 * This is called at the end of every server tick,
	 * and sends all the logged events of the past tick
	 * to clients.
	 */
	private void broadcastMeterLogs() {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (meterGroup.hasNewLogs()) {
				if (meterGroup.hasSubscribers()) {
					CompoundTag data = meterGroup.getLogManager().collectMeterLogs();
					MeterLogsPacket packet = new MeterLogsPacket(data);
					server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
				}
				
				meterGroup.cleanLogs();
			}
		}
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.remove(player);
		
		if (meterGroup != null) {
			removeSubscriber(meterGroup, player);
		}
	}
	
	/**
	 * Add a meter at the position the player is looking at
	 * or remove it if there already is one.
	 */
	public void toggleMeter(CompoundTag properties, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup == null) {
			return;
		}
		
		DimPos pos = NBTUtils.tagToDimPos(properties.getCompound("pos"));
		boolean movable = properties.getBoolean("movable");
		
		if (meterGroup.hasMeterAt(pos)) {
			removeMeter(meterGroup, meterGroup.indexOfMeterAt(pos));
		} else {
			addMeter(meterGroup, pos, movable, null, null);
		}
	}
	
	public void addMeter(ServerMeterGroup meterGroup, DimPos pos, boolean movable, String name, Integer color) {
		World world = server.getWorldOf(pos);
		
		if (world != null) {
			BlockPos blockPos = pos.asBlockPos();
			BlockState state = world.getBlockState(blockPos);
			Block block = state.getBlock();
			
			if (name == null) {
				name = meterGroup.getNextMeterName(block);
			}
			
			int nextColor = ColorUtils.nextColor();
			
			if (color == null) {
				color = nextColor;
			}
			
			int meteredEvents = ((IBlock)block).getDefaultMeteredEvents();
			boolean powered = ((IBlock)block).isPowered(world, blockPos, state);
			boolean active = ((IBlock)block).isMeterable() && ((Meterable)block).isActive(world, blockPos, state);
			
			Meter meter = new Meter(pos, name, color, movable, meteredEvents, powered, active);
			meterGroup.addMeter(meter);
			
			AddMeterPacket packet = new AddMeterPacket(meter);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void removeMeter(int index, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			removeMeter(meterGroup, index);
		}
	}
	
	public void removeMeter(ServerMeterGroup meterGroup, int index) {
		if (meterGroup.removeMeter(index)) {
			RemoveMeterPacket packet = new RemoveMeterPacket(index);
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void moveMeter(int index, DimPos toPos, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			moveMeter(index, toPos, meterGroup);
		}
	}
	
	public void moveMeter(int index, DimPos toPos, ServerMeterGroup meterGroup) {
		World world = server.getWorldOf(toPos);
		
		if (world != null) {
			meterGroup.tryMoveMeter(index, toPos, true);
		}
	}
	
	public void renameMeter(int index, String name, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setName(name), player);
	}
	
	public void recolorMeter(int index, int color, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setColor(color), player);
	}
	
	public void changeMeterMovability(int index, boolean movable, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.setIsMovable(movable), player);
	}
	
	public void toggleEventType(int index, EventType type, ServerPlayerEntity player) {
		changeMeter(index, meter -> meter.toggleEventType(type), player);
	}
	
	private void changeMeter(int index, Consumer<Meter> change, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != Meter.DUMMY) {
				change.accept(meter);
				meter.markDirty();
			}
		}
	}
	
	public void removeAllMeters(ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			meterGroup.clear();
			
			RemoveAllMetersPacket packet = new RemoveAllMetersPacket();
			server.getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
		}
	}
	
	public void subscribeToMeterGroup(String name, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = meterGroups.get(name);
		
		if (meterGroup == null) {
			meterGroup = new ServerMeterGroup(this, name);
			meterGroups.put(name, meterGroup);
		}
		
		subscribeToMeterGroup(meterGroup, player);
	}
	
	public void subscribeToMeterGroup(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		ServerMeterGroup prevSubscription = subscriptions.remove(player);
		
		if (prevSubscription != null) {
			removeSubscriber(prevSubscription, player);
		}
		
		subscriptions.put(player, meterGroup);
		meterGroup.addSubscriber(player);
		
		MeterGroupDataPacket packet = new MeterGroupDataPacket(meterGroup);
		server.getPacketHandler().sendPacketToPlayer(packet, player);
	}
	
	public void removeSubscriber(ServerMeterGroup meterGroup, ServerPlayerEntity player) {
		meterGroup.removeSubscriber(player);
		
		// If a meter group is empty and no players
		// are subscribed to it, remove it.
		if (!meterGroup.hasSubscribers() && meterGroup.getMeterCount() == 0) {
			meterGroups.remove(meterGroup.getName(), meterGroup);
		}
	}
	
	public void meterGroupDataReceived(String name, CompoundTag data, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = meterGroups.get(name);
		
		// This allows a player to carry over a meter group
		// between different worlds and/or servers
		if (meterGroup == null) {
			meterGroup = new ServerMeterGroup(this, name);
			meterGroup.fromTag(data);
			
			meterGroups.put(name, meterGroup);
		}
		
		subscribeToMeterGroup(meterGroup, player);
	}
	
	public void teleportToMeter(int index, ServerPlayerEntity player) {
		ServerMeterGroup meterGroup = subscriptions.get(player);
		
		if (meterGroup != null) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != Meter.DUMMY) {
				DimPos pos = meter.getPos();
				ServerWorld newWorld = server.getWorldOf(pos);
				
				if (newWorld != null) {
					ServerWorld oldWorld = player.getServerWorld();
					double oldX = player.getX();
					double oldY = player.getY();
					double oldZ = player.getZ();
					
					BlockPos blockPos = pos.asBlockPos();
					
					double newX = blockPos.getX() + 0.5D;
					double newY = blockPos.getY();
					double newZ = blockPos.getZ() + 0.5D;
					float yaw = player.yaw;
					float pitch = player.pitch;
					
					player.teleport(newWorld, newX, newY, newZ, yaw, pitch);
					sendClickableReturnMessage(oldWorld, oldX, oldY, oldZ, yaw, pitch, player);
				}
			}
		}
	}
	
	/**
	 * Send the player a message they can click
	 * to return to the location they were at
	 * before teleporting to a meter.
	 */
	private void sendClickableReturnMessage(ServerWorld world, double _x, double _y, double _z, float _yaw, float _pitch, ServerPlayerEntity player) {
		NumberFormat f = NumberFormat.getNumberInstance(Locale.US); // use . as decimal separator
		
		String dimensionId = DimensionType.getId(world.dimension.getType()).toString();
		String x = f.format(_x);
		String y = f.format(_y);
		String z = f.format(_z);
		String yaw = f.format(_yaw);
		String pitch = f.format(_pitch);
		
		Text message = new LiteralText("Click ").
			append(new LiteralText("[here]").styled((style) -> {
				style.
					setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Teleport to").
						append(MeterEvent.formatTextForTooltip("\n  dimension", dimensionId)).
						append(MeterEvent.formatTextForTooltip("\n  x", x)).
						append(MeterEvent.formatTextForTooltip("\n  y", y)).
						append(MeterEvent.formatTextForTooltip("\n  z", z)))).
					setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/execute in %s run tp @s %s %s %s %s %s", dimensionId, x, y, z, yaw, pitch))).
					setColor(Formatting.GREEN);
			})).
			append(new LiteralText(" to return to your previous location"));
		
		player.sendMessage(message);
	}
	
	public void blockChanged(World world, BlockPos blockPos, Block oldBlock, Block newBlock) {
		DimPos pos = new DimPos(world, blockPos);
		
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (!meterGroup.hasSubscribers() || !meterGroup.hasMeters()) {
				continue;
			}
			
			meterGroup.blockChanged(pos, oldBlock, newBlock);
		}
	}
	
	public void logPowered(World world, BlockPos pos, boolean powered) {
		tryLogEvent(new DimPos(world, pos), EventType.POWERED, powered ? 1 : 0, meter -> meter.updatePowered(powered), (m, i) -> {});
	}
	
	public void logActive(World world, BlockPos pos, boolean active) {
		tryLogEvent(new DimPos(world, pos), EventType.ACTIVE, active ? 1 : 0, meter -> meter.updateActive(active), (m, i) -> {});
	}
	
	public void logMoved(World world, BlockPos blockPos, Direction dir) {
		DimPos pos = new DimPos(world, blockPos);
		
		tryLogEvent(pos, EventType.MOVED, dir.getId(), meter -> true, (meterGroup, index) -> {
			meterGroup.tryMoveMeter(index, pos.offset(dir));
		});
	}
	
	public void logPowerChange(World world, BlockPos pos, int oldPower, int newPower) {
		if (oldPower != newPower) {
			tryLogEvent(world, pos, EventType.POWER_CHANGE, (oldPower << 8) | newPower);
		}
	}
	
	public void logRandomTick(World world, BlockPos pos) {
		tryLogEvent(world, pos, EventType.RANDOM_TICK, 0);
	}
	
	public <T> void logScheduledTick(World world, ScheduledTick<T> scheduledTick) {
		tryLogEvent(world, scheduledTick.pos, EventType.SCHEDULED_TICK, scheduledTick.priority.getIndex());
	}
	
	public void logBlockEvent(World world, BlockAction blockAction) {
		tryLogEvent(world, blockAction.getPos(), EventType.BLOCK_EVENT, blockAction.getType());
	}
	
	public void logEntityTick(World world, Entity entity) {
		tryLogEvent(world, entity.getBlockPos(), EventType.ENTITY_TICK, 0);
	}
	
	public void logBlockEntityTick(World world, BlockEntity blockEntity) {
		tryLogEvent(world, blockEntity.getPos(), EventType.BLOCK_ENTITY_TICK, 0);
	}
	
	private void tryLogEvent(World world, BlockPos pos, EventType type, int metaData) {
		tryLogEvent(new DimPos(world, pos), type, metaData, m -> true, (m, i) -> {});
	}
	
	private void tryLogEvent(DimPos pos, EventType type, int metaData, Predicate<Meter> meterPredicate, BiConsumer<ServerMeterGroup, Integer> onLog) {
		for (ServerMeterGroup meterGroup : meterGroups.values()) {
			if (!meterGroup.hasSubscribers() || !meterGroup.hasMeters()) {
				continue;
			}
			
			meterGroup.tryLogEvent(pos, type, metaData, meterPredicate, onLog);
		}
	}
}
