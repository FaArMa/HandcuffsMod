package io.github.faarma.handcuffsmod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

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
}
