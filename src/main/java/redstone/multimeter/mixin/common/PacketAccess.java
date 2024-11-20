package redstone.multimeter.mixin.common;

import java.io.DataOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;

@Mixin(Packet.class)
public interface PacketAccess {

	@Invoker("writeCompound")
	static void rsmm$writeCompound(NbtCompound nbt, DataOutput output) {
		throw new UnsupportedOperationException();
	}
}
