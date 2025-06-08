package redstone.multimeter.client.gui.element.button;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;

import net.minecraft.SharedConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.CursorType;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.util.TextUtils;

public class TextField extends AbstractButton {

	private final KeyboardHandler keyboard;
	private final Consumer<String> listener;
	private final Supplier<String> textSupplier;
	private final SuggestionsProvider suggestionsProvider;

	private String fullText;
	private String visibleText;
	private String suggestion;
	private String visibleSuggestion;
	private int suggestionIndex;
	private List<String> suggestions;
	private int textX;
	private int textY;
	private int textWidth;
	private int textHeight;
	private int selectionY;
	private int selectionHeight;

	private int maxLength;
	private int maxScroll;
	private int scrollIndex;
	private int cursorIndex;
	private long cursorTicks;
	private int selectionIndex;
	private SelectType selection;

	public TextField(MultimeterClient client, int x, int y, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> text, SuggestionsProvider suggestions) {
		this(client, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, tooltip, listener, text, suggestions);
	}


	public TextField(MultimeterClient client, int x, int y, int width, int height, Supplier<Tooltip> tooltip, Consumer<String> listener, Supplier<String> text, SuggestionsProvider suggestions) {
		super(client, x, y, width, height, () -> new TextComponent(""), tooltip);

		Minecraft minecraft = this.client.getMinecraft();

		this.keyboard = minecraft.keyboardHandler;
		this.listener = listener;
		this.textSupplier = text;
		this.suggestionsProvider = suggestions;

		this.fullText = "";
		this.visibleText = "";
		this.suggestion = "";
		this.visibleSuggestion = "";
		this.suggestionIndex = -1;
		this.suggestions = Collections.emptyList();
		this.textX = getX() + 4;
		this.textY = getY() + (getHeight() - this.font.lineHeight) / 2;
		this.textWidth = getWidth() - 8;
		this.textHeight = this.font.lineHeight;
		this.selectionY = this.textY - 1;
		this.selectionHeight = this.textHeight + 2;

		this.maxLength = 32;
		this.maxScroll = 0;
		this.scrollIndex = 0;
		this.cursorIndex = 0;
		this.cursorTicks = -1;
		this.selectionIndex = -1;
		this.selection = SelectType.NONE;
	}

	@Override
	public void render(PoseStack poses, int mouseX, int mouseY) {
		if (selection == SelectType.MOUSE) {
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
		boolean wasHovered = isHovered();
		super.mouseMove(mouseX, mouseY);

		if (isHovered()) {
			setCursor(client.getMinecraft(), CursorType.IBEAM);
		} else if (wasHovered) {
			setCursor(client.getMinecraft(), CursorType.ARROW);
		}
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseClick(mouseX, mouseY, button);

		if (!consumed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (!isFocused()) {
				setFocused(true);
			}
			if (!isSelecting() && isDoubleClick()) {
				setDraggingMouse(false);
				selectAll();
			} else if (mouseX < textX) {
				setCursor(scrollIndex - 5);
			} else if (mouseX > textX + textWidth) {
				setCursor(scrollIndex + visibleText.length() + 5);
			} else {
				setCursorFromMouse(mouseX + 2);
				startSelecting(SelectType.MOUSE);
				cursorTicks = 0L;
			}

			consumed = true;
		}

		return consumed;
	}

	@Override
	public boolean mouseRelease(double mouseX, double mouseY, int button) {
		boolean consumed = super.mouseRelease(mouseX, mouseY, button);

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			stopSelecting(SelectType.MOUSE);
		}

		return consumed;
	}

	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (selection == SelectType.MOUSE && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			setCursorFromMouse(mouseX);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
			setFocused(false);
			return true;
		}

