package dev.aperso.entice;

import dev.aperso.entice.decal.AnchoredDecal;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.memory.MemoryBuffer;
import org.apache.fury.serializer.Serializer;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.function.Function;

public class Entice {
	public static ThreadSafeFury FURY = Fury.builder()
		.requireClassRegistration(false)
		.withAsyncCompilation(true)
		.withCodegen(true)
		.buildThreadSafeFury();

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath("entice", path);
	}

	static {
		FURY.setClassChecker((classResolver, className) -> true);
		Function<Fury, Serializer<?>> entityAnchorSerializer = (fury) -> new Serializer<>(fury, AnchoredDecal.EntityAnchor.class) {
			@Override
			public  AnchoredDecal.EntityAnchor read(MemoryBuffer buffer) {
				try {
					return new AnchoredDecal.EntityAnchor(Objects.requireNonNull(Minecraft.getInstance().getConnection()).getLevel().getEntity(buffer.readInt32()));
				} catch (Exception exception) {
					return new AnchoredDecal.EntityAnchor(null);
				}
			}

			@Override
			public void write(MemoryBuffer buffer,  AnchoredDecal.EntityAnchor value) {
				buffer.writeInt32(value.entity().getId());
			}
		};
		FURY.registerSerializer(
			AnchoredDecal.EntityAnchor.class,
			entityAnchorSerializer
		);
	}
}
