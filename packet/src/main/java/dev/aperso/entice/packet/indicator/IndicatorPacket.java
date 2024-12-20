package dev.aperso.entice.packet.indicator;

import dev.aperso.entice.packet.PacketSystem;

public record IndicatorPacket(
    int target,
    Plane plane,
    float x,
    float y,
    float rot,
    float rev,
    Shape shape,
    float inset,
    int color,
    int delay // time until the skill is cast, more like duration to show on the client side
) {
    static {
        PacketSystem.register(IndicatorPacket.class);
    }
}
