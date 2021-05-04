package rsmm.fabric.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import rsmm.fabric.client.MultimeterClient;

public abstract class RSMMScreen extends Screen implements IParentElement {
	
	public final MultimeterClient multimeterClient;
	private final List<IElement> content;
	
	private IElement focused;
	
	protected RSMMScreen(MultimeterClient multimeterClient) {
		super(new LiteralText(""));
		
		this.multimeterClient = multimeterClient;
		this.content = new ArrayList<>();
	}
	
	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		mouseMove(mouseX, mouseY);
	}
	
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		return mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		return mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return mouseScroll(mouseX, mouseY, amount);
	}
	
	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return keyPress(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean charTyped(char chr, int modifiers) {
		return typeChar(chr, modifiers);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		renderContent(matrices, mouseX, mouseY, delta);
		
		List<List<Text>> tooltip = getTooltip(mouseX, mouseY);
		
		if (!tooltip.isEmpty()) {
			drawTooltip(matrices, tooltip, mouseX, mouseY + 15);
		}
	}
	
	@Override
	protected final void init() {
		clearChildren();
		initScreen();
	}
	
	protected abstract void initScreen();
	
	@Override
	public void tick() {
		IParentElement.super.tick();
	}
	
	@Override
	public void removed() {
		onRemoved();
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		if (IParentElement.super.keyPress(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			onClose();
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<IElement> getChildren() {
		return content;
	}
	
	@Override
	public IElement getFocusedElement() {
		return focused;
	}
	
	@Override
	public void setFocusedElement(IElement element) {
		IElement focused = getFocusedElement();
		
		if (element == focused) {
			return;
		}
		
		if (focused != null) {
			focused.unfocus();
		}
		
		this.focused = element;
		
		if (element != null) {
			element.focus();
		}
	}
	
	@Override
	public final int getX() {
		return 0;
	}
	
	@Override
	public final void setX(int x) {
		
	}
	
	@Override
	public final int getY() {
		return 0;
	}
	
	@Override
	public final void setY(int y) {
		
	}
	
	@Override
	public final int getWidth() {
		return width;
	}
	
	@Override
	public final void setWidth(int width) {
		
	}
	
	@Override
	public final int getHeight() {
		return height;
	}
	
	@Override
	public final void setHeight(int height) {
		
	}
	
	protected void addContent(IElement element) {
		content.add(element);
	}
	
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		IParentElement.super.render(matrices, mouseX, mouseY, delta);
	}
	
	public void drawTooltip(MatrixStack matrices, List<List<Text>> lines, int x, int y) {
		if (lines.isEmpty()) {
			return;
		}
		
		int width = 0;
		
		for (List<Text> line : lines) {
			int lineWidth = 0;
			
			for (Text text : line) {
				lineWidth += textRenderer.getWidth(text);
			}
			
			if (lineWidth > width) {
				width = lineWidth;
			}
		}
		
		int left = x + 12;
		int top = y - 12;
		
		int height = 8;
		
		if (lines.size() > 1) {
			height += 2 + 10 * (lines.size() - 1);
		}
		
		if (left + width > getX() + getWidth()) {
			left -= (28 + width);
		}
		if (top + height + 6 > getY() + getHeight()) {
			top = (getY() + getHeight()) - height - 6;
		}
		
		int z = 400;
		
		int backgroundColor  = 0xF0100010;
		int borderColorStart = 0x505000FF;
		int borderColorEnd   = 0x5028007F;
		
		matrices.push();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		
		fillGradient(model, bufferBuilder, left - 3        , top - 4         , left + width + 3, top - 3         , z, backgroundColor, backgroundColor);
		fillGradient(model, bufferBuilder, left - 3        , top + height + 3, left + width + 3, top + height + 4, z, backgroundColor, backgroundColor);
		fillGradient(model, bufferBuilder, left - 3        , top - 3         , left + width + 3, top + height + 3, z, backgroundColor, backgroundColor);
		fillGradient(model, bufferBuilder, left - 4        , top - 3         , left - 3        , top + height + 3, z, backgroundColor, backgroundColor);
		fillGradient(model, bufferBuilder, left + width + 3, top - 3         , left + width + 4, top + height + 3, z, backgroundColor, backgroundColor);
		fillGradient(model, bufferBuilder, left - 3        , top - 2         , left - 2        , top + height + 2, z, borderColorStart, borderColorEnd);
		fillGradient(model, bufferBuilder, left + width + 2, top - 2         , left + width + 3, top + height + 2, z, borderColorStart, borderColorEnd);
		fillGradient(model, bufferBuilder, left - 3        , top - 3         , left + width + 3, top - 2         , z, borderColorStart, borderColorStart);
		fillGradient(model, bufferBuilder, left - 3        , top + height + 2, left + width + 3, top + height + 3, z, borderColorEnd, borderColorEnd);
		
		bufferBuilder.end();
		
		BufferRenderer.draw(bufferBuilder);
		
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		
		matrices.translate(0.0D, 0.0D, 400.0D);
		
		int textX;
		int textY = top;
		
		for (List<Text> line : lines) {
			textX = left;
			
			for (Text text : line) {
				textRenderer.draw(text, textX, textY, -1, true, model, immediate, false, 0, 15728880);
				
				textX += textRenderer.getWidth(text);
			}
			
			textY += 10;
		}
		
		matrices.pop();
		
		immediate.draw();
	}
}
