package io.github.faarma.handcuffsmod.common.event;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.item.ItemUtils;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerS2CPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Event handler for events that can occur on both the client-side and server-side.
 * <br>
 * Client-Side: These events do not have access to net.minecraft.server or net.minecraftforge.server.
 * <br>
 * Server-Side: These events do not have access to net.minecraft.client or net.minecraftforge.client.
 */
@Mod.EventBusSubscriber(modid = HandcuffsMod.ModID, bus = Bus.FORGE)
public class EventHandler {
    /**
     * Event handler for the PlayerInteractEvent.RightClickItem event, which occurs when a player right-clicks an item.
     * If the player is handcuffed, this method cancels the interaction.
     *
     * @param event The PlayerInteractEvent.RightClickItem event that occurred.
     */
    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (ItemUtils.isPlayerCuffed(event.getPlayer())) {
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.FAIL);
        }
    }

    /**
     * Event handler for the EntityItemPickupEvent, which occurs when a player picks up an item.
     * If the player is handcuffed, this method cancels the item pickup event.
     *
     * @param event The EntityItemPickupEvent that occurred.
     */
    @SubscribeEvent
    public static void onEntityItemPickupEvent(EntityItemPickupEvent event) {
        if (ItemUtils.isPlayerCuffed(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    /**
     * Event handler for the PlayerLoggedInEvent, which occurs when a player logs into the server.
     * If the player is handcuffed, this method sends a HandcuffedPlayerS2CPacket to notify the player of their handcuffed status.
     *
     * @param event The PlayerLoggedInEvent that occurred.
     */
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
        if (ItemUtils.isPlayerCuffed(event.getPlayer())) {
            NetworkMessages.sentToPlayer((ServerPlayerEntity) event.getPlayer(), new HandcuffedPlayerS2CPacket(true, event.getPlayer().getUUID()));
        }
    }
}
