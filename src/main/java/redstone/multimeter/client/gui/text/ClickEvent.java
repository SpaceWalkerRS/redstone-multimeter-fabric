package redstone.multimeter.client.gui.text;

import java.net.URI;
import java.util.Objects;

import net.minecraft.network.chat.ClickEvent.CopyToClipboard;
import net.minecraft.network.chat.ClickEvent.OpenUrl;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;

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

	public net.minecraft.network.chat.ClickEvent resolve() {
		switch (this.action) {
		case OPEN_URL:
			return new OpenUrl(URI.create(this.value));
		case RUN_COMMAND:
			return new RunCommand(this.value);
		case SUGGEST_COMMAND:
			return new SuggestCommand(this.value);
		case COPY_TO_CLIPBOARD:
			return new CopyToClipboard(this.value);
		}

		throw new IllegalStateException("unable to resolve click event " + this.action.name());
	}

	public static ClickEvent resolve(net.minecraft.network.chat.ClickEvent clickEvent) {
		switch (clickEvent.action()) {
		case OPEN_URL:
			return openUrl(((OpenUrl) clickEvent).uri().toString());
		case RUN_COMMAND:
			return runCommand(((RunCommand) clickEvent).command());
		case SUGGEST_COMMAND:
			return suggestCommand(((SuggestCommand) clickEvent).command());
		case COPY_TO_CLIPBOARD:
			return copyToClipboard(((CopyToClipboard) clickEvent).value());
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
