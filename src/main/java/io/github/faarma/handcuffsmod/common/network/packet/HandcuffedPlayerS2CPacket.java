package io.github.faarma.handcuffsmod.common.network.packet;

import java.util.UUID;
import java.util.function.Supplier;
import io.github.faarma.handcuffsmod.client.network.packet.PacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * The HandcuffedPlayerS2CPacket class represents a custom network packet used to communicate the handcuffed status of a player.
 * <br>
 * It includes methods for encoding, decoding, and handling the packet on the client-side.
 */
public class HandcuffedPlayerS2CPacket {
    /**
     * The handcuffed status to be communicated by the packet.
     */
    private static boolean isCuffed;
    /**
     * The UUID of the target player associated with the handcuffed status.
     */
    private static UUID targetPlayer;

    /**
     * Constructs a new instance of the HandcuffedPlayerS2CPacket with handcuffed status and a target player's UUID.
     *
     * @param isCuffed     True if the player is handcuffed; otherwise, false.
     * @param targetPlayer The UUID of the target player.
     */
    public HandcuffedPlayerS2CPacket(boolean isCuffed, UUID targetPlayer) {
        HandcuffedPlayerS2CPacket.isCuffed = isCuffed;
        HandcuffedPlayerS2CPacket.targetPlayer = targetPlayer;
    }

    /**
     * Encodes the packet data into a packet buffer.
     *
     * @param message The HandcuffedPlayerS2CPacket message to encode.
     * @param buffer  The packet buffer to write data to.
     */
    public static void encode(HandcuffedPlayerS2CPacket message, PacketBuffer buffer) {
        buffer.writeBoolean(isCuffed);
        buffer.writeUUID(targetPlayer);
    }

    /**
     * Decodes a packet from a packet buffer and constructs a HandcuffedPlayerS2CPacket.
     *
     * @param buffer The packet buffer containing the encoded data.
     * @return A new HandcuffedPlayerS2CPacket instance.
     */
    public static HandcuffedPlayerS2CPacket decode(PacketBuffer buffer) {
        return new HandcuffedPlayerS2CPacket(buffer.readBoolean(), buffer.readUUID());
    }

    /**
     * Handles the received HandcuffedPlayerS2CPacket on the client-side.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    public static void handle(HandcuffedPlayerS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // We are on the Client-Side
            // Should be handled in another class and wrapped via DistExecutor#unsafeRunWhenOn
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketHandler.handcuffedPlayerHandlePacket(message, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    /**
     * Gets the handcuffed status communicated by the packet.
     *
     * @return True if the player is handcuffed; otherwise, false.
     */
    public static final boolean isCuffed() {
        return isCuffed;
    }

    /**
     * Gets the UUID of the target player associated with the handcuffed status.
     *
     * @return The UUID of the target player.
     */
    public static final UUID getTargetPlayer() {
        return targetPlayer;
    }
}
