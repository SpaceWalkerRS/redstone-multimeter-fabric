package redstone.multimeter.client.gui.element;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.screen.RSMMScreen;

public class SimpleListElement extends AbstractParentElement {
	
	protected static final int BORDER_MARGIN_TOP = 6;
	protected static final int BORDER_MARGIN_BOTTOM = 3;
	protected static final Collator COLLATOR = Collator.getInstance();
	
	protected final MultimeterClient client;
	protected final int topBorder;
	protected final int bottomBorder;
	
	private int spacing;
	private int height;
	private int minY;
	private int maxY;
	private boolean drawBackground;
	
	private Comparator<IElement> sorter;
	private Predicate<IElement> filter;
	
	private boolean shouldUpdateHovered;
	
	public SimpleListElement(MultimeterClient client, int width) {
		this(client, width, 0, 0);
	}
	
	public SimpleListElement(MultimeterClient client, int width, int topBorder, int bottomBorder) {
		this.client = client;
		this.topBorder = topBorder - BORDER_MARGIN_TOP;
		this.bottomBorder = bottomBorder - BORDER_MARGIN_BOTTOM;
		
		this.spacing = 2;
		
		this.sorter = null;
		this.filter = e -> true;
		
		setWidth(width);
	}
	
	@Override
	public void render(int mouseX, int mouseY) {
		if (shouldUpdateHovered) {
			updateHoveredElement(mouseX, mouseY);
			shouldUpdateHovered = false;
		}
		
		if (drawBackground) {
			drawBackground();
		}
		
		renderList(mouseX, mouseY);
		
		if (drawBackground) {
			drawBorders();
		}
	}
	
	@Override
	public boolean isHovered(double mouseX, double mouseY) {
		// ignore when the mouse is hovering over the borders
		return mouseX >= getX() && mouseX <= (getX() + getWidth()) && mouseY >= minY && mouseY <= maxY;
	}
	
	@Override
	public int getHeight() {
		// return the height minus the borders
		return height;
	}
	
	@Override
	public void update() {
		sort();
		updateCoords();
		super.update();
	}
	
	@Override
	protected void onChangedX(int x) {
		updateContentX();
	}
	
	@Override
	protected void onChangedY(int y) {
		updateContentY();
	}
	
	protected void drawBackground() {
		int x0 = getX();
		int y0 = getY() + topBorder;
		int x1 = getX() + getWidth();
		int y1 = getY() + getTotalHeight() - bottomBorder;
		
		int offsetY = getOffsetY();
		
		int tx0 = x0 / 2;
		int ty0 = (y0 - offsetY) / 2;
		int tx1 = x1 / 2;
		int ty1 = (y1 - offsetY) / 2;
		
		renderTextureColor(Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x20, 0x20, 0x20);
	}
	
	protected void renderList(int mouseX, int mouseY) {
		List<IElement> children = getChildren();
		
		for (int index = 0; index < children.size(); index++) {
			IElement element = children.get(index);
			
			if (element.getY() + element.getHeight() < minY) {
				continue;
			}
			if (element.getY() > maxY) {
				break;
			}
			
			if (element.isVisible()) {
				renderElement(element, mouseX, mouseY);
			}
		}
	}
	
	protected void renderElement(IElement element, int mouseX, int mouseY) {
		element.render(mouseX, mouseY);
	}
	
	protected void drawBorders() {
		boolean renderTop = topBorder > 0;
		boolean renderBottom = bottomBorder > 0;
		
		if (!renderTop && !renderBottom) {
			return;
		}
		
		int x = getX();
		int width = getWidth();
		
		int x0 = x;
		int y0;
		int x1 = x + width;
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
			
			renderTextureColor(Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
			renderGradient(x0, y1, width, 4, 0xFF000000, 0x00000000);
		}
		if (renderBottom) {
			y0 = maxY;
			y1 = maxY + bottomBorder;
			
			ty0 = y0 / 2;
			ty1 = y1 / 2;
			
			renderTextureColor(Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
			renderGradient(x0, y0 - 4, width, 4, 0x00000000, 0xFF000000);
		}
	}
	
	public int getTotalHeight() {
		return getHeight() + (topBorder + BORDER_MARGIN_TOP) + (bottomBorder + BORDER_MARGIN_BOTTOM);
	}
	
	protected int getTotalSpacing() {
		return spacing * (getChildren().size() - 1);
	}
	
	protected int getAmountOffScreen(IElement element) {
		int top = element.getY();
		
		if (top < minY) {
			return top - minY - 1;
		}
		
		int bottom = top + element.getHeight();
		
		if (bottom > maxY) {
			return bottom - maxY + 1;
		}
		
		return 0;
	}
	
	public int getEffectiveWidth() {
		return getWidth();
	}
	
	private void sort() {
		if (sorter == null) {
			return;
		}
		
		List<IElement> children = getChildren();
		Queue<IElement> queue = new PriorityQueue<>(sorter);
		
		for (int index = 0; index < children.size(); index++) {
			queue.add(children.get(index));
		}
		
		children.clear();
		
		while (!queue.isEmpty()) {
			children.add(queue.poll());
		}
	}
	
	protected void updateContentX() {
		List<IElement> children = getChildren();
		int x = getX();
		
		for (int index = 0; index < children.size(); index++) {
			children.get(index).setX(x);
		}
	}
	
	protected void updateContentY() {
		List<IElement> children = getChildren();
		int yStart = getY() + topBorder + BORDER_MARGIN_TOP + getOffsetY();
		int y = yStart;
		
		for (int index = 0; index < children.size(); index++) {
			IElement element = children.get(index);
			
			element.setY(y);
			element.setVisible(filter.test(element));
			
			if (element.isVisible()) {
				y += element.getHeight() + spacing;
			}
		}
		
		RSMMScreen screen = client.getScreen();
		
		height = (y - spacing) - yStart;
		minY = Math.max(getY() + topBorder, screen.getY());
		maxY = Math.min(getY() + getTotalHeight() - bottomBorder, screen.getY() + screen.getHeight());
		
		shouldUpdateHovered = true;
	}
	
	protected int getOffsetY() {
		return 0;
	}
	
	public boolean shouldDrawBackground() {
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
	
	public void setSorter(Comparator<IElement> sorter) {
		this.sorter = sorter;
	}
	
	public void setFilter(Predicate<IElement> filter) {
		if (filter == null) {
			filter = e -> true;
		}
		
		this.filter = filter;
	}
}
