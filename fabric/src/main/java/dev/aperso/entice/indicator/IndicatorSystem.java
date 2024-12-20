package dev.aperso.entice.indicator;

import dev.aperso.entice.packet.FabricPacketSystem;
import dev.aperso.entice.packet.indicator.IndicatorPacket;
import dev.aperso.entice.packet.indicator.Plane;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

public class IndicatorSystem {
    private static final Set<Indicator> circleIndicatorSet = new HashSet<>();
    private static final Set<Indicator> boxIndicatorSet = new HashSet<>();
    private static final Set<Indicator> pieIndicatorSet = new HashSet<>();
    @SuppressWarnings("all")
    private static final Set<Indicator> padIndicatorSet = new HashSet<>();
    private static final List<Set<Indicator>> indicatorSetList = List.of(
        circleIndicatorSet,
        boxIndicatorSet,
        pieIndicatorSet,
        padIndicatorSet
    );
    private static final ByteBuffer buffer = MemoryUtil.memAlloc(4 * 4 + 40 * 256);

    public static void initialize() {
        FabricPacketSystem.register(IndicatorPacket.class, IndicatorSystem::onPacket);
        ClientTickEvents.START_WORLD_TICK.register(ignored -> IndicatorSystem.onTick());
    }

    public static void onPacket(IndicatorPacket packet) {
        System.out.println(packet);
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        Entity entity = connection.getLevel().getEntity(packet.target());
        if (entity == null) return;
        switch (packet.shape()) {
            case CIRCLE -> circleIndicatorSet.add(new Indicator(packet, entity));
            case BOX -> boxIndicatorSet.add(new Indicator(packet, entity));
            case PIE -> pieIndicatorSet.add(new Indicator(packet, entity));
        }
    }

    public static void onRender(int bufferIndex) {
        GL43C.glBindBuffer(GL43C.GL_SHADER_STORAGE_BUFFER, bufferIndex);
        buffer.reset();
        float tickDelta = CapturedRenderingState.INSTANCE.getTickDelta();
        for (Set<Indicator> set : indicatorSetList) {
            for (Indicator indicator : set) {
                if (indicator.position == null || indicator.packet.plane() == Plane.TRACKED) {
                    Vec3 position = indicator.entity.getPosition(tickDelta);
                    indicator.position = new Vector2f((float) position.x, (float) position.y);
                    indicator.lookAngle = indicator.entity.getViewYRot(tickDelta);
                };
                buffer.putFloat(indicator.position.x);
                buffer.putFloat(indicator.position.y);
                buffer.putFloat(indicator.packet.x());
                buffer.putFloat(indicator.packet.y());
                buffer.putFloat(indicator.packet.rot());
                buffer.putFloat(indicator.packet.rev() + indicator.lookAngle);
                buffer.putFloat(indicator.packet.shape().getX());
                buffer.putFloat(indicator.packet.shape().getY());
                buffer.putFloat(indicator.packet.inset());
                buffer.putInt(indicator.packet.color());
            }
        }
        buffer.flip();
        GL43C.glBufferSubData(GL43C.GL_SHADER_STORAGE_BUFFER, 0, buffer);
    }

    public static void onTick() {
        for (Set<Indicator> set : indicatorSetList) {
            List<Indicator> marked = new ArrayList<>();
            for (Indicator indicator : set) {
                if (indicator.entity.isRemoved() || indicator.elapsedTime++ == indicator.packet.delay()) {
                    marked.add(indicator);
                }
            }
            marked.forEach(set::remove);
        }
    }
}
