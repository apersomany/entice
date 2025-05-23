package dev.aperso.entice.addon.mythic.mechanic;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
	name = "boxindicatedskill",
	aliases = {"bis"}
)
public class BoxIndicatedSkillMechanic extends IndicatedSkillMechanic {
	public float width;
	public float height;

	public BoxIndicatedSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig config) {
		super(manager, file, skill, config);
		width = config.getFloat(new String[]{"width", "w"}, 1.0F);
		height = config.getFloat(new String[]{"height", "h"}, 1.0F);
	}

	@Override
	public RangeIndicatorDecal.Shape shape() {
		return new RangeIndicatorDecal.Box(width, height);
	}
}
