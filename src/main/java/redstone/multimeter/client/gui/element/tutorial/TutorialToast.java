package redstone.multimeter.client.gui.element.tutorial;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class TutorialToast implements Toast {
	
	protected static final int TEXTURE_U = 0;
	protected static final int TEXTURE_V = 96;
	protected static final int TEXTURE_WIDTH = 160;
	protected static final int TEXTURE_HEIGHT = 32;
	protected static final int EDGE = 4;
	protected static final int INNER_WIDTH = TEXTURE_WIDTH - 2 * EDGE;
	protected static final int INNER_HEIGHT = TEXTURE_HEIGHT - 2 * EDGE;
	
	private final Text title;
	private final List<OrderedText> description;
	
	private final int toastWidth;
	private final int toastHeight;
	
	private Visibility visibility;
	
	public TutorialToast(Text title, Text description) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer font = client.textRenderer;
		
		this.toastWidth = 200;
		
		this.title = title;
		this.description = font.wrapLines(description, getWidth() - 14);
		
		this.toastHeight = 10 + 12 + 10 * this.description.size();
		
		this.visibility = Visibility.SHOW;
	}
	
	@Override
	public int getWidth() {
		return toastWidth;
	}
	
	@Override
	public int getHeight() {
		return toastHeight;
	}
	
	@Override
	public Visibility draw(MatrixStack matrices, ToastManager manager, long age) {
		drawBackground(matrices, manager, age);
		
		MinecraftClient client = manager.getClient();
		TextRenderer font = client.textRenderer;
		
		float x = 7.0F;
		float y = 7.0F;
		
		font.draw(matrices, title, x, y, 0xFF500050);
		
		y += 12.0F;
		
		for (int i = 0; i < description.size(); i++, y += 10.0F) {
			font.draw(matrices, description.get(i), x, y, 0xFF000000);
		}
		
		drawDecoration(matrices, manager, age);
		
		return visibility;
	}
	
	protected void drawBackground(MatrixStack matrices, ToastManager manager, long age) {
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		int width = getWidth();
		int height = getHeight();
		
		if (width == TEXTURE_WIDTH && height == TEXTURE_HEIGHT) {
			manager.drawTexture(matrices, 0, 0, TEXTURE_U, TEXTURE_V, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		} else {
			int x = 0;
			int y = 0;
			int u = TEXTURE_U;
			int v = TEXTURE_V;
			int w = EDGE;
			int h = EDGE;
			
			manager.drawTexture(matrices, x, y, u, v, w, h); // top left corner
			
			u += EDGE;
			w = INNER_WIDTH;
			
			for (x = EDGE; x < (width - w - EDGE); x += w) {
				manager.drawTexture(matrices, x, y, u, v, w, h); // top edge
			}
			
			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;
			
			manager.drawTexture(matrices, x, y, u, v, w, h); // top right corner
			
			v += EDGE;
			h = INNER_HEIGHT;
			
			for (y = EDGE; y < (height - h - EDGE); y += h) {
				x = 0;
				u = TEXTURE_U;
				w = EDGE;
				
				manager.drawTexture(matrices, x, y, u, v, w, h); // left edge
				
				u += EDGE;
				w = INNER_WIDTH;
				
				for (x = EDGE; x < (width - w - EDGE); x += w) {
					manager.drawTexture(matrices, x, y, u, v, w, h); // middle
				}
				
				w = width - x;
				u = TEXTURE_U + TEXTURE_WIDTH - w;
				
				manager.drawTexture(matrices, x, y, u, v, w, h); // right edge
			}
			
			h = height - y;
			v = TEXTURE_V + TEXTURE_HEIGHT - h;
			
			x = 0;
			u = TEXTURE_U;
			w = EDGE;
			
			manager.drawTexture(matrices, x, y, u, v, w, h); // bottom left corner
			
			u += EDGE;
			w = INNER_WIDTH;
			
			for (x = EDGE; x < (width - w - EDGE); x += w) {
				manager.drawTexture(matrices, x, y, u, v, w, h); // bottom edge
			}
			
			w = width - x;
			u = TEXTURE_U + TEXTURE_WIDTH - w;
			
			manager.drawTexture(matrices, x, y, u, v, w, h); // bottom right corner
		}
	}
	
	protected void drawDecoration(MatrixStack matrices, ToastManager manager, long age) {
		
	}
	
	public void hide() {
		visibility = Visibility.HIDE;
	}
}
