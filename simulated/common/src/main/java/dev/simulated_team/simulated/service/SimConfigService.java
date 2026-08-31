package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.config.server.SimServer;

public interface SimConfigService {

	SimConfigService INSTANCE = ServiceUtil.load(SimConfigService.class);

	SimServer server();

	boolean serverLoaded();

	// The client config drives the render layer, which is not part of the 26.2
	// port yet, so nothing here exposes it.
	boolean clientLoaded();
}
