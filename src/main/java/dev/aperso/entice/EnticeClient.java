package dev.aperso.entice;

import dev.aperso.entice.decal.RangeIndicatorDecal;
import net.fabricmc.api.ClientModInitializer;

public class EnticeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RangeIndicatorDecal.Client.initialize();
	}
}
