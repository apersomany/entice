package dev.aperso.entice;

import dev.aperso.entice.addon.mythic.MythicAddon;
import dev.aperso.entice.decal.RangeIndicatorDecal;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class EnticeServer extends JavaPlugin {
	public static EnticeServer INSTANCE;

	@Override
	public void onLoad() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			Field libraryLoaderField = classLoader.getClass().getDeclaredField("libraryLoader");
			libraryLoaderField.setAccessible(true);
			ClassLoader originalLibraryLoader = (ClassLoader) libraryLoaderField.get(classLoader);
			libraryLoaderField.set(classLoader, new EmptyClassLoader(originalLibraryLoader));
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void onEnable() {
		INSTANCE = this;
		RangeIndicatorDecal.Server.initialize();
		MythicAddon.initialize();
	}
}

