package rsmm.fabric.client.gui.element.meter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.element.SimpleTextElement;
import rsmm.fabric.client.gui.element.TextElement;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.gui.widget.InvisibleButton;
import rsmm.fabric.client.gui.widget.Slider;
import rsmm.fabric.client.gui.widget.TextField;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.listeners.MeterChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupListener;
import rsmm.fabric.common.listeners.MeterListener;
import rsmm.fabric.common.network.packets.MeterChangePacket;
import rsmm.fabric.common.network.packets.RemoveMeterPacket;
import rsmm.fabric.common.network.packets.TeleportToMeterPacket;
import rsmm.fabric.util.ColorUtils;

public class MeterControlsElement extends AbstractParentElement implements MeterListener, MeterGroupListener {
	
	private static final int SPACING = 200;
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ROW_HEIGHT = BUTTON_HEIGHT + 2;
	
	private static int lastSelectedMeter = -1;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int meterIndex = -1;
	private Meter meter = Meter.DUMMY;
	
	private List<List<IElement>> grid;
	
	private TextElement title;
	private Button hideButton;
	private Button deleteButton;
	private TextElement deleteConfirm;
	
	private TextElement pos;
	private TextElement name;
	private TextElement color;
	private TextElement movable;
	private TextElement meteredEvents;
	
	private TextElement dimension;
	private TextElement posX;
	private TextElement posY;
	private TextElement posZ;
	private TextElement rgb;
	private TextElement red;
	private TextElement green;
	private TextElement blue;
	private List<TextElement> eventTypeText;
	
	private TextField dimensionField;
	private TextField xField;
	private Button xPlus;
	private Button xMinus;
	private TextField yField;
	private Button yPlus;
	private Button yMinus;
	private TextField zField;
	private Button zPlus;
	private Button zMinus;
	private TextField nameField;
	private TextField colorField;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	private Button movableButton;
	private List<Button> eventTypeButtons;
	
	private boolean triedDeleting;
	
	public MeterControlsElement(MultimeterClient client, int x, int y, int width) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = 0;
		
		initControls();
		selectMeter(lastSelectedMeter);
		
		MeterChangeDispatcher.addListener(this);
		MeterGroupChangeDispatcher.addListener(this);
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (triedDeleting && (getFocusedElement() != deleteButton)) {
			undoTryDelete();
		}
		
		return success;
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		
		MeterChangeDispatcher.removeListener(this);
		MeterGroupChangeDispatcher.removeListener(this);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
		updateControlsX();
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
		updateControlsY();
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void setHeight(int height) {
		
	}
	
	@Override
	public void posChanged(Meter meter) {
		if (this.meter == meter) {
			dimensionField.updateMessage();
			xField.updateMessage();
			yField.updateMessage();
			zField.updateMessage();
		}
	}
	
	@Override
	public void nameChanged(Meter meter) {
		if (this.meter == meter) {
			title.update();
			nameField.updateMessage();
			
			onTitleChanged();
		}
	}
	
	@Override
	public void colorChanged(Meter meter) {
		if (this.meter == meter) {
			rgb.setColor(meter.getColor());
			colorField.updateMessage();
			redSlider.update();
			greenSlider.update();
			blueSlider.update();
		}
	}
	
	@Override
	public void isMovableChanged(Meter meter) {
		if (this.meter == meter) {
			movableButton.updateMessage();
		}
	}
	
	@Override
	public void meteredEventsChanged(Meter meter) {
		if (this.meter == meter) {
			for (Button button : eventTypeButtons) {
				button.updateMessage();
			}
		}
	}
	
	@Override
	public void isHiddenChanged(Meter meter) {
		if (this.meter == meter) {
			hideButton.updateMessage();
		}
	}
	
	@Override
	public void cleared(MeterGroup meterGroup) {
		selectMeter(-1);
	}
	
