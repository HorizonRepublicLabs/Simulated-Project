package dev.simulated_team.simulated.content.blocks.steering_wheel;

import net.minecraft.world.phys.Vec3;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.network.packets.SteeringWheelPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SteeringWheelHandler extends BlockHoldInteraction {
    private static SteeringWheelBlockEntity blockEntity = null;

    private static boolean updated = false;
    private static float rawAngle = 0;
    private static float effectiveAngle = 0;
    private static boolean wasShiftKeyDown = false;
    private static int angleSgn = 1;
    private static float angleLimit = 0;

    @Override
    public void startHold(final Level level, final Player player, final BlockPos blockPos) {
        super.startHold(level, player, blockPos);
        blockEntity = level.getBlockEntity(blockPos, SimBlockEntityTypes.STEERING_WHEEL.get()).orElseThrow();
        rawAngle = blockEntity.getInteractionAngle(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks());
        angleSgn = (int) blockEntity.directionConvert(1);
        updated = true;
        angleLimit = blockEntity.angleInput.getValue();
    }


    @Override
    public void renderOverlay(final int width1, final int height1, final boolean hideGui) {
        // The wheel's overlay is drawn through GuiGraphics, which 26.2 replaced
        // with the gui render state pipeline. Steering itself is unaffected.
    }

    //custom wrap because mc is a jerk (we need else if here otherwise it'll pass both if statements
    public static float wrapDegrees(float value) {
        float f = value % 360.0F;

        if (f >= 180.0F) {
            f -= 360.0F;
        } else if (f <= -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    @Override
    public void stop() {
        if (blockEntity != null && !blockEntity.isRemoved()) {
            blockEntity.held = false;
            blockEntity = null;
        }

        VeilPacketManager.server().sendPacket(new SteeringWheelPacket(true, effectiveAngle, this.getInteractionPos()));
        super.stop();
    }

    @Override
    public boolean activeOnMouseMove(final double yaw, final double pitch) {
        if (yaw != 0) {
            final float oldAngle = rawAngle;
            rawAngle += (float) (yaw / 10 * angleSgn);
            rawAngle = Mth.clamp(rawAngle, -blockEntity.angleInput.getValue(), blockEntity.angleInput.getValue());
            updated |= oldAngle != rawAngle;
        }

        return true;
    }

    @Override
    public boolean activeTick(final Level level, final LocalPlayer player) {
        effectiveAngle = rawAngle;
        if (HoldInteractionManager.unblockedShift()) {
            effectiveAngle = Mth.clamp(Math.round(effectiveAngle / 45) * 45, -blockEntity.angleInput.getValue(), blockEntity.angleInput.getValue());
            if (!wasShiftKeyDown) {
                updated = true;
            }
            wasShiftKeyDown = true;
        } else {
            if (wasShiftKeyDown) {
                updated = true;
            }
            wasShiftKeyDown = false;
        }

        this.setTargetAngle(effectiveAngle);
        return !BlockHoldInteraction.inInteractionRange(player, Vec3.atCenterOf(this.getInteractionPos()));
    }

    @Override
    public boolean isBlockActive(final BlockPos pos) {
        return super.isBlockActive(pos) && !Float.isNaN(SteeringWheelHandler.rawAngle);
    }

    public void setTargetAngle(final float targetAngle) {
        if (updated) {
            VeilPacketManager.server().sendPacket(new SteeringWheelPacket(false, targetAngle, this.getInteractionPos()));

            updated = false;
            blockEntity.targetAngleToUpdate = targetAngle;
            blockEntity.held = !Float.isNaN(targetAngle);
        }
    }

    @Override
    public int getCrouchBlockingTicks() {
        return 6;
    }
}
