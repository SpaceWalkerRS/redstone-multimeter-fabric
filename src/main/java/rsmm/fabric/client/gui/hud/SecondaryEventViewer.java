package rsmm.fabric.client.gui.hud;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

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
			
			if (row >= hud.meters.size()) {
				row = hud.meters.size() - 1;
			}
			
			Meter meter = hud.meters.get(row);
			long tick = hud.getSelectedTick();
			int subtick = getHoveredColumn(mouseX);
			
			MeterLogs logs = meter.getLogs();
			MeterEvent event = logs.getLastLogBefore(tick, subtick + 1);
			
			if (event != null && event.isAt(tick, subtick) && meter.isMetering(event.getType())) {
				return event.getTextForTooltip();
			}
		}
		
		return super.getTooltip(mouseX, mouseY);
	}
	
	@Override
	protected void drawHighlights(MatrixStack matrices, int mouseX, int mouseY) {
		
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
		return hud.client.getMeterGroup().getLogManager().getSubTickCount(hud.getSelectedTick());
	}
}
