package dev.ryanhcode.offroad.neoforge.data;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.data.OffroadAdvancementTriggers;
import dev.ryanhcode.offroad.index.OffroadAdvancements;
import dev.ryanhcode.offroad.index.OffroadSoundEvents;
import dev.ryanhcode.offroad.index.OffroadTags;
import dev.ryanhcode.offroad.neoforge.index.OffroadSoundEventsNeoForge;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;

public class OffroadDatagen {

    /// 26.2 fires the datagen event per mod, so the owning mod no longer has to
    /// be picked out of the event.
    public static void gatherDataHighPriority(final GatherDataEvent.Client event) {
        OffroadTags.addGenerators();
    }

    public static void gatherData(final GatherDataEvent.Client event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new OffroadAdvancements(output, lookupProvider));
        event.addProvider(OffroadSoundEvents.REGISTRY.getProvider(output));
    }

    public static void registerEvent(final RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, OffroadSoundEventsNeoForge::register);

        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            OffroadAdvancements.init();
            OffroadAdvancementTriggers.register();
        }
    }
}
