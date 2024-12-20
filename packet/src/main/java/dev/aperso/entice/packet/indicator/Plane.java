package dev.aperso.entice.packet.indicator;

import dev.aperso.entice.packet.PacketSystem;

public enum Plane {
    INITIAL,
    TRACKED;

    static {
        PacketSystem.register(Plane.class);
    }
}
