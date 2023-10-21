package io.github.faarma.handcuffsmod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * The ItemUtils class provides utility methods for handling player handcuff status.
 */
public class ItemUtils {
    /**
     * Checks whether a player is currently handcuffed.
     *
     * @param player The player entity to check.
     * @return True if the player is handcuffed; otherwise, false.
     */
    public static boolean isPlayerCuffed(PlayerEntity player) {
        CompoundNBT playerData = player.getPersistentData();
        if (playerData.contains("handcuffed")) {
            return playerData.getBoolean("handcuffed");
        }
        return false;
    }

    /**
     * Sets the handcuffed status of a player.
     *
     * @param player   The player entity for which to set the handcuffed status.
     * @param isCuffed True to indicate that the player is handcuffed; false to indicate otherwise.
     */
    public static void setCuffed(PlayerEntity player, boolean isCuffed) {
        CompoundNBT playerData = player.getPersistentData();
        playerData.putBoolean("handcuffed", isCuffed);
    }

    /**
     * Displays a message above the hotbar to both the self (sender) and the target player about their handcuffed status.
     * If the player is cuffed, it displays a cuffed message; if uncuffed, it displays an uncuffed message.
     *
     * @param self    The player who initiated the action.
     * @param target  The target player whose handcuffed status is being communicated.
     * @param isCuffed True if the target player is handcuffed; otherwise, false.
     */
    public static void sendCuffedStatus(PlayerEntity self, PlayerEntity target, boolean isCuffed) {
        if (isCuffed) {
            self.displayClientMessage(new TranslationTextComponent("gui.handcuffsmod.self.cuffed", target.getName().getString())
                    .withStyle(TextFormatting.RED, TextFormatting.BOLD), true);
            target.displayClientMessage(new TranslationTextComponent("gui.handcuffsmod.target.cuffed")
                    .withStyle(TextFormatting.RED, TextFormatting.BOLD), true);
        } else {
            self.displayClientMessage(new TranslationTextComponent("gui.handcuffsmod.self.uncuffed", target.getName().getString())
                    .withStyle(TextFormatting.GREEN, TextFormatting.BOLD), true);
            target.displayClientMessage(new TranslationTextComponent("gui.handcuffsmod.target.uncuffed")
                    .withStyle(TextFormatting.GREEN, TextFormatting.BOLD), true);
        }
    }

    /**
     * Selects the first slot of the player's hotbar and drops the item in the player's main hand.
     * If there is an item in the offhand, it will be dropped as well.
     *
     * @param player The player entity performing the action.
     */
    public static void selectAndDropHand(PlayerEntity player) {
        // Select the first slot of the hotbar and drop the item in the main hand
        player.inventory.selected = 0;
        player.drop(true);
        // Drop the item in the offhand if it is not empty
        if (!player.getOffhandItem().isEmpty()) {
            ItemStack itemInOffHand = player.getOffhandItem().copy();
            player.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            player.drop(itemInOffHand, true);
        }
    }

    /**
     * Transfers the item from the main hand of the self player to the main hand of the target player.
     * The target player's hands will be emptied before the transfer.
     *
     * @param self   The player entity performing the transfer.
     * @param target The player entity receiving the transferred item.
     */
    public static void transferItemToCuffed(PlayerEntity self, PlayerEntity target) {
        // Empty the hands of the cuffed player
        selectAndDropHand(target);
        // Transfer the item from self's main hand to target's main hand
        ItemStack itemInHand = self.getMainHandItem();
        ItemStack itemToTransfer = new ItemStack(itemInHand.getItem(), 1);
        itemInHand.shrink(1);
        target.setItemInHand(Hand.MAIN_HAND, itemToTransfer);
    }

    /**
     * Transfers the item from the main hand of the target player to the inventory of the self player.
     *
     * @param self   The player entity receiving the transferred item.
     * @param target The player entity from whom the item is being transferred.
     */
    public static void transferItemToUncuffed(PlayerEntity self, PlayerEntity target) {
        // Get and remove the item in the target's main hand
        ItemStack itemInHand = target.getMainHandItem().copy();
        target.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        // Add the item to self's inventory
        self.inventory.add(itemInHand);
    }
}
