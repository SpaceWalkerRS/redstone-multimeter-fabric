package redstone.multimeter.client.gui.element.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.Element;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.util.TextUtils;

public class TextField extends AbstractButton {

	private final KeyboardHandler keyboard;
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

	public TextField(MultimeterClient client, int x, int y, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> updater) {
		this(client, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, tooltip, listener, updater);
	}

	public TextField(MultimeterClient client, int x, int y, int width, int height, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> updater) {
		super(client, x, y, width, height, () -> Component.literal(""), tooltip);

		Minecraft minecraft = client.getMinecraft();

		this.keyboard = minecraft.keyboardHandler;
		this.listener = listener;
		this.updater = updater;

		this.hint = "";
		this.value = "";
		this.suffix = "";

		this.textX = getX() + 4;
		this.textY = getY() + (getHeight() - this.font.lineHeight) / 2;
		this.textWidth = getWidth() - 8;
		this.textHeight = this.font.lineHeight;
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
	public void render(PoseStack poses, int mouseX, int mouseY) {
		if (selectionMethod == SelectionMethod.MOUSE) {
			if (mouseX < textX) {
				moveCursor(-1);
			} else if (mouseX > textX + textWidth) {
				moveCursor(1);
			}
		}

		super.render(poses, mouseX, mouseY);
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (!isSelecting() && isDoubleClick()) {
				setDraggingMouse(false);
				selectAll();
			} else {
				setCursorFromMouse(mouseX);
			}

			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			consumed = stopSelecting(SelectionMethod.MOUSE) || consumed;
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean consumed = false;

		if (selectionMethod == SelectionMethod.MOUSE && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			consumed = setCursorFromMouse(mouseX);
		}

		return consumed;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
		case GLFW.GLFW_KEY_LEFT_SHIFT:
		case GLFW.GLFW_KEY_RIGHT_SHIFT:
			startSelecting(SelectionMethod.KEYBOARD);
			break;
		case GLFW.GLFW_KEY_LEFT:
			moveCursorFromKeyboard(-1);
			break;
		case GLFW.GLFW_KEY_RIGHT:
			moveCursorFromKeyboard(1);
			break;
		case GLFW.GLFW_KEY_UP:
		case GLFW.GLFW_KEY_PAGE_UP:
			setCursorFromKeyboard(0);
			break;
		case GLFW.GLFW_KEY_DOWN:
		case GLFW.GLFW_KEY_PAGE_DOWN:
			setCursorFromKeyboard(value.length());
			break;
		case GLFW.GLFW_KEY_BACKSPACE:
			erase(false);
			break;
		case GLFW.GLFW_KEY_DELETE:
			erase(true);
			break;
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_ESCAPE:
			setFocused(false);
			break;
		default:
			if (!RSMMScreen.isControlPressed()) {
				break;
			}
			switch (keyCode) {
			case GLFW.GLFW_KEY_A:
				if (selectionMethod != SelectionMethod.MOUSE) {
					selectAll();
				}
				break;
			case GLFW.GLFW_KEY_C:
				copySelectionToClipboard(false);
				break;
			case GLFW.GLFW_KEY_X:
				copySelectionToClipboard(true);
				break;
			case GLFW.GLFW_KEY_V:
				pasteClipboard();
				break;
			default:
			}
		}

		return true;
	}

	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		boolean consumed = false;

