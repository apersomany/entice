package dev.aperso.entice.indicator.mixin;

import dev.aperso.entice.indicator.IndicatorSystem;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = IrisRenderingPipeline.class, remap = false)
public abstract class IrisRenderingPipelineMixin {
    @Shadow
    private ShaderStorageBufferHolder shaderStorageBufferHolder;

    @Inject(method = "beginLevelRendering", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/uniforms/custom/CustomUniforms;update()V"))
    private void onUpdateUniforms(CallbackInfo callbackInfo) {
        IndicatorSystem.onRender(shaderStorageBufferHolder.getBufferIndex(7));
    }
}
