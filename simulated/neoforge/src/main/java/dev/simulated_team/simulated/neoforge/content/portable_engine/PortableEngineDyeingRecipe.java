package dev.simulated_team.simulated.neoforge.content.portable_engine;

import com.mojang.serialization.MapCodec;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

/// 26.2 reshaped the special crafting recipes: there is no category on the
/// constructor, assemble no longer receives the registries, canCraftInDimensions
/// is gone in favour of the inherited NOT_PLACEABLE placement info, and a
/// serializer is a record over a map codec and a stream codec. A recipe that
/// carries no data of its own describes itself with unit codecs, the way vanilla
/// RepairItemRecipe does.
public class PortableEngineDyeingRecipe extends CustomRecipe {

    public static final PortableEngineDyeingRecipe INSTANCE = new PortableEngineDyeingRecipe();
    public static final MapCodec<PortableEngineDyeingRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PortableEngineDyeingRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<PortableEngineDyeingRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(final CraftingInput input, final Level level) {
        int engines = 0;
        int dyes = 0;

        for (int i = 0; i < input.size(); ++i) {
            final ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (Block.byItem(stack.getItem()) instanceof PortableEngineBlock) {
                    ++engines;
                } else {
                    if (!stack.is(Tags.Items.DYES))
                        return false;
                    ++dyes;
                }

                if (dyes > 1 || engines > 1) {
                    return false;
                }
            }
        }

        return engines == 1 && dyes == 1;
    }

    @Override
    public ItemStack assemble(final CraftingInput input) {
        ItemStack engine = ItemStack.EMPTY;
        DyeColor color = DyeColor.RED;

        for (int i = 0; i < input.size(); ++i) {
            final ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (Block.byItem(stack.getItem()) instanceof PortableEngineBlock) {
                    engine = stack;
                } else {
                    final DyeColor color1 = DyeColor.getColor(stack);
                    if (color1 != null) {
                        color = color1;
                    }
                }
            }
        }

        final ItemStack dyedEngine = SimBlocks.PORTABLE_ENGINES.get(color)
                .asStack();
        if (!engine.isComponentsPatchEmpty()) {
            dyedEngine.applyComponents(engine.getComponentsPatch());
        }

        return dyedEngine;
    }

    @Override
    public RecipeSerializer<PortableEngineDyeingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
