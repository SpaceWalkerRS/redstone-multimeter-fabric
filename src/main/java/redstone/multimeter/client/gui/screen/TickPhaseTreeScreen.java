package redstone.multimeter.client.gui.screen;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.ScrollableListElement;
import redstone.multimeter.client.gui.element.SimpleListElement;
import redstone.multimeter.client.gui.element.TextElement;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.element.button.IButton;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickPhaseTree.TickTaskNode;

public class TickPhaseTreeScreen extends RSMMScreen {
	
	private TickPhaseTree tickPhaseTree;
	private long lastRequestTime;
	
	public TickPhaseTreeScreen(MultimeterClient client) {
		super(client, new LiteralText("Tick Phase Tree"), true);
		
		this.tickPhaseTree = this.client.getTickPhaseTree();
		this.lastRequestTime = -1;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (lastRequestTime > 0 && System.currentTimeMillis() - lastRequestTime > 30000) {
			request();
		}
	}
	
	@Override
	protected void initScreen() {
		lastRequestTime = -1;
		
		if (tickPhaseTree == null || !tickPhaseTree.isComplete()) {
			request();
			
			String text = "Requesting tick phase tree from server...";
			int x = getX() + (getWidth() - font.getStringWidth(text)) / 2;
			int y = getY() + (getHeight() - font.fontHeight) / 2;
			TextElement textElement = new TextElement(client, x, y, t -> t.add(text).setWithShadow(true), () -> {
				if (lastRequestTime > 0) {
					long time = System.currentTimeMillis();
					long delta = (time - lastRequestTime) / 1000;
					
					return Tooltip.of(String.format("Last request was %d seconds ago", delta));
				}
				
				return Tooltip.EMPTY;
			}, t -> { request(); return true; });
			
			addChild(textElement);
		} else {
			int top = 10 + (IButton.DEFAULT_HEIGHT + 2);
			int bottom = 18 + (IButton.DEFAULT_HEIGHT + 2);
			int x = getX();
			int y = getY();
			
			SimpleListElement list = new ScrollableListElement(client, getWidth(), getHeight(), top, bottom);
			
			list.setDrawBackground(true);
			list.setX(x);
			list.setY(y);
			
			TextElement text = new TextElement(client, 0, 0, t -> addTextForTickPhase(t, tickPhaseTree.root, ""));
			list.add(text);
			
			addChild(list);
		}
		
		int x = getX() + (getWidth() - IButton.DEFAULT_WIDTH) / 2;
		int y = getY() + getHeight() - (8 + IButton.DEFAULT_HEIGHT);
		
		IButton done = new Button(client, x + 4, y, IButton.DEFAULT_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("gui.done"), () -> Tooltip.EMPTY, button -> {
			close();
			return true;
		});
		addChild(done);
	}
	
	@Override
	protected boolean hasTransparentBackground() {
		return false;
	}
	
	private void request() {
		client.requestTickPhaseTree();
		lastRequestTime = System.currentTimeMillis();
	}
	
	private void addTextForTickPhase(TextElement text, TickTaskNode node, String indent) {
		if (node.parent == null) { // root
			text.add(" Server Run Loop").setWithShadow(false);
			indent = " |";
		} else {
			text.add(indent + "__ " + node.task.getName() + " " + String.join(" ", node.args));
			indent += "   |";
		}
		
		for (int index = 0; index < node.children.size(); index++) {
			addTextForTickPhase(text, node.children.get(index), indent);
		}
	}
	
	public void refresh() {
		tickPhaseTree = client.getTickPhaseTree();
		init(getWidth(), getHeight());
	}
}
