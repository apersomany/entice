package dev.aperso.entice.addon.mythic;

import dev.aperso.entice.addon.mythic.mechanic.BoxIndicatedSkillMechanic;
import dev.aperso.entice.addon.mythic.mechanic.CircleIndicatedSkillMechanic;
import dev.aperso.entice.addon.mythic.mechanic.IndicatedSkillMechanic;
import dev.aperso.entice.addon.mythic.mechanic.PieIndicatedSkillMechanic;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class MythicAddon {
	@SuppressWarnings("unchecked")
	public static void initialize() {
		try {
			ClassLoader classLoader = MythicAddon.class.getClassLoader();
			Class<SkillExecutor> skillExecutorClass = (Class<SkillExecutor>) classLoader.loadClass("io.lumine.mythic.core.skills.SkillExecutor");
			Field mechanicsField = skillExecutorClass.getDeclaredField("MECHANICS");
			mechanicsField.setAccessible(true);
			Map<String, Class<? extends SkillMechanic>> mechanics = (Map<String, Class<? extends SkillMechanic>>) mechanicsField.get(null);
			for(Class<? extends IndicatedSkillMechanic> mechanic : List.of(
				CircleIndicatedSkillMechanic.class,
				BoxIndicatedSkillMechanic.class,
				PieIndicatedSkillMechanic.class
			)) {
				MythicMechanic annotation = mechanic.getAnnotation(MythicMechanic.class);
				mechanics.put(annotation.name().toUpperCase(), mechanic);
				for(String alias : annotation.aliases()) {
					mechanics.put(alias.toUpperCase(), mechanic);
				}
			}
		} catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException ignored) {}
	}
}
