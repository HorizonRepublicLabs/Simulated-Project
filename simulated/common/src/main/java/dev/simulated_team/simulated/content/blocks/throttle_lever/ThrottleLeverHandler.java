package dev.simulated_team.simulated.content.blocks.throttle_lever;

import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.simulated_team.simulated.network.packets.ThrottleLeverSignalPacket;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.api.client.animation.AnimationTickHolder;
import net.createmod.catnip.api.client.gui.UIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ThrottleLeverHandler extends BlockHoldInteraction {
    protected boolean inverted = false;
    protected int lastSignal = 0;
    protected int signal = 0;
    protected float value = 0;
    protected float animatedValue;
    protected float lastAnimatedValue;

    @Override
    public void startHold(final Level level, final Player player, final BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof final ThrottleLeverBlockEntity be) {
            this.lastSignal = be.state;
            this.inverted = be.getBlockState().getValue(ThrottleLeverBlock.INVERTED);
            this.signal = this.inverted ? 15 - be.state : be.state;
            this.value = this.signal / 15f;
            this.animatedValue = this.lastAnimatedValue = this.value;
        }
        super.startHold(level, player, pos);
    }

    @Override
    public boolean activeTick(final Level level, final LocalPlayer player) {
        if (level.getBlockEntity(this.getInteractionPos()) instanceof ThrottleLeverBlockEntity &&
                BlockHoldInteraction.inInteractionRange(player, Vec3.atCenterOf(this.getInteractionPos()), 0)) {
            final float speed = 0.85f;
            this.lastAnimatedValue = this.animatedValue;
            this.animatedValue = this.animatedValue * (1 - speed) + this.signal / 15f * speed;
            return false;
        }
        return true;
    }

    @Override
    public void renderOverlay(final int width, final int height, final boolean hideGui) {
        // The throttle's overlay is drawn through GuiGraphics, which 26.2
        // replaced with the gui render state pipeline. The lever still works;
        // only its readout is missing until the client render layer is ported.
    }

    @Override
    public boolean activeOnMouseMove(final double yaw, final double pitch) {
        this.value -= (float) (pitch / 180.0);

        this.value = Math.min(1.0f, Math.max(0.0f, this.value));

        final int newSignal = Math.round(this.value * 15.0f);
        this.signal = Math.min(15, Math.max(0, newSignal));

        if (this.signal != this.lastSignal) {
            this.lastSignal = this.signal;
            this.changed();
        }

        return true;
    }

    private void changed() {
        VeilPacketManager.server().sendPacket(new ThrottleLeverSignalPacket(this.getInteractionPos(), this.signal));
    }
}
