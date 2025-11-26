package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;

import redstone.multimeter.interfaces.mixin.IKeyMapping;

@Mixin(KeyMapping.class)
public class KeyMappingMixin implements IKeyMapping {

	@Shadow private Key key;

	@Override
	public Key rsmm$getKey() {
		return key;
	}
}
