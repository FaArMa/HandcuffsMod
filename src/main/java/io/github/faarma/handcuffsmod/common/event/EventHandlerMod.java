package io.github.faarma.handcuffsmod.common.event;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import io.github.faarma.handcuffsmod.HandcuffsMod;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Event handler for events related to the mod that can occur on both the client-side and server-side.
 * <br>
 * Client-Side: These events do not have access to net.minecraft.server or net.minecraftforge.server.
 * <br>
 * Server-Side: These events do not have access to net.minecraft.client or net.minecraftforge.client.
 */
@Mod.EventBusSubscriber(modid = HandcuffsMod.ModID, bus = Bus.MOD)
public class EventHandlerMod {
    /**
     * Event handler for the {@link FMLClientSetupEvent}, which occurs during client setup.
     *
     * @param event The {@link FMLClientSetupEvent} that occurred.
     */
    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(HandcuffsMod.ModID, "animation"),
                42,
                (AbstractClientPlayerEntity player) -> {return new ModifierLayer<IAnimation>();}
        );
    }
}
