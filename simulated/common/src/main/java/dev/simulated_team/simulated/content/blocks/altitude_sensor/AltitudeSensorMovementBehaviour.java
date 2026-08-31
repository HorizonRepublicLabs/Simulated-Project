package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.createmod.catnip.api.client.animation.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.createmod.catnip.api.client.render.SuperRenderTypeBuffer;
import net.minecraft.util.Mth;
import net.createmod.catnip.api.data.Pair;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class AltitudeSensorMovementBehaviour implements MovementBehaviour {

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void tick(final MovementContext context) {
        MovementBehaviour.super.tick(context);

        // temporaryData <- (previousVisualHeight, visualHeight)
        final float yPos = (float) Sable.HELPER.projectOutOfSubLevel(context.world, JOMLConversion.toJOML(context.position)).y;
        if (context.temporaryData instanceof final Pair<?, ?> heights) {
            context.temporaryData = Pair.of(heights.getSecond(), yPos);
        } else {
            context.temporaryData = Pair.of(yPos, yPos);
        }
    }

    @Override
    public void renderInContraption(final MovementContext context, final VirtualRenderWorld renderWorld,
                                    final ContraptionMatrices matrices, final SuperRenderTypeBuffer buffer) {
        // The sensor's readout is drawn by the client render layer, which is not
        // part of the 26.2 port yet.
    }
}
