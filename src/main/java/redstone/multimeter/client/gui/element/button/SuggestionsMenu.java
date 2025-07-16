package redstone.multimeter.client.gui.element.button;

import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.util.Mth;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.AbstractElement;

public class SuggestionsMenu extends AbstractElement {

	// default settings
	private static final int THRESHOLD_MENU_SIZE = 1;
	private static final int MIN_MENU_SIZE = 1;
	private static final int MAX_MENU_SIZE = 10;

	// measurements used in rendering
	private static final int ENTRY_SPACING = 2;
	private static final int BORDER_SPACING = 2;

	private final FontRenderer font;
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

		this.font = MultimeterClient.INSTANCE.getFontRenderer();
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

		this.updatePosition();
		this.updateSize();
	}

	public void setThreshold(int size) {
		this.threshold = size;
	}

	public void setMinSize(int size) {
		this.minSize = size;
	}

	public void setMaxSize(int size) {
		this.maxSize = size;
	}

	public int getMaxHeight() {
		return this.maxSize * (this.font.height() + ENTRY_SPACING);
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		if (this.isShowingSuggestions()) {
			renderer.pushMatrix();
			renderer.translate(0.0D, 0.0D, 10.0D);

			this.renderBackground(renderer);
			this.renderHighlights(renderer);

			int x = this.getX() + BORDER_SPACING;
			int y = this.getY() + ENTRY_SPACING;
			int height = this.font.height() + ENTRY_SPACING;

			for (int i = 0; i < this.size && this.scroll + i < this.suggestions.size(); i++) {
				renderer.drawString(this.suggestions.get(this.scroll + i), x, y);

				y += height;
			}

			renderer.popMatrix();
		}
	}

	public boolean isShowingSuggestions() {
		return this.isEnabled() && this.hasEnoughSuggestions();
	}

	public boolean isEnabled() {
		return this.input.isFocused() && this.input.isActive();
	}

	private boolean hasEnoughSuggestions() {
		return this.suggestions.size() >= this.threshold;
	}

	private void renderBackground(GuiRenderer renderer) {
		int x0 = this.getX();
		int y0 = this.getY();
		int x1 = x0 + this.getWidth();
		int y1 = y0 + this.getHeight();
		int backgroundColor = this.getBackgroundColor();

		renderer.fill(x0, y0, x1, y1, backgroundColor);
	}

	private void renderHighlights(GuiRenderer renderer) {
		int x0 = this.getX();
		int x1 = x0 + this.getWidth();
		int height = this.font.height() + ENTRY_SPACING;

		if (this.hovered >= 0) {
			int y0 = this.getY() + (this.hovered - this.scroll) * height;
			int y1 = y0 + height;
			int hoveredColor = this.getHoveredColor();

			renderer.fill(x0, y0, x1, y1, hoveredColor);
		}
		if (this.selected >= 0) {
			int y0 = this.getY() + (this.selected - this.scroll) * height;
			int y1 = y0 + height;
			int selectedColor = this.getSelectedColor();

			renderer.fill(x0, y0, x1, y1, selectedColor);
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
		return this.input.isMouseOver(mouseX, mouseY) || super.isMouseOver(mouseX, mouseY);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
		this.hovered = -1;

		if (this.input.isMouseOver(mouseX, mouseY)) {
			this.input.setHoveredAndUpdateCursor(this.isHovered());
		} else {
			this.input.setHoveredAndUpdateCursor(false);

			if (this.isShowingSuggestions() && this.isHovered()) {
				int idx = this.scroll + (int)((mouseY - this.getY()) / (this.font.height() + ENTRY_SPACING));

				if (idx >= 0 && idx < this.suggestions.size()) {
					this.hovered = idx;
				}
			}
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		if (this.input.isMouseOver(mouseX, mouseY)) {
			return this.input.mouseClick(mouseX, mouseY, button);
		} else {
			boolean consumed = super.mouseClick(mouseX, mouseY, button);

			if (!consumed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (this.isShowingSuggestions()) {
					int idx = this.hovered;

					if (idx >= 0) {
						this.setSelection(idx);
						this.useSelection();
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
		return this.input.mouseRelease(mouseX, mouseY, button) || consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.input.isMouseOver(mouseX, mouseY) && this.input.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.input.isMouseOver(mouseX, mouseY)) {
			return this.input.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		}
		if (this.isShowingSuggestions()) {
			this.moveSelection(-(int)scrollY);
			return true;
		}

		return false;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
			this.setFocused(false);
		}

		boolean consumed = false;

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.setFocused(false);
			consumed = true;
		}

		if (this.hasSuggestions()) {
			consumed = true;

			switch (keyCode) {
			case GLFW.GLFW_KEY_UP:
				this.moveSelection(-1);
				break;
			case GLFW.GLFW_KEY_PAGE_UP:
				this.setSelection(0);
				break;
			case GLFW.GLFW_KEY_DOWN:
				this.moveSelection(1);
				break;
			case GLFW.GLFW_KEY_PAGE_DOWN:
				this.setSelection(this.suggestions.size() - 1);
				break;
			case GLFW.GLFW_KEY_TAB:
			case GLFW.GLFW_KEY_ENTER:
				this.useSelection();
				if (keyCode == GLFW.GLFW_KEY_ENTER) {
					this.setFocused(false);
				}
				break;
			default:
				consumed = false;
			}
		}

		return consumed || this.input.keyPress(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return this.input.keyRelease(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		return this.input.typeChar(chr, modifiers);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		this.input.setFocused(focused);
	}

	@Override
	public void tick() {
	}

	@Override
	public int getHeight() {
		// suggestions are always listed top down, so if the menu
		// appears above the text field, extend it down until it
		// connects with it again
		return this.getY() < this.input.getY() ? this.getMaxHeight() : super.getHeight();
	}

	@Override
	public void update() {
		if (this.isEnabled()) {
			this.suggestions = this.provider.provide(this.input.getValueBeforeCursor());
		} else {
			this.suggestions = Collections.emptyList();
		}

		this.updateSize();
		this.setSelection(0);
	}

	public void updatePosition() {
		this.setX(this.input.getX() + 2);

		if (this.fitsBelowInput() || !this.fitsAboveInput()) {
			this.setY(this.input.getY() + this.input.getHeight() - 1);
		} else {
			this.setY(this.input.getY() + 1 - this.getMaxHeight());
		}
	}

	private boolean fitsAboveInput() {
		return this.input.getY() + 1 - this.getMaxHeight() >= 0;
	}

	private boolean fitsBelowInput() {
		return this.input.getY() + this.input.getHeight() - 1 + this.getMaxHeight() <= MultimeterClient.MINECRAFT.screen.height;
	}

	public void updateSize() {
		if (this.hasEnoughSuggestions()) {
			this.size = Mth.clamp(this.suggestions.size(), this.minSize, this.maxSize);

			int width = 2 * BORDER_SPACING;
			int height = this.size * (this.font.height() + ENTRY_SPACING);

			for (int i = 0; i < this.size && this.scroll + i < this.suggestions.size(); i++) {
				String suggestion = this.suggestions.get(this.scroll + i);
				int suggestionWidth = this.font.width(suggestion);

				if (suggestionWidth > width) {
					width = suggestionWidth;
				}
			}

			this.setWidth(width + 2 * BORDER_SPACING);
			this.setHeight(height);
		} else {
			this.size = 0;

			this.setWidth(0);
			this.setHeight(0);
		}
	}

	public boolean hasSuggestions() {
		return !this.suggestions.isEmpty();
	}

	public boolean hasSelection() {
		return this.hasSuggestions() && this.selected >= 0;
	}

	public String getSelection() {
		return this.hasSelection() ? this.suggestions.get(this.selected) : null;
	}

	public void moveSelection(int amount) {
		int newSelection = this.selected + amount;

		if (newSelection < 0) {
			newSelection = this.suggestions.size() - 1;
		}
		if (newSelection >= this.suggestions.size()) {
			newSelection = 0;
		}

		this.setSelection(newSelection);
	}

	public void setSelection(int index) {
		this.selected = Mth.clamp(index, 0, this.suggestions.size() - 1);

		this.updateScroll();
		this.input.updateSuggestion();
	}

	public void useSelection() {
		if (this.hasSelection()) {
			this.input.setValue(this.getSelection());
		}
	}

	private void updateScroll() {
		if (this.selected >= this.scroll + this.size) {
			this.scroll(this.selected - (this.scroll + this.size) + 1);
		}
		if (this.selected < this.scroll) {
			this.scroll(this.selected - this.scroll);
		}
	}

	private void scroll(int amount) {
		this.setScroll(this.scroll + amount);
	}

	private void setScroll(int value) {
		int oldScroll = this.scroll;
		this.scroll = Mth.clamp(value, 0, this.suggestions.size() - this.size);

		if (this.scroll != oldScroll) {
			this.updateSize();
		}
	}
}
