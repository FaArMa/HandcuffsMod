package io.github.faarma.handcuffsmod.common.item;

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
     * The target player entity that will be affected when handcuffs are applied.
     */
    private PlayerEntity targetPlayer;
    /**
     * The duration in ticks for using this item.
     */
    private static final int useTimeTicks = 100;

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
        if (!(target instanceof PlayerEntity)) {
            return ActionResultType.FAIL;
        }
        this.targetPlayer = (PlayerEntity) target;
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
        return useTimeTicks;
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
        if (world.isClientSide()) {
            return;
        }
        if (((PlayerEntity) entity).getCooldowns().isOnCooldown(item.getItem())) {
            ((PlayerEntity) entity).stopUsingItem();
            return;
        }
        if (tick > 1) {
            return;
        }

        ((PlayerEntity) entity).getCooldowns().addCooldown(item.getItem(), 50);
        boolean isCuffed = ItemUtils.isPlayerCuffed(this.targetPlayer);

        if (isCuffed) {
            ItemUtils.setCuffed(this.targetPlayer, false);
        } else {
            ItemUtils.setCuffed(this.targetPlayer, true);
        }
    }
}
