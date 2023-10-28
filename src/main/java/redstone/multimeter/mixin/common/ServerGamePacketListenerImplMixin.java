package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import redstone.multimeter.common.network.PacketWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerPacketListener;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl implements IServerPacketListener {

	@Shadow private ServerPlayer player;

	private ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
		super(server, connection, cookie);
	}

	@Override
	public boolean rsmm$handleCustomPayload(CustomPacketPayload packet) {
		if (packet instanceof PacketWrapper p) {
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(p, player);
			return true;
		}

		return false;
	}
}
