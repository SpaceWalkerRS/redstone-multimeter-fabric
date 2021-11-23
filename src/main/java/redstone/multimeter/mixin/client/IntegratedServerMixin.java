package redstone.multimeter.mixin.client;

import java.io.File;
import java.net.Proxy;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer implements IMinecraftServer {
	
	@Shadow private boolean field_5524;
	
	public IntegratedServerMixin(File file, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, YggdrasilAuthenticationService yggdrasilAuthenticationService, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache) {
		super(file, proxy, dataFixer, commandManager, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/integrated/IntegratedServer;field_77592:Ljava/util/Queue;"
			)
	)
	private void onTickStart(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		// When the server is paused, the tick method is not called
		if (field_77592.isEmpty()) {
			getMultimeterServer().tickStart();
		}
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		// When the server is paused, the tick method is not called
		if (field_5524) {
			getMultimeterServer().tickEnd();
		}
	}
	
	@Override
	public boolean isPaused() {
		return field_5524;
	}
}
