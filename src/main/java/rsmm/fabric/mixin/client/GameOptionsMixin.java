package rsmm.fabric.mixin.client;

import java.io.File;
import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import rsmm.fabric.client.KeyBindings;
import rsmm.fabric.client.MultimeterClient;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	
	@Shadow @Final @Mutable private KeyBinding[] keysAll;
	@Shadow private MinecraftClient client;
	
	@Inject(
			method = "load",
			at = @At(
					value = "HEAD"
			)
	)
	private void onLoadInjectAtHead(CallbackInfo ci) {
		Collection<KeyBinding> rsmmKeyBindings = KeyBindings.getKeyBindings();
		KeyBinding[] mcKeyBindings = keysAll;
		
		keysAll = new KeyBinding[mcKeyBindings.length + rsmmKeyBindings.size()];
		int index = 0;
		
		for (int i = 0; i < mcKeyBindings.length; i++) {
			keysAll[index++] = mcKeyBindings[i];
		}
		for (KeyBinding key : rsmmKeyBindings) {
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
		KeyBindings.load(new File(client.runDirectory, MultimeterClient.CONFIG_PATH));
	}
	
	@Inject(
			method = "write",
			at = @At(
					value = "HEAD"
			)
	)
	private void saveOptions(CallbackInfo ci) {
		KeyBindings.save(new File(client.runDirectory, MultimeterClient.CONFIG_PATH));
	}
}
