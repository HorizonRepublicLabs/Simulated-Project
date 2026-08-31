package dev.simulated_team.simulated.data.advancements;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public abstract class SimulatedCriterionTriggerBase<T extends SimulatedCriterionTriggerBase.Instance> implements CriterionTrigger<T> {

    private final Identifier id;

    public SimulatedCriterionTriggerBase(final Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return this.id;
    }

    protected void trigger(final ServerPlayer player, @Nullable final List<Supplier<Object>> suppliers) {
        final PlayerAdvancements playerAdvancements = player.getAdvancements();
        final Map<PlayerAdvancements.TriggerInstanceKey, T> listenersForType = playerAdvancements.getTriggerMapForType(this);

        if (listenersForType == null || listenersForType.isEmpty()) {
            return;
        }

        final List<PlayerAdvancements.TriggerInstanceKey> matched = new LinkedList<>();

        for (final Map.Entry<PlayerAdvancements.TriggerInstanceKey, T> entry : listenersForType.entrySet()) {
            if (entry.getValue().test(suppliers)) {
                matched.add(entry.getKey());
            }
        }

        for (final PlayerAdvancements.TriggerInstanceKey key : matched) {
            playerAdvancements.award(key.advancement(), key.criterion());
        }
    }

    public abstract static class Instance implements CriterionTriggerInstance {
        private final Identifier id;
        public Instance(final Identifier id) {
            this.id = id;
        }
        public Identifier getId() {
            return this.id;
        }
        protected abstract boolean test (@Nullable List<Supplier<Object>> suppliers);
    }
}