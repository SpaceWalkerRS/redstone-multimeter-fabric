package redstone.multimeter.client.gui.element.button;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.TextureRegion;

public class Slider extends AbstractButton {
	
	private static final int SLIDER_WIDTH = 8;
	
	private final Consumer<Double> valueConsumer;
	private final Supplier<Double> valueSupplier;
	private final double steps;
	
	private double value;
	
	public Slider(MultimeterClient client, int x, int y, Supplier<Text> message, Supplier<List<Text>> tooltip, Consumer<Double> onSlide, Supplier<Double> valueSupplier, long steps) {
		this(client, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip, onSlide, valueSupplier, steps);
	}
	
	public Slider(MultimeterClient client, int x, int y, int width, int height, Supplier<Text> message, Supplier<List<Text>> tooltip, Consumer<Double> valueConsumer, Supplier<Double> valueSupplier, long steps) {
		super(client, x, y, width, height, message, tooltip);
		
		this.valueConsumer = valueConsumer;
		this.valueSupplier = valueSupplier;
		this.steps = steps;
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (!consumed && isActive() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			updateValue(mouseX);
			consumed = true;
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean wasDragging = isDraggingMouse();
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);
		
		if (wasDragging != isDraggingMouse()) {
			playClickSound();
		}
		
		return consumed;
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isActive() && isDraggingMouse() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			updateValue(mouseX);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
	}
	
	@Override
	public void onRemoved() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void update() {
		setValue(valueSupplier.get(), false);
	}
	
	@Override
	protected void renderButton(MatrixStack matrices) {
		super.renderButton(matrices);
		
		TextureRegion texture = getButtonTexture();
		int x = getX() + getSliderX();
		int y = getY();
		int width = SLIDER_WIDTH;
		int height = getHeight();
		
		drawTexturedButton(matrices, texture, x, y, width, height);
	}
	
	@Override
	protected TextureRegion getBackgroundTexture() {
		return TextureRegion.BASIC_BUTTON_INACTIVE;
	}
	
	private int getSliderX() {
		int range = getWidth() - SLIDER_WIDTH;
		return Math.round((float)(value * range));
	}
	
	public double getValue() {
		return value;
	}
	
	private void updateValue(double mouseX) {
		int min = getX() + SLIDER_WIDTH / 2;
		int range = getWidth() - SLIDER_WIDTH;
		
		setValue((mouseX - min) / range, true);
	}
	
	private void setValue(double newValue, boolean updateListener) {
		value = MathHelper.clamp(newValue, 0.0D, 1.0D);
		value = Math.round(steps * value) / steps;
		
		if (updateListener) {
			valueConsumer.accept(value);
		}
		
		updateMessage();
	}
}
