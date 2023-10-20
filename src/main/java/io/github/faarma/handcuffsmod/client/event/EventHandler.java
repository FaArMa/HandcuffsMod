package io.github.faarma.handcuffsmod.client.event;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.item.ItemUtils;
import java.lang.reflect.Field;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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

    /**
     * Event handler for the MouseScrollEvent, which occurs when the mouse scroll wheel is moved.
     * If the player is handcuffed, this method cancels the mouse scroll event.
     *
     * @param event The MouseScrollEvent that occurred.
     */
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onMouseScrollEvent(MouseScrollEvent event) {
        if (ItemUtils.isPlayerCuffed(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }

    /**
     * Event handler for the KeyInputEvent, which occurs when a key is pressed or released.
     * This method is responsible for handling key input when a player is handcuffed, preventing certain actions.
     *
     * @param event The KeyInputEvent that occurred.
     */
    @SubscribeEvent
    public static void onKeyInputEvent(KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        GameSettings gs = mc.options;
        // Yes, if the first is null it means that you are playing (no inventory, no chat, etc.).
        if (mc.screen != null || !ItemUtils.isPlayerCuffed(mc.player)) {
            return;
        }
        /*
         * Keys that should be ignored if pressed
         * - Drop Selected Item
         * - Pick Block
         * - Swap Item With Offhand
         * - Hotbar Slot 1-9
         * 
         * XXX Maybe blocking movement and clicks so Pehkui is not needed?
         * 
         */
        KeyBinding[] blacklistKeys = { gs.keyDrop, gs.keyPickItem, gs.keySwapOffhand,
                gs.keyHotbarSlots[0], gs.keyHotbarSlots[1], gs.keyHotbarSlots[2],
                gs.keyHotbarSlots[3], gs.keyHotbarSlots[4], gs.keyHotbarSlots[5],
                gs.keyHotbarSlots[6], gs.keyHotbarSlots[7], gs.keyHotbarSlots[8] };
        // Attempt to cancel the action of the "blacklistKeys" key.
        // XXX It may be more convenient to use Access Transformers or to have the Reflection done only once.
        for (KeyBinding keyBinding : blacklistKeys) {
            if (keyBinding.isDown()) {
                // Effectively, to cancel the pressed button I must change the clickCount...which is private.
                Field clickCountField = ObfuscationReflectionHelper.findField(KeyBinding.class, "field_151474_i");
                try {
                    clickCountField.setInt(keyBinding, 0);
                } catch (IllegalAccessException e) {
                    System.err.println(e.toString() + " (an attempt was made to cancel the pressed action but it failed)");
                }
                // Ensure the key is not considered pressed.
                keyBinding.setDown(false);
                return;
            }
        }
    }
}
