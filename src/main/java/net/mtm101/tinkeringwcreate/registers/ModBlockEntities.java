package net.mtm101.tinkeringwcreate.registers;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankBlockEntity;

import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;

public class ModBlockEntities
{
    public static final BlockEntityEntry<SmelteryTankBlockEntity> SMELTERY_TANK_BLOCK_ENTITY = REGISTRATE.blockEntity("smelterytank", SmelteryTankBlockEntity::new)
            .validBlock(ModBlocks.SMELTERY_TANK)
            .register();

    // this is still weird as fuck
    public static void register()
    {

    }
}
