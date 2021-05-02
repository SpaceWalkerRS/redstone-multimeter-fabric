package rsmm.fabric.client.gui.element.meter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.element.AbstractParentElement;
import rsmm.fabric.client.gui.element.IElement;
import rsmm.fabric.client.gui.widget.TextField;
import rsmm.fabric.common.Meter;

public class MeterControlsElement extends AbstractParentElement {
	
	private final MultimeterClient client;
	private final List<IElement> children;
	private final TextRenderer font;
	
	private int x;
	private int y;
	private int width;
	
	private IElement focused;
	
	private int meterIndex;
	private Meter meter;
	
	private TextField nameField;
	
	public MeterControlsElement(MultimeterClient client, int x, int y, int width) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.children = new ArrayList<>();
		this.font = minecraftClient.textRenderer;
		
		this.x = x;
		this.y = y;
		this.width = width;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		
		Text title = new LiteralText(String.format("Controls For Meter #%d (%s)", meterIndex, meter.getName()));
		int textWidth = font.getWidth(title);
		int x = getX() + (getWidth() - textWidth) / 2;
		
		font.drawWithShadow(matrices, title, x, getY(), 0);
	}
	
	@Override
	public List<IElement> getChildren() {
		return children;
	}
	
	@Override
	public IElement getFocusedElement() {
		return focused;
	}
	
	@Override
	public void setFocusedElement(IElement element) {
		focused = element;
	}
	
	@Override
	public void focus() {
		
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return 0;
	}
	
	@Override
	public void setHeight(int height) {
		
	}
	
	public int getSelectedMeter() {
		return meterIndex;
	}
	
	public boolean selectMeter(int index) {
		meterIndex = index;
		
		Meter oldMeter = meter;
		meter = client.getMeterGroup().getMeter(meterIndex);
		
		return oldMeter != meter;
	}
}
