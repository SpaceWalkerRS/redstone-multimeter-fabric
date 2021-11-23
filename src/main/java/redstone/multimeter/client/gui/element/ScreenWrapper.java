package redstone.multimeter.client.gui.element;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.gui.CursorType;

public class ScreenWrapper extends Screen {
	
	private final Screen parent;
	private final RSMMScreen screen;
	
	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		this.parent = parent;
		this.screen = screen;
		
		this.screen.wrapper = this;
	}
	
	@Override
	public void method_2214(int mouseX, int mouseY, float delta) {
		screen.render(mouseX, mouseY);
	}
	
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		return screen.mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		return screen.mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return screen.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public final boolean mouseScrolled(double amount) {
		return false; // scrolling is handled in MouseMixin and InputHandler
	}
	
	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (screen.keyPress(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (screen.shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			screen.close();
			return true;
		}
		
		return false;
	}
	
	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return screen.keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean charTyped(char chr, int modifiers) {
		return screen.typeChar(chr, modifiers);
	}
	
	@Override
	public boolean method_15913() {
		return screen.shouldCloseOnEsc();
	}
	
	@Override
	protected final void method_2224() {
		screen.setWidth(field_2561);
		screen.setHeight(field_2559);
		
		screen.removeChildren();
		screen.initScreen();
		screen.update();
	}
	
	@Override
	public void method_2225() {
		screen.tick();
	}
	
	@Override
	public void method_2234() {
		screen.onRemoved();
		screen.setCursor(field_2563, CursorType.ARROW);
	}
	
	public Screen getParent() {
		return parent;
	}
	
	public RSMMScreen getScreen() {
		return screen;
	}
}
