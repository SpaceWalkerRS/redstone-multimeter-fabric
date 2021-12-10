package redstone.multimeter.client.gui.element;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenWrapper extends Screen {
	
	private final Screen parent;
	private final RSMMScreen screen;
	
	private double prevX;
	private double prevY;
	private double mouseX;
	private double mouseY;
	private int touchEvents;
	private int lastButton;
	private long buttonTicks;
	
	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		this.parent = parent;
		this.screen = screen;
		
		this.screen.wrapper = this;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		screen.render(mouseX, mouseY);
	}
	
	@Override
	public void handleInputEvents() {
		if (Mouse.isCreated()) {
			handleMouse();
		}
		if (Keyboard.isCreated()) {
			handleKeyboard();
		}
	}
	
	@Override
	public final void init() {
		screen.setWidth(width);
		screen.setHeight(height);
		
		screen.removeChildren();
		screen.initScreen();
		screen.update();
	}
	
	@Override
	public void tick() {
		screen.tick();
	}
	
	@Override
	public void removed() {
		screen.onRemoved();
	}
	
	public Screen getParent() {
		return parent;
	}
	
	public RSMMScreen getScreen() {
		return screen;
	}
	
	private void handleMouse() {
		updateMousePos();
		handleScroll();
		
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			
			if (Mouse.getEventButtonState()) {
				if (client.options.touchscreen && touchEvents++ > 0) {
					continue;
				}

				lastButton = button;
				buttonTicks = MinecraftClient.getTime();
				
				screen.mouseClick(mouseX, mouseY, button);
			} else {
				if (client.options.touchscreen && --touchEvents > 0) {
					return;
				}

				screen.mouseRelease(mouseX, mouseY, button);
				
				if (button == lastButton) {
					lastButton = -1;
				}
			}
		}
		
		handleDrag();
	}
	
	private void updateMousePos() {
		prevX = mouseX;
		prevY = mouseY;
		mouseX = Mouse.getX() * width / client.frameBufferWidth;
		mouseY = height - 1 - Mouse.getY() * height / client.frameBufferHeight;
		
		if (mouseX != prevX || mouseY != prevY) {
			screen.mouseMove(mouseX, mouseY);
		}
	}
	
	private void handleScroll() {
		double scrollY = 0.05D * Mouse.getDWheel();
		
		if (scrollY != 0) {
			screen.mouseScroll(mouseX, mouseY, 0, scrollY);
		}
	}
	
	private void handleDrag() {
		if (lastButton != -1 && buttonTicks > 0L) {
			double deltaX = mouseX - prevX;
			double deltaY = mouseY - prevY;
			
			screen.mouseDrag(mouseX, mouseY, lastButton, deltaX, deltaY);
		}
	}
	
	private void handleKeyboard() {
		while (Keyboard.next()) {
			char chr = Keyboard.getEventCharacter();
			int key = Keyboard.getEventKey();
			
			boolean consumed = false;
			
			if (key != Keyboard.CHAR_NONE) {
				if (Keyboard.getEventKeyState()) {
					consumed = screen.keyPress(key);
					
					if (!consumed && key == Keyboard.KEY_ESCAPE) {
						screen.close();
						consumed = true;
					}
				} else {
					consumed = screen.keyRelease(key);
				}
			}
			if (!consumed && chr >= ' ') {
				screen.typeChar(chr);
			}
			
			client.handleKeyboardEvents();
		}
	}
}
