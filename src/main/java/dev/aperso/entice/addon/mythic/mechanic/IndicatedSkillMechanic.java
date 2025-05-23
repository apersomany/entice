package dev.aperso.entice.addon.mythic.mechanic;

import dev.aperso.entice.Entice;
import dev.aperso.entice.EnticeServer;
import dev.aperso.entice.decal.AnchoredDecal;
import dev.aperso.entice.decal.RangeIndicatorDecal;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

import java.io.File;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public abstract class IndicatedSkillMechanic extends SkillMechanic implements IMetaSkill {
	public enum Plane {
		INITIAL,
		TRACKED
	}

	public Skill skill;
	public int delay;
	public float inset;
	public int color;
	public Plane plane;
	public float x;
	public float y;
	public float rot;
	public float rev;
	public boolean progress;

	public IndicatedSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig config) {
		super(manager, file, skill, config);
		String skillName = config.getString(new String[]{"skill", "s"});
		Runnable populateSkill = () -> {
			Optional<Skill> optionalSkill = manager.getSkill(file, this, skillName);
			if (optionalSkill.isPresent()) {
				this.skill = optionalSkill.get();
				this.skill.addParent(this);
			} else {
				MythicLogger.errorMechanicConfig(this, config, String.format("Skill %s not found", skillName));
			}
		};
		if (file == null) {
			populateSkill.run();
		} else {
			manager.queueSecondPass(populateSkill);
		}
		this.delay = config.getInteger(new String[]{"delay", "d"}, 20);
		this.inset = config.getFloat(new String[]{"inset", "i"}, Float.MAX_VALUE);
		Chroma color = config.getColor(new String[]{"color", "c"});
		if (color == null) {
			this.color = 0xFF0000FF;
		} else {
			this.color = color.value();
		}
		this.plane = config.getEnum(new String[]{"plane", "p"}, Plane.class, Plane.TRACKED);
		this.x = config.getFloat("x", 0.0F);
		this.y = config.getFloat("y", 0.0F);
		this.rot = config.getFloat(new String[]{"rot", "j"}, 0.0F);
		this.rev = config.getFloat(new String[]{"rev", "g"}, 0.0F);
		this.progress = config.getBoolean(new String[]{"progress", "showprogress"}, false);
	}

	public abstract RangeIndicatorDecal.Shape shape();

	public SkillResult cast(SkillMetadata metadata) {
		if (this.skill != null) {
			Entity caster = metadata.getCaster().getEntity().getBukkitEntity();
			AnchoredDecal.Anchor anchor = switch (plane) {
				case INITIAL -> {
					Location location = caster.getLocation();
					yield new AnchoredDecal.StaticAnchor(
						caster.getLocation().toVector().toVector3f(),
						location.getYaw()
					);
				}
				case TRACKED -> {
					try {
						yield new AnchoredDecal.EntityAnchor((net.minecraft.world.entity.Entity) caster.getClass().getDeclaredMethod("getHandle").invoke(caster));
					} catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				}
			};
			RangeIndicatorDecal.Shape shape = shape();
			RangeIndicatorDecal decal = new RangeIndicatorDecal(anchor, shape);
			decal.offset = new Vector3f(x, 0, y);
			decal.rot = rot;
			decal.rev = rev;
			decal.inset = inset;
			decal.color = color;
			decal.duration = delay;
			if (progress) {
				decal.scale = 0;
				decal.scalePerTick = 1f / decal.duration;
			}
			caster.getWorld().sendPluginMessage(
				EnticeServer.INSTANCE,
				"entice:range_indicator_decal",
				Entice.FURY.serializeJavaObject(decal)
			);
			SkillMetadata clonedMetadata = metadata.deepClone();
			Bukkit.getScheduler().scheduleSyncDelayedTask(
				this.getPlugin(),
				() -> {
					if (this.skill != null) {
						this.skill.execute(clonedMetadata);
					}
				},
				this.delay
			);
			return SkillResult.SUCCESS;
		} else {
			return SkillResult.ERROR;
		}
	}
}