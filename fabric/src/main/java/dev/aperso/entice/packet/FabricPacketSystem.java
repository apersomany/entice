package dev.aperso.entice.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FabricPacketSystem extends PacketSystem {
    private static final Map<Class<?>, Consumer<Object>> listenerMap = new HashMap<>();

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(PacketPayload.TYPE, PacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PacketPayload.TYPE, PacketPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PacketPayload.TYPE, (packet, context) -> {
            listenerMap.get(packet.object().getClass()).accept(packet.object());
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> packetClass, Consumer<T> listener) {
        listenerMap.put(packetClass, (object) -> listener.accept((T) object));
    }

    public static <T> void send(T packet) {
        ClientPlayNetworking.send(new PacketPayload(packet));
    }
}
