package io.github.faarma.handcuffsmod.client.network.packet;

import java.util.function.Supplier;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import io.github.faarma.handcuffsmod.common.item.ItemUtils;
import io.github.faarma.handcuffsmod.common.network.packet.ChangeHotbarSlotS2CPacket;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerAnimationS2CPacket;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet handling for packets sent from the server to the client.
 * <br>
 * Client-Side: These events do not have access to net.minecraft.server or net.minecraftforge.server.
 */
public class PacketHandler {
    /**
     * Handles the HandcuffedPlayerS2CPacket received from the server.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    @SuppressWarnings("resource")
    public static void handcuffedPlayerHandlePacket(HandcuffedPlayerS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        // Set the handcuffed status for the target player on the client-side.
        PlayerEntity targetPlayer = Minecraft.getInstance().level.getPlayerByUUID(HandcuffedPlayerS2CPacket.getTargetPlayer());
        ItemUtils.setCuffed(targetPlayer, HandcuffedPlayerS2CPacket.isCuffed());
        // Add or remove the handcuff animation
        ModifierLayer<IAnimation> animation = ItemUtils.getPlayerAnimation(targetPlayer);
        animation.setAnimation(HandcuffedPlayerS2CPacket.isCuffed() ? ItemUtils.getHandcuffedAnimation() : null);
    }

    /**
     * Handles the HandcuffedPlayerAnimationS2CPacket  received from the server.
     * This method updates the animation of the target player based on the animation state received in the packet.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    @SuppressWarnings("resource")
    public static void handcuffedPlayerAnimationHandlePacket(HandcuffedPlayerAnimationS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        PlayerEntity targetPlayer = Minecraft.getInstance().level.getPlayerByUUID(HandcuffedPlayerAnimationS2CPacket.getTargetPlayer());
        // Use the animation for handcuffed standing or crouching
        ModifierLayer<IAnimation> animation = ItemUtils.getPlayerAnimation(targetPlayer);
        animation.setAnimation(HandcuffedPlayerAnimationS2CPacket.getAnimation() == 0 ? ItemUtils.getHandcuffedAnimation() : ItemUtils.getHandcuffedSneakAnimation());
    }

    /**
     * Handles the ChangeHotbarSlotS2CPacket received from the server.
     *
     * @param message          The received packet message.
     * @param contextSupplier  A supplier providing the network context.
     */
    @SuppressWarnings("resource")
    public static void changeHotbarSlotHandlePacket(ChangeHotbarSlotS2CPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        // Close the player's screens (inventory, chat, etc.).
        Minecraft.getInstance().player.closeContainer();
        // Select the first slot of the hotbar
        Minecraft.getInstance().player.inventory.selected = ChangeHotbarSlotS2CPacket.getSlot();
        // Release any pressed key
        KeyBinding.releaseAll();
    }
}
