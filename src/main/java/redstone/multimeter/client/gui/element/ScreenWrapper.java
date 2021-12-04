package redstone.multimeter.client.gui.element;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.screen.Screen;

public class ScreenWrapper extends Screen {
	
	private final Screen parent;
	private final RSMMScreen screen;
	
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
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		System.out.println("check scroll");
		if (button == IElement.MOUSE_SCROLL_UP) {
			screen.mouseScroll(mouseX, mouseY, 0, -5);
		} else if (button == IElement.MOUSE_SCROLL_DOWN) {
			screen.mouseScroll(mouseX, mouseY, 0, 5);
		} else {
			screen.mouseClick(mouseX, mouseY, button);
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		screen.mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	protected void mouseDragged(int mouseX, int mouseY, int button, long duration) {
		screen.mouseDrag(mouseX, mouseY, button, 0, 0);
	}
	
	@Override
	public void handleKeyboardEvents() {
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
}
