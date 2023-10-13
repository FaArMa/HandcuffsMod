package io.github.faarma.handcuffsmod;

import io.github.faarma.handcuffsmod.common.item.ItemInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HandcuffsMod.ModID)
public class HandcuffsMod {
    public static final String ModID = "handcuffsmod";

    public HandcuffsMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemInit.Register(modEventBus);
    }
}
