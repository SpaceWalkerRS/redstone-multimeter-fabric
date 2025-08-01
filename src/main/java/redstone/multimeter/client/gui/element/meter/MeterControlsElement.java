package redstone.multimeter.client.gui.element.meter;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.lwjgl.glfw.GLFW;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.AbstractParentElement;
import redstone.multimeter.client.gui.element.SimpleList;
import redstone.multimeter.client.gui.element.Label;
import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.Slider;
import redstone.multimeter.client.gui.element.button.SuggestionsProvider;
import redstone.multimeter.client.gui.element.button.TextField;
import redstone.multimeter.client.gui.element.button.ToggleButton;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterProperties.MutableMeterProperties;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.network.packets.MeterUpdatePacket;
import redstone.multimeter.common.network.packets.RemoveMeterPacket;
import redstone.multimeter.common.network.packets.TeleportToMeterPacket;
import redstone.multimeter.util.ColorUtils;

public class MeterControlsElement extends AbstractParentElement {

	private final MultimeterClient client;
	private final Label deleteConfirm;
	private final SimpleList controls;

	private int height;

	private Meter meter;

	private Label title;
	private Button hideButton;
	private Button deleteButton;

	private boolean triedDeleting;

	public MeterControlsElement(int x, int y, int width) {
		super(x, y, width, 0);

		this.client = MultimeterClient.INSTANCE;

		this.title = new Label(0, 0, t -> {
			if (this.meter != null) {
				t.addLine(Texts.translatable("rsmm.gui.meterControls.edit", this.meter.getName()).format(Formatting.UNDERLINED)).setShadow(true);
			}
		});
		this.hideButton = new BasicButton(0, 0, 18, 18, () -> Texts.literal(this.meter != null && this.meter.isHidden() ? "\u25A0" : "\u25A1"), () -> Tooltips.translatable("rsmm.gui.meterControls." + (this.meter == null || this.meter.isHidden() ? "unhide" : "hide")), button -> {
			this.client.getMeterGroup().toggleHidden(this.meter);
			return true;
		});
		this.deleteButton = new BasicButton(0, 0, 18, 18, () -> Texts.literal("X").format(this.triedDeleting ? Formatting.RED : Formatting.WHITE), () -> Tooltips.keybind(Texts.translatable("rsmm.gui.meterControls.delete"), Keybinds.TOGGLE_METER), button -> {
			this.tryDelete();

			if (this.triedDeleting && Screen.hasShiftDown()) {
				this.tryDelete(); // delete without asking for confirmation first
			}

			return true;
		});

		this.deleteConfirm = new Label(0, 0, t -> t.addLine(Texts.translatable("rsmm.gui.meterControls.delete.warning").format(Formatting.ITALIC)).setShadow(true));
		this.deleteConfirm.setVisible(false);

		this.controls = new SimpleList(this.getWidth());

		addChild(this.title);
		addChild(this.hideButton);
		addChild(this.deleteButton);
		addChild(this.deleteConfirm);
		addChild(this.controls);
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (this.triedDeleting && getFocusedElement() != this.deleteButton) {
			this.undoTryDelete();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean consumed = super.keyPress(keyCode, scanCode, modifiers);

		if (this.triedDeleting && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.undoTryDelete();
			consumed = true;
		}

		return consumed;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		this.updateCoords();
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		this.updateCoords();
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && this.meter != null;
	}

	@Override
	public void update() {
		Meter prevMeter = this.meter;
		this.meter = this.client.getHud().getSelectedMeter();

		if (this.meter != prevMeter) {
			this.createControls();
		}

		super.update();
		this.updateCoords();
	}

	private void createControls() {
		this.controls.clear();

		if (this.meter == null) {
			return;
		}

		int totalWidth = 375;
		int buttonWidth = 150;

		MeterPropertyElement pos = new MeterPropertyElement(totalWidth, buttonWidth, "pos", () -> Tooltips.translatable("rsmm.gui.meterControls.clickToTeleport"), t -> {
			this.teleport();
			return true;
		});
		pos.addControl("dimension", new TextField(0, 0, 0, 0, Tooltips::empty, text -> {
			try {
				ResourceLocation dimension = new ResourceLocation(text);
				DimPos newPos = this.meter.getPos().relative(dimension);

				this.changePos(newPos);
			} catch (ResourceLocationException e) {

			}
		}, () -> this.meter.getPos().getDimension().toString()), SuggestionsProvider.resources(Registries.DIMENSION_TYPE, false));
		pos.addCoordinateControl(Axis.X, this.meter::getPos, this::changePos);
		pos.addCoordinateControl(Axis.Y, this.meter::getPos, this::changePos);
		pos.addCoordinateControl(Axis.Z, this.meter::getPos, this::changePos);

		MeterPropertyElement name = new MeterPropertyElement(totalWidth, buttonWidth, "name");
		name.addControl("", new TextField(0, 0, 0, 0, Tooltips::empty, this::changeName, this.meter::getName));

		MeterPropertyElement color = new MeterPropertyElement(totalWidth, buttonWidth, "color");
		color.addControl("hex", style -> style.withColor(this.meter.getColor()), new TextField(0, 0, 0, 0, Tooltips::empty, this::changeColor, () -> ColorUtils.toRGBString(this.meter.getColor())));
		color.addControl("red", style -> style.withColor(Formatting.RED), new Slider(0, 0, 0, 0, () -> {
			int c = this.meter.getColor();
			int red = ColorUtils.getRed(c);

			return Texts.literal(String.valueOf(red));
		}, Tooltips::empty, value -> {
			int red = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setRed(this.meter.getColor(), red);

			this.changeColor(c);
		}, () -> {
			int c = this.meter.getColor();
			int red = ColorUtils.getRed(c);

			return (double)red / 0xFF;
		}, 0xFF));
		color.addControl("blue", style -> style.withColor(Formatting.BLUE), new Slider(0, 0, 0, 0, () -> {
			int c = this.meter.getColor();
			int blue = ColorUtils.getBlue(c);

			return Texts.literal(String.valueOf(blue));
		}, Tooltips::empty, value -> {
			int blue = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setBlue(this.meter.getColor(), blue);

			this.changeColor(c);
		}, () -> {
			int c = this.meter.getColor();
			int blue = ColorUtils.getBlue(c);

			return (double)blue / 0xFF;
		}, 0xFF));
		color.addControl("green", style -> style.withColor(Formatting.GREEN), new Slider(0, 0, 0, 0, () -> {
			int c = this.meter.getColor();
			int green = ColorUtils.getGreen(c);

			return Texts.literal(String.valueOf(green));
		}, Tooltips::empty, value -> {
			int green = (int)Math.round(value * 0xFF);
			int c = ColorUtils.setGreen(this.meter.getColor(), green);

			this.changeColor(c);
		}, () -> {
			int c = this.meter.getColor();
			int green = ColorUtils.getGreen(c);

			return (double)green / 0xFF;
		}, 0xFF));

		MeterPropertyElement movable = new MeterPropertyElement(totalWidth, buttonWidth, "movable");
		movable.addControl("", new ToggleButton(0, 0, 0, 0, this.meter::isMovable, button -> this.toggleMovable()));

		MeterPropertyElement eventTypes = new MeterPropertyElement(totalWidth, buttonWidth, "eventTypes");
		for (EventType type : EventType.ALL) {
			KeyMapping keybind = Keybinds.TOGGLE_EVENT_TYPES[type.getId()];
			Supplier<Tooltip> tooltip = () -> keybind.isUnbound()
				? Tooltips.EMPTY
				: Tooltips.keybind(keybind);

			eventTypes.addControl(type.getName(), UnaryOperator.identity(), new ToggleButton(0, 0, 0, 0, () -> this.meter.isMetering(type), button -> this.toggleEventType(type)), tooltip);
		}

		this.controls.add(pos);
		this.controls.add(name);
		this.controls.add(color);
		this.controls.add(movable);
		this.controls.add(eventTypes);

		this.client.getTutorial().onMeterControlsOpened();
	}

	private void updateCoords() {
		this.height = 0;

		if (this.meter != null) {
			int x = this.getX();

			this.title.setX(x + 2);
			this.controls.setX(x);

			x += this.title.getWidth() + 10;
			this.hideButton.setX(x);

			x += this.hideButton.getWidth() + 2;
			this.deleteButton.setX(x);

			x += this.deleteButton.getWidth() + 5;
			this.deleteConfirm.setX(x);

			int y = this.getY() + Button.DEFAULT_HEIGHT;

			this.title.setY(y);
			this.deleteConfirm.setY(y);

			y -= 6;
			this.hideButton.setY(y);
			this.deleteButton.setY(y);

			y += Button.DEFAULT_HEIGHT + 10;
			this.controls.setY(y);

			this.height = (this.controls.getY() + this.controls.getHeight()) - this.getY();
		}
	}

	private void tryDelete() {
		if (this.triedDeleting) {
			RemoveMeterPacket packet = new RemoveMeterPacket(this.meter.getId());
			this.client.sendPacket(packet);
		}

		this.triedDeleting = !this.triedDeleting;
		this.deleteButton.update();
		this.deleteConfirm.setVisible(this.triedDeleting);
	}

	private void undoTryDelete() {
		this.triedDeleting = false;
		this.deleteButton.update();
		this.deleteConfirm.setVisible(false);
	}

	private void teleport() {
		TeleportToMeterPacket packet = new TeleportToMeterPacket(this.meter.getId());
		this.client.sendPacket(packet);
	}

	private void changePos(DimPos pos) {
		this.changeProperty(properties -> properties.setPos(pos));
	}

	private void changeName(String name) {
		this.changeProperty(properties -> properties.setName(name));
	}

	private void changeColor(String color) {
		try {
			this.changeColor(ColorUtils.fromRGBString(color));
		} catch (NumberFormatException e) {
		}
	}

	private void changeColor(int color) {
		this.changeProperty(properties -> properties.setColor(color));
	}

	private void toggleMovable() {
		this.changeProperty(properties -> properties.setMovable(!this.meter.isMovable()));
	}

	private void toggleEventType(EventType type) {
		this.changeProperty(properties -> {
			properties.setEventTypes(this.meter.getEventTypes() ^ type.flag());
		});
	}

	private void changeProperty(Consumer<MutableMeterProperties> consumer) {
		MutableMeterProperties newProperties = new MutableMeterProperties();
		consumer.accept(newProperties);

		MeterUpdatePacket packet = new MeterUpdatePacket(this.meter.getId(), newProperties);
		this.client.sendPacket(packet);
	}
}
