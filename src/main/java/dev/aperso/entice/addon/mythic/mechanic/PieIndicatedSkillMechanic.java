package dev.aperso.entice.addon.mythic.mechanic;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
	name = "pieindicatedskill",
	aliases = {"pis"}
)
public class PieIndicatedSkillMechanic extends IndicatedSkillMechanic {
	public float radius;
	public float aperture;

	public PieIndicatedSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig config) {
		super(manager, file, skill, config);
		radius = config.getFloat(new String[]{"radius", "r"}, 1.0F);
		aperture = config.getFloat(new String[]{"aperture", "a"}, 90.0F);
	}

	@Override
	public RangeIndicatorDecal.Shape shape() {
		return new RangeIndicatorDecal.Pie(radius, aperture);
	}
}
