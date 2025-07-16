package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import net.minecraft.util.math.MathHelper;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.texture.TextureRegion;
import redstone.multimeter.client.gui.texture.TextureRegions;
import redstone.multimeter.client.gui.tooltip.Tooltip;

public class Slider extends AbstractButton {

	private static final int SLIDER_WIDTH = 8;

	private final Consumer<Double> valueConsumer;
	private final Supplier<Double> valueSupplier;
	private final double steps;

	private double value;

	public Slider(int x, int y, Supplier<Text> message, Supplier<Tooltip> tooltip, Consumer<Double> onSlide, Supplier<Double> valueSupplier, long steps) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, tooltip, onSlide, valueSupplier, steps);
	}

	public Slider(int x, int y, int width, int height, Supplier<Text> message, Supplier<Tooltip> tooltip, Consumer<Double> valueConsumer, Supplier<Double> valueSupplier, long steps) {
		super(x, y, width, height, message, tooltip);

		this.valueConsumer = valueConsumer;
		this.valueSupplier = valueSupplier;
		this.steps = steps;
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && this.isActive() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.updateValue(mouseX);
			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean wasDragging = this.isDraggingMouse();
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (wasDragging != this.isDraggingMouse()) {
			Button.playClickSound();
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isActive() && this.isDraggingMouse() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.updateValue(mouseX);
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
	public void tick() {
	}

	@Override
	public void update() {
		this.setValue(this.valueSupplier.get(), false);
	}

	@Override
	protected void renderButton(GuiRenderer renderer) {
		super.renderButton(renderer);

		TextureRegion texture = this.getHandleTexture();
		int x0 = this.getX() + this.getSliderX();
		int y0 = this.getY();
		int x1 = x0 + SLIDER_WIDTH;
		int y1 = y0 + this.getHeight();

		renderer.blitSpliced(texture, x0, y0, x1, y1, EDGE);
	}

	@Override
	protected TextureRegion getButtonTexture() {
		return TextureRegions.BASIC_BUTTON_INACTIVE;
	}

	protected TextureRegion getHandleTexture() {
		return super.getButtonTexture();
	}

	private int getSliderX() {
		int range = this.getWidth() - SLIDER_WIDTH;
		return Math.round((float)(this.value * range));
	}

	public double getValue() {
		return this.value;
	}

	private void updateValue(double mouseX) {
		int min = this.getX() + SLIDER_WIDTH / 2;
		int range = this.getWidth() - SLIDER_WIDTH;

		this.setValue((mouseX - min) / range, true);
	}

	private void setValue(double newValue, boolean updateListener) {
		this.value = MathHelper.clamp(newValue, 0.0D, 1.0D);
		this.value = Math.round(this.steps * this.value) / this.steps;

		if (updateListener) {
			this.valueConsumer.accept(this.value);
		}

		this.updateMessage();
	}
}
