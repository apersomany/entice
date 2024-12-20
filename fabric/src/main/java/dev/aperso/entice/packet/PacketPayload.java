package dev.aperso.entice.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PacketPayload(Object object) implements CustomPacketPayload {
    public static final Type<PacketPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("entice", "packet"));

    // TODO: Optimize copies (currently involves at least one copy each)
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPayload> CODEC = StreamCodec.of(
        (buffer, packet) -> buffer.writeBytes(FabricPacketSystem.encode(packet.object())),
        (buffer) -> new PacketPayload(FabricPacketSystem.decode(buffer.nioBuffer()))
    );

    @Override
    public @NotNull Type<PacketPayload> type() {
        return TYPE;
    }
}
