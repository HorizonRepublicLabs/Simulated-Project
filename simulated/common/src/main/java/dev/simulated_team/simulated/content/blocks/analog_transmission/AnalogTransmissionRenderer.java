package dev.simulated_team.simulated.content.blocks.analog_transmission;

import com.simibubi.create.foundation.render.CreateRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.simulated_team.simulated.index.SimPartialModels;
import com.simibubi.create.foundation.render.CreateCachedBuffers;
import net.createmod.catnip.api.client.render.SuperByteBuffer;
import net.createmod.catnip.api.client.render.SuperRenderTypeBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogTransmissionRenderer extends KineticBlockEntityRenderer<AnalogTransmissionBlockEntity> {

    public AnalogTransmissionRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(final AnalogTransmissionBlockEntity be, final float partialTicks, final PoseStack ms, final SuperRenderTypeBuffer buffer, final int light, final int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) {
            return;
        }

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        final BlockState state = be.getBlockState();
        final Direction.Axis axis = ((IRotate) state.getBlock()).getRotationAxis(state);

        final SuperByteBuffer cogwheel = kineticRotationTransform(
                CreateCachedBuffers.partialFacingVertical(SimPartialModels.ANALOG_TRANSMISSION_COG, state, Direction.fromAxisAndDirection(state.getValue(AnalogTransmissionBlock.AXIS), Direction.AxisDirection.POSITIVE)),
                be.getExtraKinetics(),
                axis,
                getAngleForBe(be.getExtraKinetics(), be.getBlockPos(), axis),
                light);

        cogwheel.renderInto(ms, buffer.getBuffer(CreateRenderTypes.solidMovingBlock()));

        final VertexConsumer vb = buffer.getBuffer(CreateRenderTypes.solidMovingBlock());
        KineticBlockEntityRenderer.renderRotatingKineticBlock(be, shaft(getRotationAxisOf(be)), ms, vb, light);
    }

    @Override
    protected BlockState getRenderedBlockState(final AnalogTransmissionBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
