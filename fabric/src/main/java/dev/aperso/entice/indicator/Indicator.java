package dev.aperso.entice.indicator;

import dev.aperso.entice.packet.indicator.IndicatorPacket;
import net.minecraft.world.entity.Entity;
import org.joml.Vector2f;

class Indicator {
    final IndicatorPacket packet;
    final Entity entity;
    Vector2f position;
    float lookAngle;
    int elapsedTime;

    Indicator(IndicatorPacket packet, Entity entity) {
        this.packet = packet;
        this.entity = entity;
    }
}
