package dev.simulated_team.simulated.mixin.hold_interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(final ClientLevel clientLevel, final GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "isShiftKeyDown", at = @At("RETURN"), cancellable = true)
    private void simulated$handlerShiftBlock(final CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && !HoldInteractionManager.canCrouch()) {
            cir.setReturnValue(false);
        }
    }

    /// 26.2 moved the key state off a mutable Input with a shiftKeyDown field
    /// onto an immutable record that tick reads from ClientInput and ships to
    /// the server whole, so the crouch is masked by handing back a copy. Every
    /// read in tick goes through here — the changed-since-last-tick comparison,
    /// the packet and lastSentInput alike — so the three stay consistent and the
    /// client does not resend on every tick.
    @WrapOperation(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/player/ClientInput;keyPresses:Lnet/minecraft/world/entity/player/Input;"))
    private Input simulated$shhhhDontTellTheServer(final ClientInput instance, final Operation<Input> original) {
        final Input keyPresses = original.call(instance);

        if (keyPresses.shift() && !HoldInteractionManager.canCrouch()) {
            return new Input(keyPresses.forward(), keyPresses.backward(), keyPresses.left(),
                    keyPresses.right(), keyPresses.jump(), false, keyPresses.sprint());
        }

        return keyPresses;
    }
}
