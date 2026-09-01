package dev.simulated_team.simulated.mixin.hold_interaction;

import com.llamalad7.mixinextras.sugar.Local;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Only the look-around hook is left here. 26.2 renamed onPress to onButton and
/// reshaped its arguments, and both the button and the scroll paths have
/// cancellable NeoForge events covering them, so those two moved to
/// SimNeoForgeClientEvents rather than being retargeted at moving bytecode.
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    /// The vertical delta already carries the inverted-mouse sign in 26.2; the
    /// separate multiplier the 1.21 code read out of a local is gone.
    @Inject(method = "turnPlayer", cancellable = true,
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void simulated$turnPlayer(final double d, final CallbackInfo ci,
                                      @Local(ordinal = 4) final double xo, @Local(ordinal = 5) final double yo) {
        if (SimDistUtil.getClientPlayer() != null && !SimDistUtil.getClientPlayer().isSpectator()) {
            final InteractCallback.Result status = SimulatedCommonClientEvents.onMouseMove(xo, yo);
            if (status.cancelled()) {
                ci.cancel();
            }
        }
    }
}
