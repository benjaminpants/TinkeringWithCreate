package net.mtm101.tinkeringwcreate;

import com.mojang.logging.LogUtils;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankArmorLayer;
import net.mtm101.tinkeringwcreate.registers.*;
import org.slf4j.Logger;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(TinkeringWCreate.MOD_ID)
public class TinkeringWCreate
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "tinkeringwcreate";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public TinkeringWCreate(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModFluids.register();
        ModPartialModels.register();
        ModFluids.fluidRegister.register(modEventBus);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
    }
}