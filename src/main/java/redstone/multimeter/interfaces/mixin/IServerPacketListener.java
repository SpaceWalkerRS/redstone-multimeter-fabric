package redstone.multimeter.interfaces.mixin;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IServerPacketListener {

	boolean rsmm$handleCustomPayload(CustomPacketPayload payload);

}
