package dev.simulated_team.simulated;

import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import dev.simulated_team.simulated.content.items.merging_glue.MergingGlueItemHandler;
import dev.simulated_team.simulated.index.SimPartialModels;

/// Trimmed to the half of the client the interaction layer needs. Ponder, the
/// Veil render stages and the block entity renderers are still staged out, so
/// their registration is absent here rather than referenced and broken; it comes
/// back with them.
public class SimulatedClient {

    public static final MergingGlueItemHandler MERGING_GLUE_ITEM_HANDLER = new MergingGlueItemHandler();

    public static void init() {
        SimPartialModels.init();
        BlockPropertiesTooltip.init();
    }
}
