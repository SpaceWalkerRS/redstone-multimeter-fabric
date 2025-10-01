package redstone.multimeter.client.gui.element.input;

import net.minecraft.util.StringUtil;

public class CharacterEvent {

	private final char character;
	private final int modifiers;

	CharacterEvent(char character, int modifiers) {
		this.character = character;
		this.modifiers = modifiers;
	}

	public int character() {
		return this.character;
	}

	public String characterAsString() {
		return Character.toString(this.character);
	}

	public boolean characterAllowedInChat() {
		return StringUtil.isAllowedChatCharacter(this.character);
	}

	public int modifiers() {
		return this.modifiers;
	}

	public static CharacterEvent.Type type(char character, int modifiers) {
		return new Type(character, modifiers);
	}

	public static class Type extends CharacterEvent {

		Type(char character, int modifiers) {
			super(character, modifiers);
		}
	}
}
