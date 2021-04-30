package rsmm.fabric.command.argument;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

import rsmm.fabric.RedstoneMultimeterMod;

public class RSMMArgumentTypes {
	
	public static void register() {
		ArgumentTypes.register(String.format("%s:hex_color_argument", RedstoneMultimeterMod.MOD_ID), HexColorArgumentType.class, new ConstantArgumentSerializer<>(HexColorArgumentType::color));
		ArgumentTypes.register(String.format("%s:event_type_argument", RedstoneMultimeterMod.MOD_ID), MeterEventArgumentType.class, new ConstantArgumentSerializer<>(MeterEventArgumentType::eventType));
	}
}
