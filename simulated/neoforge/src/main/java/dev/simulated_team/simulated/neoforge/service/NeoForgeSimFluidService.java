package dev.simulated_team.simulated.neoforge.service;

import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import dev.simulated_team.simulated.service.SimFluidService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class NeoForgeSimFluidService implements SimFluidService {
    public long mbToLoaderUnits(final long mb) {
        return mb;
    }

    @Override
    public Fluid getFluidInItem(final ItemStack stack) {
        // 26.2 reaches an item's fluid through a resource handler on an access.
        final ItemAccess access = ItemAccess.forStack(stack.copy());
        final ResourceHandler<FluidResource> handler = access.getCapability(Capabilities.Fluid.ITEM);

        if (handler != null && handler.size() > 0) {
            final FluidResource resource = handler.getResource(0);
            if (!resource.isEmpty()) {
                return resource.getFluid();
            }
        }

        return null;
    }
}
