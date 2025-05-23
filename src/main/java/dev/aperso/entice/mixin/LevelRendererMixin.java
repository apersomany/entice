package dev.aperso.entice.mixin;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Inject(method = "renderLevel", at = @At("TAIL"))
	public void onRenderLevel(
		DeltaTracker deltaTracker,
		boolean shouldRenderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightTexture lightTexture,
		Matrix4f viewMatrix,
		Matrix4f projectionMatrix,
		CallbackInfo ci
	) {
		RangeIndicatorDecal.Client.render(camera, viewMatrix);
	}
}
