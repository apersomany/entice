package dev.aperso.entice.fx;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.aperso.entice.Entice;
import dev.aperso.entice.state.RenderState;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1i;
import org.ladysnake.satin.api.managed.uniform.Uniform4f;
import org.ladysnake.satin.api.managed.uniform.UniformMat4;

public class SobelFx implements ShaderEffectRenderCallback, ClientTickEvents.StartTick {
    public record SobelPacketPayload(boolean enabled, int color) implements CustomPacketPayload {
        public static final Type<SobelPacketPayload> TYPE = new Type<>(Entice.resource("sobel"));

        public static final StreamCodec<ByteBuf, SobelPacketPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SobelPacketPayload::enabled,
            ByteBufCodecs.INT,
            SobelPacketPayload::color,
            SobelPacketPayload::new
        );

        @Override
        public @NotNull Type<SobelPacketPayload> type() {
            return TYPE;
        }
    }

    public static final SobelFx INSTANCE = new SobelFx();

    public static void initialize() {
        ShaderEffectRenderCallback.EVENT.register(INSTANCE);
        ClientTickEvents.START_CLIENT_TICK.register(INSTANCE);
        PayloadTypeRegistry.playS2C().register(SobelPacketPayload.TYPE, SobelPacketPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SobelPacketPayload.TYPE, (sobelPacketPayload, context) -> {
            INSTANCE.enabled = sobelPacketPayload.enabled;
            INSTANCE.color = sobelPacketPayload.color;
        });
    }

    private final ManagedShaderEffect effect = ShaderEffectManager.getInstance().manage(
        Entice.resource("shaders/post/fx/sobel.json"),
        effect -> {
            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget instanceof ReadableDepthFramebuffer framebuffer) {
                effect.setSamplerUniform("Depth", framebuffer.getStillDepthMap());
            }
        }
    );

    private boolean enabled = false;
    private int color = 0xFFFFFFFF;

    private final Uniform4f uColor = effect.findUniform4f("Color");
    private final UniformMat4 uProjInvMat = effect.findUniformMat4("ProjInvMat");

    @Override
    public void renderShaderEffects(float tickDelta) {
        if (!enabled) return;
        uColor.set(
            (float) ((color >> 24) & 0xFF) / 255f,
            (float) ((color >> 16) & 0xFF) / 255f,
            (float) ((color >> 8) & 0xFF) / 255f,
            (float) (color & 0xFF) / 255f
        );
        uProjInvMat.set(RenderState.PROJECTION_INVERSE);
        effect.render(tickDelta);
    }

    @Override
    public void onStartTick(Minecraft minecraft) {
        if (minecraft.player == null || color != 0xFFFFFFFF) return;
        enabled = minecraft.player.getMainHandItem().is(Items.BARRIER);
    }
}
