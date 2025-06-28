package redstone.multimeter.client.gui.text;

import java.util.Objects;

public class HoverEvent {

	private final Action action;
	private final Object value;

	private HoverEvent(Action action, Object value) {
		this.action = action;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof HoverEvent)) {
			return false;
		}
		HoverEvent event = (HoverEvent) o;
		return this.action == event.action && Objects.equals(this.value, event.value);
	}

	public Action getAction() {
		return this.action;
	}

	public <T> T getValue() {
		return (T) this.value;
	}
	
	public net.minecraft.network.chat.HoverEvent resolve() {
		switch (this.action) {
		case SHOW_TEXT:
			return new net.minecraft.network.chat.HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, ((Text) this.value).resolve());
		}

		throw new IllegalStateException("unable to resolve click event " + this.action.name());
	}

	public static HoverEvent resolve(net.minecraft.network.chat.HoverEvent hoverEvent) {
		if (hoverEvent.getAction() == net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT) {
			return showText(Texts.resolve(hoverEvent.getValue()));
		}

		return null;
	}

	public static HoverEvent showText(Text text) {
		return new HoverEvent(Action.SHOW_TEXT, text);
	}

	public static enum Action {

		SHOW_TEXT

	}
}
