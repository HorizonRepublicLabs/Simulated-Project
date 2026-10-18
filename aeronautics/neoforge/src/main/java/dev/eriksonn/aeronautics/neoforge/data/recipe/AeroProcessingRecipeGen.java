package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import net.neoforged.neoforge.data.event.DatapackRegistryGatherer;

import java.util.List;

/// 26.3: recipes are a datapack registry, so the generators are bootstrapped
/// through GatherDataRegistryEntriesEvent instead of running as a DataProvider.
public class AeroProcessingRecipeGen {
	public static void registerAll(final DatapackRegistryGatherer gatherer) {
		List.<BaseRecipeProvider>of(
				new AeroMixingRecipes(),
				new AeroCrushingRecipes(),
				new AeroMechanicalCraftingRecipes(),
				new AeroWashingRecipes(),
				new AeroDeployingRecipes()
		).forEach(gen -> gatherer.add(gen.asBootstrap()));
	}
}
