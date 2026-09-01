package dev.eriksonn.aeronautics.mixin.flammable_blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/// 26.2 made FireBlock#setFlammable private, so registering flammability for a
/// modded block needs an invoker.
@Mixin(FireBlock.class)
public interface FireBlockInvoker {
	@Invoker("setFlammable")
	void aeronautics$setFlammable(Block block, int encouragement, int flammability);
}
