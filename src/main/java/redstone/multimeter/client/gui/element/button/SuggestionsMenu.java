package redstone.multimeter.client.gui.element.button;

import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.AbstractElement;

public class SuggestionsMenu extends AbstractElement {

	// default settings
	private static final int THRESHOLD_MENU_SIZE = 1;
	private static final int MIN_MENU_SIZE = 1;
	private static final int MAX_MENU_SIZE = 10;

	// measurements used in rendering
	private static final int ENTRY_SPACING = 2;
	private static final int BORDER_SPACING = 2;

	private final Font font;
	private final TextField input;
	private final SuggestionsProvider provider;

	private int threshold;
	private int minSize;
	private int maxSize;

	private List<String> suggestions;
	private int selected;
	private int hovered;
	private int scroll;
	private int size;

	public SuggestionsMenu(TextField input, SuggestionsProvider provider) {
		super(0, 0, 0, 0);

		this.font = MultimeterClient.MINECRAFT.font;
		this.input = input;
		this.provider = provider;

		this.threshold = THRESHOLD_MENU_SIZE;
		this.minSize = MIN_MENU_SIZE;
		this.maxSize = MAX_MENU_SIZE;

		this.suggestions = Collections.emptyList();
		this.selected = -1;
		this.hovered = -1;
		this.scroll = 0;
		this.size = 0;

		updatePosition();
		updateSize();
	}

	public void setThreshold(int size) {
		threshold = size;
	}

	public void setMinSize(int size) {
		minSize = size;
	}

	public void setMaxSize(int size) {
		maxSize = size;
	}

	public int getMaxHeight() {
		return maxSize * (font.lineHeight + ENTRY_SPACING);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		if (isShowingSuggestions()) {
			PoseStack poses = graphics.pose();

			poses.pushPose();
			poses.translate(0.0D, 0.0D, 1000.0D);

			renderBackground(graphics);
			renderHighlights(graphics);

			int x = getX() + BORDER_SPACING;
			int y = getY() + ENTRY_SPACING;
			int height = font.lineHeight + ENTRY_SPACING;

			for (int i = 0; i < size && scroll + i < suggestions.size(); i++) {
				renderText(font, graphics, suggestions.get(scroll + i), x, y, false, 0xFFFFFFFF);

				y += height;
			}

			poses.popPose();
		}
	}

	public boolean isShowingSuggestions() {
		return isEnabled() && hasEnoughSuggestions();
	}

	public boolean isEnabled() {
		return input.isFocused() && input.isActive();
	}

	private boolean hasEnoughSuggestions() {
		return suggestions.size() >= threshold;
	}

	private void renderBackground(GuiGraphics graphics) {
		int x = getX();
		int y = getY();
		int width = getWidth();
		int height = getHeight();
		int backgroundColor = getBackgroundColor();

		renderRect(graphics, x, y, width, height, backgroundColor);
	}

	private void renderHighlights(GuiGraphics graphics) {
		int x = getX();
		int width = getWidth();
		int height = font.lineHeight + ENTRY_SPACING;

		if (hovered >= 0) {
			int y = getY() + (hovered - scroll) * height;
			int hoveredColor = getHoveredColor();

			renderRect(graphics, x, y, width, height, hoveredColor);
		}
		if (selected >= 0) {
			int y = getY() + (selected - scroll) * height;
			int selectedColor = getSelectedColor();

			renderRect(graphics, x, y, width, height, selectedColor);
		}
	}

	private int getBackgroundColor() {
		return 0xFF000000;
	}

	private int getHoveredColor() {
		return 0xFF303030;
	}

	private int getSelectedColor() {
		return 0xFF606060;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return input.isMouseOver(mouseX, mouseY) || super.isMouseOver(mouseX, mouseY);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		hovered = -1;

		if (input.isMouseOver(mouseX, mouseY)) {
			input.setHoveredAndUpdateCursor(isHovered());
		} else {
			input.setHoveredAndUpdateCursor(false);

			if (isShowingSuggestions() && isHovered()) {
				int idx = scroll + (int)((mouseY - getY()) / (font.lineHeight + ENTRY_SPACING));

				if (idx >= 0 && idx < suggestions.size()) {
					hovered = idx;
				}
			}
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if (input.isMouseOver(mouseX, mouseY)) {
			return input.mouseClick(mouseX, mouseY, button);
		} else {
			boolean consumed = super.mouseClick(mouseX, mouseY, button);

			if (!consumed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (isShowingSuggestions()) {
					int idx = hovered;

					if (idx >= 0) {
						setSelection(idx);
						useSelection();
					}
				}

				consumed = true;
			}

			return consumed;
		}
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);
		return input.mouseRelease(mouseX, mouseY, button) || consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return input.isMouseOver(mouseX, mouseY) && input.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (input.isMouseOver(mouseX, mouseY)) {
			return input.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		}
		if (isShowingSuggestions()) {
			moveSelection((int)scrollY);
			return true;
		}

		return false;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
			setFocused(false);
		}

