package dev.simulated_team.simulated.data.advancements;

import net.minecraft.world.level.storage.loot.ValidationContextSource;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SimpleSimulatedTrigger extends SimulatedCriterionTriggerBase<SimulatedCriterionTriggerBase.Instance> {
    public SimpleSimulatedTrigger(final Identifier id) {
        super(id);
    }

    public void trigger(final ServerPlayer player) {
        super.trigger(player, null);
    }

    public Instance instance() {
        return new Instance(this.getId());
    }

    @Override
    public @NotNull Codec<SimulatedCriterionTriggerBase.Instance> codec() {
        return Identifier.CODEC.xmap(Instance::new, SimulatedCriterionTriggerBase.Instance::getId);
    }

    public static class Instance extends SimulatedCriterionTriggerBase.Instance {

        @Override
        public void validate(final ValidationContextSource context) {
            // Nothing to validate; the trigger carries no predicates.
        }

        public Instance(final Identifier id) {
            super(id);
        }

        @Override
        protected boolean test(@Nullable final List<Supplier<Object>> suppliers) {
            return true;
        }

    }
}