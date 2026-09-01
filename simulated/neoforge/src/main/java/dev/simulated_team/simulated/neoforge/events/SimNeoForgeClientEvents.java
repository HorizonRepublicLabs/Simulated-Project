package dev.simulated_team.simulated.neoforge.events;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimKeys;
import dev.simulated_team.simulated.neoforge.service.SimpleResourceManagerRegistryService;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.List;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Simulated.MOD_ID, value = Dist.CLIENT)
public class SimNeoForgeClientEvents {

	@SubscribeEvent
	public static void preClientTick(final ClientTickEvent.Pre event) {
		SimulatedCommonClientEvents.preClientTick(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void postClientTick(final ClientTickEvent.Post event) {
		SimulatedCommonClientEvents.postClientTick(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void postRenderGui(final RenderGuiEvent.Post event) {
		SimulatedCommonClientEvents.renderOverlays(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
	}

	@SubscribeEvent
	public static void keyInput(final InputEvent.Key event) {
		SimulatedCommonClientEvents.onAfterKeyPress(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
	}

	@SubscribeEvent
	public static void postMouseButtonInput(final InputEvent.MouseButton.Post event) {
		SimulatedCommonClientEvents.onAfterMouseInput(event.getButton(), event.getModifiers(), event.getAction());
	}

	/// 26.2 renamed MouseHandler#onPress and reshaped its arguments, so the click
	/// and scroll hooks come off NeoForge's cancellable input events instead of a
	/// mixin aimed at bytecode that moved.
	@SubscribeEvent
	public static void preMouseButtonInput(final InputEvent.MouseButton.Pre event) {
		if (!simulated$playerCanInteract()) {
			return;
		}

		if (SimulatedCommonClientEvents.onBeforeMouseInput(
				InteractCallback.Input.mouse(event.getButton()), event.getModifiers(), event.getAction()).cancelled()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void mouseScroll(final InputEvent.MouseScrollingEvent event) {
		if (!simulated$playerCanInteract()) {
			return;
		}

		if (SimulatedCommonClientEvents.onMouseScroll(event.getScrollDeltaX(), event.getScrollDeltaY()).cancelled()) {
			event.setCanceled(true);
		}
	}

	private static boolean simulated$playerCanInteract() {
		final Player player = SimDistUtil.getClientPlayer();
		return player != null && !player.isSpectator();
	}

	@SubscribeEvent
	public static void playerInteractRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntity().isLocalPlayer()) {
			final InteractionResult res = SimulatedCommonClientEvents.onRightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec());

			if (res != null) {
				event.setCancellationResult(res);
				event.setCanceled(true);
				return;
			}
		}

		if (event.getItemStack().is(SimItems.HONEY_GLUE)) {
			event.setUseBlock(TriState.FALSE);
			if (event.getLevel().isClientSide()) {
				SimClickInteractions.HONEY_GLUE_MANAGER.selectPos(event.getPos(), event.getEntity(), event.getItemStack());
			}
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void itemTooltip(final ItemTooltipEvent event) {
		SimulatedCommonClientEvents.appendTooltip(event.getItemStack(), event.getFlags(), event.getEntity(), event.getToolTip());
	}

	@EventBusSubscriber(modid = Simulated.MOD_ID, value = Dist.CLIENT)
	public static class ModBusEvents {

		@SubscribeEvent
		public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
			SimKeys.registerTo(event::register);
		}


		/// 26.2 replaced the flat registration with a sorted graph keyed by
		/// identifier. The registry the listeners arrive through carries no name
		/// of its own, so each gets one from its position in that list.
		@SubscribeEvent
		public static void addReloadListener(final AddClientReloadListenersEvent event) {
			final List<PreparableReloadListener> listeners = SimpleResourceManagerRegistryService.LISTENERS;

			for (int i = 0; i < listeners.size(); i++) {
				event.addListener(Simulated.path("reload_listener_" + i), listeners.get(i));
			}
		}
	}
}
