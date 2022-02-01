package redstone.multimeter.client.gui.screen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ScreenWrapper extends GuiScreen {
	
	private final GuiScreen parent;
	private final RSMMScreen screen;
	
	private double prevX;
	private double prevY;
	private double mouseX;
	private double mouseY;
	private int touchEvents;
	private int lastButton;
	private long buttonTicks;
	
	public ScreenWrapper(GuiScreen parent, RSMMScreen screen) {
		this.parent = parent;
		this.screen = screen;
		
		this.screen.wrapper = this;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float tickDelta) {
		screen.render(mouseX, mouseY);
	}
	
	@Override
	public void handleInput() {
		if (Mouse.isCreated()) {
			handleMouseEvents();
		}
		if (Keyboard.isCreated()) {
			handleKeyboardEvents();
		}
	}
	
	@Override
	public final void initGui() {
		screen.init(width, height);
	}
	
	@Override
	public void updateScreen() {
		screen.tick();
	}
	
	@Override
	public void onGuiClosed() {
		screen.onRemoved();
	}
	
	public GuiScreen getParent() {
		return parent;
	}
	
	public RSMMScreen getScreen() {
		return screen;
	}
	
	private void handleMouseEvents() {
		updateMousePos();
		handleScroll();
		
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			
			if (Mouse.getEventButtonState()) {
				if (mc.gameSettings.touchscreen && touchEvents++ > 0) {
					continue;
				}

				lastButton = button;
				buttonTicks = Minecraft.getSystemTime();
				
				screen.mouseClick(mouseX, mouseY, button);
			} else {
				if (mc.gameSettings.touchscreen && --touchEvents > 0) {
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
		mouseX = Mouse.getX() * width / mc.displayWidth;
		mouseY = height - 1 - Mouse.getY() * height / mc.displayHeight;
		
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
	
	private void handleKeyboardEvents() {
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
			
			mc.dispatchKeypresses();
		}
	}
}
