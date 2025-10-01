package redstone.multimeter.client.gui.screen;

import redstone.multimeter.client.gui.element.ScrollableList;
import redstone.multimeter.client.gui.element.SimpleList;
import redstone.multimeter.client.gui.element.Label;
import redstone.multimeter.client.gui.element.button.BasicButton;
import redstone.multimeter.client.gui.element.button.Button;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltips;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickPhaseTree.Node;

public class TickPhaseTreeScreen extends RSMMScreen {

	private TickPhaseTree tickPhaseTree;
	private long lastRequestTime;

	public TickPhaseTreeScreen() {
		super(Texts.translatable("rsmm.gui.tickPhases.title"), true);

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

		if (!tickPhaseTree.isComplete()) {
			request();

			Text text = Texts.translatable("rsmm.gui.tickPhases.requesting");
			int x = getX() + (getWidth() - font.width(text)) / 2;
			int y = getY() + (getHeight() - font.height()) / 2;
			Label textElement = new Label(x, y, t -> t.addLine(text).setShadow(true), () -> {
				if (lastRequestTime > 0) {
					long time = System.currentTimeMillis();
					long delta = (time - lastRequestTime) / 1000;

					return Tooltips.split(Texts.translatable("rsmm.gui.tickPhases.requesting.tooltip", delta));
				}

				return Tooltips.empty();
			}, (t, e) -> {
				request();
				return true;
			});

			x = getX() + (getWidth() - Button.DEFAULT_WIDTH) / 2;
			y = getY() + getHeight() - (8 + Button.DEFAULT_HEIGHT);

			Button done = new BasicButton(x + 4, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiDone, Tooltips::empty, (button, event) -> {
				close();
				return true;
			});

			addChild(textElement);
			addChild(done);
		} else {
			int top = 10 + (Button.DEFAULT_HEIGHT + 2);
			int bottom = 18 + (Button.DEFAULT_HEIGHT + 2);
			int x = getX();
			int y = getY();

			SimpleList list = new ScrollableList(getWidth(), getHeight(), top, bottom);

			list.setDrawBackground(true);
			list.setX(x);
			list.setY(y);

			Label text = new Label(0, 0, t -> addTextForTickPhase(t, tickPhaseTree.root, ""));
			list.add(text);

			x = getX() + getWidth() / 2;
			y = getY() + getHeight() - (Button.DEFAULT_HEIGHT + 8);

			Button rebuild = new BasicButton(x - (4 + Button.DEFAULT_WIDTH), y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, () -> Texts.translatable("rsmm.gui.tickPhases.refresh"), Tooltips::empty, (button, event) -> {
				rebuild();
				return true;
			});
			Button done = new BasicButton(x + 4, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Texts::guiDone, Tooltips::empty, (button, event) -> {
				close();
				return true;
			});

			addChild(list);
			addChild(rebuild);
			addChild(done);
		}
	}

	@Override
	protected boolean hasTransparentBackground() {
		return false;
	}

	private void request() {
		client.requestTickPhaseTree();
		lastRequestTime = System.currentTimeMillis();
	}

	private void rebuild() {
		client.rebuildTickPhaseTree();
		close();
	}

	private void addTextForTickPhase(Label text, Node node, String indent) {
		boolean root = (node.parent == null);

		text.addLine(Texts.composite(
			indent + (root ? "" : "__"),
			node.task.getName(),
			" ",
			String.join(" ", node.args)
		));

		if (!root) {
			indent += "   ";
		}

		indent += "|";

		for (int index = 0; index < node.children.size(); index++) {
			addTextForTickPhase(text, node.children.get(index), indent);
		}
	}

	public void refresh() {
		tickPhaseTree = client.getTickPhaseTree();
		init(getWidth(), getHeight());
	}
}
