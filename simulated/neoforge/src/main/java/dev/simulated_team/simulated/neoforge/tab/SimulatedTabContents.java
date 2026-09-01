package dev.simulated_team.simulated.neoforge.tab;

import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/// Fills the shared creative tab with every item the three registrates
/// collected, honouring the search only and invisible transforms.
///
/// SimulatedCreativeTab groups these into sections with drawn banners, but that
/// half belongs to the staged-out client render layer, so the items are listed
/// flat until it comes back. This lives in the loader module because
/// CreativeModeTab.Output is only public on the patched classes.
public class SimulatedTabContents {

	public static void fill(final CreativeModeTab.Output output) {
		for (final Supplier<Item> entry : SimulatedRegistrate.TAB_ITEMS) {
			final Item item = entry.get();
			if (CreativeTabItemTransforms.VisibilityType.INVISIBLE.has(item)) {
				continue;
			}

			final ItemStack stack = CreativeTabItemTransforms.applyTransform(item.getDefaultInstance());
			if (CreativeTabItemTransforms.VisibilityType.SEARCH_ONLY.has(item)) {
				output.accept(stack, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
			} else {
				output.accept(stack);
			}
		}
	}
}
