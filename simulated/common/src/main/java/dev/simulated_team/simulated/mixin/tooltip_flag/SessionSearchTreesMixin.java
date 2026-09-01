package dev.simulated_team.simulated.mixin.tooltip_flag;

import dev.simulated_team.simulated.mixin_interface.tooltip_flag.TooltipFlagExtension;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SessionSearchTrees.class)
public class SessionSearchTreesMixin {
    /// The lambda this rides on was renumbered in 26.2. It is the one that
    /// builds the flag -- it calls TooltipFlag.Default#asCreative and stores the
    /// result -- rather than the three that merely receive it as a parameter.
    @ModifyVariable(method = "lambda$updateCreativeTooltips$0", at = @At(value = "STORE"))
    private static TooltipFlag markAsCreativeSearch(final TooltipFlag value) {
        // 26.2 may hand back NeoForge's ClientTooltipFlag wrapper rather than
        // the vanilla Default this extension is mixed into, so the mark is
        // applied only when the flag is one that carries it.
        if (value instanceof final TooltipFlagExtension extension) {
            extension.simulated$setCreativeSearch(true);
        }

        return value;
    }
}