		boolean consumed = false;

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			setFocused(false);
			consumed = true;
		}

		if (hasSuggestions()) {
			consumed = true;

			switch (keyCode) {
			case GLFW.GLFW_KEY_UP:
				moveSelection(-1);
				break;
			case GLFW.GLFW_KEY_PAGE_UP:
				setSelection(0);
				break;
			case GLFW.GLFW_KEY_DOWN:
				moveSelection(1);
				break;
			case GLFW.GLFW_KEY_PAGE_DOWN:
				setSelection(suggestions.size() - 1);
				break;
			case GLFW.GLFW_KEY_TAB:
			case GLFW.GLFW_KEY_ENTER:
				useSelection();
				if (keyCode == GLFW.GLFW_KEY_ENTER) {
					setFocused(false);
				}
				break;
			default:
				consumed = false;
			}
		}

		return consumed || input.keyPress(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return input.keyRelease(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		return input.typeChar(chr, modifiers);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		input.setFocused(focused);
	}

	@Override
	public void tick() {
	}

	@Override
	public void update() {
		if (isEnabled()) {
			suggestions = provider.provide(input.getValueBeforeCursor());
		} else {
			suggestions = Collections.emptyList();
		}

		updateSize();
		setSelection(0);
	}

	public void updatePosition() {
		setX(input.getX() + 2);

		if (fitsBelowInput() || !fitsAboveInput()) {
			setY(input.getY() + input.getHeight() - 1);
		} else {
			setY(input.getY() + 1 - getMaxHeight());
		}
	}

	private boolean fitsAboveInput() {
		return input.getY() + 1 - getMaxHeight() >= 0;
	}

	private boolean fitsBelowInput() {
		return input.getY() + input.getHeight() - 1 + getMaxHeight() <= MultimeterClient.MINECRAFT.screen.height;
	}

	public void updateSize() {
		if (hasEnoughSuggestions()) {
			size = Mth.clamp(suggestions.size(), minSize, maxSize);

			int width = 2 * BORDER_SPACING;
			int height = size * (font.lineHeight + ENTRY_SPACING);

			for (int i = 0; i < size && scroll + i < suggestions.size(); i++) {
				String suggestion = suggestions.get(scroll + i);
				int suggestionWidth = font.width(suggestion);

				if (suggestionWidth > width) {
					width = suggestionWidth;
				}
			}

			setWidth(width + 2 * BORDER_SPACING);
			setHeight(height);
		} else {
			size = 0;

			setWidth(0);
			setHeight(0);
		}
	}

	public boolean hasSuggestions() {
		return !suggestions.isEmpty();
	}

	public boolean hasSelection() {
		return hasSuggestions() && selected >= 0;
	}

	public String getSelection() {
		return hasSelection() ? suggestions.get(selected) : null;
	}

	public void moveSelection(int amount) {
		int newSelection = selected + amount;

		if (newSelection < 0) {
			newSelection = suggestions.size() - 1;
		}
		if (newSelection >= suggestions.size()) {
			newSelection = 0;
		}

		setSelection(newSelection);
	}

	public void setSelection(int index) {
		selected = Mth.clamp(index, 0, suggestions.size() - 1);

		updateScroll();
		input.updateSuggestion();
	}

	public void useSelection() {
		if (hasSelection()) {
			input.setValue(getSelection());
		}
	}

	private void updateScroll() {
		if (selected >= scroll + size) {
			scroll(selected - (scroll + size) + 1);
		}
		if (selected < scroll) {
			scroll(selected - scroll);
		}
	}

	private void scroll(int amount) {
		setScroll(scroll + amount);
	}

	private void setScroll(int value) {
		int oldScroll = scroll;
		scroll = Mth.clamp(value, 0, suggestions.size() - size);

		if (scroll != oldScroll) {
			updateSize();
		}
	}
}
