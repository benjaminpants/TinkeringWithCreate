package net.mtm101.tinkeringwcreate.registers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankArmorLayer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance()
                .getEntityRenderDispatcher();
        TinkersBacktankArmorLayer.registerOnAll(dispatcher);
    }
}