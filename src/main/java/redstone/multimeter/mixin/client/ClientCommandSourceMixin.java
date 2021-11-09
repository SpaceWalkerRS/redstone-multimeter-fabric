package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IClientCommandSource;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements IClientCommandSource {
	
	@Shadow @Final private MinecraftClient client;
	
	@Override
	public MultimeterClient getMultimeterClient() {
		return ((IMinecraftClient)client).getMultimeterClient();
	}
}
