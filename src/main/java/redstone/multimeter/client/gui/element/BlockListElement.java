package redstone.multimeter.client.gui.element;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.util.TextUtils;

public class BlockListElement extends SelectableScrollableListElement {
	
	private final RenderItem itemRenderer;
	private final FontRenderer textRenderer;
	private final Consumer<ResourceLocation> selectionListener;
	
	public BlockListElement(MultimeterClient client, int width, int height, Consumer<ResourceLocation> selector) {
		this(client, width, height, 0, 0, selector);
	}
	
	public BlockListElement(MultimeterClient client, int width, int height, int topBorder, int bottomBorder, Consumer<ResourceLocation> selectionListener) {
		super(client, width, height, topBorder, bottomBorder);
		
		Minecraft minecraftClient = this.client.getMinecraftClient();
		
		this.itemRenderer = minecraftClient.getRenderItem();
		this.textRenderer = minecraftClient.fontRenderer;
		this.selectionListener = selectionListener;
		
		setSorter((o1, o2) -> {
			if (o1 instanceof BlockListEntry && o2 instanceof BlockListEntry) {
				BlockListEntry e1 = (BlockListEntry)o1;
				BlockListEntry e2 = (BlockListEntry)o2;
				
				return COLLATOR.compare(e1.blockId.toString(), e2.blockId.toString());
			}
			
			return 0;
		});
	}
	
	@Override
	protected void selectionChanged(IElement element) {
		super.selectionChanged(element);
		
		if (element instanceof BlockListEntry) {
			BlockListEntry entry = (BlockListEntry)element;
			selectionListener.accept(entry.blockId);
		}
	}
	
	public void add(ResourceLocation blockId) {
		addChild(new BlockListEntry(getEffectiveWidth(), IButton.DEFAULT_HEIGHT, blockId));
	}
	
	public void add(Collection<ResourceLocation> blockIds) {
		for (ResourceLocation blockId : blockIds) {
			add(blockId);
		}
	}
	
	public void setBlockFilter(Predicate<ResourceLocation> filter) {
		setFilter(e -> {
			if (e instanceof BlockListEntry) {
				BlockListEntry entry = (BlockListEntry)e;
				return filter.test(entry.blockId);
			}
			
			return false;
		});
	}
	
	private class BlockListEntry extends AbstractElement {
		
		private final ResourceLocation blockId;
		private final Block block;
		private final ItemStack stack;
		
		protected BlockListEntry(int width, int height, ResourceLocation blockId) {
			super(0, 0, width, height);
			
			this.blockId = blockId;
			this.block = Block.REGISTRY.getObject(blockId);
			
			if (this.block == null) {
				this.stack = null;
			} else {
				this.stack = new ItemStack(this.block);
			}
		}
		
		@Override
		public void render(int mouseX, int mouseY) {
			int height = getHeight();
			int x = getX() + 2;
			int y = getY() + (height - 16) / 2;
			
			RenderHelper.enableGUIStandardItemLighting();
			itemRenderer.renderItemIntoGUI(stack, x, y);
			RenderHelper.disableStandardItemLighting();
			
			x = getX() + 22;
			y = getY() + height - (height + textRenderer.FONT_HEIGHT) / 2;
			String text = textRenderer.trimStringToWidth(blockId.toString(), getWidth() - 22);
			
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
				String rawId = blockId.toString();
				
				if (textRenderer.getStringWidth(rawId) > (getWidth() - 22)) {
					tooltip = Tooltip.of(TextUtils.toLines(textRenderer, rawId));
				}
			}
			
			return tooltip;
		}
		
		@Override
		public void update() {
			
		}
	}
}
