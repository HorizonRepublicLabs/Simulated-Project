package dev.eriksonn.aeronautics.neoforge;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import dev.eriksonn.aeronautics.index.client.AeroClientRegistries;
import dev.eriksonn.aeronautics.index.client.AeroSituationalMusic;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/// Most of the client entrypoint belongs to the staged-out render layer: the
/// ponder plugin, the render types and the balloon effect renderer are all held
/// back. What remains has to run here rather than lazily, because a registry can
/// only be created before NewRegistryEvent fires, and the situational music
/// registry is otherwise first touched on a client tick.
@Mod(value = Aeronautics.MOD_ID, dist = Dist.CLIENT)
public class AeronauticsNeoForgeClient {
	public AeronauticsNeoForgeClient(final IEventBus modBus, final ModContainer container) {
		AeroClientRegistries.init();
		// Registers the partial models with flywheel. Without it they are first
		// touched when a visual is built, long after model bake, and flywheel
		// gets a null model back.
		AeroPartialModels.init();
		AeroSituationalMusic.init();
	}
}
