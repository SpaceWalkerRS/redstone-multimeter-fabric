package redstone.multimeter.client.gui.element.meter;

import java.util.Arrays;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction.Axis;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;
import redstone.multimeter.common.network.packets.TeleportToMeterPacket;
import redstone.multimeter.util.ColorUtils;

public class MeterControlsElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final TextElement deleteConfirm;
	private final SimpleListElement controls;
	
	private int height;
	
	private Meter meter;
	
	private TextElement title;
	private Button hideButton;
	private Button deleteButton;
	
	private boolean triedDeleting;
	
	public MeterControlsElement(MultimeterClient client, int x, int y, int width) {
		super(x, y, width, 0);
		
		this.client = client;
		
		this.title = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText(String.format("Edit Meter \'%s\'", meter == null ? "" : meter.getName())).setStyle(new Style().setUnderline(true))).setWithShadow(true));
		this.hideButton = new Button(this.client, 0, 0, 18, 18, () -> new LiteralText(meter != null && meter.isHidden() ? "\u25A0" : "\u25A1"), () -> Arrays.asList(new LiteralText(String.format("%s Meter", meter == null || meter.isHidden() ? "Unhide" : "Hide"))), button -> {
			this.client.getMeterGroup().toggleHidden(meter);
			return true;
		});
		this.deleteButton = new Button(this.client, 0, 0, 18, 18, () -> new LiteralText("X").setStyle(new Style().setColor(triedDeleting ? Formatting.RED : Formatting.WHITE)), () -> Arrays.asList(new LiteralText("Delete Meter")), button -> {
			tryDelete();
			
			if (triedDeleting && Screen.hasShiftDown()) {
				tryDelete(); // delete without asking for confirmation first
			}
			
			return true;
		});
		this.deleteConfirm = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText("Are you sure you want to delete this meter? YOU CANNOT UNDO THIS!").setStyle(new Style().setItalic(true))).setWithShadow(true));
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
	public boolean keyPress(int key) {
		boolean consumed = super.keyPress(key);
		
		if (triedDeleting && key == Keyboard.KEY_ESCAPE) {
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
	public void update() {
		Meter prevMeter = meter;
		meter = client.getHUD().getSelectedMeter();
		
		if (meter != prevMeter) {
			createControls();
		}
		
		super.update();
		
		updateCoords();
		setVisible(meter != null);
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
		
		MeterControlsListBuilder builder = new MeterControlsListBuilder(client, getWidth());
		
		builder.addCategory("Pos", () -> Arrays.asList(new LiteralText("Click to teleport!")), t -> {
			teleport();
			return true;
		});
		
		builder.addControl("Pos", "dimension", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> null, text -> changePos(meter.getPos().offset(new Identifier(text))), () -> meter.getPos().getDimensionId().toString()));
		builder.addCoordinateControl("Pos", Axis.X, () -> meter.getPos(), pos -> changePos(pos));
		builder.addCoordinateControl("Pos", Axis.Y, () -> meter.getPos(), pos -> changePos(pos));
		builder.addCoordinateControl("Pos", Axis.Z, () -> meter.getPos(), pos -> changePos(pos));
		
		builder.addControl("Name", "", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> null, text -> changeName(text), () -> meter.getName()));
		
		builder.addControl("Color", () -> new LiteralText("rgb"), (client, width, height) -> new TextField(client, 0, 0, width, height, () -> null, text -> {
			try {
				changeColor(ColorUtils.fromRGBString(text));
			} catch (NumberFormatException e) {
				
			}
		}, () -> ColorUtils.toRGBString(meter.getColor()).toUpperCase()));
		builder.addControl("Color", () -> new LiteralText("red").setStyle(new Style().setColor(Formatting.RED)), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int color = meter.getColor();
			int red = ColorUtils.getRed(color);
			
			return new LiteralText(String.valueOf(red));
		}, () -> null, value -> {
			int red = (int)Math.round(value * 0xFF);
			int color = ColorUtils.setRed(meter.getColor(), red);
			
			changeColor(color);
		}, () -> {
			int color = meter.getColor();
			int red = ColorUtils.getRed(color);
			
			return (double)red / 0xFF;
		}, 0xFF));
		builder.addControl("Color", () -> new LiteralText("green").setStyle(new Style().setColor(Formatting.GREEN)), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int color = meter.getColor();
			int green = ColorUtils.getGreen(color);
			
			return new LiteralText(String.valueOf(green));
		}, () -> null, value -> {
			int green = (int)Math.round(value * 0xFF);
			int color = ColorUtils.setGreen(meter.getColor(), green);
			
			changeColor(color);
		}, () -> {
			int color = meter.getColor();
			int green = ColorUtils.getGreen(color);
			
			return (double)green / 0xFF;
		}, 0xFF));
		builder.addControl("Color", () -> new LiteralText("blue").setStyle(new Style().setColor(Formatting.BLUE)), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int color = meter.getColor();
			int blue = ColorUtils.getBlue(color);
			
			return new LiteralText(String.valueOf(blue));
		}, () -> null, value -> {
			int blue = (int)Math.round(value * 0xFF);
			int color = ColorUtils.setBlue(meter.getColor(), blue);
			
			changeColor(color);
		}, () -> {
			int color = meter.getColor();
			int blue = ColorUtils.getBlue(color);
			
			return (double)blue / 0xFF;
		}, 0xFF));
		
		
		builder.addControl("Movable", "", (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> meter.isMovable(), button -> toggleMovable()));
		
		for (EventType type : EventType.ALL) {
			builder.addControl("Event Types", type.getName(), (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> meter.isMetering(type), button -> toggleEventType(type)));
		}
		
		builder.setMidpoint(100);
		builder.build(controls);
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
	
	private void changePos(DimPos pos) {
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
			properties.setEventTypes(meter.getEventTypes());
			properties.toggleEventType(type);
		});
	}
	
	private void changeProperty(Consumer<MeterProperties> consumer) {
		MeterProperties newProperties = new MeterProperties();
		consumer.accept(newProperties);
		
		MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
		client.getPacketHandler().send(packet);
	}
}
