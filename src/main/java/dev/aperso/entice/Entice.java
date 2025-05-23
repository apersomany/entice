package dev.aperso.entice;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.Serializer;

import java.util.Objects;
import java.util.function.Function;

public class Entice {
	public static ThreadSafeFury FURY = Fury.builder()
		.requireClassRegistration(false)
			.buildThreadSafeFury();

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath("entice", path);
	}

	static {
		FURY.setClassChecker((classResolver, className) -> true);
		Function<Fury, Serializer<?>> entitySerializer = (fury) -> new Serializer<>(fury, Entity.class) {
			@Override
			public Entity read(MemoryBuffer buffer) {
				return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getLevel().getEntity(buffer.readInt32());
			}

			@Override
			public void write(MemoryBuffer buffer, Entity value) {
				buffer.writeInt32(value.getId());
			}
		};
		FURY.registerSerializer(
			Entity.class,
			entitySerializer
		);
		FURY.registerSerializer(
			ServerPlayer.class,
			entitySerializer
		);
	}
}
