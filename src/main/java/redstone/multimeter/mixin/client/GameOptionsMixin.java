package redstone.multimeter.mixin.client;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

	@Shadow @Final @Mutable private KeyBinding[] keyBindings;
	@Shadow private Minecraft minecraft;

	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/options/GameOptions;load()V"
		)
	)
	private void init(Minecraft minecraft, File file, CallbackInfo ci) {
		Collection<KeyBinding> rsmmKeys = Keybinds.getKeybinds();

		KeyBinding[] mcKeys = keyBindings;
		keyBindings = new KeyBinding[mcKeys.length + rsmmKeys.size()];

		int index = 0;
		for (int i = 0; i < mcKeys.length; i++) {
			keyBindings[index++] = mcKeys[i];
		}
		for (KeyBinding key : rsmmKeys) {
			keyBindings[index++] = key;
		}
	}

	@Inject(
		method = "load",
		at = @At(
			value = "TAIL"
		)
	)
	private void load(CallbackInfo ci) {
		Path dir = MultimeterClient.getConfigDirectory(minecraft);

		Keybinds.load(dir);
		Options.load(dir);
	}

	@Inject(
		method = "save",
		at = @At(
			value = "HEAD"
		)
	)
	private void save(CallbackInfo ci) {
		Path dir = MultimeterClient.getConfigDirectory(minecraft);

		Keybinds.save(dir);
		Options.save(dir);
	}
}