		switch (keyCode) {
		case GLFW.GLFW_KEY_LEFT_SHIFT:
		case GLFW.GLFW_KEY_RIGHT_SHIFT:
			startSelecting(SelectType.KEYBOARD);
			break;
		case GLFW.GLFW_KEY_LEFT:
			moveCursorFromKeyboard(-1);
			break;
		case GLFW.GLFW_KEY_RIGHT:
			moveCursorFromKeyboard(1);
			break;
		case GLFW.GLFW_KEY_UP:
			if (!suggestions.isEmpty()) {
				moveSuggestion(-1);
				break;
			}
			// fall through
		case GLFW.GLFW_KEY_PAGE_UP:
			setCursorFromKeyboard(0);
			break;
		case GLFW.GLFW_KEY_DOWN:
			if (!suggestions.isEmpty()) {
				moveSuggestion(1);
				break;
			}
			// fall through
		case GLFW.GLFW_KEY_PAGE_DOWN:
			setCursorFromKeyboard(fullText.length());
			break;
		case GLFW.GLFW_KEY_BACKSPACE:
			erase(false);
			break;
		case GLFW.GLFW_KEY_DELETE:
			erase(true);
			break;
		case GLFW.GLFW_KEY_TAB:
			if (!suggestions.isEmpty()) {
				useSuggestion();
			}
			break;
		default:
			if (!RSMMScreen.isControlPressed()) {
				break;
			}
			switch (keyCode) {
			case GLFW.GLFW_KEY_A:
				if (selection != SelectType.MOUSE) {
					selectAll();
				}
				break;
			case GLFW.GLFW_KEY_C:
				copyTextToClipboard(false);
				break;
			case GLFW.GLFW_KEY_X:
				copyTextToClipboard(true);
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
		if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			stopSelecting(SelectType.KEYBOARD);
		}

		return false;
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
	public void onRemoved() {
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		update();

		if (!focused) {
			selectionIndex = -1;
			cursorTicks = -1;
			setCursor(fullText.length());
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
		textX = x + 4;
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		textY = y + getHeight() - (getHeight() + font.lineHeight) / 2;
		selectionY = textY - 1;
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		Tooltip tooltip = super.getTooltip(mouseX, mouseY);

		if (tooltip.isEmpty() && !isFocused() && visibleText.length() < fullText.length()) {
			tooltip = Tooltip.of(TextUtils.toLines(font, fullText));
		}

		return tooltip;
	}

	@Override
	public void update() {
		if (!isFocused() && textSupplier != null) {
			setText(textSupplier.get(), false);
			updateVisibleText();
		}
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		textWidth = width - 8;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
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
		int color = getTextColor();
		renderText(font, poses, visibleText, textX, textY, true, color);

		if (isFocused()) {
			if (isActive()) {
				if (suggestionIndex >= 0) {
					int suggestionColor = getSuggestionColor();
					renderText(font, poses, visibleSuggestion, textX + font.width(visibleText), textY, true, suggestionColor);
				}
				if ((cursorTicks / 6) % 2 == 0) {
					if (cursorIndex == fullText.length()) {
						int x = textX + font.width(visibleText);
						renderText(font, poses, "_", x, textY, true, color);
					} else {
						int width = font.width(fullText.substring(scrollIndex, cursorIndex));
						int x = textX + width;

						renderRect(poses, x, selectionY, 1, selectionHeight, color);
					}
				}
			}
			if (hasSelection()) {
				drawSelectionHighlight(poses);
			}
		}
	}

	private void drawSelectionHighlight(PoseStack poses) {
		int start = Math.min(cursorIndex, selectionIndex);
		int end = Math.max(cursorIndex, selectionIndex);

		int x0 = textX;
		int x1 = textX + textWidth;

		if (start >= scrollIndex) {
			String t = fullText.substring(scrollIndex, start);
			x0 = textX + font.width(t);
		}
		if (end <= scrollIndex + visibleText.length()) {
			String t = fullText.substring(scrollIndex, end);
			x1 = textX + font.width(t);
		}

		if (x0 >= x1) {
			return;
		}

		int y0 = selectionY;
		int y1 = selectionY + selectionHeight;
		int z = 0;

		RenderSystem.color4f(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		Matrix4f pose = poses.last().pose();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);

		bufferBuilder.vertex(pose, x0, y0, z).endVertex();
		bufferBuilder.vertex(pose, x0, y1, z).endVertex();
		bufferBuilder.vertex(pose, x1, y1, z).endVertex();
		bufferBuilder.vertex(pose, x1, y0, z).endVertex();

		tessellator.end();

		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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

	private int getTextColor() {
		return isActive() ? 0xFFFFFFFF : 0xFFB0B0B0;
	}

	private int getSuggestionColor() {
		return 0xFF808080;
	}

	public String getText() {
		return fullText;
	}

	public void clear() {
		replace("", 0, fullText.length());
	}

	private void write(String text) {
		if (!isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(selectionIndex, cursorIndex);
			int end = Math.max(selectionIndex, cursorIndex);

			replace(text, start, end);
		} else {
			insert(text, cursorIndex);
		}
	}

	private void erase(boolean forward) {
		if (!isActive()) {
			return;
		}

		if (hasSelection()) {
			int start = Math.min(selectionIndex, cursorIndex);
			int end = Math.max(selectionIndex, cursorIndex);

			replace("", start, end);
		} else {
			if (forward) {
				replace("", cursorIndex, cursorIndex + 1);
			} else if (cursorIndex > 0) {
				replace("", cursorIndex - 1, cursorIndex);
			}
		}
	}

	private void useSuggestion() {
		replace(suggestion, 0, fullText.length());
	}

	private void insert(String text, int index) {
		replace(text, index, index);
	}

	private void replace(String text, int start, int end) {
		String t0 = fullText.substring(0, start);
		String t1 = fullText.substring(end);

		setText(t0 + text + t1, true);
		setCursor(start + text.length());
	}

	private boolean setText(String text, boolean updateListener) {
		if (fullText.equals(text) || text.length() > maxLength) {
			return false;
		}

		fullText = text;

		updateMaxScroll();
		updateVisibleText();

		selectionIndex = -1;
		selection = SelectType.NONE;

		if (updateListener) {
			listener.accept(fullText);
		}

		return true;
	}

	private void copyTextToClipboard(boolean erase) {
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

	private void updateSuggestions() {
		if (!isFocused() || !isActive() || cursorIndex < fullText.length() || selection != SelectType.NONE) {
			suggestions = Collections.emptyList();
		} else {
			suggestions = suggestionsProvider.provide(fullText);
		}

		setSuggestion(0);
	}

	private void updateVisibleText() {
		visibleText = font.plainSubstrByWidth(fullText.substring(scrollIndex), textWidth, false);

		if (!suggestion.isEmpty()) {
			visibleSuggestion = font.plainSubstrByWidth(suggestion.substring(visibleText.length()), textWidth - font.width(visibleText), false);
		} else {
			visibleSuggestion = "";
		}
	}

	private void moveSuggestion(int amount) {
		int nextIndex = suggestionIndex + amount;

		if (nextIndex < 0) {
			nextIndex = suggestions.size() - 1;
		}
		if (nextIndex >= suggestions.size()) {
			nextIndex = 0;
		}

		setSuggestion(nextIndex);
	}

	private void setSuggestion(int index) {
		if (suggestions.isEmpty()) {
			suggestionIndex = -1;
			suggestion = "";
		} else {
			suggestionIndex = Mth.clamp(index, 0, suggestions.size() - 1);
			suggestion = suggestions.get(suggestionIndex);
		}

		updateVisibleText();
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	private void updateMaxScroll() {
		maxScroll = 0;

		if (!fullText.isEmpty()) {
			String text = font.plainSubstrByWidth(fullText + "_", textWidth + 1, true);

			if (text.length() <= fullText.length()) {
				maxScroll = fullText.length() - text.length() + 1;
			}
		}

		setCursor(cursorIndex);
	}

	private void scroll(int amount) {
		setScroll(scrollIndex + amount);
	}

	private void setScroll(int scroll) {
		int prevScroll = scrollIndex;
		scrollIndex = Mth.clamp(scroll, 0, maxScroll);

		if (scrollIndex != prevScroll) {
			updateVisibleText();
		}
	}

	private void setCursorFromMouse(double mouseX) {
		if (selection != SelectType.KEYBOARD) {
			String text = font.plainSubstrByWidth(visibleText, (int)mouseX - textX);
			setCursor(scrollIndex + text.length());
		}
	}

	private void moveCursorFromKeyboard(int amount) {
		setCursorFromKeyboard(cursorIndex + amount);
	}

	private void setCursorFromKeyboard(int index) {
		if (selection != SelectType.MOUSE) {
			setCursor(index);
		}
	}

	private void moveCursor(int amount) {
		setCursor(cursorIndex + amount);
	}

	private void setCursor(int index) {
		cursorIndex = Mth.clamp(index, 0, fullText.length());
		onCursorMoved();
	}

	private void onCursorMoved() {
		updateSuggestions();

		if (!isSelecting()) {
			selectionIndex = -1;
		}

		if (cursorIndex == fullText.length()) {
			setScroll(maxScroll);
			return;
		}
		if (cursorIndex < scrollIndex) {
			scroll(cursorIndex - scrollIndex);
			return;
		}

		int max = scrollIndex + visibleText.length();

		if (cursorIndex > max) {
			scroll(cursorIndex - max);
		}
	}

	private boolean isDoubleClick() {
		return cursorTicks >= 0 && cursorTicks < Options.Miscellaneous.DOUBLE_CLICK_TIME.get();
	}

	private boolean hasSelection() {
		return selectionIndex >= 0 && selectionIndex != cursorIndex;
	}

	private String getSelection() {
		int start = Math.min(selectionIndex, cursorIndex);
		int end = Math.max(selectionIndex, cursorIndex);

		return fullText.substring(start, end);
	}

	private void startSelecting(SelectType type) {
		if (selection == SelectType.NONE) {
			selection = type;

			if (selectionIndex < 0) {
				selectionIndex = cursorIndex;
			}
		}
	}

	private void stopSelecting(SelectType type) {
		if (selection == type) {
			selection = SelectType.NONE;
		}
	}

	private void selectAll() {
		setCursor(fullText.length());
		selectionIndex = 0;
	}

	private boolean isSelecting() {
		return selection != SelectType.NONE;
	}

	private enum SelectType {
		NONE, MOUSE, KEYBOARD
	}
}
