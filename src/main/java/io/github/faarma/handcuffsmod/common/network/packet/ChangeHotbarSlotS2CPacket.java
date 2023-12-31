package io.github.faarma.handcuffsmod.common.network.packet;

import java.util.function.Supplier;
import io.github.faarma.handcuffsmod.client.network.packet.PacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * The ChangeHotbarSlotS2CPacket class represents a custom network packet used to communicate a change in the player's selected hotbar slot.
 * <br>
 * It includes methods for encoding, decoding, and handling the packet on the client-side.
 */
public class ChangeHotbarSlotS2CPacket {
    /**
     * The hotbar slot index to be communicated by the packet.
     */
    private static byte slot;

    /**
     * Constructs a new instance of the ChangeHotbarSlotS2CPacket with the selected hotbar slot index.
     *
     * @param slot The hotbar slot index to be communicated.
     */
    public ChangeHotbarSlotS2CPacket(byte slot) {
        ChangeHotbarSlotS2CPacket.slot = slot;
    }

    /**
     * Encodes the packet data into a packet buffer.
     *
     * @param message The ChangeHotbarSlotS2CPacket message to encode.
     * @param buffer  The packet buffer to write data to.
     */
    public static void encode(ChangeHotbarSlotS2CPacket message, PacketBuffer buffer) {
        buffer.writeByte(slot);
    }

    /**
     * Decodes a packet from a packet buffer and constructs a ChangeHotbarSlotS2CPacket.
     *
     * @param buffer The packet buffer containing the encoded data.
     * @return A new ChangeHotbarSlotS2CPacket instance.
     */
    public static ChangeHotbarSlotS2CPacket decode(PacketBuffer buffer) {
        return new ChangeHotbarSlotS2CPacket(buffer.readByte());
    }

    /**
     * Handles the received ChangeHotbarSlotS2CPacket on the client-side.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    public static void handle(ChangeHotbarSlotS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // We are on the Client-Side
            // Should be handled in another class and wrapped via DistExecutor#unsafeRunWhenOn
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketHandler.changeHotbarSlotHandlePacket(message, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    /**
     * Gets the hotbar slot index communicated by the packet.
     *
     * @return The hotbar slot index.
     */
    public static final byte getSlot() {
        return slot;
    }
}
