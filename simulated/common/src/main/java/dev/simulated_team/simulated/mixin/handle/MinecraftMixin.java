package dev.simulated_team.simulated.mixin.handle;

import dev.simulated_team.simulated.index.SimClickInteractions;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    /// 26.2 replaced InteractionResult#shouldSwing with a swing source on the
    /// success result. All three of its call sites sit on the branches this hook
    /// wants — entity, block and item use — the same set the boolean covered.
    @Inject(method = "startUseItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/InteractionResult$Success;swingSource()Lnet/minecraft/world/InteractionResult$SwingSource;"))
    private void makeHandleHandlerCountUsing(final CallbackInfo ci) {
        SimClickInteractions.HANDLE_HANDLER.actuallyUsedBlockCountdown = 4;
    }
}
