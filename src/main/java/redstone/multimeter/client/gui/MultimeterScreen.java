package redstone.multimeter.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.meter.MeterControlsElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;

public class MultimeterScreen extends RSMMScreen {
	
	private final boolean isPauseScreen;
	
	private ScrollableListElement list;
	private boolean scrollHud;
	
	public MultimeterScreen(MultimeterClient client) {
		super(client, new LiteralText(RedstoneMultimeterMod.MOD_NAME), false);
		
		this.isPauseScreen = !Screen.hasShiftDown();
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		
		if (!consumed) {
			consumed = client.getInputHandler().mouseClick(mouseX, mouseY, button);
		}
		
		return true;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean consumed = super.keyPress(keyCode, scanCode, modifiers);
		
		if (!consumed) {
			consumed = client.getInputHandler().keyPress(keyCode, scanCode, modifiers);
			
			if (!scrollHud && KeyBindings.SCROLL_HUD.matchesKey(keyCode, scanCode)) {
				scrollHud = true;
			}
		}
		
		return consumed;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		if (scrollHud && KeyBindings.SCROLL_HUD.matchesKey(keyCode, scanCode)) {
			scrollHud = false;
		}
		
		return super.keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (scrollHud && client.getMeterGroup().hasMeters()) {
			client.getHUD().stepBackward(Math.round((float)scrollY));
			return true;
		}
		
		return super.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		minecraftClient.keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void update() {
		super.update();
		list.updateCoords();
	}
	
	@Override
	protected void initScreen() {
		minecraftClient.keyboard.enableRepeatEvents(true);
		
		list = new ScrollableListElement(client, getWidth(), getHeight());
		list.setX(getX());
		list.setY(getY());
		
		MultimeterHud hud = client.getHUD();
		
		list.add(hud);
		list.add(new MeterControlsElement(client, 0, 0, list.getEffectiveWidth()));
		
		addChild(list);
		
		hud.onInitScreen(list.getEffectiveWidth(), list.getHeight());
	}
	
	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}
	
	@Override
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
		if (client.getMeterGroup().hasMeters()) {
			super.renderContent(matrices, mouseX, mouseY);
		} else {
			String text;
			
			if (client.hasSubscription()) {
				text = "Nothing to see here! Add a meter to get started.";
			} else {
				text = "Nothing to see here! Subscribe to a meter group to get started.";
			}
			
			int textWidth = font.getStringWidth(text);
			int textHeight = font.fontHeight;
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;
			
			renderText(font, matrices, text, x, y, true, 0xFFFFFFFF);
		}
	}
}
