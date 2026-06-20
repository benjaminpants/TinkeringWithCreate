package net.mtm101.tinkeringwcreate.registers;

import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.mtm101.tinkeringwcreate.backtanks.TinkerBacktankRenderer;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankBlockEntity;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankBlockEntity;
import net.mtm101.tinkeringwcreate.smelterytank.AbstractHeatingTankRenderer;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankBlockEntity;

import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;

public class ModBlockEntities
{
    public static final BlockEntityEntry<SmelteryTankBlockEntity> SMELTERY_TANK_BLOCK_ENTITY = REGISTRATE.blockEntity("smelterytank", SmelteryTankBlockEntity::new)
            .validBlock(ModBlocks.SMELTERY_TANK)
            .renderer(() -> AbstractHeatingTankRenderer::new)
            .register();

    public static final BlockEntityEntry<FoundryTankBlockEntity> FOUNDRY_TANK_BLOCK_ENTITY = REGISTRATE.blockEntity("foundrytank", FoundryTankBlockEntity::new)
            .validBlock(ModBlocks.FOUNDRY_TANK)
            .renderer(() -> AbstractHeatingTankRenderer::new)
            .register();

    public static final BlockEntityEntry<TinkersBacktankBlockEntity> TINKERS_BACKTANK = REGISTRATE
            .blockEntity("tinkers_backtank", TinkersBacktankBlockEntity::new)
            //.visual(() -> SingleAxisRotatingVisual::backtank)
            .validBlocks(ModBlocks.TINKERS_BACKTANK)
            .renderer(() -> TinkerBacktankRenderer::new)
            .register();

    // this is still weird as fuck
    public static void register()
    {

    }
}
