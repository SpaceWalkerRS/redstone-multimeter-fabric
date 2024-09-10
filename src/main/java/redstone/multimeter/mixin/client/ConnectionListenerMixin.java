package redstone.multimeter.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ConnectionListener;

import redstone.multimeter.interfaces.mixin.IConnection;

@Mixin(ConnectionListener.class)
public class ConnectionListenerMixin implements IConnection {

	@Shadow private List<Connection> connections;

	@Override
	public void rsmm$handleRsmmPackets() {
		synchronized (connections) {
			for (Connection connection : connections) {
				if (connection.isConnected()) {
					((IConnection) connection).rsmm$handleRsmmPackets();
				}
			}
		}
	}
}
