package io.github.faarma.handcuffsmod.common.item;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import io.github.faarma.handcuffsmod.common.network.packet.ChangeHotbarSlotS2CPacket;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * The ItemUtils class provides utility methods for handling player handcuff status.
 */
public class ItemUtils {
    /**
     * The HANDCUFFED constant represents the tag used to store the handcuffed status in a player's persistent data.
     * This tag is used to check and set whether a player is currently handcuffed.
     */
    private static final String HANDCUFFED = "handcuffed";

    /**
     * Checks whether a player is currently handcuffed.
     *
     * @param player The player entity to check.
     * @return True if the player is handcuffed; otherwise, false.
     */
    public static boolean isPlayerCuffed(PlayerEntity player) {
        CompoundNBT playerData = player.getPersistentData();
        if (playerData.contains(HANDCUFFED)) {
            return playerData.getBoolean(HANDCUFFED);
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
        playerData.putBoolean(HANDCUFFED, isCuffed);
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
        // Since the code is called from Server-Side, I have to send this to the player or it will not see that the slot changed.
        NetworkMessages.sentToPlayer((ServerPlayerEntity) player, new ChangeHotbarSlotS2CPacket(0));
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

    /**
     * Retrieves the player's animation layer associated with the mod.
     *
     * @param player The player entity for which to retrieve the animation layer.
     * @return The animation layer for the player.
     */
    @SuppressWarnings("unchecked")
    public static ModifierLayer<IAnimation> getPlayerAnimation(PlayerEntity player) {
        return (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayerEntity) player)
                .get(new ResourceLocation(HandcuffsMod.MOD_ID, "animation"));
    }

    /**
     * Retrieves a KeyframeAnimationPlayer for the "handcuffed" animation.
     *
     * @return A KeyframeAnimationPlayer for the "handcuffed" animation.
     */
    public static KeyframeAnimationPlayer getHandcuffedAnimation() {
        return new KeyframeAnimationPlayer(
                PlayerAnimationRegistry.getAnimation(new ResourceLocation(HandcuffsMod.MOD_ID, HANDCUFFED))
                );
    }

    /**
     * Retrieves a KeyframeAnimationPlayer for the "handcuffed_sneak" animation.
     *
     * @return A KeyframeAnimationPlayer for the "handcuffed_sneak" animation.
     */
    public static KeyframeAnimationPlayer getHandcuffedSneakAnimation() {
        return new KeyframeAnimationPlayer(
                PlayerAnimationRegistry.getAnimation(new ResourceLocation(HandcuffsMod.MOD_ID, "handcuffed_sneak"))
                );
    }
}
