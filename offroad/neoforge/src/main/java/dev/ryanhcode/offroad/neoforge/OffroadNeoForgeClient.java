package dev.ryanhcode.offroad.neoforge;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.OffroadClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/// Trimmed to what the shipped client needs. 26.2's catnip dropped
/// BaseConfigScreen, so the config screen extension point goes with it, but
/// OffroadClient#init still has to run: it registers the partial models with
/// flywheel, and a partial model first touched when a visual is built -- after
/// model bake -- resolves to null.
@Mod(value = Offroad.MOD_ID, dist = Dist.CLIENT)
public class OffroadNeoForgeClient {

	public OffroadNeoForgeClient(final IEventBus modBus, final ModContainer container) {
		OffroadClient.init();
	}
}
