package redstone.multimeter.client.gui.element.input;

import net.minecraft.util.StringUtil;

public class CharacterEvent {

	private final int codePoint;
	private final int modifiers;

	CharacterEvent(int codePoint, int modifiers) {
		this.codePoint = codePoint;
		this.modifiers = modifiers;
	}

	public int codePoint() {
		return this.codePoint;
	}

	public String codePointAsString() {
		return Character.toString(this.codePoint);
	}

	public boolean codePointAllowedInChat() {
		return StringUtil.isAllowedChatCharacter(this.codePoint);
	}

	public int modifiers() {
		return this.modifiers;
	}

	public static CharacterEvent.Type type(int codePoint, int modifiers) {
		return new Type(codePoint, modifiers);
	}

	public static class Type extends CharacterEvent {

		Type(int codePoint, int modifiers) {
			super(codePoint, modifiers);
		}
	}
}
