package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.math.MathHelper;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.FontRenderer;
import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.client.option.Options;

public class TextField extends AbstractButton {

	private final FontRenderer font;
	private final Consumer<String> listener;
	private final Supplier<String> updater;

	private SuggestionsMenu suggestions;

	private String hint;
	private String value;
	private String suffix;

	private int textX;
	private int textY;
	private int textWidth;
	private int textHeight;
	private int selectionY;
	private int selectionHeight;

	private int maxLength;
	private int maxScroll;
	private int scroll;
	private int cursor;
	private long cursorTicks;
	private int selection;
	private SelectionMethod selectionMethod;

	public TextField(int x, int y, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> updater) {
		this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, tooltip, listener, updater);
	}

	public TextField(int x, int y, int width, int height, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> updater) {
		super(x, y, width, height, () -> Texts.literal(""), tooltip);

		this.font = MultimeterClient.INSTANCE.getFontRenderer();
		this.listener = listener;
		this.updater = updater;

		this.hint = "";
		this.value = "";
		this.suffix = "";

		this.textX = this.getX() + 4;
		this.textY = this.getY() + (this.getHeight() - this.font.height()) / 2;
		this.textWidth = this.getWidth() - 8;
		this.textHeight = this.font.height();
		this.selectionY = this.textY - 1;
		this.selectionHeight = this.textHeight + 2;

		this.maxLength = 32;
		this.maxScroll = 0;
		this.scroll = 0;
		this.cursor = 0;
		this.cursorTicks = -1;
		this.selection = -1;
		this.selectionMethod = SelectionMethod.NONE;
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		if (this.selectionMethod == SelectionMethod.MOUSE) {
			if (mouseX < this.textX) {
				this.moveCursor(-1);
			} else if (mouseX > this.textX + this.textWidth) {
				this.moveCursor(1);
			}
		}

		super.render(renderer, mouseX, mouseY);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && button == MOUSE_BUTTON_LEFT) {
			if (!this.isSelecting() && this.isDoubleClick()) {
				this.setDraggingMouse(false);
				this.selectAll();
			} else {
				this.setCursorFromMouse(mouseX);
			}

			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (button == MOUSE_BUTTON_LEFT) {
			consumed = this.stopSelecting(SelectionMethod.MOUSE) || consumed;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = false;

		if (this.selectionMethod == SelectionMethod.MOUSE && button == MOUSE_BUTTON_LEFT) {
			consumed = this.setCursorFromMouse(mouseX);
		}

		return consumed;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	@Override
	public boolean keyPress(int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
			this.startSelecting(SelectionMethod.KEYBOARD);
			break;
		case Keyboard.KEY_LEFT:
			this.moveCursorFromKeyboard(-1);
			break;
		case Keyboard.KEY_RIGHT:
			this.moveCursorFromKeyboard(1);
			break;
		case Keyboard.KEY_UP:
		case Keyboard.KEY_PRIOR:
			this.setCursorFromKeyboard(0);
			break;
		case Keyboard.KEY_DOWN:
		case Keyboard.KEY_NEXT:
			this.setCursorFromKeyboard(this.value.length());
			break;
		case Keyboard.KEY_BACK:
			this.erase(false);
			break;
		case Keyboard.KEY_DELETE:
			this.erase(true);
			break;
		case Keyboard.KEY_RETURN:
		case Keyboard.KEY_ESCAPE:
			this.setFocused(false);
			break;
		default:
			if (!RSMMScreen.isControlPressed()) {
				break;
			}
			switch (keyCode) {
			case Keyboard.KEY_A:
				if (this.selectionMethod != SelectionMethod.MOUSE) {
					this.selectAll();
				}
				break;
			case Keyboard.KEY_C:
				this.copySelectionToClipboard(false);
				break;
			case Keyboard.KEY_X:
				this.copySelectionToClipboard(true);
				break;
			case Keyboard.KEY_V:
				this.pasteClipboard();
				break;
			default:
			}
		}

		return true;
	}

	@Override
	public boolean keyRelease(int keyCode) {
		boolean consumed = false;

		if (keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT) {
			consumed = this.stopSelecting(SelectionMethod.KEYBOARD);
		}

		return consumed;
	}

	@Override
	public boolean typeChar(char chr) {
		if (this.isActive() && SharedConstants.isValidChatChar(chr)) {
			this.write(String.valueOf(chr));
			return true;
		}

		return false;
	}

	@Override
	public void setHovered(boolean hovered) {
		if (this.suggestions == null) {
			this.setHoveredAndUpdateCursor(hovered);
		}
	}

	public void setHoveredAndUpdateCursor(boolean hovered) {
//		boolean wasHovered = isHovered();
		super.setHovered(hovered);

		// TODO: LegacyLWJGL3 compat
//		if (hovered) {
//			Element.setCursor(CursorType.IBEAM);
//		} else if (wasHovered) {
//			Element.setCursor(CursorType.ARROW);
//		}
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);

		this.update();

		if (!focused) {
			this.stopSelecting();
			this.clearSelection();
			this.setCursor(this.value.length());

			this.cursorTicks = -1;
		}
	}

	@Override
	public void tick() {
		if (this.isFocused()) {
			this.cursorTicks++;
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);

		if (this.suggestions != null) {
			this.suggestions.updatePosition();
		}

		this.textX = x + 4;
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		if (this.suggestions != null) {
			this.suggestions.updatePosition();
		}

		this.textY = y + this.getHeight() - (this.getHeight() + this.font.height()) / 2;
		this.selectionY = this.textY - 1;
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		Tooltip tooltip = super.getTooltip(mouseX, mouseY);

		if (tooltip.isEmpty() && !this.isFocused() && this.font.width(this.value) > this.textWidth) {
			tooltip = Tooltips.split(this.font, this.value);
		}

		return tooltip;
	}

	@Override
	public void update() {
		if (!this.isFocused() && this.updater != null) {
			this.setValue(this.updater.get(), false);
		}
		if (this.suggestions != null) {
			this.suggestions.update();
		}
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);

		if (this.suggestions != null) {
			this.suggestions.updateSize();
		}

		this.textWidth = width - 8;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);

		if (this.suggestions != null) {
			this.suggestions.updatePosition();
		}

		this.textHeight = this.font.height();
		this.selectionHeight = this.textHeight + 2;
	}

	@Override
	protected void renderButton(GuiRenderer renderer) {
		int x0 = this.getX();
		int y0 = this.getY();
		int x1 = x0 + this.getWidth();
		int y1 = y0 + this.getHeight();
		int borderColor = this.getBorderColor();
		int backgroundColor = 0xFF000000;

		renderer.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, backgroundColor);
		renderer.borders(x0, y0, x1, y1, borderColor);
	}

	@Override
	protected void renderButtonMessage(GuiRenderer renderer) {
		if (!this.isFocused() && this.isActive() && this.value.isEmpty() && !this.hint.isEmpty()) {
			// ideally the hint would be entirely visible but just in case...
			String visibleHint = Formatting.ITALIC + this.font.trim(this.hint, this.textWidth, false);
			int hintColor = this.getHintColor();

			renderer.drawStringWithShadow(visibleHint, this.textX, this.textY, hintColor);
		} else {
			String scrolledValue = this.value.substring(this.scroll);
			String visibleValue = this.font.trim(scrolledValue, this.textWidth, false);
			String coveredValue = scrolledValue.substring(0, this.cursor - this.scroll);

			int valueColor = this.getTextColor();

			// render input value
			renderer.drawStringWithShadow(visibleValue, this.textX, this.textY, valueColor);

			int valueWidth = this.font.width(visibleValue);
			int coveredWidth = this.font.width(coveredValue);

			// render remainder
			if (coveredValue.length() < visibleValue.length() && this.suggestions != null && this.suggestions.hasSuggestions()) {
				String visibleRemainder = visibleValue.substring(coveredValue.length());
				int remainderColor = this.getRemainderColor();

				renderer.drawStringWithShadow(visibleRemainder, this.textX + coveredWidth, this.textY, remainderColor);
			}

			// render suggestion suffix
			if (!this.suffix.isEmpty() && valueWidth < this.textWidth) {
				String visibleSuffix = this.font.trim(this.suffix, this.textWidth - valueWidth, false);
				int suggestionColor = this.getSuggestionColor();

				renderer.drawStringWithShadow(visibleSuffix, this.textX + valueWidth, this.textY, suggestionColor);
			}

			if (this.isFocused()) {
				if (this.isActive() && (this.cursorTicks / 6) % 2 == 0) {
					if (this.cursor == this.value.length()) {
						renderer.drawStringWithShadow("_", this.textX + valueWidth, this.textY, valueColor);
					} else {
						renderer.fill(this.textX + coveredWidth, this.selectionY, this.textX + coveredWidth + 1, this.selectionY + this.selectionHeight, valueColor);
					}
				}
				if (this.hasSelection()) {
					this.drawSelectionHighlight(renderer);
				}
			}
		}
	}

	private void drawSelectionHighlight(GuiRenderer renderer) {
		int start = Math.min(this.cursor, this.selection);
		int end = Math.max(this.cursor, this.selection);

		int x0 = this.textX;
		int x1 = this.textX + this.textWidth;

		String visibleText = this.font.trim(this.value.substring(this.scroll), this.textWidth, false);

		if (start >= this.scroll) {
			String t = this.value.substring(this.scroll, start);
			x0 = this.textX + this.font.width(t);
		}
		if (end <= this.scroll + visibleText.length()) {
			String t = this.value.substring(this.scroll, end);
			x1 = this.textX + this.font.width(t);
		}

		if (x0 >= x1) {
			return;
		}

		int y0 = this.selectionY;
		int y1 = this.selectionY + this.selectionHeight;

		renderer.highlight(x0, y0, x1, y1, 0xFF0000FF);
	}

	private int getBorderColor() {
		if (!this.isActive()) {
			return 0xFF404040;
		}
		if (this.isFocused()) {
			return 0xFFFFFFFF;
		}

		return this.isHovered() ? 0xFFB0B0B0 : 0xFF808080;
	}

	private int getHintColor() {
		return 0xFF606060;
	}

	private int getTextColor() {
		return this.isActive() ? 0xFFFFFFFF : 0xFFB0B0B0;
	}

	private int getRemainderColor() {
		return 0xFFB0B0B0;
	}

	private int getSuggestionColor() {
		return 0xFF808080;
	}

	public SuggestionsMenu setSuggestions(SuggestionsProvider provider) {
		return this.suggestions = new SuggestionsMenu(this, provider);
	}

	public void setHint(String text) {
		this.hint = text;
	}

	public String getValue() {
		return this.value;
	}

	public String getValueBeforeCursor() {
		return this.value.substring(0, Math.min(this.cursor, this.value.length()));
	}

	public void setValue(String text) {
		this.replace(text, 0, this.value.length());
	}

	public void clear() {
		this.replace("", 0, this.value.length());
	}

	private void write(String text) {
		if (!this.isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(this.selection, this.cursor);
			int end = Math.max(this.selection, this.cursor);

			this.replace(text, start, end);
		} else {
			this.insert(text, this.cursor);
		}
	}

	private void erase(boolean forward) {
		if (!this.isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(this.selection, this.cursor);
			int end = Math.max(this.selection, this.cursor);

			this.replace("", start, end);
		} else {
			if (forward) {
				this.replace("", this.cursor, this.cursor + 1);
			} else if (this.cursor > 0) {
				this.replace("", this.cursor - 1, this.cursor);
			}
		}
	}

	private boolean insert(String text, int index) {
		return this.replace(text, index, index);
	}

	private boolean replace(String text, int start, int end) {
		String t0 = this.value.substring(0, start);
		String t1 = this.value.substring(end);

		return this.setValue(t0 + text + t1, start + text.length(), true);
	}

	private boolean setValue(String text, boolean updateListener) {
		return this.setValue(text, text.length(), updateListener);
	}

	private boolean setValue(String text, int cursor, boolean updateListener) {
		if (this.value.equals(text) || text.length() > this.maxLength) {
			return false;
		}

		this.value = text;

		this.stopSelecting();
		this.clearSelection();

		if (this.suggestions != null) {
			this.suggestions.update();
		}

		if (cursor >= 0) {
			this.setCursor(cursor);
		}

		if (updateListener) {
			this.listener.accept(this.value);
		}

		return true;
	}

	private void copySelectionToClipboard(boolean erase) {
		if (this.hasSelection()) {
			String text = this.getSelection();

			if (!text.isEmpty()) {
				Screen.setClipboard(text);

				if (erase) {
					this.erase(false);
				}
			}
		}
	}

	private void pasteClipboard() {
		String text = Screen.getClipboard();

		if (!text.isEmpty()) {
			this.write(text);
		}
	}

	public void updateSuggestion() {
		this.suffix = "";

		if (this.suggestions.hasSelection()) {
			String suggestion = this.suggestions.getSelection();
			int index = suggestion.indexOf(this.value);

			if (index == 0) {
				this.suffix = suggestion.substring(this.value.length());
			}
		}

		this.updateMaxScroll();
		this.updateScroll();
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int length) {
		this.maxLength = length;
	}

	private void updateScroll() {
		if (this.scroll > this.maxScroll) {
			this.scroll(this.maxScroll - this.scroll);
		}
		if (this.cursor < this.scroll) {
			this.scroll(this.cursor - this.scroll);
		}

		String valueAndCursor = this.font.trim(this.value + "_", this.textWidth + 1, true);
		int maxCursorForScroll = this.scroll + valueAndCursor.length() - 1;

		if (this.cursor > maxCursorForScroll) {
			this.scroll(this.cursor - maxCursorForScroll);
		}
	}

	private void updateMaxScroll() {
		this.maxScroll = 0;

		if (!this.value.isEmpty()) {
			String valueAndCursor = this.font.trim(this.value + "_", this.textWidth + 1, true);

			if (valueAndCursor.length() <= this.value.length()) {
				this.maxScroll = this.value.length() - (valueAndCursor.length() - 1);
			}
		}
	}

	private void scroll(int amount) {
		this.setScroll(this.scroll + amount);
	}

	private void setScroll(int value) {
		this.scroll = MathHelper.clamp(value, 0, this.maxScroll);
	}

	private boolean setCursorFromMouse(double mouseX) {
		if (this.selectionMethod != SelectionMethod.KEYBOARD) {
			String scrolledValue = this.value.substring(this.scroll);
			String visibleValue = this.font.trim(scrolledValue, this.textWidth, false);

			// first handle clicks on the border:
			// scroll a bit to the left or right
			if (mouseX < this.textX) {
				this.setCursor(this.scroll - 5);
			} else if (mouseX > this.textX + this.textWidth) {
				this.setCursor(this.scroll + visibleValue.length() + 5);
			// then handle clicks within the border:
			// move cursor to mouse position
			} else {
				String coveredValue = this.font.trim(visibleValue, (int)(mouseX + 2) - this.textX, false);

				this.setCursor(this.scroll + coveredValue.length());
				this.startSelecting(SelectionMethod.MOUSE);
			}

			return true;
		} else {
			return false;
		}
	}

	private void moveCursorFromKeyboard(int amount) {
		this.setCursorFromKeyboard(this.cursor + amount);
	}

	private void setCursorFromKeyboard(int index) {
		if (this.selectionMethod != SelectionMethod.MOUSE) {
			this.setCursor(index);
		}
	}

	private void moveCursor(int amount) {
		this.setCursor(this.cursor + amount);
	}

	private void setCursor(int index) {
		this.cursor = MathHelper.clamp(index, 0, this.value.length());
		this.cursorTicks = this.isFocused() ? 0 : -1;

		this.onCursorMoved();
	}

	private void onCursorMoved() {
		if (!this.isSelecting()) {
			this.clearSelection();

			if (this.suggestions != null) {
				this.suggestions.update();
			}
		}

		this.updateScroll();
	}

	private boolean isDoubleClick() {
		return this.cursorTicks >= 0 && this.cursorTicks < Options.Miscellaneous.DOUBLE_CLICK_TIME.get();
	}

	private boolean hasSelection() {
		return this.selection >= 0 && this.selection != this.cursor;
	}

	private String getSelection() {
		int start = Math.min(this.selection, this.cursor);
		int end = Math.max(this.selection, this.cursor);

		return this.value.substring(start, end);
	}

	private void clearSelection() {
		this.selection = -1;
	}

	private boolean startSelecting(SelectionMethod method) {
		if (this.selectionMethod == SelectionMethod.NONE) {
			this.selectionMethod = method;

			if (this.selection < 0) {
				this.selection = this.cursor;
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean stopSelecting(SelectionMethod method) {
		if (this.selectionMethod == method) {
			this.selectionMethod = SelectionMethod.NONE;
			return true;
		} else {
			return false;
		}
	}

	private boolean stopSelecting() {
		return this.stopSelecting(this.selectionMethod);
	}

	private void selectAll() {
		this.setCursor(this.value.length());
		this.selection = 0;
	}

	private boolean isSelecting() {
		return this.selectionMethod != SelectionMethod.NONE;
	}

	private enum SelectionMethod {
		NONE, MOUSE, KEYBOARD
	}
}
