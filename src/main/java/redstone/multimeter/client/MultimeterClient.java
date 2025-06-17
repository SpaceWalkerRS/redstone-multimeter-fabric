package redstone.multimeter.client;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HitResult;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.gui.screen.MultimeterScreen;
import redstone.multimeter.client.gui.screen.OptionsScreen;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.screen.ScreenWrapper;
import redstone.multimeter.client.gui.screen.TickPhaseTreeScreen;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.meter.ClientMeterPropertiesManager;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.client.render.MeterRenderer;
import redstone.multimeter.client.tutorial.Tutorial;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.common.network.packets.AddMeterPacket;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.MeterGroupRefreshPacket;
import redstone.multimeter.common.network.packets.MeterGroupSubscriptionPacket;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RebuildTickPhaseTreePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;
import redstone.multimeter.common.network.packets.TickPhaseTreePacket;

public class MultimeterClient {

	private static final Function<String, String> VERSION_WARNING = (modVersion) -> {
		String warning;

		if (modVersion.isEmpty()) {
			warning = "WARNING: the server is running an unknown version of Redstone Multimeter. If you are experiencing issues, ask the server operator for the correct version of Redstone Multimeter to install.";
		} else {
			warning = "WARNING: the server is running a different version of Redstone Multimeter. If you are experiencing issues, install version " + modVersion + " of Redstone Multimeter.";
		}

		return warning;
	};

	public static Minecraft MINECRAFT;

	private final Minecraft minecraft;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final MeterRenderer meterRenderer;
	private final MultimeterHud hud;
	private final SavedMeterGroupsManager savedMeterGroupsManager;
	private final ClientMeterPropertiesManager meterPropertiesManager;
	private final Tutorial tutorial;

	private final TickPhaseTree tickPhaseTree;

	private ClientMeterGroup meterGroup;
	private ClientMeterGroup meterGroupPreview;
	private boolean connected; // true if the client is connected to a MultimeterServer
	private boolean hudEnabled;
	private long prevGameTime;

	private int pendingMeterGroupSlot = -1;
	private String pendingMeterGroupName = null;

