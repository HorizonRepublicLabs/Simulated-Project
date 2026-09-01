package dev.eriksonn.aeronautics.neoforge.events;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.simulated_team.simulated.service.SimPlatformService;
import dev.eriksonn.aeronautics.Aeronautics;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.eriksonn.aeronautics.data.AeroAdvancementTriggers;
import dev.eriksonn.aeronautics.events.AeronauticsCommonEvents;
import dev.eriksonn.aeronautics.index.*;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroProcessingRecipeGen;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import dev.eriksonn.aeronautics.neoforge.service.NeoForgeAeroConfigService;
import net.createmod.catnip.api.config.ConfigBase;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Aeronautics.MOD_ID)
public class AeroNeoForgeCommonEvents {

	@SubscribeEvent
	public static void serverStop(ServerStoppedEvent event) {
		AeronauticsCommonEvents.onServerStopped(event.getServer());
	}

	@SubscribeEvent
	public static void postServerTick(ServerTickEvent.Post event) {
		final MinecraftServer server = event.getServer();
		for (final ServerLevel level : server.getAllLevels()) {
			AeronauticsCommonEvents.onServerTickEnd(level);
		}
	}

	@EventBusSubscriber(modid = Aeronautics.MOD_ID)
	public static class ModBusEvents {

		@SubscribeEvent
		public static void registerEvent(RegisterEvent event) {
			AeroArmInteractionPoints.init();

			if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
				// AeroAdvancements builds its icons from registered items, and 26.2
				// binds item components only after registration finishes, so it is
				// left to open itself once the registries are bound. The jei recipes
				// build item stacks too and wait for common setup.
				AeroAdvancementTriggers.register();
			}
		}

		// todo: move this somewhere more proper
		private static void jeiCompat() {
			MysteriousItemConversionCategory.RECIPES.add(
					ConversionRecipe.create(AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get().getDefaultInstance(),
							AeroBlocks.LEVITITE.asItem().getDefaultInstance()));
			MysteriousItemConversionCategory.RECIPES.add(
					ConversionRecipe.create(AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get().getDefaultInstance(),
							AeroBlocks.PEARLESCENT_LEVITITE.asItem().getDefaultInstance()));

			Identifier recipeId = Aeronautics.path("conversion_music_disc_cloud_skipper");
			ConversionRecipe recipe = new StandardProcessingRecipe.Builder<>(ConversionRecipe::new, recipeId)
					.withItemIngredients(ItemHelper.ingredientOf(AeroTags.ItemTags.CONVERTS_TO_CLOUD_SKIPPER))
					.withSingleItemOutput(AeroItems.MUSIC_DISC_CLOUD_SKIPPER.asStack())
					.build();
			MysteriousItemConversionCategory.RECIPES.add(
					new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeId), recipe));
		}

		@SubscribeEvent(priority = EventPriority.HIGH)
		/// GatherDataEvent is abstract in 26.2; listeners attach to a concrete
		/// subclass. 26.2 fires the datagen event per mod, so the owning mod no longer
		/// has to be picked out of the event.
		public static void gatherDataHighPriority(GatherDataEvent.Client event) {
			AeroTags.addGenerators();
		}

		@SubscribeEvent
		public static void gatherData(GatherDataEvent.Client event) {
			final DataGenerator generator = event.getGenerator();
			final PackOutput output = generator.getPackOutput();
			final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

			generator.addProvider(true, new AeroAdvancements(output, lookupProvider));
			generator.addProvider(true, AeroProcessingRecipeGen.registerAll(output, lookupProvider));
			event.addProvider(AeroSoundEvents.REGISTRY.getProvider(output));
		}

		@SubscribeEvent
		public static void commonSetup(FMLCommonSetupEvent event) {
			AeroFluidsNeoForge.registerFluidInteractions();

			if (SimPlatformService.INSTANCE.isLoaded("jei")) {
				jeiCompat();
			}
		}

		@SubscribeEvent
		public static void loadConfig(final ModConfigEvent.Loading event) {
			for (final ConfigBase config : NeoForgeAeroConfigService.CONFIGS.values()) {
				if (config.specification == event.getConfig().getSpec()) {
					config.onLoad();
				}
			}
		}

		@SubscribeEvent
		public static void reloadConfig(final ModConfigEvent.Reloading event) {
			for (final ConfigBase config : NeoForgeAeroConfigService.CONFIGS.values()) {
				if (config.specification == event.getConfig().getSpec()) {
					config.onReload();
				}
			}
		}
	}
}
