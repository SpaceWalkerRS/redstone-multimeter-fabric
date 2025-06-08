package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.util.TextUtils;

public class BlockListElement extends SelectableScrollableListElement {

	private final ItemRenderer itemRenderer;
	private final Font font;
	private final Consumer<ResourceLocation> selectionListener;

	public BlockListElement(MultimeterClient client, int width, int height, Consumer<ResourceLocation> selector) {
		this(client, width, height, 0, 0, selector);
	}

	public BlockListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder, Consumer<ResourceLocation> selectionListener) {
		super(client, width, height, topBorder, bottomBorder);

		Minecraft minecraft = this.client.getMinecraft();

		this.itemRenderer = minecraft.getItemRenderer();
		this.font = minecraft.font;
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

	public void add(ResourceLocation key) {
		addChild(new BlockListEntry(getEffectiveWidth(), IButton.DEFAULT_HEIGHT, key));
	}

	public void add(Collection<ResourceLocation> keys) {
		for (ResourceLocation key : keys) {
			add(key);
		}
	}

	public void setBlockFilter(Predicate<ResourceLocation> filter) {
		setFilter(e -> {
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


			Block block = Registry.BLOCK.get(key);

			if (block == null) {
				this.icon = null;
			} else {
				this.icon = new ItemStack(block.asItem());
			}

			this.key = key;
		}

		@Override
		public void render(PoseStack poses, int mouseX, int mouseY) {
			int height = getHeight();
			int x = getX() + 2;
			int y = getY() + (height - 16) / 2;

			if (icon != null) {
				itemRenderer.renderGuiItem(icon, x, y);
			}

			x = getX() + 22;
			y = getY() + height - (height + font.lineHeight) / 2;
			String text = font.plainSubstrByWidth(key.toString(), getWidth() - 22);

			renderText(font, poses, text, x, y, true, 0xFFFFFFFF);
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
		public boolean keyPress(int keyCode, int scanCode, int modifiers) {
			return false;
		}

		@Override
		public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
			return false;
		}

		@Override
		public boolean typeChar(char chr, int modifiers) {
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

				if (font.width(keyString) > (getWidth() - 22)) {
					tooltip = Tooltip.of(TextUtils.toLines(font, keyString));
				}
			}

			return tooltip;
		}

		@Override
		public void update() {
		}
	}
}