	public MultimeterClient(Minecraft minecraft) {
		MINECRAFT = minecraft;

		this.minecraft = minecraft;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.meterRenderer = new MeterRenderer(this);
		this.hud = new MultimeterHud(this);
		this.savedMeterGroupsManager = new SavedMeterGroupsManager(this);
		this.meterPropertiesManager = new ClientMeterPropertiesManager(this);
		this.tutorial = new Tutorial(this);

		this.tickPhaseTree = new TickPhaseTree();

		this.meterGroup = new ClientMeterGroup(this);
		this.meterGroupPreview = new ClientMeterGroup(this);
		this.connected = false;
		this.hudEnabled = true;
		this.prevGameTime = -1;

		this.hud.init();

		reloadResources();
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}

	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}

	public void sendPacket(RSMMPacket packet) {
		minecraft.getNetworkHandler().sendPacket(packetHandler.encode(packet));
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public MeterRenderer getMeterRenderer() {
		return meterRenderer;
	}

	public MultimeterHud getHud() {
		return hud;
	}

	public SavedMeterGroupsManager getSavedMeterGroupsManager() {
		return savedMeterGroupsManager;
	}

	public ClientMeterPropertiesManager getMeterPropertiesManager() {
		return meterPropertiesManager;
	}

	public Tutorial getTutorial() {
		return tutorial;
	}

	public ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}

	public ClientMeterGroup getMeterGroupPreview() {
		return meterGroupPreview;
	}

	public boolean hasSubscription() {
		return meterGroup.isSubscribed();
	}

	public boolean isPreviewing() {
		return meterGroupPreview.isPreviewing();
	}

	public TickPhaseTree getTickPhaseTree() {
		return tickPhaseTree;
	}

	public void requestTickPhaseTree() {
		tickPhaseTree.reset();

		TickPhaseTreePacket packet = new TickPhaseTreePacket(new NbtCompound());
		sendPacket(packet);
	}

	public void rebuildTickPhaseTree() {
		tickPhaseTree.reset();

		RebuildTickPhaseTreePacket packet = new RebuildTickPhaseTreePacket();
		sendPacket(packet);
	}

	public void refreshTickPhaseTree(NbtCompound nbt) {
		if (tickPhaseTree.isComplete()) {
			tickPhaseTree.reset();
		}

		tickPhaseTree.fromNbt(nbt);

		if (hasRSMMScreenOpen()) {
			RSMMScreen screen = getScreen();

			if (screen instanceof TickPhaseTreeScreen) {
				((TickPhaseTreeScreen)screen).refresh();
			}
		}
	}

	/**
	 * Check if this client is connected to a Multimeter server
	 */
	public boolean isConnected() {
		return connected;
	}

	public boolean isHudEnabled() {
		return hudEnabled;
	}

	public boolean isHudActive() {
		return hud.hasContent() && (hudEnabled || hud.isOnScreen());
	}

	public long getPrevGameTime() {
		return prevGameTime;
	}

	public Path getConfigDirectory() {
		return getConfigDirectory(minecraft);
	}


	public static Path getConfigDirectory(Minecraft minecraft) {
		return minecraft.runDir.toPath().resolve(RedstoneMultimeterMod.CONFIG_PATH);
	}

	public void reloadResources() {
		meterPropertiesManager.reload();
	}

	/**
	 * A packet is sent each time the level ticks time.
	 */
	public void tickTime(long gameTime) {
		prevGameTime = gameTime;

		meterGroup.tick();
		hud.tickTime();
		savedMeterGroupsManager.tick();
	}

	public void onStartup() {
		savedMeterGroupsManager.load();
	}

	public void onShutdown() {
		savedMeterGroupsManager.save();
		meterGroup.getLogManager().getPrinter().stop(false);
	}

	public void onConnect() {
		if (!connected) {
			HandshakePacket packet = new HandshakePacket();
			sendPacket(packet);
		}
	}

	public void onDisconnect() {
		if (connected) {
			connected = false;

			hud.reset();
			tickPhaseTree.reset();
			meterGroup.unsubscribe(true);
		}
	}

	public void onHandshake(String modVersion) {
		if (!connected) {
			connected = true;

			if (Options.Miscellaneous.VERSION_WARNING.get() && !RedstoneMultimeterMod.MOD_VERSION.equals(modVersion)) {
				Text warning = new LiteralText(VERSION_WARNING.apply(modVersion)).setFormatting(Formatting.RED);
				sendMessage(warning, false);
			}

			hud.reset();

			if (Options.RedstoneMultimeter.CREATE_GROUP_ON_JOIN.get()) {
				createDefaultMeterGroup();
			}
		}
	}

	public void createDefaultMeterGroup() {
		String name = Options.RedstoneMultimeter.DEFAULT_METER_GROUP.get();

		if (!MeterGroup.isValidName(name)) {
			name = minecraft.getSession().getUsername();
		}

		subscribeToMeterGroup(-1, name);
	}

	public void subscribeToMeterGroup(int slot, String name) {
		pendingMeterGroupSlot = slot;
		pendingMeterGroupName = name;

		MeterGroupSubscriptionPacket packet = new MeterGroupSubscriptionPacket(name, true);
		sendPacket(packet);
	}

	public void handleSubscribeToMeterGroup(String name) {
		int slot = name.equals(pendingMeterGroupName)
			? pendingMeterGroupSlot
			: -1;

		meterGroup.subscribe(slot,  name);

		pendingMeterGroupSlot = -1;
		pendingMeterGroupName = null;
	}

	public void unsubscribeFromMeterGroup() {
		MeterGroupSubscriptionPacket packet = new MeterGroupSubscriptionPacket(meterGroup.getName(), false);
		sendPacket(packet);
	}

	public void refreshMeterGroup() {
		MeterGroupRefreshPacket packet = new MeterGroupRefreshPacket(meterGroup);
		sendPacket(packet);
	}

	/**
	 * Request the server to add a meter at the position
	 * the player is looking at or remove it if there
	 * already is one.
	 */
	public void toggleMeter() {
		onTargetBlock(pos -> {
			Meter meter = meterGroup.getMeterAt(pos);

			if (meter == null) {
				addMeter(pos);
			} else {
				RemoveMeterPacket packet = new RemoveMeterPacket(meter.getId());
				sendPacket(packet);

				tutorial.onMeterRemoveRequested(pos);
			}
		});
	}

	private void addMeter(DimPos pos) {
		MutableMeterProperties properties = new MutableMeterProperties();
		properties.setPos(pos);

		if (meterPropertiesManager.validate(properties)) {
			AddMeterPacket packet = new AddMeterPacket(properties);
			sendPacket(packet);

			tutorial.onMeterAddRequested(pos);
		}
	}

	public void resetMeter() {
		onTargetMeter(meter -> {
			MutableMeterProperties newProperties = new MutableMeterProperties();
			newProperties.setPos(meter.getPos());

			if (meterPropertiesManager.validate(newProperties)) {
				MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
				sendPacket(packet);
			}
		});
	}

	public void togglePrinter() {
		if (hud.hasContent()) {
			meterGroup.getLogManager().getPrinter().toggle();
		}
	}

	public void openMeterControls() {
		onTargetMeter(meter -> {
			openScreen(new MultimeterScreen(this));
			hud.selectMeter(meter);
		});
	}

	public void toggleEventType(EventType type) {
		onTargetMeter(meter -> {
			MutableMeterProperties newProperties = new MutableMeterProperties();
			newProperties.setEventTypes(meter.getEventTypes());
			newProperties.toggleEventType(type);

			MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
			sendPacket(packet);
		});
	}

	private void onTargetMeter(Consumer<Meter> action) {
		onTargetBlock(pos -> {
			Meter meter = meterGroup.getMeterAt(pos);

			if (meter != null) {
				action.accept(meter);
			}
		});
	}

	private void onTargetBlock(Consumer<DimPos> action) {
		HitResult hit = minecraft.crosshairTarget;

		if (hit.type == HitResult.Type.BLOCK) {
			World world = minecraft.world;
			BlockPos pos = hit.getPos();

			action.accept(new DimPos(world, pos));
		}
	}

	public void toggleHud() {
		if (hud.hasContent()) {
			hudEnabled = !hudEnabled;

			String message = String.format("%s Multimeter HUD", hudEnabled ? "Enabled" : "Disabled");
			sendMessage(new LiteralText(message), true);

			tutorial.onToggleHud(hudEnabled);
		}
	}

	public RSMMScreen getScreen() {
		if (minecraft.screen != null && minecraft.screen instanceof ScreenWrapper) {
			ScreenWrapper screenWrapper = (ScreenWrapper)minecraft.screen;
			return screenWrapper.getScreen();
		}

		return null;
	}

	public void openScreen(RSMMScreen screen) {
		minecraft.openScreen(new ScreenWrapper(minecraft.screen, screen));
		tutorial.onScreenOpened(screen);
	}

	public boolean hasScreenOpen() {
		return minecraft.screen != null;
	}

	public boolean hasRSMMScreenOpen() {
		return minecraft.screen != null && minecraft.screen instanceof ScreenWrapper;
	}

	public boolean hasMultimeterScreenOpen() {
		RSMMScreen screen = getScreen();
		return screen != null && screen instanceof MultimeterScreen;
	}

	public boolean hasOptionsScreenOpen() {
		RSMMScreen screen = getScreen();
		return screen != null && screen instanceof OptionsScreen;
	}

	public void sendMessage(Text message, boolean actionBar) {
		minecraft.player.addMessage(message, actionBar);
	}
}
