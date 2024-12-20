package dev.aperso.entice;

import dev.aperso.entice.indicator.IndicatorSystem;
import dev.aperso.entice.packet.FabricPacketSystem;
import net.fabricmc.api.ClientModInitializer;

public class Entice implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricPacketSystem.initialize();
        IndicatorSystem.initialize();
    }
}
