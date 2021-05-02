package rsmm.fabric.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class HexColorArgumentType implements ArgumentType<Integer> {
	
	private HexColorArgumentType() {
		
	}
	
	public static HexColorArgumentType color() {
		return new HexColorArgumentType();
	}
	
	public static <S> Integer getColor(CommandContext<S> context, String name) {
		return context.getArgument(name, Integer.class);
	}
	
	@Override
	public Integer parse(StringReader reader) throws CommandSyntaxException {
		String asString = reader.readString();
		
		if (asString.length() > 6) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
		}
		
		while (asString.length() < 6) {
			asString += 0;
		}
		
		try {
			int r = Integer.valueOf(asString.substring(0, 2), 16);
			int g = Integer.valueOf(asString.substring(2, 4), 16);
			int b = Integer.valueOf(asString.substring(4, 6), 16);
			
			return 0xFF000000 | (r << 16) | (g << 8) | b;
		} catch (Exception e) {
			throw new DynamicCommandExceptionType(value -> new LiteralMessage(String.format("Invalid color \'%s\'", value))).createWithContext(reader, asString);
		}
	}
}
