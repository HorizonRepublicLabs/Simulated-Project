package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.api.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

/// Where the lever's handle actually sits, shared by the renderer that draws it
/// and the grip handler that ray-tests against it. It lives apart from the
/// renderer because the interaction layer needs the geometry while the render
/// layer is still staged out, and because two copies of a transform drift.
public final class ThrottleLeverGeometry {

    public static final double ANGLE_LIMIT = 40.0;

    private ThrottleLeverGeometry() {
    }

    public static float handleAngle(final ThrottleLeverBlockEntity blockEntity, final float partialTicks) {
        final float state = blockEntity.clientAngle.getValue(partialTicks);
        final AttachFace face = blockEntity.getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACE);
        final float angle = (float) (((state / 15) * (ANGLE_LIMIT * 2) - ANGLE_LIMIT) / 180 * Math.PI);

        return face == AttachFace.WALL ? -angle : angle;
    }

    public static void transformHandle(final ThrottleLeverBlockEntity blockEntity, final float partialTicks, final PoseStack ms) {
        final PoseTransformStack stack = TransformStack.of(ms);

        transform(stack, blockEntity.getBlockState());
        stack
                .translate(1 / 2f, 3.0 / 16.0, 1 / 2f)
                .rotateX(handleAngle(blockEntity, partialTicks))
                .translateBack(1 / 2f, 3.0 / 16.0, 1 / 2f);
    }

    public static <T extends TransformStack<T>> TransformStack<T> transform(final TransformStack<T> buffer, final BlockState leverState) {
        final AttachFace attached = leverState.getValue(AnalogLeverBlock.FACE);
        final Direction facing = leverState.getValue(AnalogLeverBlock.FACING);

        final float rX;
        switch (attached) {
            case FLOOR -> rX = 0;
            case WALL -> rX = 90;
            default -> rX = 180;
        }

        final float rY = AngleHelper.horizontalAngle(facing);
        buffer.rotateCentered((float) (rY / 180 * Math.PI), Direction.UP);
        buffer.rotateCentered((float) (rX / 180 * Math.PI), Direction.EAST);
        buffer.rotateCentered(attached == AttachFace.CEILING ? (float) Math.PI : 0.0f, Direction.UP);
        return buffer;
    }
}
