package dev.aperso.entice.decal;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.aperso.entice.Entice;
import dev.aperso.entice.EnticeServer;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RangeIndicatorDecal extends AnchoredDecal {
	public interface Shape {
		Vector3f volume(float scale);
		default Vector3f offset(float scale) {
			return new Vector3f();
		}
		ShaderInstance shader(float scale);
	}

	public static float HEIGHT = 8;

	public record Circle(float radius) implements Shape {
		@Override
		public Vector3f volume(float scale) {
			return new Vector3f(radius * 2 * scale, HEIGHT, radius * 2 * scale);
		}

		@Override
		public ShaderInstance shader(float scale) {
			Client.CIRCLE.safeGetUniform("Radius").set(radius * scale);
			return Client.CIRCLE;
		}
	}

	public record Box(float width, float depth) implements Shape {
		@Override
		public Vector3f volume(float scale) {
			return new Vector3f(width, HEIGHT, depth * scale);
		}

		@Override
		public Vector3f offset(float scale) {
			return new Vector3f(0, 0, depth * scale / 2);
		}

		@Override
		public ShaderInstance shader(float scale) {
			Client.BOX.safeGetUniform("Width").set(width / 2);
			Client.BOX.safeGetUniform("Depth").set(depth / 2 * scale);
			return Client.BOX;
		}
	}

	public record Pie(float radius, float aperture) implements Shape {
		@Override
		public Vector3f volume(float scale) {
			return new Vector3f(radius * 2 * scale, HEIGHT, radius * 2 * scale);
		}

		@Override
		public ShaderInstance shader(float scale) {
			Client.PIE.safeGetUniform("Radius").set(radius * scale);
			Client.PIE.safeGetUniform("Aperture").set(aperture * (float) Math.PI / 180 / 2);
			return Client.PIE;
		}
	}

	public static abstract class Client {
		public static ShaderInstance CIRCLE;
		public static ShaderInstance BOX;
		public static ShaderInstance PIE;

		public static void onReloadShaders(ResourceProvider resourceProvider) throws IOException {
			if (CIRCLE != null) CIRCLE.close();
			if (BOX != null) BOX.close();
			if (PIE != null) PIE.close();
			CIRCLE = new ShaderInstance(resourceProvider, "decal/range_indicator/circle", DefaultVertexFormat.POSITION);
			BOX = new ShaderInstance(resourceProvider, "decal/range_indicator/box", DefaultVertexFormat.POSITION);
			PIE = new ShaderInstance(resourceProvider, "decal/range_indicator/pie", DefaultVertexFormat.POSITION);
		}

		public record RangeIndicatorDecalPayload(RangeIndicatorDecal decal) implements CustomPacketPayload {
			public static final Type<RangeIndicatorDecalPayload> TYPE = new Type<>(Entice.resource("range_indicator_decal"));

			public static final StreamCodec<ByteBuf, RangeIndicatorDecalPayload> CODEC = StreamCodec.of(
				(buffer, packet) -> {},
				(buffer) -> {
					byte[] byteBuffer = new byte[buffer.readableBytes()];
					buffer.readBytes(byteBuffer);
					RangeIndicatorDecal decal = Entice.FURY.deserializeJavaObject(byteBuffer, RangeIndicatorDecal.class);
					return new RangeIndicatorDecalPayload(decal);
				}
			);

			@Override
			public @NotNull Type<RangeIndicatorDecalPayload> type() {
				return TYPE;
			}
		}

		// todo: probably shouldn't be using a HashSet here. optimize with deque-like struct
		public static Set<RangeIndicatorDecal> DECALS = new HashSet<>();

		public static void render(Camera camera, Matrix4f viewMatrix) {
			for (RangeIndicatorDecal decal : DECALS) {
				decal.render(camera, viewMatrix);
			}
		}

		public static void initialize() {
			PayloadTypeRegistry.playS2C().register(RangeIndicatorDecalPayload.TYPE, RangeIndicatorDecalPayload.CODEC);
			ClientPlayNetworking.registerGlobalReceiver(
				RangeIndicatorDecalPayload.TYPE,
				(payload, context) -> DECALS.add(payload.decal)
			);
			ClientTickEvents.END_WORLD_TICK.register(clientLevel -> {
				ArrayList<RangeIndicatorDecal> marked = new ArrayList<>();
				for (RangeIndicatorDecal decal : DECALS) {
					if (--decal.duration < 0) {
						marked.add(decal);
					} else {
						decal.scale += decal.scalePerTick;
					}
				}
				for (RangeIndicatorDecal decal : marked) {
					DECALS.remove(decal);
				}
			});
		}
	}

	public static abstract class Server {
		public static void initialize() {
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(
				EnticeServer.INSTANCE,
				"entice:range_indicator_decal"
			);
		}
	}

	public Shape shape;
	public float inset = Float.MAX_VALUE;
	public int color = 0xFF0000FF;
	public int duration = Integer.MAX_VALUE;
	public float scale = 1;
	public float scalePerTick = 0;

	public RangeIndicatorDecal(Anchor anchor, Shape shape) {
		super(anchor);
		this.shape = shape;
	}

	@Override
	public Vector3f volume() {
		return shape.volume(scale + scalePerTick * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
	}

	@Override
	public Vector3f offset() {
		return shape.offset(scale + scalePerTick * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)).add(super.offset());
	}

	@Override
	public ShaderInstance shader() {
		ShaderInstance shader = shape.shader(scale + scalePerTick * Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
		shader.setSampler("DepthSampler", Minecraft.getInstance().getMainRenderTarget().getDepthTextureId());
		shader.safeGetUniform("Inset").set(inset);
		shader.safeGetUniform("Color").set(color);
		return shader;
	}
}