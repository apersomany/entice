package dev.aperso.entice.mixin;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixins {
	@Inject(method = "reloadShaders", at = @At("HEAD"))
	public void onReloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
		try {
			RangeIndicatorDecal.Client.onReloadShaders(resourceProvider);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
