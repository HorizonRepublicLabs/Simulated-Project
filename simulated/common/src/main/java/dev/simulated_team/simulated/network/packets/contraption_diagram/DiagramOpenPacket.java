package dev.simulated_team.simulated.network.packets.contraption_diagram;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.DiagramConfig;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record DiagramOpenPacket(int entityID, DiagramConfig config) implements CustomPacketPayload {
    public static final Type<DiagramOpenPacket> TYPE = new Type<>(Simulated.path("open_diagram"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DiagramOpenPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DiagramOpenPacket::entityID,
            DiagramConfig.STREAM_CODEC, DiagramOpenPacket::config,
            DiagramOpenPacket::new
    );

    public void handle(final ClientPacketContext context) {
        // The client side of this packet drives the render layer, which is
        // not part of the 26.2 port yet.
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
