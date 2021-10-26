package redstone.multimeter.client.gui.hud.element;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.MeterLogs;

public class SecondaryEventViewer extends MeterEventViewer {
	
	public SecondaryEventViewer(MultimeterHud hud) {
		super(hud);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (hud.isPaused() && getColumnCount() > 0) {
			super.render(matrices, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public List<Text> getTooltip(int mouseX, int mouseY) {
		if (isHovered(mouseX, mouseY)) {
			int row = hud.getHoveredRow(mouseY);
			Meter meter = hud.meters.get(row);
			long tick = hud.getSelectedTick();
			int subtick = getHoveredColumn(mouseX);
			
			MeterLogs logs = meter.getLogs();
			MeterEvent event = logs.getLogAt(tick, subtick);
			
			if (event != null && meter.isMetering(event.getType())) {
				return event.getTextForTooltip();
			}
		}
		
		return super.getTooltip(mouseX, mouseY);
	}
	
	@Override
	protected void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		if (isHovered(mouseX, mouseY)) {
			int column = getHoveredColumn(mouseX);
			int row = hud.getHoveredRow(mouseY);
			
			drawHighlight(matrices, column, 1, row, 1, false);
		}
	}
	
	@Override
	protected void drawDecorators(MatrixStack matrices) {
		
	}
	
	@Override
	protected void drawMeterEvents(MatrixStack matrices) {
		long tick = hud.getSelectedTick();
		int subticks = hud.client.getMeterGroup().getLogManager().getSubTickCount(tick);
		
		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderSubtickLogs(matrices, x, y, tick, subticks, meter);
		});
	}
	
	@Override
	protected int getColumnCount() {
		if (hud.isPaused()) {
			return hud.client.getMeterGroup().getLogManager().getSubTickCount(hud.getSelectedTick());
		}
		
		return 0;
	}
}
