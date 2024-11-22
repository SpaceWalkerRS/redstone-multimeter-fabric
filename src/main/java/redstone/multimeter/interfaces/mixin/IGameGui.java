package redstone.multimeter.interfaces.mixin;

public interface IGameGui {

	default void setOverlayMessage(String message, boolean tinted) {
		throw new UnsupportedOperationException();
	}
}
