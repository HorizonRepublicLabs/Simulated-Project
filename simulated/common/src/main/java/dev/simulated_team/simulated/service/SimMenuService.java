package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

public interface SimMenuService {

	SimMenuService INSTANCE = ServiceUtil.load(SimMenuService.class);



	void openScreen(ServerPlayer player, MenuProvider factory, Consumer<RegistryFriendlyByteBuf> extraDataWriter);
}
