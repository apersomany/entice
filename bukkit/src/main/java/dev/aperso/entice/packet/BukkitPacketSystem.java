package dev.aperso.entice.packet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BukkitPacketSystem extends PacketSystem implements PluginMessageListener {
    private static final String channel = "entice:packet";
    private static final Map<Class<?>, BiConsumer<Player, Object>> listenerMap = new HashMap<>();
    private static Plugin plugin;

    public static void initialize(Plugin plugin) {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, new BukkitPacketSystem());
        BukkitPacketSystem.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> packetClass, BiConsumer<Player, T> listener) {
        listenerMap.put(packetClass, (player, object) -> listener.accept(player, (T) object) );
    }

    public static <T> void send(T packet, PluginMessageRecipient audience) {
        audience.sendPluginMessage(plugin, channel, encode(packet));
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        Object packet = decode(message);
        listenerMap.get(packet.getClass()).accept(player, packet);
    }
}
