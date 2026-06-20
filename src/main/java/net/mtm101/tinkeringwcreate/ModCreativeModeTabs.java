package net.mtm101.tinkeringwcreate;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mtm101.tinkeringwcreate.registers.ModBlocks;
import net.mtm101.tinkeringwcreate.registers.ModItems;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModCreativeModeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkeringWCreate.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MOD_TABS.register("tinkeringwcreate",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SMELTERY_TANK.asItem()))
                    .title(Component.translatable("creativetab.tinkeringwcreate"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.SMELTERY_TANK.asItem());
                        pOutput.accept(ModBlocks.FOUNDRY_TANK.asItem());
                        pOutput.accept(ModItems.CRUSHED_COBALT.get());
                        pOutput.accept(ModItems.ANDESITE_ALLOY_NUGGET.get());
                        Consumer<ItemStack> output = pOutput::accept;
                        acceptTool(output, ModItems.TINKERS_BACKTANK);
                        acceptTool(output, ModItems.DIVING_HELMET);
                        acceptPart(output, ModItems.BACKTANK_CASE);
                        acceptPart(output, ModItems.BACKTANK_STRAPS);
                        pOutput.accept(ModItems.BACKTANK_CASE_GOLD_CAST.get());
                        pOutput.accept(ModItems.BACKTANK_STRAPS_GOLD_CAST.get());
                        pOutput.accept(ModItems.BACKTANK_CASE_SAND_CAST.get());
                        pOutput.accept(ModItems.BACKTANK_STRAPS_SAND_CAST.get());
                        pOutput.accept(ModItems.BACKTANK_CASE_RED_SAND_CAST.get());
                        pOutput.accept(ModItems.BACKTANK_STRAPS_RED_SAND_CAST.get());
                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MOD_TABS.register(eventBus);
    }

    /** Adds a tool to the tab */
    private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
        ToolBuildHandler.addVariants(output, tool.get(), "");
    }

    /** Adds a tool to the tab */
    private static void acceptTools(Consumer<ItemStack> output, EnumObject<?,? extends IModifiable> tools) {
        tools.forEach(tool -> ToolBuildHandler.addVariants(output, tool, ""));
    }

    private static void acceptPart(Consumer<ItemStack> output, Supplier<? extends IMaterialItem> item) {
        item.get().addVariants(output, "");
    }
}