	@Override
	public void meterAdded(MeterGroup meterGroup, int index) {
		
	}
	
	@Override
	public void meterRemoved(MeterGroup meterGroup, int index) {
		if (meterIndex == index) {
			selectMeter(-1);
		} else if (meterIndex > index) {
			selectMeter(meterIndex - 1);
		}
	}

	public int getSelectedMeter() {
		return meterIndex;
	}
	
	public boolean selectMeter(int index) {
		if (index >= client.getMeterGroup().getMeterCount()) {
			index = -1;
		}
		
		meterIndex = index;
		lastSelectedMeter = meterIndex;
		
		Meter oldMeter = meter;
		Meter newMeter = client.getMeterGroup().getMeter(meterIndex);
		
		if (newMeter == null) {
			newMeter = Meter.DUMMY;
		}
		
		if (oldMeter == newMeter) {
			return false;
		}
		
		meter = newMeter;
		
		updateControls();
		updateHeight();
		setVisible(meter != Meter.DUMMY);
		
		return true;
	}
	
	private void initControls() {
		int half = BUTTON_HEIGHT / 2 - 1;
		
		grid = new ArrayList<>();
		
		for (int column = 0; column < 3; column++) {
			grid.add(new ArrayList<>());
		}
		
		title = new SimpleTextElement(client, x, y, () -> {
			String title = String.format("Edit Meter #%d (\'%s\')", meterIndex, meter.getName());
			return new LiteralText(title).formatted(Formatting.UNDERLINE);
		});
		addChild(title);
		
		hideButton = new Button(client, x, y, 18, 18, () -> new LiteralText(meter.isHidden() ? "\u25A0" : "\u25A1"), () -> Arrays.asList(new LiteralText(String.format("%s Meter", meter.isHidden() ? "Unhide" : "Hide"))), (button) -> {
			meter.toggleHidden();
			return true;
		});
		addChild(hideButton);
		
		deleteButton = new Button(client, x, y, 18, 18, () -> new LiteralText("X").formatted(triedDeleting ? Formatting.RED : Formatting.WHITE), () -> Arrays.asList(new LiteralText("Delete Meter")), (button) -> {
			tryDelete();
			
			if (Screen.hasShiftDown()) {
				tryDelete(); // Delete without asking for confirmation first
			}
			
			return true;
		});
		addChild(deleteButton);
		
		deleteConfirm = new SimpleTextElement(client, x, y, () -> new LiteralText("Are you sure you want to delete this meter? YOU CAN'T UNDO THIS").formatted(Formatting.ITALIC));
		addChild(deleteConfirm);
		
		pos = new SimpleTextElement(client, x, y, () -> new LiteralText("Pos").formatted(Formatting.ITALIC), () -> {
			return Arrays.asList(new LiteralText("Click to teleport!"));
		}, (t) -> {
			teleport();
			return true;
		}, t -> false);
		addToGrid(0, pos);
		
		addToGrid(0, null); // x
		addToGrid(0, null); // y
		addToGrid(0, null); // z
		
		name = new SimpleTextElement(client, x, y, () -> new LiteralText("Name").formatted(Formatting.ITALIC));
		addToGrid(0, name);
		
		color = new SimpleTextElement(client, x, y, () -> new LiteralText("Color").formatted(Formatting.ITALIC));
		addToGrid(0, color);
		
		addToGrid(0, null); // red
		addToGrid(0, null); // green
		addToGrid(0, null); // blue
		
		movable = new SimpleTextElement(client, x, y, () -> new LiteralText("Movable").formatted(Formatting.ITALIC));
		addToGrid(0, movable);
		
		meteredEvents = new SimpleTextElement(client, x, y, () -> new LiteralText("Metered Events").formatted(Formatting.ITALIC));
		addToGrid(0, meteredEvents);
		
		dimension = new SimpleTextElement(client, x, y, () -> new LiteralText("dimension"));
		addToGrid(1, dimension);
		
		posX = new SimpleTextElement(client, x, y, () -> new LiteralText("x"));
		addToGrid(1, posX);
		
		posY = new SimpleTextElement(client, x, y, () -> new LiteralText("y"));
		addToGrid(1, posY);
		
		posZ = new SimpleTextElement(client, x, y, () -> new LiteralText("z"));
		addToGrid(1, posZ);
		
		addToGrid(1, null); // name
		
		rgb = new SimpleTextElement(client, x, y, () -> new LiteralText("rgb"));
		rgb.setColor(meter.getColor());
		addToGrid(1, rgb);
		
		red = new SimpleTextElement(client, x, y, () -> new LiteralText("red").formatted(Formatting.RED));
		addToGrid(1, red);
		
		green = new SimpleTextElement(client, x, y, () -> new LiteralText("green").formatted(Formatting.GREEN));
		addToGrid(1, green);
		
		blue = new SimpleTextElement(client, x, y, () -> new LiteralText("blue").formatted(Formatting.BLUE));
		addToGrid(1, blue);
		
		addToGrid(1, null); // movable
		
		eventTypeText = new ArrayList<>();
		
		for (EventType type : EventType.TYPES) {
			TextElement textElement = new SimpleTextElement(client, x, y, () -> new LiteralText(type.getName()));
			
			eventTypeText.add(textElement);
			addToGrid(1, textElement);
		}
		
		dimensionField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> meter.getPos().getWorldId().toString(), (text) -> {
			changePos();
		});
		addToGrid(2, dimensionField);
		
