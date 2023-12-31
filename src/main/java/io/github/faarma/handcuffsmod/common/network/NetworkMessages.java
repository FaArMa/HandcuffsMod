package io.github.faarma.handcuffsmod.common.network;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.network.packet.ChangeHotbarSlotS2CPacket;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerAnimationC2SPacket;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerAnimationS2CPacket;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * The NetworkMessages class manages network communication and packet registration for the mod.
 */
public class NetworkMessages {
    /**
     * The protocol version for network communication.
     */
    private static final String PROTOCOL_VERSION = "1";
    /**
     * The main network channel instance for the mod.
     */
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HandcuffsMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    /**
     * Registers network packets for the mod.
     */
    public static void registerPackets() {
        int packetID = 0;

        INSTANCE.messageBuilder(HandcuffedPlayerS2CPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(HandcuffedPlayerS2CPacket::encode)
                .decoder(HandcuffedPlayerS2CPacket::decode)
                .consumer(HandcuffedPlayerS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(ChangeHotbarSlotS2CPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ChangeHotbarSlotS2CPacket::encode)
                .decoder(ChangeHotbarSlotS2CPacket::decode)
                .consumer(ChangeHotbarSlotS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(HandcuffedPlayerAnimationS2CPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(HandcuffedPlayerAnimationS2CPacket::encode)
                .decoder(HandcuffedPlayerAnimationS2CPacket::decode)
                .consumer(HandcuffedPlayerAnimationS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(HandcuffedPlayerAnimationC2SPacket.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(HandcuffedPlayerAnimationC2SPacket::encode)
            .decoder(HandcuffedPlayerAnimationC2SPacket::decode)
            .consumer(HandcuffedPlayerAnimationC2SPacket::handle)
            .add();
    }

    /**
     * Sends a message to a specific player.
     *
     * @param target  The player entity to receive the message.
     * @param message The message to send.
     */
    public static <MSG> void sentToPlayer(ServerPlayerEntity target, MSG message) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> target), message);
    }

    /**
     * Sends a message to all players tracking a specified player entity.
     *
     * @param target  The player entity being tracked.
     * @param message The message to send.
     */
    public static <MSG> void sentToAllPlayersTrackingPlayer(PlayerEntity target, MSG message) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> target), message);
    }

    /**
     * Sends a message to all players tracking a specified player entity, including the tracked player itself.
     *
     * @param target  The player entity being tracked.
     * @param message The message to send.
     */
    public static <MSG> void sentToAllPlayersTrackingPlayerAndSelf(PlayerEntity target, MSG message) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), message);
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     */
    public static <MSG> void sentToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
