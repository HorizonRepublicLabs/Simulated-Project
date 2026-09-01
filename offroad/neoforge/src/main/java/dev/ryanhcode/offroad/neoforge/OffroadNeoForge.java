package dev.ryanhcode.offroad.neoforge;


import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.data.OffroadTags;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import dev.ryanhcode.offroad.neoforge.data.OffroadDatagen;
import dev.ryanhcode.offroad.neoforge.service.NeoForgeOffroadConfigService;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@Mod(Offroad.MOD_ID)
public class OffroadNeoForge {
    public OffroadNeoForge(final IEventBus modBus, final ModContainer modContainer) {
        this.modBusRegistry(modBus);
        this.listenCommonEvents(NeoForge.EVENT_BUS);

        Offroad.init();

        NeoForgeOffroadConfigService.register(modContainer);
    }

    private void listenCommonEvents(final IEventBus eventBus) {

    }

    private void modBusRegistry(final IEventBus modBus) {
        // NeoForgeOffroadConfigService carries @EventBusSubscriber already.

        modBus.addListener(OffroadNeoForge::init);
        // GatherDataEvent is abstract in 26.2; listeners attach to a concrete subclass.
        modBus.addListener(EventPriority.HIGHEST, (GatherDataEvent.Client event) -> OffroadDatagen.gatherDataHighPriority(event));
        modBus.addListener(EventPriority.LOWEST, (GatherDataEvent.Client event) -> OffroadDatagen.gatherData(event));
        modBus.addListener(OffroadDatagen::registerEvent);
        modBus.addListener((ModifyDefaultComponentsEvent event) -> OffroadCommonEvents.modifyDefaultComponents(event::modify));
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post event) -> OffroadCommonEvents.tickLevelEvent(event.getLevel()));

        Offroad.getRegistrate().registerEventListeners(modBus);
    }

    private static void init(final FMLCommonSetupEvent event) {

    }
}