		xField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().asBlockPos().getX()), (text) -> {
			changePos();
		});
		addToGrid(2, xField);
		
		xPlus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("+"), (button) -> {
			changePos(meter.getPos().offset(Screen.hasControlDown() ? 10 : 1, 0, 0));
			return true;
		});
		addChild(xPlus);
		
		xMinus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("-"), (button) -> {
			changePos(meter.getPos().offset(Screen.hasControlDown() ? -10 : -1, 0, 0));
			return true;
		});
		addChild(xMinus);
		
		yField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().asBlockPos().getY()), (text) -> {
			changePos();
		});
		addToGrid(2, yField);
		
		yPlus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("+"), (button) -> {
			changePos(meter.getPos().offset(0, Screen.hasControlDown() ? 10 : 1, 0));
			return true;
		});
		addChild(yPlus);
		
		yMinus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("-"), (button) -> {
			changePos(meter.getPos().offset(0, Screen.hasControlDown() ? -10 : -1, 0));
			return true;
		});
		addChild(yMinus);
		
		zField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().asBlockPos().getZ()), (text) -> {
			changePos();
		});
		addToGrid(2, zField);
		
		zPlus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("+"), (button) -> {
			changePos(meter.getPos().offset(0, 0, Screen.hasControlDown() ? 10 : 1));
			return true;
		});
		addChild(zPlus);
		
		zMinus = new InvisibleButton(client, x, y, half, half, () -> new LiteralText("-"), (button) -> {
			changePos(meter.getPos().offset(0, 0, Screen.hasControlDown() ? -10 : -1));
			return true;
		});
		addChild(zMinus);
		
		nameField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> meter.getName(), (text) -> {
			changeName(text);
		});
		addToGrid(2, nameField);
		
		colorField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> ColorUtils.toRGBString(meter.getColor()), (text) -> {
			try {
				changeColor(ColorUtils.fromRGBString(text));
			} catch (Exception e) {
				
			}
		});
		addToGrid(2, colorField);
		
		redSlider = new Slider(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
			int red = ColorUtils.getRed(meter.getColor());
			String text = Integer.toString(red);
			
			return new LiteralText(text);
		}, () -> {
			int red = ColorUtils.getRed(meter.getColor());
			return red / 255.0D;
		}, (slider) -> {
			changeColor();
		}, (value) -> {
			int red = (int)(value * 255);
			return red / 255.0D;
		});
		addToGrid(2, redSlider);
		
		greenSlider = new Slider(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
			int green = ColorUtils.getGreen(meter.getColor());
			String text = Integer.toString(green);
			
			return new LiteralText(text);
		}, () -> {
			int green = ColorUtils.getGreen(meter.getColor());
			return green / 255.0D;
		}, (slider) -> {
			changeColor();
		}, (value) -> {
			int green = (int)(value * 255);
			return green / 255.0D;
		});
		addToGrid(2, greenSlider);
		
		blueSlider = new Slider(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
			int blue = ColorUtils.getBlue(meter.getColor());
			String text = Integer.toString(blue);
			
			return new LiteralText(text);
		}, () -> {
			int blue = ColorUtils.getBlue(meter.getColor());
			return blue / 255.0D;
		}, (slider) -> {
			changeColor();
		}, (value) -> {
			int blue = (int)(value * 255);
			return blue / 255.0D;
		});
		addToGrid(2, blueSlider);
		
		movableButton = new Button(client, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
			String text = String.valueOf(meter.isMovable());
			Formatting formatting = meter.isMovable() ? Formatting.GREEN : Formatting.RED;
			
			return new LiteralText(text).formatted(formatting);
		}, (button) -> {
			changeIsMovable(!meter.isMovable());
			return true;
		});
		addToGrid(2, movableButton);
		
		eventTypeButtons = new ArrayList<>();
		
		for (EventType type : EventType.TYPES) {
			Button eventTypeButton = new Button(client, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
				String text = String.valueOf(meter.isMetering(type));
				Formatting formatting = meter.isMetering(type) ? Formatting.GREEN : Formatting.RED;
				
				return new LiteralText(text).formatted(formatting);
			}, (button) -> {
				toggleEventType(type);
				return true;
			});
			
			eventTypeButtons.add(eventTypeButton);
			addToGrid(2, eventTypeButton);
		}
		
		updateControlsX();
		updateControlsY();
		updateHeight();
		
		setVisible(false);
	}
	
	private void addToGrid(int column, IElement child) {
		if (column >= 0 && column < grid.size()) {
			grid.get(column).add(child);
			
			if (child != null) {
				addChild(child);
			}
		}
	}
	
	private void updateTitle() {
		title.update();
		onTitleChanged();
	}

	private void onTitleChanged() {
		int x = title.getX() + title.getWidth() + 10;
		
		hideButton.setX(x);
		deleteButton.setX(x + hideButton.getWidth() + 2);
		deleteConfirm.setX(x + hideButton.getWidth() + 2 + deleteButton.getWidth() + 5);
	}
	
	private void updateControls() {
		updateTitle();
		hideButton.updateMessage();
		undoTryDelete();
		
		rgb.setColor(meter.getColor());
		
		dimensionField.updateMessage();
		xField.updateMessage();
		yField.updateMessage();
		zField.updateMessage();
		nameField.updateMessage();
		colorField.updateMessage();
		redSlider.update();
		greenSlider.update();
		blueSlider.update();
		movableButton.updateMessage();
		for (Button button : eventTypeButtons) {
			button.updateMessage();
		}
	}
	
	private void updateControlsX() {
		int titleX = x + 2;
		int controlsX = x + SPACING;
		int dx = 7;
		
		title.setX(titleX);
		onTitleChanged();
		
		for (IElement e : grid.get(0)) {
			if (e != null) {
				e.setX(titleX);
			}
		}
		for (IElement e : grid.get(1)) {
			if (e != null) {
				e.setX(controlsX - dx - e.getWidth());
			}
		}
		for (IElement e : grid.get(2)) {
			if (e != null) {
				e.setX(controlsX);
			}
		}
		
		int plusMinusX = xField.getX() + xField.getWidth();
		
		xPlus.setX(plusMinusX);
		xMinus.setX(plusMinusX);
		yPlus.setX(plusMinusX);
		yMinus.setX(plusMinusX);
		zPlus.setX(plusMinusX);
		zMinus.setX(plusMinusX);
	}
	
	private void updateControlsY() {
		int titleY = y + ROW_HEIGHT;
		int dy = 6;
		
		title.setY(titleY);
		hideButton.setY(titleY - dy);
		deleteButton.setY(titleY - dy);
		deleteConfirm.setY(titleY);
		
		for (List<IElement> column : grid) {
			int controlY = titleY + ROW_HEIGHT;
			
			for (IElement e : column) {
				if (e != null) {
					if (e instanceof TextElement) {
						e.setY(controlY + dy);
					} else {
						e.setY(controlY);
					}
				}
				
				controlY += ROW_HEIGHT;
			}
		}
		
		xPlus.setY(xField.getY() + 1);
		xMinus.setY(xPlus.getY() + xPlus.getHeight());
		yPlus.setY(yField.getY() + 1);
		yMinus.setY(yPlus.getY() + yPlus.getHeight());
		zPlus.setY(zField.getY() + 1);
		zMinus.setY(zPlus.getY() + zPlus.getHeight());
	}
	
	private void updateHeight() {
		if (meter == Meter.DUMMY) {
			height = 0;
		} else {
			List<IElement> children = getChildren();
			IElement bottom = children.get(children.size() - 1);
			
			height = bottom.getY() + bottom.getHeight() + 2 - y;
		}
	}
	
	private void tryDelete() {
		if (triedDeleting) {
			RemoveMeterPacket packet = new RemoveMeterPacket(meterIndex);
			client.getPacketHandler().sendPacket(packet);
		}
		
		triedDeleting = !triedDeleting;
		deleteButton.updateMessage();
		deleteConfirm.setVisible(triedDeleting);
	}
	
	private void undoTryDelete() {
		triedDeleting = false;
		deleteButton.updateMessage();
		deleteConfirm.setVisible(false);
	}
	
	private void teleport() {
		TeleportToMeterPacket packet = new TeleportToMeterPacket(meterIndex);
		client.getPacketHandler().sendPacket(packet);
	}
	
	private void changePos() {
		try {
			Identifier worldId = new Identifier(dimensionField.getText());
			int x = Integer.valueOf(xField.getText());
			int y = Integer.valueOf(yField.getText());
			int z = Integer.valueOf(zField.getText());
			
			changePos(new WorldPos(worldId, new BlockPos(x, y, z)));
		} catch (Exception e) {
			
		}
	}
	
	private void changePos(WorldPos pos) {
		MeterChangePacket packet = new MeterChangePacket(meterIndex);
		packet.addPos(pos);
		client.getPacketHandler().sendPacket(packet);
	}
	
	private void changeName(String name) {
		MeterChangePacket packet = new MeterChangePacket(meterIndex);
		packet.addName(name);
		client.getPacketHandler().sendPacket(packet);
	}
	
	private void changeColor() {
		int r = (int)(255 * redSlider.getValue());
		int g = (int)(255 * greenSlider.getValue());
		int b = (int)(255 * blueSlider.getValue());
		
		changeColor(ColorUtils.fromRGB(r, g, b));
	}
	
	private void changeColor(int color) {
		MeterChangePacket packet = new MeterChangePacket(meterIndex);
		packet.addColor(color);
		client.getPacketHandler().sendPacket(packet);
	}
	
	private void changeIsMovable(boolean movable) {
		MeterChangePacket packet = new MeterChangePacket(meterIndex);
		packet.addIsMovable(movable);
		client.getPacketHandler().sendPacket(packet);
	}
	
	private void toggleEventType(EventType type) {
		MeterChangePacket packet = new MeterChangePacket(meterIndex);
		packet.addEventType(type);
		client.getPacketHandler().sendPacket(packet);
	}
}
