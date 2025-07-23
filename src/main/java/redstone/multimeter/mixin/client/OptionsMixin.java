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

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import redstone.multimeter.client.Keybinds;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;

@Mixin(net.minecraft.client.Options.class)
public class OptionsMixin {

	@Shadow @Final @Mutable private KeyMapping[] keyMappings;
	@Shadow private Minecraft minecraft;

	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Options;load()V"
		)
	)
	private void init(Minecraft minecraft, File file, CallbackInfo ci) {
		Collection<KeyMapping> rsmmKeys = Keybinds.getKeybinds();

		KeyMapping[] mcKeys = keyMappings;
		keyMappings = new KeyMapping[mcKeys.length + rsmmKeys.size()];

		int index = 0;
		for (int i = 0; i < mcKeys.length; i++) {
			keyMappings[index++] = mcKeys[i];
		}
		for (KeyMapping key : rsmmKeys) {
			keyMappings[index++] = key;
		}

		Keybinds.patchLegacyAmecsOptions(minecraft.gameDirectory.toPath());
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
