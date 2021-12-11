package redstone.multimeter.client;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.gui.MultimeterScreen;
import redstone.multimeter.client.gui.OptionsScreen;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.element.ScreenWrapper;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.client.meter.ClientMeterPropertiesManager;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.client.render.MeterRenderer;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.AddMeterPacket;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.MeterGroupRefreshPacket;
import redstone.multimeter.common.network.packets.MeterGroupSubscriptionPacket;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;

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
	
	private final MinecraftClient client;
	private final ClientPacketHandler packetHandler;
	private final InputHandler inputHandler;
	private final MeterRenderer meterRenderer;
	private final MultimeterHud hud;
	private final ClientMeterPropertiesManager meterPropertiesManager;
	
	private ClientMeterGroup meterGroup;
	private boolean connected; // true if the client is connected to a MultimeterServer
	private boolean hudEnabled;
	private long prevServerTime;
	
	public MultimeterClient(MinecraftClient client) {
		this.client = client;
		this.packetHandler = new ClientPacketHandler(this);
		this.inputHandler = new InputHandler(this);
		this.meterRenderer = new MeterRenderer(this);
		this.hud = new MultimeterHud(this);
		this.meterPropertiesManager = new ClientMeterPropertiesManager(this);
		
		this.meterGroup = new ClientMeterGroup(this);
		this.connected = false;
		this.hudEnabled = true;
		this.prevServerTime = -1;
		
		this.hud.init();
		
		reloadResources();
	}
	
	public MinecraftClient getMinecraftClient() {
		return client;
	}
	
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
	
	public MeterRenderer getMeterRenderer() {
		return meterRenderer;
	}
	
	public MultimeterHud getHUD() {
		return hud;
	}
	
	public ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	public boolean hasSubscription() {
		return meterGroup.isSubscribed();
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
	
	public long getPrevServerTime() {
		return prevServerTime;
	}
	
	public File getConfigFolder() {
		return getConfigFolder(client);
	}
	
	public static File getConfigFolder(MinecraftClient client) {
		return new File(client.runDirectory, RedstoneMultimeterMod.CONFIG_PATH);
	}
	
	public void reloadResources() {
		meterPropertiesManager.reload();
	}
	
	/**
	 * At the end of each server tick, the server sends a packet
	 * to clients with the current server time.
	 */
	public void onServerTick(long serverTime) {
		prevServerTime = serverTime;
		
		meterGroup.tick();
		hud.onServerTick();
	}
	
	public void onShutdown() {
		meterGroup.getLogManager().getPrinter().stop(false);
	}
	
	public void onConnect() {
		if (!connected) {
			HandshakePacket packet = new HandshakePacket();
			packetHandler.send(packet);
		}
	}
	
	public void onDisconnect() {
		if (connected) {
			connected = false;
			
			hud.reset();
			meterGroup.unsubscribe(true);
		}
	}
	
	public void onHandshake(String modVersion) {
		if (!connected) {
			connected = true;
			
			if (Options.Miscellaneous.VERSION_WARNING.get() && !RedstoneMultimeterMod.MOD_VERSION.equals(modVersion)) {
				Text warning = new LiteralText(VERSION_WARNING.apply(modVersion)).setStyle(new Style().setColor(Formatting.RED));
				sendMessage(warning);
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
			name = client.getSession().getUsername();
		}
		
		subscribeToMeterGroup(name);
	}
	
	public void subscribeToMeterGroup(String name) {
		MeterGroupSubscriptionPacket packet = new MeterGroupSubscriptionPacket(name, true);
		packetHandler.send(packet);
	}
	
	public void unsubscribeFromMeterGroup() {
		MeterGroupSubscriptionPacket packet = new MeterGroupSubscriptionPacket(meterGroup.getName(), false);
		packetHandler.send(packet);
	}
	
	public void refreshMeterGroup() {
		MeterGroupRefreshPacket packet = new MeterGroupRefreshPacket(meterGroup);
		packetHandler.send(packet);
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
				packetHandler.send(packet);
			}
		});
	}
	
	private void addMeter(DimPos pos) {
		MeterProperties properties = new MeterProperties();
		properties.setPos(pos);
		
		if (meterPropertiesManager.validate(properties)) {
			AddMeterPacket packet = new AddMeterPacket(properties);
			packetHandler.send(packet);
		}
	}
	
	public void resetMeter() {
		onTargetMeter(meter -> {
			MeterProperties newProperties = new MeterProperties();
			newProperties.setPos(meter.getPos());
			
			if (meterPropertiesManager.validate(newProperties)) {
				MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
				packetHandler.send(packet);
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
			MeterProperties newProperties = new MeterProperties();
			newProperties.setEventTypes(meter.getEventTypes());
			newProperties.toggleEventType(type);
			
			MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
			packetHandler.send(packet);
		});
	}
	
	private void onTargetMeter(Consumer<Meter> consumer) {
		onTargetBlock(pos -> {
			Meter meter = meterGroup.getMeterAt(pos);
			
			if (meter != null) {
				consumer.accept(meter);
			}
		});
	}
	
	private void onTargetBlock(Consumer<DimPos> consumer) {
		HitResult hitResult = client.result;
		
		if (hitResult.type == HitResult.Type.BLOCK) {
			World world = client.world;
			BlockPos blockPos = hitResult.getBlockPos();
			
			consumer.accept(new DimPos(world, blockPos));
		}
	}
	
	public void toggleHud() {
		if (hud.hasContent()) {
			hudEnabled = !hudEnabled;
			
			String message = String.format("%s Multimeter HUD", hudEnabled ? "Enabled" : "Disabled");
			sendMessage(new LiteralText(message));
		}
	}
	
	public RSMMScreen getScreen() {
		if (client.currentScreen != null && client.currentScreen instanceof ScreenWrapper) {
			ScreenWrapper screenWrapper = (ScreenWrapper)client.currentScreen;
			return screenWrapper.getScreen();
		}
		
		return null;
	}
	
	public void openScreen(RSMMScreen screen) {
		client.openScreen(new ScreenWrapper(client.currentScreen, screen));
	}
	
	public boolean hasScreenOpen() {
		return client.currentScreen != null;
	}
	
	public boolean hasRSMMScreenOpen() {
		return client.currentScreen != null && client.currentScreen instanceof ScreenWrapper;
	}
	
	public boolean hasMultimeterScreenOpen() {
		RSMMScreen screen = getScreen();
		return screen != null && screen instanceof MultimeterScreen;
	}
	
	public boolean hasOptionsScreenOpen() {
		RSMMScreen screen = getScreen();
		return screen != null && screen instanceof OptionsScreen;
	}
	
	public void sendMessage(Text message) {
		client.player.sendMessage(message);
	}
}
