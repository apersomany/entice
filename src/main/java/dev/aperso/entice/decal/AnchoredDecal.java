package dev.aperso.entice.decal;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;


public abstract class AnchoredDecal extends Decal {
	public interface Anchor {
		Vector3f origin();
		float rev();
	}

	public record StaticAnchor(Vector3f origin, float rev) implements Anchor {}

	public record EntityAnchor(Entity entity) implements Anchor {
		@Override
		public Vector3f origin() {
			float tickDelta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
			return entity.getPosition(tickDelta).toVector3f();
		}

		@Override
		public float rev() {
			return entity.getYRot();
		}
	}

	public Anchor anchor;

	public AnchoredDecal(Anchor anchor) {
		this.anchor = anchor;
	}

	@Override
	public Vector3f origin() {
		return anchor.origin().add(super.origin());
	}

	@Override
	public float rev() {
		return anchor.rev() + super.rev();
	}
}
