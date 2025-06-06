package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.resource.Identifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.util.TextUtils;

public class BlockListElement extends SelectableScrollableListElement {

	private static final Random RANDOM = new Random();

	private final TextureManager textureManager;
	private final ItemRenderer itemRenderer;
	private final TextRenderer textRenderer;
	private final Consumer<Identifier> selectionListener;

	public BlockListElement(MultimeterClient client, int width, int height, Consumer<Identifier> selector) {
		this(client, width, height, 0, 0, selector);
	}

	public BlockListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder, Consumer<Identifier> selectionListener) {
		super(client, width, height, topBorder, bottomBorder);

		Minecraft minecraft = this.client.getMinecraft();

		this.textureManager = minecraft.getTextureManager();
		this.itemRenderer = new ItemRenderer();
		this.textRenderer = minecraft.textRenderer;
		this.selectionListener = selectionListener;

		setSorter((o1, o2) -> {
			if (o1 instanceof BlockListEntry && o2 instanceof BlockListEntry) {
				BlockListEntry e1 = (BlockListEntry)o1;
				BlockListEntry e2 = (BlockListEntry)o2;

				return COLLATOR.compare(e1.key.toString(), e2.key.toString());
			}

			return 0;
		});
	}

	@Override
	protected void selectionChanged(Element element) {
		super.selectionChanged(element);

		if (element instanceof BlockListEntry) {
			BlockListEntry entry = (BlockListEntry)element;
			selectionListener.accept(entry.key);
		}
	}

	public void add(Identifier key) {
		addChild(new BlockListEntry(getEffectiveWidth(), IButton.DEFAULT_HEIGHT, key));
	}

	public void add(Collection<Identifier> keys) {
		for (Identifier key : keys) {
			add(key);
		}
	}

	public void setBlockFilter(Predicate<Identifier> filter) {
		setFilter(e -> {
			if (e instanceof BlockListEntry) {
				BlockListEntry entry = (BlockListEntry)e;
				return filter.test(entry.key);
			}

			return false;
		});
	}

	private class BlockListEntry extends AbstractElement {

		private final Identifier key;
		private final ItemStack icon;

		protected BlockListEntry(int width, int height, Identifier key) {
			super(0, 0, width, height);

			Block block = (Block) Block.REGISTRY.get(key.toString());
			Item item = null;

			if (block != null) {
				item = Item.byBlock(block);

				// some blocks do not have direct a item counterpart
				// so to get the item, we try a few different things
				if (item == null) {
					// first check for an item with the same key
					item = (Item) Item.REGISTRY.get(key.toString());
				}
				if (item == null) {
					// if nothing has worked yet, use the item dropped
					// by the block when broken
					item = block.getDropItem(0, RANDOM, 0);
				}
			}

			this.key = key;
			this.icon = (item == null) ? null : new ItemStack(item);
		}

		@Override
		public void render(int mouseX, int mouseY) {
			int height = getHeight();
			int x = getX() + 2;
			int y = getY() + (height - 16) / 2;

			if (icon != null) {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				itemRenderer.renderGuiItem(textRenderer, textureManager, icon, x, y);
				GL11.glDisable(GL11.GL_LIGHTING);
			}

			x = getX() + 22;
			y = getY() + height - (height + textRenderer.fontHeight) / 2;
			String text = textRenderer.trim(key.toString(), getWidth() - 22);

			renderText(textRenderer, text, x, y, true, 0xFFFFFFFF);
		}

		@Override
		public void mouseMove(double mouseX, double mouseY) {
		}

		@Override
		public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			return false;
		}

		@Override
		public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
			return false;
		}

		@Override
		public boolean keyPress(int keyCode) {
			return false;
		}

		@Override
		public boolean keyRelease(int keyCode) {
			return false;
		}

		@Override
		public boolean typeChar(char chr) {
			return false;
		}

		@Override
		public void onRemoved() {
		}

		@Override
		public void tick() {
		}

		@Override
		public Tooltip getTooltip(int mouseX, int mouseY) {
			Tooltip tooltip = super.getTooltip(mouseX, mouseY);

			if (tooltip.isEmpty()) {
				String keyString = key.toString();

				if (textRenderer.getWidth(keyString) > (getWidth() - 22)) {
					tooltip = Tooltip.of(TextUtils.toLines(textRenderer, keyString));
				}
			}

			return tooltip;
		}

		@Override
		public void update() {
		}
	}
}
