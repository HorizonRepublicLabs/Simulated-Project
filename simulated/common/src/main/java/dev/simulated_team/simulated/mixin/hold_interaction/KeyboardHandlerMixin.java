package dev.simulated_team.simulated.mixin.hold_interaction;

import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Shadow @Final private Minecraft minecraft;

    /// 26.2 passes the press as a KeyEvent and dropped Minecraft.screen, which
    /// the 1.21 version injected in front of. The screen check this method did
    /// for itself is the same one, so the hook moves to HEAD rather than chasing
    /// a field that no longer exists.
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void simulated$preOnPress(final long windowPointer, final int action, final KeyEvent keyEvent, final CallbackInfo ci) {
        if (this.minecraft.gui.screen() != null) {
            return;
        }

        if (SimDistUtil.getClientPlayer() != null && !SimDistUtil.getClientPlayer().isSpectator()) {
            final InteractCallback.Result status = SimulatedCommonClientEvents.onBeforeMouseInput(
                    InteractCallback.Input.key(keyEvent.key(), keyEvent.keycode()), keyEvent.modifiers(), action);

            if (status.cancelled()) {
                ci.cancel();
            }
        }
    }
}
