package dev.aperso.entice;

import dev.aperso.entice.mythic.MythicIntegration;
import dev.aperso.entice.packet.BukkitPacketSystem;
import org.bukkit.plugin.java.JavaPlugin;

public class Entice extends JavaPlugin {
    @Override
    public void onLoad() {
        BukkitPacketSystem.initialize(this);
        MythicIntegration.initialize();
    }
}