		if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			consumed = stopSelecting(SelectionMethod.KEYBOARD);
		}

		return consumed;
	}

	@Override
	public boolean typeChar(char chr, int modifiers) {
		if (isActive() && SharedConstants.isAllowedChatCharacter(chr)) {
			write(String.valueOf(chr));
			return true;
		}

		return false;
	}

	@Override
	public void setHovered(boolean hovered) {
		if (suggestions == null) {
			setHoveredAndUpdateCursor(hovered);
		}
	}

	public void setHoveredAndUpdateCursor(boolean hovered) {
		boolean wasHovered = isHovered();
		super.setHovered(hovered);

		if (hovered) {
			Element.setCursor(CursorType.IBEAM);
		} else if (wasHovered) {
			Element.setCursor(CursorType.ARROW);
		}
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);

		update();

		if (!focused) {
			stopSelecting();
			clearSelection();
			setCursor(value.length());

			cursorTicks = -1;
		}
	}

	@Override
	public void tick() {
		if (isFocused()) {
			cursorTicks++;
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);

		if (suggestions != null) {
			suggestions.updatePosition();
		}

		textX = x + 4;
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		if (suggestions != null) {
			suggestions.updatePosition();
		}

		textY = y + getHeight() - (getHeight() + font.lineHeight) / 2;
		selectionY = textY - 1;
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		Tooltip tooltip = super.getTooltip(mouseX, mouseY);

		if (tooltip.isEmpty() && !isFocused() && font.width(value) > textWidth) {
			tooltip = Tooltip.of(TextUtils.toLines(font, value));
		}

		return tooltip;
	}

	@Override
	public void update() {
		if (!isFocused() && updater != null) {
			setValue(updater.get(), false);
		}
		if (suggestions != null) {
			suggestions.update();
		}
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);

		if (suggestions != null) {
			suggestions.updateSize();
		}

		textWidth = width - 8;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);

		if (suggestions != null) {
			suggestions.updatePosition();
		}

		textHeight = font.lineHeight;
		selectionHeight = textHeight + 2;
	}

	@Override
	protected void renderButton(PoseStack poses) {
		int x = getX();
		int y = getY();
		int width = getWidth();
		int height = getHeight();
		int borderColor = getBorderColor();
		int backgroundColor = 0xFF000000;

		renderRect(poses, (bufferBuilder, pose) -> {
			drawRect(bufferBuilder, pose, x, y, width, height, borderColor);
			drawRect(bufferBuilder, pose, x + 1, y + 1, width - 2, height - 2, backgroundColor);
		});
	}

	@Override
	protected void renderButtonMessage(PoseStack poses) {
		if (!isFocused() && isActive() && value.isEmpty() && !hint.isEmpty()) {
			// ideally the hint would be entirely visible but just in case...
			String visibleHint = ChatFormatting.ITALIC + font.plainSubstrByWidth(hint, textWidth, false);
			int hintColor = getHintColor();

			renderText(font, poses, visibleHint, textX, textY, true, hintColor);
		} else {
			String scrolledValue = value.substring(scroll);
			String visibleValue = font.plainSubstrByWidth(scrolledValue, textWidth, false);
			String coveredValue = scrolledValue.substring(0, cursor - scroll);

			int valueColor = getTextColor();

			// render input value
			renderText(font, poses, visibleValue, textX, textY, true, valueColor);

			int valueWidth = font.width(visibleValue);
			int coveredWidth = font.width(coveredValue);

			// render remainder
			if (coveredValue.length() < visibleValue.length() && suggestions != null && suggestions.hasSuggestions()) {
				String visibleRemainder = visibleValue.substring(coveredValue.length());
				int remainderColor = getRemainderColor();

				renderText(font, poses, visibleRemainder, textX + coveredWidth, textY, true, remainderColor);
			}

			// render suggestion suffix
			if (!suffix.isEmpty() && valueWidth < textWidth) {
				String visibleSuffix = font.plainSubstrByWidth(suffix, textWidth - valueWidth, false);
				int suggestionColor = getSuggestionColor();

				renderText(font, poses, visibleSuffix, textX + valueWidth, textY, true, suggestionColor);
			}

			if (isFocused()) {
				if (isActive() && (cursorTicks / 6) % 2 == 0) {
					if (cursor == value.length()) {
						renderText(font, poses, "_", textX + valueWidth, textY, true, valueColor);
					} else {
						renderRect(poses, textX + coveredWidth, selectionY, 1, selectionHeight, valueColor);
					}
				}
				if (hasSelection()) {
					drawSelectionHighlight(poses);
				}
			}
		}
	}

	private void drawSelectionHighlight(PoseStack poses) {
		int start = Math.min(cursor, selection);
		int end = Math.max(cursor, selection);

		int x0 = textX;
		int x1 = textX + textWidth;

		String visibleText = font.plainSubstrByWidth(value.substring(scroll), textWidth, false);

		if (start >= scroll) {
			String t = value.substring(scroll, start);
			x0 = textX + font.width(t);
		}
		if (end <= scroll + visibleText.length()) {
			String t = value.substring(scroll, end);
			x1 = textX + font.width(t);
		}

		if (x0 >= x1) {
			return;
		}

		int y0 = selectionY;
		int y1 = selectionY + selectionHeight;
		int z = 0;

		RenderSystem.setShader(() -> GameRenderer.getPositionShader());
		RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = poses.last().pose();

		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

		bufferBuilder.vertex(pose, x0, y0, z).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).endVertex();

		tessellator.end();

		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private int getBorderColor() {
		if (!isActive()) {
			return 0xFF404040;
		}
		if (isFocused()) {
			return 0xFFFFFFFF;
		}

		return isHovered() ? 0xFFB0B0B0 : 0xFF808080;
	}

	private int getHintColor() {
		return 0xFF606060;
	}

	private int getTextColor() {
		return isActive() ? 0xFFFFFFFF : 0xFFB0B0B0;
	}

	private int getRemainderColor() {
		return 0xFFB0B0B0;
	}

	private int getSuggestionColor() {
		return 0xFF808080;
	}

	public SuggestionsMenu setSuggestions(SuggestionsProvider provider) {
		return suggestions = new SuggestionsMenu(this, provider);
	}

	public void setHint(String text) {
		hint = text;
	}

	public String getValue() {
		return value;
	}

	public String getValueBeforeCursor() {
		return value.substring(0, Math.min(cursor, value.length()));
	}

	public void setValue(String text) {
		replace(text, 0, value.length());
	}

	public void clear() {
		replace("", 0, value.length());
	}

	private void write(String text) {
		if (!isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(selection, cursor);
			int end = Math.max(selection, cursor);

			replace(text, start, end);
		} else {
			insert(text, cursor);
		}
	}

	private void erase(boolean forward) {
		if (!isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(selection, cursor);
			int end = Math.max(selection, cursor);

			replace("", start, end);
		} else {
			if (forward) {
				replace("", cursor, cursor + 1);
			} else if (cursor > 0) {
				replace("", cursor - 1, cursor);
			}
		}
	}

	private boolean insert(String text, int index) {
		return replace(text, index, index);
	}

	private boolean replace(String text, int start, int end) {
		String t0 = value.substring(0, start);
		String t1 = value.substring(end);

		return setValue(t0 + text + t1, start + text.length(), true);
	}

	private boolean setValue(String text, boolean updateListener) {
		return setValue(text, text.length(), updateListener);
	}

	private boolean setValue(String text, int cursor, boolean updateListener) {
		if (value.equals(text) || text.length() > maxLength) {
			return false;
		}

		value = text;

		stopSelecting();
		clearSelection();

		if (suggestions != null) {
			suggestions.update();
		}

		if (cursor >= 0) {
			setCursor(cursor);
		}

		if (updateListener) {
			listener.accept(value);
		}

		return true;
	}

	private void copySelectionToClipboard(boolean erase) {
		if (hasSelection()) {
			String text = getSelection();

			if (!text.isEmpty()) {
				keyboard.setClipboard(text);

				if (erase) {
					erase(false);
				}
			}
		}
	}

	private void pasteClipboard() {
		String text = keyboard.getClipboard();

		if (!text.isEmpty()) {
			write(text);
		}
	}

	public void updateSuggestion() {
		suffix = "";

		if (suggestions.hasSelection()) {
			String suggestion = suggestions.getSelection();
			int index = suggestion.indexOf(value);

			if (index == 0) {
				suffix = suggestion.substring(value.length());
			}
		}

		updateMaxScroll();
		updateScroll();
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int length) {
		maxLength = length;
	}

	private void updateScroll() {
		if (scroll > maxScroll) {
			scroll(maxScroll - scroll);
		}
		if (cursor < scroll) {
			scroll(cursor - scroll);
		}

		String valueAndCursor = font.plainSubstrByWidth(value + "_", textWidth + 1, true);
		int maxCursorForScroll = scroll + valueAndCursor.length() - 1;

		if (cursor > maxCursorForScroll) {
			scroll(cursor - maxCursorForScroll);
		}
	}

	private void updateMaxScroll() {
		maxScroll = 0;

		if (!value.isEmpty()) {
			String valueAndCursor = font.plainSubstrByWidth(value + "_", textWidth + 1, true);

			if (valueAndCursor.length() <= value.length()) {
				maxScroll = value.length() - (valueAndCursor.length() - 1);
			}
		}
	}

	private void scroll(int amount) {
		setScroll(scroll + amount);
	}

	private void setScroll(int value) {
		scroll = Mth.clamp(value, 0, maxScroll);
	}

	private boolean setCursorFromMouse(double mouseX) {
		if (selectionMethod != SelectionMethod.KEYBOARD) {
			String scrolledValue = value.substring(scroll);
			String visibleValue = font.plainSubstrByWidth(scrolledValue, textWidth, false);

			// first handle clicks on the border:
			// scroll a bit to the left or right
			if (mouseX < textX) {
				setCursor(scroll - 5);
			} else if (mouseX > textX + textWidth) {
				setCursor(scroll + visibleValue.length() + 5);
			// then handle clicks within the border:
			// move cursor to mouse position
			} else {
				String coveredValue = font.plainSubstrByWidth(visibleValue, (int)(mouseX + 2) - textX, false);

				setCursor(scroll + coveredValue.length());
				startSelecting(SelectionMethod.MOUSE);
			}

			return true;
		} else {
			return false;
		}
	}

	private void moveCursorFromKeyboard(int amount) {
		setCursorFromKeyboard(cursor + amount);
	}

	private void setCursorFromKeyboard(int index) {
		if (selectionMethod != SelectionMethod.MOUSE) {
			setCursor(index);
		}
	}

	private void moveCursor(int amount) {
		setCursor(cursor + amount);
	}

	private void setCursor(int index) {
		cursor = Mth.clamp(index, 0, value.length());
		cursorTicks = isFocused() ? 0 : -1;

		onCursorMoved();
	}

	private void onCursorMoved() {
		if (!isSelecting()) {
			clearSelection();

			if (suggestions != null) {
				suggestions.update();
			}
		}

		updateScroll();
	}

	private boolean isDoubleClick() {
		return cursorTicks >= 0 && cursorTicks < Options.Miscellaneous.DOUBLE_CLICK_TIME.get();
	}

	private boolean hasSelection() {
		return selection >= 0 && selection != cursor;
	}

	private String getSelection() {
		int start = Math.min(selection, cursor);
		int end = Math.max(selection, cursor);

		return value.substring(start, end);
	}

	private void clearSelection() {
		selection = -1;
	}

	private boolean startSelecting(SelectionMethod method) {
		if (selectionMethod == SelectionMethod.NONE) {
			selectionMethod = method;

			if (selection < 0) {
				selection = cursor;
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean stopSelecting(SelectionMethod method) {
		if (selectionMethod == method) {
			selectionMethod = SelectionMethod.NONE;
			return true;
		} else {
			return false;
		}
	}

	private boolean stopSelecting() {
		return stopSelecting(selectionMethod);
	}

	private void selectAll() {
		setCursor(value.length());
		selection = 0;
	}

	private boolean isSelecting() {
		return selectionMethod != SelectionMethod.NONE;
	}

	private enum SelectionMethod {
		NONE, MOUSE, KEYBOARD
	}
}
