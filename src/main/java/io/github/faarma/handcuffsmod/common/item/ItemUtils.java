package io.github.faarma.handcuffsmod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
}
