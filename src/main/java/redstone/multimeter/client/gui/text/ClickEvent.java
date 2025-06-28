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
