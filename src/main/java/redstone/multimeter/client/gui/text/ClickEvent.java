package redstone.multimeter.client.gui.text;

import java.util.Objects;

public class ClickEvent {

	private final Action action;
	private final String value;

	private ClickEvent(Action action, String value) {
		this.action = action;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ClickEvent)) {
			return false;
		}
		ClickEvent event = (ClickEvent) o;
		return this.action == event.action && Objects.equals(this.value, event.value);
	}

	public Action getAction() {
		return this.action;
	}

	public String getValue() {
		return this.value;
	}

	public net.minecraft.text.ClickEvent resolve() {
		switch (this.action) {
		case OPEN_URL:
			return new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.OPEN_URL, this.value);
		case RUN_COMMAND:
			return new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, this.value);
		case SUGGEST_COMMAND:
			return new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND, this.value);
		case COPY_TO_CLIPBOARD:
// :(
//			return new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.COPY_TO_CLIPBOARD, this.value);
			return null;
		}

		throw new IllegalStateException("unable to resolve click event " + this.action.name());
	}

	public static ClickEvent resolve(net.minecraft.text.ClickEvent clickEvent) {
		switch (clickEvent.getAction()) {
		case OPEN_URL:
			return openUrl(clickEvent.getValue());
		case RUN_COMMAND:
			return runCommand(clickEvent.getValue());
		case SUGGEST_COMMAND:
			return suggestCommand(clickEvent.getValue());
// :(
//		case COPY_TO_CLIPBOARD:
//			return copyToClipboard(clickEvent.getValue());
		default:
			return null;
		}
	}

	public static ClickEvent openUrl(String url) {
		return new ClickEvent(Action.OPEN_URL, url);
	}

	public static ClickEvent runCommand(String command) {
		return new ClickEvent(Action.RUN_COMMAND, command);
	}

	public static ClickEvent suggestCommand(String command) {
		return new ClickEvent(Action.SUGGEST_COMMAND, command);
	}

	public static ClickEvent copyToClipboard(String text) {
		return new ClickEvent(Action.COPY_TO_CLIPBOARD, text);
	}

	public static enum Action {

		OPEN_URL, RUN_COMMAND, SUGGEST_COMMAND, COPY_TO_CLIPBOARD

	}
}
