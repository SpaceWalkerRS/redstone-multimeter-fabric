package redstone.multimeter.mixin.client;

import java.io.File;
import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.option.Options;

@Mixin(GameSettings.class)
public class GameSettingsMixin {
	
	@Shadow private KeyBinding[] keyBindings;
	@Shadow private Minecraft mc;
	
	private boolean initialized;
	
	@Inject(
			method = "loadOptions",
			at = @At(
					value = "HEAD"
			)
	)
	private void initOptions(CallbackInfo ci) {
		if (initialized) {
			return;
		}
		
		Collection<KeyBinding> rsmmKeys = KeyBindings.getKeyBindings();
		
		KeyBinding[] mcKeys = keyBindings;
		keyBindings = new KeyBinding[mcKeys.length + rsmmKeys.size()];
		
		int index = 0;
		for (int i = 0; i < mcKeys.length; i++) {
			keyBindings[index++] = mcKeys[i];
		}
		for (KeyBinding key : rsmmKeys) {
			keyBindings[index++] = key;
		}
		
		initialized = true;
	}
	
	@Inject(
			method = "loadOptions",
			at = @At(
					value = "RETURN"
			)
	)
	private void loadOptions(CallbackInfo ci) {
		File folder = MultimeterClient.getConfigFolder(mc);
		
		KeyBindings.load(folder);
		Options.load(folder);
	}
	
	@Inject(
			method = "saveOptions",
			at = @At(
					value = "HEAD"
			)
	)
	private void saveOptions(CallbackInfo ci) {
		File folder = MultimeterClient.getConfigFolder(mc);
		
		KeyBindings.save(folder);
		Options.save(folder);
	}
}
