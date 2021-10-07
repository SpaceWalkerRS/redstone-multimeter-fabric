package rsmm.fabric.client.gui.element.controls;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.element.SimpleTextElement;
import rsmm.fabric.client.gui.element.TextElement;
import rsmm.fabric.client.gui.widget.Button;
import rsmm.fabric.client.gui.widget.ButtonFactory;
import rsmm.fabric.client.gui.widget.IButton;

public class ControlElement extends AbstractParentElement {
	
	protected static final int RESET_WIDTH = 50;
	
	protected final MultimeterClient client;
	protected final int midpoint;
	
	protected final TextElement name;
	protected final IButton control;
	protected final IButton reset;
	
	public ControlElement(MultimeterClient client, int midpoint, int controlWidth, Supplier<Text> name, Supplier<List<Text>> tooltip, ButtonFactory control) {
		this(client, midpoint, controlWidth, name, tooltip, control, null, null);
	}
	
	public ControlElement(MultimeterClient client, int midpoint, int controlWidth, Supplier<Text> name, Supplier<List<Text>> tooltip, ButtonFactory control, Supplier<Boolean> isReset, Runnable resetter) {
		super(0, 0, midpoint + 4 + controlWidth + 10 + (resetter == null ? 0 : RESET_WIDTH), IButton.DEFAULT_HEIGHT);
		
		this.client = client;
		this.midpoint = midpoint;
		
		this.name = new SimpleTextElement(this.client, 0, 0, true, name, tooltip, t -> false, t -> false);
		this.control = control.create(this.client, controlWidth, IButton.DEFAULT_HEIGHT);
		this.reset = resetter == null ? null : createReset(isReset, resetter);
		
		addChild(this.name);
		addChild(this.control);
		if (this.reset != null) {
			addChild(this.reset);
		}
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	protected void onChangedX(int x) {
		x += midpoint;
		
		name.setX(x - 2 - name.getWidth());
		control.setX(x + 2);
		if (reset != null) {
			reset.setX(control.getX() + control.getWidth() + 10);
		}
	}
	
	@Override
	protected void onChangedY(int y) {
		name.setY(y + 6);
		control.setY(y);
		if (reset != null) {
			reset.setY(y);
		}
	}
	
	private IButton createReset(Supplier<Boolean> isReset, Runnable resetter) {
		return new Button(client, 0, 0, RESET_WIDTH, IButton.DEFAULT_HEIGHT, () -> new TranslatableText("controls.reset"), button -> {
			resetter.run();
			return true;
		}) {
			
			@Override
			public void update() {
				setActive(!isReset.get());
			}
		};
	}
}
