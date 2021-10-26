package redstone.multimeter.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.meter.MeterControlsElement;
import redstone.multimeter.client.gui.hud.MultimeterHud;

public class MultimeterScreen extends RSMMScreen {
	
	private final boolean isPauseScreen;
	
	private ScrollableListElement list;
	private boolean lockScrolling;
	
	public MultimeterScreen() {
		super(new LiteralText(RedstoneMultimeterMod.MOD_NAME), false);
		
		this.isPauseScreen = !Screen.hasShiftDown();
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean success = super.mouseClick(mouseX, mouseY, button);
		
		if (!success) {
			success = multimeterClient.getInputHandler().mouseClick(mouseX, mouseY, button);
		}
		
		return true;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		boolean success = super.keyPress(keyCode, scanCode, modifiers);
		
		if (!success) {
			success = multimeterClient.getInputHandler().keyPress(keyCode, scanCode, modifiers);
			
			if (!lockScrolling && KeyBindings.SCROLL_HUD.matchesKey(keyCode, scanCode)) {
				lockScrolling = true;
			}
		}
		
		return success;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		if (lockScrolling && KeyBindings.SCROLL_HUD.matchesKey(keyCode, scanCode)) {
			lockScrolling = false;
		}
		
		return super.keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (lockScrolling && multimeterClient.getMeterGroup().hasMeters()) {
			multimeterClient.getHUD().stepBackward(Math.round((float)scrollY));
			return true;
		}
		
		return super.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}
	
	@Override
	public void onRemoved() {
		super.onRemoved();
		client.keyboard.setRepeatEvents(false);
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public void update() {
		super.update();
		list.updateCoords();
	}
	
	@Override
	protected void initScreen() {
		client.keyboard.setRepeatEvents(true);
		
		list = new ScrollableListElement(multimeterClient, getWidth(), getHeight());
		list.setX(getX());
		list.setY(getY());
		
		MultimeterHud hud = multimeterClient.getHUD();
		
		list.add(hud);
		list.add(new MeterControlsElement(multimeterClient, 0, 0, list.getEffectiveWidth()));
		
		addContent(list);
		
		hud.onInitScreen(list.getEffectiveWidth(), list.getHeight());
	}
	
	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}
	
	@Override
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (multimeterClient.getMeterGroup().hasMeters()) {
			super.renderContent(matrices, mouseX, mouseY, delta);
		} else {
			String text = "Nothing to see here! Add a meter to get started.";
			
			int textWidth = textRenderer.getWidth(text);
			int textHeight = textRenderer.fontHeight;
			int x = getX() + (getWidth() - textWidth) / 2;
			int y = getY() + (getHeight() - textHeight) / 2;
			
			textRenderer.drawWithShadow(matrices, text, x, y, 0xFFFFFF);
		}
	}
}
