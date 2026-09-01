package dev.simulated_team.simulated.neoforge;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.neoforge.NeoForgeSimStats;
import dev.simulated_team.simulated.index.neoforge.SimNeoForgeRecipeTypes;
import dev.simulated_team.simulated.neoforge.events.SimNeoForgeCommonEvents;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimConfigService;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimEntityDataSerialization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import dev.simulated_team.simulated.neoforge.tab.SimulatedTabContents;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Simulated.MOD_ID)
public final class SimulatedNeoForge {
    public static final CreativeModeTab TAB = CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + Simulated.MOD_ID + ".group"))
            .icon(() -> new ItemStack(SimBlocks.PHYSICS_ASSEMBLER.get()))
            // The section aware population lives in SimulatedCreativeTab, which is
            // staged out with the client render layer, so the tab is filled through
            // the plain vanilla path meanwhile.
            .displayItems((parameters, output) -> SimulatedTabContents.fill(output))
            .build();

    public SimulatedNeoForge(final IEventBus modEventBus, final ModContainer modContainer) {
        // deferred register tab
        final DeferredRegister<CreativeModeTab> tabRegister = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Simulated.MOD_ID);
        tabRegister.register("main_tab", () -> TAB);
        tabRegister.register(modEventBus);


        SimNeoForgeRecipeTypes.register(modEventBus);

        NeoForgeSimEntityDataSerialization.register(modEventBus);
        Simulated.getRegistrate().registerEventListeners(modEventBus);

        NeoForgeSimStats.register(modEventBus);

        if (ModList.get().isLoaded("computercraft")) {
        }

        Simulated.init();
        NeoForgeSimConfigService.register(ModLoadingContext.get(), modContainer);
    }
}
