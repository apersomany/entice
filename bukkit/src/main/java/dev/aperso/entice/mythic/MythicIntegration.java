package dev.aperso.entice.mythic;

import dev.aperso.entice.mythic.mechanic.IndicatedSkillMechanic;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class MythicIntegration {
    @SuppressWarnings("unchecked")
    public static void initialize() {
        try {
            ClassLoader classLoader = MythicIntegration.class.getClassLoader();
            Class<SkillExecutor> skillExecutorClass = (Class<SkillExecutor>) classLoader.loadClass("io.lumine.mythic.core.skills.SkillExecutor");
            Field mechanicsField = skillExecutorClass.getDeclaredField("MECHANICS");
            mechanicsField.setAccessible(true);
            Map<String, Class<? extends SkillMechanic>> mechanics = (Map<String, Class<? extends SkillMechanic>>) mechanicsField.get(null);
            for (Class<? extends SkillMechanic> mechanic : List.of(
                IndicatedSkillMechanic.class
            )) {
                MythicMechanic annotation = mechanic.getAnnotation(MythicMechanic.class);
                mechanics.put(annotation.name().toUpperCase(), mechanic);
                for (String alias : annotation.aliases()) {
                    mechanics.put(alias.toUpperCase(), mechanic);
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {}
    }
}
