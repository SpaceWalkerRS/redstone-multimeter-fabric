package redstone.multimeter.client.tutorial;

import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.meter.Meter;

public interface TutorialListener {
	
	default void onScreenOpened(RSMMScreen screen) {
		
	}
	
	default void onToggleHud(boolean enabled) {
		
	}
	
	default void onPauseHud(boolean paused) {
		
	}
	
	default void onScrollHud(int amount) {
		
	}
	
	default void onMeterControlsOpened() {
		
	}
	
	default void onJoinMeterGroup() {
		
	}
	
	default void onLeaveMeterGroup() {
		
	}
	
	default void onMeterGroupRefreshed() {
		
	}
	
	default void onMeterAddRequested(WorldPos pos) {
		
	}
	
	default void onMeterAdded(Meter meter) {
		
	}
	
	default void onMeterRemoveRequested(WorldPos pos) {
		
	}
	
	default void onMeterRemoved(Meter meter) {
		
	}
}
