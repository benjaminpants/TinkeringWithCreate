package net.mtm101.tinkeringwcreate.registers;


import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankBlock;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankBlock;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankBlockEntity;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankItem;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankModel;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankBlock;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankItem;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankModel;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;

public class ModBlocks
{
    public static final BlockEntry<SmelteryTankBlock> SMELTERY_TANK = REGISTRATE.block("smeltery_tank", SmelteryTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .onRegister(REGISTRATE.blockModel(() -> SmelteryTankModel::standard))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(SmelteryTankItem::new)
            .build()
            .register();

    public static final BlockEntry<FoundryTankBlock> FOUNDRY_TANK = REGISTRATE.block("foundry_tank", FoundryTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .onRegister(REGISTRATE.blockModel(() -> FoundryTankModel::standard))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(FoundryTankItem::new)
            .build()
            .register();

    public static final BlockEntry<TinkersBacktankBlock> TINKERS_BACKTANK =
            REGISTRATE.block("tinkers_backtank", TinkersBacktankBlock::new)
                    .initialProperties(SharedProperties::copperMetal)
                    //.transform(BuilderTransformers.backtank(AllItems.COPPER_BACKTANK::get))
                    .register();


    // this is still weird as fuck
    public static void register()
    {
        //Blocks.GLASS
    }
}
