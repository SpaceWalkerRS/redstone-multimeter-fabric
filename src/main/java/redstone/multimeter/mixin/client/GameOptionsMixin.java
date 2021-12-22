package redstone.multimeter.mixin.client;

import java.io.File;
import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	
	@Shadow @Final @Mutable private KeyBinding[] keysAll;
	@Shadow private MinecraftClient client;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/client/options/GameOptions;load()V"
			)
	)
	private void initOptions(MinecraftClient client, File optionsFile, CallbackInfo ci) {
		Collection<KeyBinding> rsmmKeys = KeyBindings.getKeyBindings();
		
		KeyBinding[] mcKeys = keysAll;
		keysAll = new KeyBinding[mcKeys.length + rsmmKeys.size()];
		
		int index = 0;
		for (int i = 0; i < mcKeys.length; i++) {
			keysAll[index++] = mcKeys[i];
		}
		for (KeyBinding key : rsmmKeys) {
			keysAll[index++] = key;
		}
	}
	
	@Inject(
			method = "load",
			at = @At(
					value = "RETURN"
			)
	)
	private void loadOptions(CallbackInfo ci) {
		File folder = MultimeterClient.getConfigFolder(client);
		
		KeyBindings.load(folder);
		Options.load(folder);
	}
	
	@Inject(
			method = "save",
			at = @At(
					value = "HEAD"
			)
	)
	private void saveOptions(CallbackInfo ci) {
		File folder = MultimeterClient.getConfigFolder(client);
		
		KeyBindings.save(folder);
		Options.save(folder);
	}
}
