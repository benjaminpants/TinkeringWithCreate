package net.mtm101.tinkeringwcreate.smelteryemulation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

public interface IFakeSmeltery {
    void setFakeMultiblock(FakeMultiblock us);
    void publicTick();
    void updateContentSize(int mb, int items);
    MeltingModuleInventory getMeltingInventory();
    SmelteryTank<HeatingStructureBlockEntity> getTank();
    void saveSynced(CompoundTag compound);
    void saveAdditional(CompoundTag compound);
    void load(CompoundTag nbt);
    void setLevel(Level pLevel);
    MultitankFuelModule getFuelModule();
    void setTempAndRate(int temp, int rate);
}
