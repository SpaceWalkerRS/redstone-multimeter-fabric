package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.util.Blocks;
import redstone.multimeter.util.Items;

public class BlockSelectorList extends SelectorList {

	private static final Random RANDOM = new Random();

	private final Consumer<String> selectionListener;

	public BlockSelectorList(int width, int height, Consumer<String> selectionListener) {
		this(width, height, 0, 0, selectionListener);
	}

	public BlockSelectorList(int width, int height, int topBorder, int bottomBorder, Consumer<String> selectionListener) {
		super(width, height, topBorder, bottomBorder);

		this.selectionListener = selectionListener;

		this.setSorter((o1, o2) -> {
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
			this.selectionListener.accept(entry.key);
		}
	}

	public void add(String key) {
		this.addChild(new BlockListEntry(this.getEffectiveWidth(), Button.DEFAULT_HEIGHT, key));
	}

	public void add(Collection<String> keys) {
		for (String key : keys) {
			this.add(key);
		}
	}

	public void setBlockFilter(Predicate<String> filter) {
		this.setFilter(e -> {
			if (e instanceof BlockListEntry) {
				BlockListEntry entry = (BlockListEntry)e;
				return filter.test(entry.key);
			}

			return false;
		});
	}

	private class BlockListEntry extends AbstractElement {

		private final String key;
		private final ItemStack icon;

		protected BlockListEntry(int width, int height, String key) {
			super(0, 0, width, height);

			Integer block = Blocks.REGISTRY.get(key);
			Item item = null;

			if (block != null) {
				item = Item.BY_ID[block];

				// some blocks do not have direct a item counterpart
				// so to get the item, we try a few different things
				if (item == null) {
					// first check for an item with the same key
					item = Item.BY_ID[Items.REGISTRY.get(key)];
				}
				if (item == null) {
					// if nothing has worked yet, use the item dropped
					// by the block when broken
					item = Item.BY_ID[Block.BY_ID[block].getDropItem(0, RANDOM, 0)];
				}
			}

			this.key = key;
			this.icon = (item == null) ? null : new ItemStack(item);
		}

		@Override
		public void render(GuiRenderer renderer, int mouseX, int mouseY) {
			int height = this.getHeight();
			int x = this.getX() + 2;
			int y = this.getY() + (height - 16) / 2;

			if (this.icon != null) {
				renderer.renderItem(this.icon, x, y);
			}

			x = this.getX() + 22;
			y = this.getY() + height - (height + font.height()) / 2;
			String text = font.trim(this.key.toString(), getWidth() - 22);

			renderer.drawStringWithShadow(text, x, y);
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
				String keyString = this.key.toString();

				if (font.width(keyString) > (this.getWidth() - 22)) {
					tooltip = Tooltips.literal(keyString);
				}
			}

			return tooltip;
		}

		@Override
		public void update() {
		}
	}
}
