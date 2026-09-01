package dev.eriksonn.aeronautics.neoforge.content.fluids.levitite;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class LevititeBlendNeoForge extends BaseFlowingFluid implements LevititeBlendDummyInterface {
	public LevititeBlendNeoForge(Properties properties) {
		super(properties);
	}

	/// 26.2 ticks fluids on the server level and hands the block state along.
	@Override
	public void tick(ServerLevel level, BlockPos pos, BlockState blockState, FluidState state) {
		super.tick(level, pos, blockState, state);
		LevititeBlendDummyInterface.super.levititeBlendTick(level, pos, state);
	}

	@Override
	public boolean isSource(FluidState fluidState) {
		return true;
	}

	@Override
	public int getAmount(FluidState fluidState) {
		return 8;
	}
}
