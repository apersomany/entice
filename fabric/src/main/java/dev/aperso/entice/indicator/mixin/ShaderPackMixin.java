package dev.aperso.entice.indicator.mixin;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.irisshaders.iris.gl.buffer.BuiltShaderStorageInfo;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.ShaderPack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(value = ShaderPack.class, remap = false)
public class ShaderPackMixin {
    @Shadow
    @Final
    private Int2ObjectArrayMap<BuiltShaderStorageInfo> bufferObjects;

    @Inject(method = "<init>(Ljava/nio/file/Path;Lcom/google/common/collect/ImmutableList;Z)V", at = @At("TAIL"))
    public void onInit(Path root, ImmutableList<StringPair> environmentDefines, boolean isZip, CallbackInfo callbackInfo) {
        bufferObjects.put(7, new BuiltShaderStorageInfo(4 * 4 + 40 * 256, false, 0, 0, null));
    }
}
