package io.github.faarma.handcuffsmod.common.item;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The ItemInit class is responsible for initializing and registering custom items in Minecraft Forge.
 * It uses the DeferredRegister system provided by Forge to defer the registration of items until
 * the appropriate event bus is available. This helps ensure that item registration occurs at the
 * correct time during the game's initialization process.
 */
public class ItemInit {
    /**
     * A DeferredRegister instance for items.
     * This allows for the deferred registration of items with ForgeRegistries.ITEMS.
     */
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffsMod.ModID);

    /**
     * A RegistryObject that represents the HANDCUFFS item.
     * This object is used to register and retrieve the item during the game's initialization.
     */
    private static final RegistryObject<Item> HANDCUFFS = ITEMS.register("handcuffs", () -> new HandcuffsItem());

    /**
     * Registers the items defined in this class with the specified event bus.
     * This method should be called during the game's initialization to ensure that the items are registered properly.
     *
     * @param modEventBus The event bus on which the item registration should occur.
     */
    public static void Register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
