package redstone.multimeter.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.LocalConnection;
import net.minecraft.server.network.ConnectionListener;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;

import redstone.multimeter.interfaces.mixin.IConnection;

@Mixin(ConnectionListener.class)
public class ConnectionListenerMixin implements IConnection {

	@Shadow private List<ServerPlayNetworkHandler> connections;

	@Override
	public void rsmm$handleRsmmPackets() {
		synchronized (connections) {
			for (ServerPlayNetworkHandler handler : connections) {
				if (handler != null && handler.connection != null) {
					if (handler.connection instanceof LocalConnection && ((LocalConnection)handler.connection).isOpen()) {
						((IConnection)handler.connection).rsmm$handleRsmmPackets();
					}
				}
			}
		}
	}
}
