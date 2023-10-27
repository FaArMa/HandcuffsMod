package io.github.faarma.handcuffsmod.common.network.packet;

import java.util.UUID;
import java.util.function.Supplier;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * The HandcuffedPlayerAnimationC2SPacket class represents a custom network packet used to communicate player animations related to handcuffing.
 * <br>
 * It includes methods for encoding, decoding, and handling the packet on the server-side.
 */
public class HandcuffedPlayerAnimationC2SPacket {
    /**
     * The animation value to be communicated by the packet.
     */
    private static byte animation;
    /**
     * The UUID of the target player associated with the animation.
     */
    private static UUID targetPlayer;

    /**
     * Constructs a new instance of the HandcuffedPlayerAnimationS2CPacket with an animation value and a target player's UUID.
     *
     * @param animation     The animation value to be communicated.
     * @param targetPlayer  The UUID of the target player.
     */
    public HandcuffedPlayerAnimationC2SPacket(byte animation, UUID targetPlayer) {
        HandcuffedPlayerAnimationC2SPacket.animation = animation;
        HandcuffedPlayerAnimationC2SPacket.targetPlayer = targetPlayer;
    }

    /**
     * Encodes the packet data into a packet buffer.
     *
     * @param message The HandcuffedPlayerAnimationS2CPacket message to encode.
     * @param buffer  The packet buffer to write data to.
     */
    public static void Encode(HandcuffedPlayerAnimationC2SPacket message, PacketBuffer buffer) {
        buffer.writeByte(animation);
        buffer.writeUUID(targetPlayer);
    }

    /**
     * Decodes a packet from a packet buffer and constructs a HandcuffedPlayerAnimationS2CPacket.
     *
     * @param buffer The packet buffer containing the encoded data.
     * @return A new HandcuffedPlayerAnimationS2CPacket instance.
     */
    public static HandcuffedPlayerAnimationC2SPacket Decode(PacketBuffer buffer) {
        return new HandcuffedPlayerAnimationC2SPacket(buffer.readByte(), buffer.readUUID());
    }

    /**
     * Handles the received HandcuffedPlayerAnimationC2SPacket on the server-side.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    public static void Handle(HandcuffedPlayerAnimationC2SPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // We are on the Server-Side
            NetworkMessages.sentToAllPlayersTrackingPlayer(contextSupplier.get().getSender(), new HandcuffedPlayerAnimationS2CPacket(animation, targetPlayer));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
