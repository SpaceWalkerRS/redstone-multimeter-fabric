package redstone.multimeter.client.gui.element;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

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
		int x0 = getX();
		int y0 = getY() + topBorder;
		int x1 = getX() + getWidth();
		int y1 = getY() + getTotalHeight() - bottomBorder;
		
		int offsetY = getOffsetY();
		
		int tx0 = x0 / 2;
		int ty0 = (y0 - offsetY) / 2;
		int tx1 = x1 / 2;
		int ty1 = (y1 - offsetY) / 2;
		
		drawTextureColor(matrices, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x20, 0x20, 0x20);
	}
	
	protected void drawBorders(MatrixStack matrices) {
		boolean renderTop = topBorder > 0;
		boolean renderBottom = bottomBorder > 0;
		
		int x0 = getX();
		int y0;
		int x1 = getX() + getWidth();
		int y1;
		
		int tx0 = x0 / 2;
		int ty0;
		int tx1 = x1 / 2;
		int ty1;
		
		if (renderTop) {
			y0 = minY - topBorder;
			y1 = minY;
			
			ty0 = y0 / 2;
			ty1 = y1 / 2;
			
			drawTextureColor(matrices, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
		}
		if (renderBottom) {
			y0 = maxY;
			y1 = maxY + bottomBorder;
			
			ty0 = y0 / 2;
			ty1 = y1 / 2;
			
			drawTextureColor(matrices, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
		}
		
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableTexture();
		RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
		
		y0 = minY;
		y1 = maxY;
		int z = 0;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		bufferBuilder.vertex(x0, y0 + 4, z).color(0x0, 0x0, 0x0, 0x0 ).next();
		bufferBuilder.vertex(x1, y0 + 4, z).color(0x0, 0x0, 0x0, 0x0 ).next();
		bufferBuilder.vertex(x1, y0    , z).color(0x0, 0x0, 0x0, 0xFF).next();
		bufferBuilder.vertex(x0, y0    , z).color(0x0, 0x0, 0x0, 0xFF).next();
		
		bufferBuilder.vertex(x0, y1    , z).color(0x0, 0x0, 0x0, 0xFF).next();
		bufferBuilder.vertex(x1, y1    , z).color(0x0, 0x0, 0x0, 0xFF).next();
		bufferBuilder.vertex(x1, y1 - 4, z).color(0x0, 0x0, 0x0, 0x0 ).next();
		bufferBuilder.vertex(x0, y1 - 4, z).color(0x0, 0x0, 0x0, 0x0 ).next();
		
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
