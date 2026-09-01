package dev.simulated_team.simulated.neoforge;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.SimulatedClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/// SimNeoForgeClientEvents carries @EventBusSubscriber and registers itself on
/// both buses, so the explicit registrations the 1.21 version did here would
/// subscribe it twice. 26.2's catnip dropped BaseConfigScreen, so the config
/// screen extension point goes with it.
@Mod(value = Simulated.MOD_ID, dist = Dist.CLIENT)
public class SimulatedNeoForgeClient {

	public SimulatedNeoForgeClient(final IEventBus modEventBus, final ModContainer container) {
		SimulatedClient.init();
	}
}
