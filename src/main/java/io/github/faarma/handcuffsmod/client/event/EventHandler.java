package io.github.faarma.handcuffsmod.client.event;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.item.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Event handler for events that can occur on the client-side.
 * <br>
 * Client-Side: These events do not have access to net.minecraft.server or net.minecraftforge.server.
 */
@Mod.EventBusSubscriber(modid = HandcuffsMod.ModID, bus = Bus.FORGE, value = Dist.CLIENT)
public class EventHandler {
    /**
     * Event handler for the GuiOpenEvent event, which occurs when a GUI screen is opened.
     * This method cancels the opening of the InventoryScreen if the player is handcuffed.
     *
     * @param event The GuiOpenEvent that occurred.
     */
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onGuiOpenEvent(GuiOpenEvent event) {
        if (Minecraft.getInstance().level == null || !(Minecraft.getInstance().player instanceof PlayerEntity)) {
            return;
        }
        if (event.getGui() instanceof InventoryScreen) {
            if (ItemUtils.isPlayerCuffed(Minecraft.getInstance().player)) {
                event.setCanceled(true);
            }
        }
    }
}
