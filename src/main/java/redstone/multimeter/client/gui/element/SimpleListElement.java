package redstone.multimeter.client.gui.element;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import redstone.multimeter.client.MultimeterClient;

public class SimpleListElement extends AbstractParentElement {
	
	protected static final int BORDER_MARGIN_TOP = 6;
	protected static final int BORDER_MARGIN_BOTTOM = 3;
	
	protected final MultimeterClient client;
	protected final int topBorder;
	protected final int bottomBorder;
	
	private int spacing;
	private int height;
	private int minY;
	private int maxY;
	private boolean drawBackground;
	
	public SimpleListElement(MultimeterClient client, int width) {
		this(client, width, 0, 0);
	}
	
	public SimpleListElement(MultimeterClient client, int width, int topBorder, int bottomBorder) {
		super(0, 0, width, 0);
		
		this.client = client;
		this.topBorder = topBorder - BORDER_MARGIN_TOP;
		this.bottomBorder = bottomBorder - BORDER_MARGIN_BOTTOM;
		
		this.spacing = 2;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (drawBackground) {
			drawBackground(matrices);
		}
		
		for (IElement element : getChildren()) {
			if (element.getY() + element.getHeight() < minY) {
				continue;
			}
			if (element.getY() > maxY) {
				break;
			}
			
			if (element.isVisible()) {
				element.render(matrices, mouseX, mouseY, delta);
			}
		}
		
		if (drawBackground) {
			drawBorders(matrices);
		}
	}
	
	@Override
	public boolean isHovered(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= minY && mouseY <= maxY;
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public void onChangedX(int x) {
		updateContentX();
	}
	
	@Override
	public void onChangedY(int y) {
		updateContentY();
	}
	
	protected void drawBackground(MatrixStack matrices) {
		int width = getWidth();
		int height = getTotalHeight();
		int left = getX();
		int right = left + width;
		int top = getY() + topBorder;
		int bottom = getY() + height - bottomBorder;
		int offsetY = getOffsetY();
		int z = 0;
		
		RenderSystem.setShader(() -> GameRenderer.getPositionTexColorShader());
		RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		
		bufferBuilder.vertex(left , bottom, z).texture(left  / 32.0F, (bottom - offsetY) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(right, bottom, z).texture(right / 32.0F, (bottom - offsetY) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(right, top   , z).texture(right / 32.0F, (top    - offsetY) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(left , top   , z).texture(left  / 32.0F, (top    - offsetY) / 32.0F).color(32, 32, 32, 255).next();
		
		tessellator.draw();
	}
	
	protected void drawBorders(MatrixStack matrices) {
		boolean renderTop = topBorder > 0;
		boolean renderBottom = bottomBorder > 0;
		
		if (!renderTop && !renderBottom) {
			return;
		}
		
		int width = getWidth();
		int height = getTotalHeight();
		int left = getX();
		int right = getX() + width;
		int top = minY;
		int bottom = maxY;
		int z = -100;
		
		RenderSystem.setShader(() -> GameRenderer.getPositionTexColorShader());
		RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		
		if (renderTop) {
			bufferBuilder.vertex(left , top, z            ).texture(0.0F         , top / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(right, top, z            ).texture(width / 32.0F, top / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(right, top - topBorder, z).texture(width / 32.0F, 0.0F       ).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(left , top - topBorder, z).texture(0.0F         , 0.0F       ).color(64, 64, 64, 255).next();
		}
		if (renderBottom) {
			bufferBuilder.vertex(left , bottom + bottomBorder, z).texture(0.0F         , height / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(right, bottom + bottomBorder, z).texture(width / 32.0F, height / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(right, bottom               , z).texture(width / 32.0F, bottom / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(left , bottom               , z).texture(0.0F         , bottom / 32.0F).color(64, 64, 64, 255).next();
		}
		
		tessellator.draw();
		
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableTexture();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		z = 0;
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		if (renderTop) {
			bufferBuilder.vertex(left , top + 4, z).color(0, 0, 0, 0  ).next();
			bufferBuilder.vertex(right, top + 4, z).color(0, 0, 0, 0  ).next();
			bufferBuilder.vertex(right, top    , z).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(left , top    , z).color(0, 0, 0, 255).next();
		}
		if (renderBottom) {
			bufferBuilder.vertex(left , bottom    , z).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(right, bottom    , z).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(right, bottom - 4, z).color(0, 0, 0, 0  ).next();
			bufferBuilder.vertex(left , bottom - 4, z).color(0, 0, 0, 0  ).next();
		}
		
		tessellator.draw();
	}
	
	protected int getTotalHeight() {
		return getHeight() + (topBorder + BORDER_MARGIN_TOP) + (bottomBorder + BORDER_MARGIN_BOTTOM);
	}
	
	protected int getTotalSpacing() {
		return spacing * (getChildren().size() - 1);
	}
	
	protected void updateContentX() {
		int x = getX();
		
		for (IElement element : getChildren()) {
			element.setX(x);
		}
	}
	
	protected void updateContentY() {
		int yStart = getY() + topBorder + BORDER_MARGIN_TOP + getOffsetY();
		int y = yStart;
		
		for (IElement element : getChildren()) {
			element.setY(y);
			y += element.getHeight() + spacing;
		}
		
		RSMMScreen screen = client.getScreen();
		
		height = (y - spacing) - yStart;
		minY = Math.max(getY() + topBorder, screen.getY());
		maxY = Math.min(getY() + getTotalHeight() - bottomBorder, screen.getY() + screen.getHeight());
	}
	
	protected int getOffsetY() {
		return 0;
	}
	
	public boolean isDrawingBackground() {
		return drawBackground;
	}
	
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}
	
	public void add(IElement element) {
		addChild(element);
	}
	
	public void addAll(Collection<? extends IElement> elements) {
		for (IElement element : elements) {
			addChild(element);
		}
	}
	
	public void clear() {
		removeChildren();
		updateCoords();
	}
	
	public void updateCoords() {
		updateContentX();
		updateContentY();
	}
	
	public void setSpacing(int spacing) {
		if (spacing < 0) {
			spacing = 0;
		}
		
		this.spacing = spacing;
	}
}
