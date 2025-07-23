package dev.aperso.entice;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import dev.aperso.entice.fx.SobelFx;
import net.fabricmc.api.ClientModInitializer;

public class EnticeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RangeIndicatorDecal.Client.initialize();
		SobelFx.initialize();
	}
}
