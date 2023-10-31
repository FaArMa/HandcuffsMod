package io.github.faarma.handcuffsmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.faarma.handcuffsmod.common.item.ItemInit;
import io.github.faarma.handcuffsmod.common.network.NetworkMessages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class of the Handcuffs Mod.
 */
@Mod(HandcuffsMod.MOD_ID)
public class HandcuffsMod {
    /**
     * The Mod ID, which should match the entry in the META-INF/mods.toml file.
     */
    public static final String MOD_ID = "handcuffsmod";
    /**
     * Directly reference a log4j logger for logging purposes.
     */
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Constructor for the HandcuffsMod class.
     */
    public HandcuffsMod() {
        // Get the mod event bus for registration.
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register network packets for the mod.
        NetworkMessages.registerPackets();
        // Register custom items using the ItemInit class.
        ItemInit.register(modEventBus);
    }
}
