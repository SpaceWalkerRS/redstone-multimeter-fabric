package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.util.registry.Registry;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.util.TextUtils;

public class BlockListElement extends SelectableScrollableListElement {

	private final ItemRenderer itemRenderer;
	private final TextRenderer textRenderer;
	private final Consumer<Identifier> selectionListener;

	public BlockListElement(MultimeterClient client, int width, int height, Consumer<Identifier> selector) {
		this(client, width, height, 0, 0, selector);
	}

	public BlockListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder, Consumer<Identifier> selectionListener) {
		super(client, width, height, topBorder, bottomBorder);

		Minecraft minecraft = this.client.getMinecraft();

		this.itemRenderer = minecraft.getItemRenderer();
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


			Block block = Registry.BLOCK.get(key);

			if (block == null) {
				this.icon = null;
			} else {
				this.icon = new ItemStack(block.asItem());
			}

			this.key = key;
		}

		@Override
		public void render(int mouseX, int mouseY) {
			int height = getHeight();
			int x = getX() + 2;
			int y = getY() + (height - 16) / 2;

			if (icon != null) {
				Lighting.turnOnGui();
				GlStateManager.enableTexture();

				itemRenderer.renderGuiItem(icon, x, y);
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
