package rsmm.fabric.client.gui.element.meter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.gui.widget.Slider;
import rsmm.fabric.client.gui.widget.TextField;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.listeners.MeterListener;
import rsmm.fabric.common.listeners.MeterChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupChangeDispatcher;
import rsmm.fabric.common.listeners.MeterGroupListener;
import rsmm.fabric.common.packet.types.MeterChangePacket;
import rsmm.fabric.util.ColorUtils;

public class MeterControlsElement extends AbstractParentElement implements MeterListener, MeterGroupListener {
	
	private static final int SPACING = 160;
	private static final int ROW_HEIGHT = 22;
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	
	private static int lastSelectedMeter = -1;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int meterIndex;
	private Meter meter;
	
	private TextField dimensionField;
	private TextField xField;
	private TextField yField;
	private TextField zField;
	private TextField nameField;
	private TextField colorField;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	private Button movableButton;
	private List<Button> eventTypeButtons;
	
	public MeterControlsElement(MultimeterClient client, int x, int y, int width) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = 0;
		
		selectMeter(lastSelectedMeter);
		
		MeterChangeDispatcher.addListener(this);
		MeterGroupChangeDispatcher.addListener(this);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		
		if (meter != null) {
			int x = this.x + 2;
			int y = this.y + 20;
			
			int x1;
			int dx = 7;
			
			Text title = new LiteralText(String.format("Edit Meter #%d (%s)", meterIndex, meter.getName())).formatted(Formatting.UNDERLINE);
			font.drawWithShadow(matrices, title, x, y, 0xFFFFFF);
			
			Text posText = new LiteralText("Pos:").formatted(Formatting.ITALIC);
			y = dimensionField.getY() + 6;
			font.drawWithShadow(matrices, posText, x, y, 0xFFFFFF);
			
			Text dimensionText = new LiteralText("dimension");
			x1 = dimensionField.getX() - dx - font.getWidth(dimensionText);
			font.drawWithShadow(matrices, dimensionText, x1, y, 0xFFFFFF);
			
			Text xText = new LiteralText("x");
			x1 = xField.getX() - dx - font.getWidth(xText);
			y = xField.getY() + 6;
			font.drawWithShadow(matrices, xText, x1, y, 0xFFFFFF);
			
			Text yText = new LiteralText("y");
			x1 = yField.getX() - dx - font.getWidth(yText);
			y = yField.getY() + 6;
			font.drawWithShadow(matrices, yText, x1, y, 0xFFFFFF);
			
			Text zText = new LiteralText("z");
			x1 = zField.getX() - dx - font.getWidth(zText);
			y = zField.getY() + 6;
			font.drawWithShadow(matrices, zText, x1, y, 0xFFFFFF);
			
			Text nameText = new LiteralText("Name:").formatted(Formatting.ITALIC);
			y = nameField.getY() + 6;
			font.drawWithShadow(matrices, nameText, x, y, 0xFFFFFF);
			
			Text colorText = new LiteralText("Color:").formatted(Formatting.ITALIC);
			y = colorField.getY() + 6;
			font.drawWithShadow(matrices, colorText, x, y, 0xFFFFFF);
			
			Text rgbText = new LiteralText("rgb");
			x1 = colorField.getX() - dx - font.getWidth(rgbText);
			font.drawWithShadow(matrices, rgbText, x1, y, meter.getColor());
			
			Text redText = new LiteralText("red").formatted(Formatting.RED);
			x1 = redSlider.getX() - dx - font.getWidth(redText);
			y = redSlider.getY() + 6;
			font.drawWithShadow(matrices, redText, x1, y, 0xFFFFFF);
			
			Text greenText = new LiteralText("green").formatted(Formatting.GREEN);
			x1 = greenSlider.getX() - dx - font.getWidth(greenText);
			y = greenSlider.getY() + 6;
			font.drawWithShadow(matrices, greenText, x1, y, 0xFFFFFF);
			
			Text blueText = new LiteralText("blue").formatted(Formatting.BLUE);
			x1 = blueSlider.getX() - dx - font.getWidth(blueText);
			y = blueSlider.getY() + 6;
			font.drawWithShadow(matrices, blueText, x1, y, 0xFFFFFF);
			
			Text movableText = new LiteralText("Movable:").formatted(Formatting.ITALIC);
			y = movableButton.getY() + 6;
			font.drawWithShadow(matrices, movableText, x, y, 0xFFFFFF);
			
			
			Text eventsText = new LiteralText("Metered Events:").formatted(Formatting.ITALIC);
			y = eventTypeButtons.get(0).getY() + 6;
			font.drawWithShadow(matrices, eventsText, x, y, 0xFFFFFF);
			
			for (EventType type : EventType.TYPES) {
				Button button = eventTypeButtons.get(type.getIndex());
				
				Text eventTypeText = new LiteralText(type.getName());
				x1 = button.getX() - dx - font.getWidth(eventTypeText);
				y = button.getY() + 6;
				font.drawWithShadow(matrices, eventTypeText, x1, y, 0xFFFFFF);
			}
		}
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
		updateCoordinates();
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
		updateCoordinates();
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
			nameField.updateMessage();
		}
	}
	
	@Override
	public void colorChanged(Meter meter) {
		if (this.meter == meter) {
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
		
		if (oldMeter == newMeter) {
			return false;
		}
		
		meter = newMeter;
		
		onMeterChanged();
		updateCoordinates();
		
		return true;
	}
	
	private void onMeterChanged() {
		clearChildren();
		
		if (meter != null) {
			dimensionField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> meter.getPos().getWorldId().toString(), (text) -> {
				changePos();
			});
			addChild(dimensionField);
			
			xField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().getX()), (text) -> {
				changePos();
			});
			addChild(xField);
			
			yField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().getY()), (text) -> {
				changePos();
			});
			addChild(yField);
			
			zField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> String.valueOf(meter.getPos().getZ()), (text) -> {
				changePos();
			});
			addChild(zField);
			
			nameField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> meter.getName(), (text) -> {
				changeName(text);
			});
			addChild(nameField);
			
			colorField = new TextField(font, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> ColorUtils.toHexString(meter.getColor()), (text) -> {
				try {
					changeColor(ColorUtils.fromString(text));
				} catch (Exception e) {
					
				}
			});
			addChild(colorField);
			
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
			addChild(redSlider);
			
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
			addChild(greenSlider);
			
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
			addChild(blueSlider);
			
			movableButton = new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
				String text = String.valueOf(meter.isMovable());
				Formatting formatting = meter.isMovable() ? Formatting.GREEN : Formatting.RED;
				
				return new LiteralText(text).formatted(formatting);
			}, (button) -> {
				changeIsMovable(!meter.isMovable());
			});
			addChild(movableButton);
			
			eventTypeButtons = new ArrayList<>();
			
			for (EventType type : EventType.TYPES) {
				Button eventTypeButton = new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
					String text = String.valueOf(meter.isMetering(type));
					Formatting formatting = meter.isMetering(type) ? Formatting.GREEN : Formatting.RED;
					
					return new LiteralText(text).formatted(formatting);
				}, (button) -> {
					toggleEventType(type);
				});
				
				eventTypeButtons.add(eventTypeButton);
				addChild(eventTypeButton);
			}
		}
	}
	
	private void updateCoordinates() {
		int y = this.y;
		
		if (meter != null) {
			int x = this.x + SPACING;
			y += 40;
			
			for (IElement element : getChildren()) {
				element.setX(x);
				element.setY(y);
				
				y += ROW_HEIGHT;
			}
		}
		
		this.height = y - this.y;
	}
	
	private void changePos() {
		try {
			Identifier worldId = new Identifier(dimensionField.getText());
			int x = Integer.valueOf(xField.getText());
			int y = Integer.valueOf(yField.getText());
			int z = Integer.valueOf(zField.getText());
			
			WorldPos pos = new WorldPos(worldId, x, y, z);
			
			MeterChangePacket packet = new MeterChangePacket(meterIndex);
			packet.addPos(pos);
			client.getPacketHandler().sendPacket(packet);
		} catch (Exception e) {
			
		}
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
