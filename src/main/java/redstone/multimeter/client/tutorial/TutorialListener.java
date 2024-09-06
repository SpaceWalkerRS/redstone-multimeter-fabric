package redstone.multimeter.client.tutorial;

import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.Meter;

public interface TutorialListener {

	default void onScreenOpened(Screen screen) {
	}

	default void onScreenOpened(RSMMScreen screen) {
	}

	default void onToggleHud(boolean enabled) {
	}

	default void onToggleFocusMode(boolean enabled) {
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

	default void onMeterAddRequested(DimPos pos) {
	}

	default void onMeterAdded(Meter meter) {
	}

	default void onMeterRemoveRequested(DimPos pos) {
	}

	default void onMeterRemoved(Meter meter) {
	}
}
