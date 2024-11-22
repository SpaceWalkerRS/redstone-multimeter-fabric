package redstone.multimeter.mixin.common;

import java.io.DataOutputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;

@Mixin(Packet.class)
public interface PacketAccess {

	@Invoker("writeCompound")
	static void rsmm$writeCompound(NbtCompound nbt, DataOutputStream output) {
		throw new UnsupportedOperationException();
	}
}
