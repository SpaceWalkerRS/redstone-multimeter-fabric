package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Direction.Axis;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;
import redstone.multimeter.common.network.packets.TeleportToMeterPacket;
import redstone.multimeter.util.ColorUtils;
import redstone.multimeter.util.TextUtils;

public class MeterControlsElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final TextElement deleteConfirm;
	private final SimpleListElement controls;
	
	private int height;
	
	private Meter meter;
	
	private TextElement title;
	private IButton hideButton;
	private IButton deleteButton;
	
	private boolean triedDeleting;
	
	public MeterControlsElement(MultimeterClient client, int x, int y, int width) {
		super(x, y, width, 0);
		
		this.client = client;
		
		this.title = new TextElement(this.client, 0, 0, t -> t.add(Text.literal(String.format("Edit Meter \'%s\'", meter == null ? "" : meter.getName())).formatted(Formatting.UNDERLINE)).setWithShadow(true));
		this.hideButton = new Button(this.client, 0, 0, 18, 18, () -> Text.literal(meter != null && meter.isHidden() ? "\u25A0" : "\u25A1"), () -> Tooltip.of(String.format("%s Meter", meter == null || meter.isHidden() ? "Unhide" : "Hide")), button -> {
			this.client.getMeterGroup().toggleHidden(meter);
			return true;
		});
		this.deleteButton = new Button(this.client, 0, 0, 18, 18, () -> Text.literal("X").formatted(triedDeleting ? Formatting.RED : Formatting.WHITE), () -> Tooltip.of("Delete Meter").add(TextUtils.formatKeybind(KeyBindings.TOGGLE_METER)), button -> {
			tryDelete();
			
			if (triedDeleting && Screen.hasShiftDown()) {
				tryDelete(); // delete without asking for confirmation first
			}
			
			return true;
		});
		this.deleteConfirm = new TextElement(this.client, 0, 0, t -> t.add(Text.literal("Are you sure you want to delete this meter? YOU CANNOT UNDO THIS!").formatted(Formatting.ITALIC)).setWithShadow(true));
		this.controls = new SimpleListElement(this.client, getWidth());
		
		this.deleteConfirm.setVisible(false);
		
		addChild(this.title);
		addChild(this.hideButton);
		addChild(this.deleteButton);
		addChild(this.deleteConfirm);
		addChild(this.controls);
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (triedDeleting && getFocusedElement() != deleteButton) {
			undoTryDelete();
			consumed = true;
		}
		
		return consumed;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean consumed = super.keyPress(keyCode, scanCode, modifiers);
		
		if (triedDeleting && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			undoTryDelete();
			consumed = true;
		}
		
		return consumed;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean isVisible() {
		return super.isVisible() && meter != null;
	}
	
	@Override
	public void update() {
		Meter prevMeter = meter;
		meter = client.getHUD().getSelectedMeter();
		
		if (meter != prevMeter) {
			createControls();
		}
		
		super.update();
		updateCoords();
	}
	
	@Override
	public void onChangedX(int x) {
		updateCoords();
	}
	
	@Override
	public void onChangedY(int y) {
		updateCoords();
	}
	
	private void createControls() {
		controls.clear();
		
		if (meter == null) {
			return;
		}
		
		int totalWidth = 375;
		int buttonWidth = 150;
		
		MeterPropertyElement pos = new MeterPropertyElement(client, totalWidth, buttonWidth, "Pos", () -> Tooltip.of("Click to teleport!"), t -> {
			teleport();
			return true;
		});
		pos.addControl("dimension", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
			try {
				Identifier worldId = new Identifier(text);
				WorldPos newPos = meter.getPos().offset(worldId);
				
				changePos(newPos);
			} catch (InvalidIdentifierException e) {
				
			}
		}, () -> meter.getPos().getWorldId().toString()));
		pos.addCoordinateControl(Axis.X, () -> meter.getPos(), p -> changePos(p));
		pos.addCoordinateControl(Axis.Y, () -> meter.getPos(), p -> changePos(p));
		pos.addCoordinateControl(Axis.Z, () -> meter.getPos(), p -> changePos(p));
		
		MeterPropertyElement name = new MeterPropertyElement(client, totalWidth, buttonWidth, "Name");
		name.addControl("", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> changeName(text), () -> meter.getName()));
		
		MeterPropertyElement color = new MeterPropertyElement(client, totalWidth, buttonWidth, "Color");
		color.addControl("rgb", style -> style.withColor(meter.getColor()), (client, width, height) -> new TextField(client, 0, 0, width, height,  () -> Tooltip.EMPTY, text -> {
			try {
				changeColor(ColorUtils.fromRGBString(text));
			} catch (NumberFormatException e) {
				
			}
		}, () -> ColorUtils.toRGBString(meter.getColor())));
		color.addControl("red", style -> style.withColor(Formatting.RED), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int red = ColorUtils.getRed(c);
			
			return Text.literal(String.valueOf(red));
		}, () -> Tooltip.EMPTY, value -> {
			int red = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setRed(meter.getColor(), red);
			
			changeColor(c);
		}, () -> {
			int c = meter.getColor();
			int red = ColorUtils.getRed(c);
			
			return (double)red / 0xFF;
		}, 0xFF));
		color.addControl("blue", style -> style.withColor(Formatting.BLUE), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int blue = ColorUtils.getBlue(c);
			
			return Text.literal(String.valueOf(blue));
		}, () -> Tooltip.EMPTY, value -> {
			int blue = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setBlue(meter.getColor(), blue);
			
			changeColor(c);
		}, () -> {
			int c = meter.getColor();
			int blue = ColorUtils.getBlue(c);
			
			return (double)blue / 0xFF;
		}, 0xFF));
		color.addControl("green", style -> style.withColor(Formatting.GREEN), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int green = ColorUtils.getGreen(c);
			
			return Text.literal(String.valueOf(green));
		}, () -> Tooltip.EMPTY, value -> {
			int green = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setGreen(meter.getColor(), green);
			
			changeColor(c);
		}, () -> {
			int c = meter.getColor();
			int green = ColorUtils.getGreen(c);
			
			return (double)green / 0xFF;
		}, 0xFF));
		
		MeterPropertyElement movable = new MeterPropertyElement(client, totalWidth, buttonWidth, "Movable");
		movable.addControl("", (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> meter.isMovable(), button -> toggleMovable()));
		
		MeterPropertyElement eventTypes = new MeterPropertyElement(client, totalWidth, buttonWidth, "Event Types");
		for (EventType type : EventType.ALL) {
			KeyBinding keyBinding = KeyBindings.TOGGLE_EVENT_TYPES[type.getIndex()];
			Supplier<Tooltip> tooltip = () -> Tooltip.EMPTY;
			
			if (!keyBinding.isUnbound()) {
				tooltip = () -> Tooltip.of(TextUtils.formatKeybind(keyBinding));
			}
			
			eventTypes.addControl(type.getName(), (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> meter.isMetering(type), button -> toggleEventType(type)), tooltip);
		}
		
		controls.add(pos);
		controls.add(name);
		controls.add(color);
		controls.add(movable);
		controls.add(eventTypes);
	}
	
	private void updateCoords() {
		height = 0;
		
		if (meter != null) {
			int x = getX();
			
			title.setX(x + 2);
			controls.setX(x);
			
			x += title.getWidth() + 10;
			hideButton.setX(x);
			
			x += hideButton.getWidth() + 2;
			deleteButton.setX(x);
			
			x += deleteButton.getWidth() + 5;
			deleteConfirm.setX(x);
			
			int y = getY() + IButton.DEFAULT_HEIGHT;
			
			title.setY(y);
			deleteConfirm.setY(y);
			
			y -= 6;
			hideButton.setY(y);
			deleteButton.setY(y);
			
			y += IButton.DEFAULT_HEIGHT + 10;
			controls.setY(y);
			
			height = (controls.getY() + controls.getHeight()) - getY();
		}
	}
	
	private void tryDelete() {
		if (triedDeleting) {
			RemoveMeterPacket packet = new RemoveMeterPacket(meter.getId());
			client.getPacketHandler().send(packet);
		}
		
		triedDeleting = !triedDeleting;
		deleteButton.update();
		deleteConfirm.setVisible(triedDeleting);
	}
	
	private void undoTryDelete() {
		triedDeleting = false;
		deleteButton.update();
		deleteConfirm.setVisible(false);
	}
	
	private void teleport() {
		TeleportToMeterPacket packet = new TeleportToMeterPacket(meter.getId());
		client.getPacketHandler().send(packet);
	}
	
	private void changePos(WorldPos pos) {
		changeProperty(properties -> properties.setPos(pos));
	}
	
	private void changeName(String name) {
		changeProperty(properties -> properties.setName(name));
	}
	
	private void changeColor(int color) {
		changeProperty(properties -> properties.setColor(color));
	}
	
	private void toggleMovable() {
		changeProperty(properties -> properties.setMovable(!meter.isMovable()));
	}
	
	private void toggleEventType(EventType type) {
		changeProperty(properties -> {
			properties.setEventTypes(meter.getEventTypes() ^ type.flag());
		});
	}
	
	private void changeProperty(Consumer<MutableMeterProperties> consumer) {
		MutableMeterProperties newProperties = new MutableMeterProperties();
		consumer.accept(newProperties);
		
		MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
		client.getPacketHandler().send(packet);
	}
}
