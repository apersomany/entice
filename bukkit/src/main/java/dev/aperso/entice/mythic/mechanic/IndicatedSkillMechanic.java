package dev.aperso.entice.mythic.mechanic;

import dev.aperso.entice.packet.BukkitPacketSystem;
import dev.aperso.entice.packet.indicator.IndicatorPacket;
import dev.aperso.entice.packet.indicator.Plane;
import dev.aperso.entice.packet.indicator.Shape;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

@MythicMechanic(
    name = "indicatedskill",
    aliases = { "is" }
)
public class IndicatedSkillMechanic extends SkillMechanic implements IMetaSkill {
    @MythicField(name = "skill", aliases = { "s" })
    protected Skill skill;

    @MythicField(name = "delay", aliases = { "d" })
    protected int delay;

    @MythicField(name = "color", aliases = { "c" })
    protected int color;

    @MythicField(name = "plane", aliases = { "p" })
    protected Plane plane;

    @MythicField(name = "x")
    protected float x;

    @MythicField(name = "y")
    protected float y;

    @MythicField(name = "rot", aliases = { "j" })
    protected float rot;

    @MythicField(name = "rev", aliases = { "g" })
    protected float rev;

    public IndicatedSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig config) {
        super(manager, file, skill, config);
        String skillName = config.getString(new String[]{"skill", "s"});
        manager.queueSecondPass(() -> {
            Optional<Skill> optionalSkill = manager.getSkill(file, this, skillName);
            if (optionalSkill.isPresent()) {
                this.skill = optionalSkill.get();
                this.skill.addParent(this);
            } else {
                MythicLogger.errorMechanicConfig(this, config, String.format("Skill %s not found", skillName));
            }
        });
        this.delay = config.getInteger(new String[] { "delay", "d" }, 20);
        // I don't know why, but the default value is not working for color
        Chroma chroma = config.getColor(new String[] { "color", "c" });
        if (chroma == null) {
            this.color = 0xFF0000FF;
        } else {
            this.color = chroma.value();
        }
        this.plane = config.getEnum(new String[] { "plane", "p" }, Plane.class, Plane.TRACKED);
        this.x = config.getFloat("x", 0);
        this.y = config.getFloat("y", 0);
        this.rot = config.getFloat(new String[] { "rot", "j" }, 0);
        this.rev = config.getFloat(new String[] { "rev", "g" }, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata metadata) {
        if (metadata.getCaster().getEntity().getBukkitEntity() instanceof Player player) {
            BukkitPacketSystem.send(
                new IndicatorPacket(
                    player.getEntityId(),
                    plane,
                    x,
                    y,
                    rot,
                    rev,
                    Shape.CIRCLE,
                    4,
                    color,
                    delay
                ),
                player
            );
        }
        if (this.skill == null) {
            return SkillResult.ERROR;
        }
        return SkillResult.SUCCESS;
    }

    @Override
    public String toString() {
        return "IndicatedSkillMechanic{" +
            "skill=" + skill +
            ", delay=" + delay +
            ", color=" + color +
            ", plane=" + plane +
            ", x=" + x +
            ", y=" + y +
            ", rot=" + rot +
            ", rev=" + rev +
            '}';
    }
}