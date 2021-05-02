package rsmm.fabric.command.argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;

import rsmm.fabric.common.event.EventType;

public class MeterEventArgumentType implements ArgumentType<EventType> {
	
	private static final Collection<String> TYPE_NAMES = new ArrayList<>();
	
	static {
		for (EventType type : EventType.TYPES) {
			TYPE_NAMES.add(type.getName());
		}
	}
	
	private MeterEventArgumentType() {
		
	}
	
	public static MeterEventArgumentType eventType() {
		return new MeterEventArgumentType();
	}
	
	public static <S> EventType getEventType(CommandContext<S> context, String name) {
		return context.getArgument(name, EventType.class);
	}
	
	@Override
	public EventType parse(StringReader reader) throws CommandSyntaxException {
		String name = StringArgumentType.word().parse(reader);
		EventType type = EventType.fromName(name);
		
		if (type == null) {
			throw new DynamicCommandExceptionType(value -> new LiteralMessage(String.format("Unknown event type \'%s\'", value))).createWithContext(reader, name);
		}
		
		return type;
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(TYPE_NAMES, builder);
	}
}
