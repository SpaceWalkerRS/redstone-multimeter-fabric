package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.input.CharacterEvent;
import redstone.multimeter.client.gui.element.input.KeyEvent;
import redstone.multimeter.client.gui.element.input.MouseEvent;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.Tooltips;

public class BlockSelectorList extends SelectorList {

	private final Consumer<ResourceLocation> selectionListener;

	public BlockSelectorList(int width, int height, Consumer<ResourceLocation> selectionListener) {
		this(width, height, 0, 0, selectionListener);
	}

	public BlockSelectorList(int width, int height, int topBorder, int bottomBorder, Consumer<ResourceLocation> selectionListener) {
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

	public void add(ResourceLocation key) {
		this.addChild(new BlockListEntry(this.getEffectiveWidth(), Button.DEFAULT_HEIGHT, key));
	}

	public void add(Collection<ResourceLocation> keys) {
		for (ResourceLocation key : keys) {
			this.add(key);
		}
	}

	public void setBlockFilter(Predicate<ResourceLocation> filter) {
		this.setFilter(e -> {
			if (e instanceof BlockListEntry) {
				BlockListEntry entry = (BlockListEntry)e;
				return filter.test(entry.key);
			}

			return false;
		});
	}

	private class BlockListEntry extends AbstractElement {

		private final ResourceLocation key;
		private final ItemStack icon;

		protected BlockListEntry(int width, int height, ResourceLocation key) {
			super(0, 0, width, height);

			Block block = BuiltInRegistries.BLOCK.getOptional(key).orElse(null);

			if (block == null) {
				this.icon = null;
			} else {
				this.icon = new ItemStack(block.asItem());
			}

			this.key = key;
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
		public boolean mouseDrag(MouseEvent.Drag event) {
			return false;
		}

		@Override
		public boolean mouseScroll(MouseEvent.Scroll event) {
			return false;
		}

		@Override
		public boolean keyPress(KeyEvent.Press event) {
			return false;
		}

		@Override
		public boolean keyRelease(KeyEvent.Release event) {
			return false;
		}

		@Override
		public boolean typeChar(CharacterEvent.Type event) {
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
