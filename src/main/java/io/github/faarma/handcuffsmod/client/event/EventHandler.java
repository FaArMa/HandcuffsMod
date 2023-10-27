package io.github.faarma.handcuffsmod.client.event;

import java.lang.reflect.Field;
import org.lwjgl.glfw.GLFW;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import io.github.faarma.handcuffsmod.HandcuffsMod;
import io.github.faarma.handcuffsmod.common.item.ItemUtils;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import io.github.faarma.handcuffsmod.common.network.packet.HandcuffedPlayerAnimationC2SPacket;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
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
     * The clickCount field is a private field in the KeyBinding class that holds the click count of a key binding.
     * It is used in the onKeyInputEvent method to cancel specific key actions when a player is handcuffed.
     */
    private static Field clickCountField = ObfuscationReflectionHelper.findField(KeyBinding.class, "field_151474_i");

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
     * Event handler for the RawMouseEvent, which occurs when a mouse event (like click) is detected.
     * This method is responsible for handling mouse input when a player is handcuffed, preventing certain actions.
     *
     * @param event The RawMouseEvent that occurred.
     */
    @SubscribeEvent
    public static void onRawMouseEvent(RawMouseEvent event) {
        Minecraft mc = Minecraft.getInstance();
        GameSettings gs = mc.options;
        // Yes, if the first is null it means that you are playing (no inventory, no chat, etc.).
        if (mc.screen != null || !ItemUtils.isPlayerCuffed(mc.player) || event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        /*
         * Mouse buttons that should be ignored if pressed
         * - Gameplay
         *   - Attack/Destroy
         *   - Use Item/Place Block
         */
        if (event.getButton() == gs.keyAttack.getKey().getValue() || event.getButton() == gs.keyUse.getKey().getValue()) {
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
        // If the player is crouching, the animation must be changed.
        if (event.getKey() == gs.keyShift.getKey().getValue()) {
            ModifierLayer<IAnimation> animation = ItemUtils.getPlayerAnimation(mc.player);
            if (event.getAction() == GLFW.GLFW_PRESS) {
                animation.setAnimation(ItemUtils.getHandcuffedSneakAnimation());
                NetworkMessages.sentToServer(new HandcuffedPlayerAnimationC2SPacket((byte) 1, mc.player.getUUID()));
            }
            if (event.getAction() == GLFW.GLFW_RELEASE) {
                animation.setAnimation(ItemUtils.getHandcuffedAnimation());
                NetworkMessages.sentToServer(new HandcuffedPlayerAnimationC2SPacket((byte) 0, mc.player.getUUID()));
            }
        }
        /*
         * Keys that should be ignored if pressed
         * - Movement
         *   - Jump
         *   - Strafe Left
         *   - Stafe Right
         *   - Walk Backwards
         *   - Walk Forwards
         * - Gameplay
         *   - Attack/Destroy
         *   - Pick Block
         *   - Use Item/Place Block
         * - Inventory
         *   - Drop Selected Item
         *   - Swap Item With Offhand
         *   - Hotbar Slot 1-9
         */
        KeyBinding[] blacklistKeys = {
            gs.keyJump, gs.keyLeft, gs.keyRight, gs.keyDown, gs.keyUp,
            gs.keyAttack, gs.keyPickItem, gs.keyUse,
            gs.keyDrop, gs.keySwapOffhand,
            gs.keyHotbarSlots[0], gs.keyHotbarSlots[1], gs.keyHotbarSlots[2],
            gs.keyHotbarSlots[3], gs.keyHotbarSlots[4], gs.keyHotbarSlots[5],
            gs.keyHotbarSlots[6], gs.keyHotbarSlots[7], gs.keyHotbarSlots[8]
        };
        // Attempt to cancel the action of the "blacklistKeys" key.
        for (KeyBinding keyBinding : blacklistKeys) {
            if (keyBinding.isDown()) {
                // Effectively, to cancel the pressed button I must change the clickCount...which is private.
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
