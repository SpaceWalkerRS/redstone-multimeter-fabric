package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Direction.Axis;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;
import redstone.multimeter.common.network.packets.TeleportToMeterPacket;
import redstone.multimeter.util.ColorUtils;
import redstone.multimeter.util.Dimensions;
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

		this.title = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText(String.format("Edit Meter \'%s\'", meter == null ? "" : meter.getName())).setFormatting(Formatting.UNDERLINE)).setWithShadow(true));
		this.hideButton = new Button(this.client, 0, 0, 18, 18, () -> new LiteralText(meter != null && meter.isHidden() ? "\u25A0" : "\u25A1"), () -> Tooltip.of(String.format("%s Meter", meter == null || meter.isHidden() ? "Unhide" : "Hide")), button -> {
			this.client.getMeterGroup().toggleHidden(meter);
			return true;
		});
		this.deleteButton = new Button(this.client, 0, 0, 18, 18, () -> new LiteralText("X").setFormatting(triedDeleting ? Formatting.RED : Formatting.WHITE), () -> Tooltip.of("Delete Meter").add(TextUtils.formatKeybindInfo(Keybinds.TOGGLE_METER)), button -> {
			tryDelete();

			if (triedDeleting && Screen.isShiftDown()) {
				tryDelete(); // delete without asking for confirmation first
			}

			return true;
		});

		this.deleteConfirm = new TextElement(this.client, 0, 0, t -> t.add(new LiteralText("Are you sure you want to delete this meter? YOU CANNOT UNDO THIS!").setFormatting(Formatting.ITALIC)).setWithShadow(true));
		this.deleteConfirm.setVisible(false);

		this.controls = new SimpleListElement(this.client, getWidth());

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
	public boolean keyPress(int keyCode) {
		boolean consumed = super.keyPress(keyCode);

		if (triedDeleting && keyCode == Keyboard.KEY_ESCAPE) {
			undoTryDelete();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		updateCoords();
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		updateCoords();
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
		meter = client.getHud().getSelectedMeter();

		if (meter != prevMeter) {
			createControls();
		}

		super.update();
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
		pos.addControl("dimension", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, dimension -> {
			changePos(meter.getPos().offset(dimension));
		}, () -> meter.getPos().getDimension().toString()), SuggestionsProvider.matching(Dimensions.REGISTRY, false));
		pos.addCoordinateControl(Axis.X, () -> meter.getPos(), p -> changePos(p));
		pos.addCoordinateControl(Axis.Y, () -> meter.getPos(), p -> changePos(p));
		pos.addCoordinateControl(Axis.Z, () -> meter.getPos(), p -> changePos(p));

		MeterPropertyElement name = new MeterPropertyElement(client, totalWidth, buttonWidth, "Name");
		name.addControl("", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> changeName(text), () -> meter.getName()));

		MeterPropertyElement color = new MeterPropertyElement(client, totalWidth, buttonWidth, "Color");
		color.addControl("rgb", (client, width, height) -> new TextField(client, 0, 0, width, height, () -> Tooltip.EMPTY, text -> {
			try {
				changeColor(ColorUtils.fromRGBString(text));
			} catch (NumberFormatException e) {

			}
		}, () -> ColorUtils.toRGBString(meter.getColor())));
		color.addControl("red", style -> style.setColor(Formatting.RED), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int red = ColorUtils.getRed(c);

			return new LiteralText(String.valueOf(red));
		}, () -> Tooltip.EMPTY, value -> {
			int red = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setRed(meter.getColor(), red);

			changeColor(c);
		}, () -> {
			int c = meter.getColor();
			int red = ColorUtils.getRed(c);

			return (double)red / 0xFF;
		}, 0xFF));
		color.addControl("blue", style -> style.setColor(Formatting.BLUE), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int blue = ColorUtils.getBlue(c);

			return new LiteralText(String.valueOf(blue));
		}, () -> Tooltip.EMPTY, value -> {
			int blue = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setBlue(meter.getColor(), blue);

			changeColor(c);
		}, () -> {
			int c = meter.getColor();
			int blue = ColorUtils.getBlue(c);

			return (double)blue / 0xFF;
		}, 0xFF));
		color.addControl("green", style -> style.setColor(Formatting.GREEN), (client, width, height) -> new Slider(client, 0, 0, width, height, () -> {
			int c = meter.getColor();
			int green = ColorUtils.getGreen(c);

			return new LiteralText(String.valueOf(green));
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
			KeyBinding keybind = Keybinds.TOGGLE_EVENT_TYPES[type.getIndex()];
			Supplier<Tooltip> tooltip = () -> Tooltip.EMPTY;

			if (keybind.getKeyCode() != Keyboard.KEY_NONE) {
				tooltip = () -> Tooltip.of(TextUtils.formatKeybindInfo(keybind));
			}

			eventTypes.addControl(type.getName(), (client, width, height) -> new ToggleButton(client, 0, 0, width, height, () -> meter.isMetering(type), button -> toggleEventType(type)), tooltip);
		}

		controls.add(pos);
		controls.add(name);
		controls.add(color);
		controls.add(movable);
		controls.add(eventTypes);

		client.getTutorial().onMeterControlsOpened();
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
			client.sendPacket(packet);
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
		client.sendPacket(packet);
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
			properties.setEventTypes(meter.getEventTypes() ^ type.flag());
		});
	}

	private void changeProperty(Consumer<MutableMeterProperties> consumer) {
		MutableMeterProperties newProperties = new MutableMeterProperties();
		consumer.accept(newProperties);

		MeterUpdatePacket packet = new MeterUpdatePacket(meter.getId(), newProperties);
		client.sendPacket(packet);
	}
}
