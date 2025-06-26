package dev.aperso.entice.mixin;

import com.mojang.blaze3d.shaders.AbstractUniform;
import dev.aperso.entice.state.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(EffectInstance.class)
public abstract class EffectInstanceMixin {
    @Shadow public abstract void setSampler(String string, IntSupplier intSupplier);

    @Shadow public abstract AbstractUniform safeGetUniform(String string);

    @Inject(method = "apply", at = @At("HEAD"))
    public void onApply(CallbackInfo ci) {
        setSampler("DepthSampler", () -> Minecraft.getInstance().getMainRenderTarget().getDepthTextureId());
        safeGetUniform("ProjInvMat").set(RenderState.PROJECTION_INVERSE);
    }
}
