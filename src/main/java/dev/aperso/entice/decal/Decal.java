package dev.aperso.entice.decal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public abstract class Decal {
	public Vector3f origin = new Vector3f();
	public Vector3f offset = new Vector3f();
	public float rot = 0;
	public float rev = 0;

	public Vector3f origin() {
		return origin;
	}

	public Vector3f offset() {
		return offset;
	}

	public float rot() {
		return rot;
	}

	public float rev() {
		return rev;
	}

	public abstract Vector3f volume();
	public abstract ShaderInstance shader();

	protected static abstract class Client {
		protected static VertexBuffer vertexBuffer;
		protected static RenderType renderType = RenderType.create(
			"decal",
			DefaultVertexFormat.POSITION,
			VertexFormat.Mode.TRIANGLE_STRIP,
			4 * 3 * 14,
			false,
			false,
			RenderType.CompositeState.builder()
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.createCompositeState(false)
		);
	}

	protected static void draw(ShaderInstance shader) {
		if (Client.vertexBuffer == null) {
			BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
			for (int mask : new int[] { 6, 2, 4, 0, 1, 2, 3, 6, 7, 4, 5, 1, 7, 3 }) {
				buffer.addVertex(
					(mask & 0b001) != 0 ? 0.5f : -0.5f,
					(mask & 0b010) != 0 ? 0.5f : -0.5f,
					(mask & 0b100) != 0 ? 0.5f : -0.5f
				);
			}
			Client.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			Client.vertexBuffer.bind();
			Client.vertexBuffer.upload(buffer.buildOrThrow());
		}
		Client.renderType.setupRenderState();
		Client.vertexBuffer.bind();
		RenderSystem.disableDepthTest();
		GL11.glCullFace(GL11.GL_FRONT);
		Client.vertexBuffer.drawWithShader(
			RenderSystem.getModelViewMatrix(),
			RenderSystem.getProjectionMatrix(),
			shader
		);
		GL11.glCullFace(GL11.GL_BACK);
		RenderSystem.enableDepthTest();
		Client.renderType.clearRenderState();
	}

	public void render(Camera camera, Matrix4f viewMatrix) {
		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.mul(viewMatrix);
		modelViewStack.translate(camera.getPosition().toVector3f().negate());
		Matrix4f modelMatrix = modelMatrix();
		modelViewStack.mul(modelMatrix);
		Matrix4f mvpInvMat = new Matrix4f(RenderSystem.getProjectionMatrix()).mul(modelViewStack).invert();
		Vector3f volume = volume();
		modelViewStack.scale(volume);
		ShaderInstance shader = shader();
		shader.safeGetUniform("MvpInvMat").set(mvpInvMat);
		RenderSystem.applyModelViewMatrix();
		modelViewStack.popMatrix();
//		Vec3 cameraPositionWorld = camera.getPosition();
//		Vector4f cameraPositionLocal = new Vector4f(
//			(float) cameraPositionWorld.x,
//			(float) cameraPositionWorld.y,
//			(float) cameraPositionWorld.z,
//			1
//		);
//		cameraPositionLocal.mul(modelMatrix.invertAffine());
//		boolean insideVolume = true;
//		insideVolume &= Math.abs(cameraPositionLocal.x) * 2 < volume.x;
//		insideVolume &= Math.abs(cameraPositionLocal.y) * 2 < volume.y;
//		insideVolume &= Math.abs(cameraPositionLocal.z) * 2 < volume.z;
		draw(shader);
	}

	public Matrix4f modelMatrix() {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(origin());
		matrix.rotateY(rev() * (float) Math.PI / 180 * -1);
		matrix.translate(offset());
		matrix.rotateY(rot() * (float) Math.PI / 180);
		return matrix;
	}
}
