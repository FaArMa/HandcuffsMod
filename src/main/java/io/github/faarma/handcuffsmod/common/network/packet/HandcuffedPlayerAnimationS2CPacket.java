package io.github.faarma.handcuffsmod.common.network.packet;

import java.util.UUID;
import java.util.function.Supplier;
import io.github.faarma.handcuffsmod.client.network.packet.PacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * The HandcuffedPlayerAnimationS2CPacket class represents a custom network packet used to communicate player animations related to handcuffing.
 * <br>
 * It includes methods for encoding, decoding, and handling the packet on the client-side.
 */
public class HandcuffedPlayerAnimationS2CPacket {
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
    public HandcuffedPlayerAnimationS2CPacket(byte animation, UUID targetPlayer) {
        HandcuffedPlayerAnimationS2CPacket.animation = animation;
        HandcuffedPlayerAnimationS2CPacket.targetPlayer = targetPlayer;
    }

    /**
     * Encodes the packet data into a packet buffer.
     *
     * @param message The HandcuffedPlayerAnimationS2CPacket message to encode.
     * @param buffer  The packet buffer to write data to.
     */
    public static void Encode(HandcuffedPlayerAnimationS2CPacket message, PacketBuffer buffer) {
        buffer.writeByte(animation);
        buffer.writeUUID(targetPlayer);
    }

    /**
     * Decodes a packet from a packet buffer and constructs a HandcuffedPlayerAnimationS2CPacket.
     *
     * @param buffer The packet buffer containing the encoded data.
     * @return A new HandcuffedPlayerAnimationS2CPacket instance.
     */
    public static HandcuffedPlayerAnimationS2CPacket Decode(PacketBuffer buffer) {
        return new HandcuffedPlayerAnimationS2CPacket(buffer.readByte(), buffer.readUUID());
    }

    /**
     * Handles the received HandcuffedPlayerAnimationS2CPacket on the client-side.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    public static void Handle(HandcuffedPlayerAnimationS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // We are on the Client-Side
            // Should be handled in another class and wrapped via DistExecutor#unsafeRunWhenOn
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketHandler.HandcuffedPlayerAnimationHandlePacket(message, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    /**
     * Gets the animation value communicated by the packet.
     *
     * @return The animation value.
     */
    public static final byte getAnimation() {
        return animation;
    }

    /**
     * Gets the UUID of the target player associated with the animation.
     *
     * @return The UUID of the target player.
     */
    public static final UUID getTargetPlayer() {
        return targetPlayer;
    }
}
