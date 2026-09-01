package dev.ryanhcode.offroad;

import dev.ryanhcode.offroad.index.OffroadPartialModels;

public class OffroadClient {
	public static void init() {
		// The ponder plugin belongs to the staged-out ponder layer.
		OffroadPartialModels.init();
	}
}
