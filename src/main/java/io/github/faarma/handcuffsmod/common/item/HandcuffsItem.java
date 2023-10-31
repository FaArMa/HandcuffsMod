package io.github.faarma.handcuffsmod.common.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Custom item class representing Handcuffs in the game.
 * Extends the Item class provided by Forge.
 */
public class HandcuffsItem extends Item {
    /**
     * A mapping of player UUIDs to their corresponding handcuffed player UUIDs.
     * The key represents the handcuffer's UUID, and the value represents the handcuffed player's UUID.
     */
    private static final Map<UUID, UUID> targetPlayers = new HashMap<>();
    /**
     * The duration in ticks for using this item.
     */
    private static final int USE_TIME_TICKS = 100;

    /**
     * Constructor for the HandcuffsItem class.
     * It initializes the item with specific properties, including stack size and creative tab.
     */
    public HandcuffsItem() {
        super(new Item.Properties().stacksTo(4).tab(ItemGroup.TAB_TOOLS));
    }

    /**
     * Handles the interaction of this item with a living entity, such as a player.
     *
     * @param item   The ItemStack representing this item.
     * @param player The player performing the interaction.
     * @param target The living entity being interacted with.
     * @param hand   The hand used for the interaction (main hand or offhand).
     * @return An ActionResultType indicating the result of the interaction.
     */
    @Override
    public ActionResultType interactLivingEntity(ItemStack item, PlayerEntity player, LivingEntity target, Hand hand) {
        if (!(target instanceof PlayerEntity) || hand.equals(Hand.OFF_HAND) || player.getCooldowns().isOnCooldown(item.getItem())) {
            return ActionResultType.FAIL;
        }
        targetPlayers.put(player.getUUID(), target.getUUID());
        player.startUsingItem(hand);
        return ActionResultType.SUCCESS;
    }

    /**
     * Gets the duration in ticks for using this item.
     *
     * @param item The ItemStack representing this item.
     * @return The use duration in ticks.
     */
    @Override
    public int getUseDuration(ItemStack item) {
        return USE_TIME_TICKS;
    }

    /**
     * Handles the on-use tick event for this item.
     *
     * @param world  The World instance.
     * @param entity The living entity using this item.
     * @param item   The ItemStack representing this item.
     * @param tick   The current tick during item usage.
     */
    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack item, int tick) {
        if (world.isClientSide() || tick > 1) {
            return;
        }
        final PlayerEntity player = (PlayerEntity) entity;
        final PlayerEntity targetPlayer = world.getPlayerByUUID(targetPlayers.get(player.getUUID()));

        player.getCooldowns().addCooldown(item.getItem(), 50);
        boolean isCuffed = ItemUtils.isPlayerCuffed(targetPlayer);

        ItemUtils.setCuffed(targetPlayer, !isCuffed);
        NetworkMessages.sentToAllPlayersTrackingPlayerAndSelf(targetPlayer, new HandcuffedPlayerS2CPacket(!isCuffed, targetPlayer.getUUID()));
        ItemUtils.sendCuffedStatus(player, targetPlayer, !isCuffed);

        if (isCuffed) {
            ItemUtils.transferItemToUncuffed(player, targetPlayer);
        } else {
            ItemUtils.transferItemToCuffed(player, targetPlayer);
        }

        targetPlayers.remove(player.getUUID());
        player.stopUsingItem();
    }
}
