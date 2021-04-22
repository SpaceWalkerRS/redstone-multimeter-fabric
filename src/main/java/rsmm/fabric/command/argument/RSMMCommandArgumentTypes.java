package rsmm.fabric.command.argument;

import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;

public class RSMMCommandArgumentTypes {
	
	public static void register() {
		ArgumentTypes.register("rsmm-fabric:event_type", MeterEventArgumentType.class, new ConstantArgumentSerializer<>(MeterEventArgumentType::eventType));
	}
}
