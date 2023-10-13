package io.github.faarma.handcuffsmod.common.item;

import io.github.faarma.handcuffsmod.HandcuffsMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffsMod.ModID);

    private static final RegistryObject<Item> HANDCUFFS = ITEMS.register("handcuffs", () -> new HandcuffsItem());

    public static void Register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
