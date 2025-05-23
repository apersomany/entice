package dev.aperso.entice.decal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

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
				.setCullState(RenderStateShard.NO_CULL)
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
		Client.vertexBuffer.drawWithShader(
			RenderSystem.getModelViewMatrix(),
			RenderSystem.getProjectionMatrix(),
			shader
		);
		RenderSystem.disableDepthTest();
		Client.renderType.clearRenderState();
	}

	public void render(Camera camera, Matrix4f viewMatrix) {
		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.mul(viewMatrix);
		modelViewStack.translate(camera.getPosition().toVector3f().negate());
		modelViewStack.translate(origin());
		modelViewStack.rotateY(rev() * (float) Math.PI / 180 * -1);
		modelViewStack.translate(offset());
		modelViewStack.rotateY(rot() * (float) Math.PI / 180);
		RenderSystem.applyModelViewMatrix();
		Matrix4f mvpInvMat = new Matrix4f(RenderSystem.getProjectionMatrix()).mul(RenderSystem.getModelViewMatrix()).invert();
		modelViewStack.scale(volume());
		ShaderInstance shader = shader();
		shader.safeGetUniform("MvpInvMat").set(mvpInvMat);
		RenderSystem.applyModelViewMatrix();
		modelViewStack.popMatrix();
		draw(shader);
	}

//	public Matrix4f modelMatrixInverse(Vector3f camera) {
//		Matrix4f matrix = new Matrix4f();
//		matrix.translate(camera);
//		matrix.translate(origin());
//		matrix.rotateY(rev() * (float) Math.PI / 180 * -1);
//		matrix.translate(offset());
//		matrix.rotateY(rot() * (float) Math.PI / 180);
//		return matrix.invert();
//	}
}
