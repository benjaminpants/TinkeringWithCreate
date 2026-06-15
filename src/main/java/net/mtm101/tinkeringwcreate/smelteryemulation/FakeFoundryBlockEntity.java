package net.mtm101.tinkeringwcreate.smelteryemulation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.mtm101.tinkeringwcreate.foundrytank.FoundryTankBlockEntity;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankBlockEntity;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.SmelteryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;

import java.lang.reflect.Field;
import java.util.Collections;

// NEVER USE THIS AS AN ACTUAL BLOCK ENTITY!!
public class FakeFoundryBlockEntity extends FoundryBlockEntity implements IFakeSmeltery
{
    public FoundryTankBlockEntity myParent;
    public FakeMultitankFuelModule fakeFuelModule = new FakeMultitankFuelModule(this, () -> structure != null ? structure.getTanks() : Collections.emptyList());

    public FakeFoundryBlockEntity(FoundryTankBlockEntity myParent) {
        super(myParent.getBlockPos(),myParent.getBlockState());
        this.myParent = myParent;
        setLevel(myParent.getLevel());
        forceSet("fuelModule", fakeFuelModule);
    }


    public void publicTick()
    {
        if (!level.isClientSide)
        {
            serverTick(getLevel(), getBlockPos(), getBlockState());
        }
        else
        {
            clientTick(getLevel(), getBlockPos(), getBlockState());
        }
    }

    protected void forceSet(String name, Object toSet) {
        Field f;
        try {
            f = this.getClass().getSuperclass().getSuperclass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        f.setAccessible(true);
        try {
            f.set(this, toSet);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MultitankFuelModule getFuelModule() {
        return fakeFuelModule;
    }

    public void setTempAndRate(int temp, int rate)
    {
        fakeFuelModule.setTempAndRate(temp, rate);
    }

    public FakeFoundryBlockEntity(BlockPos pos, BlockState state) throws Exception {
        super(pos, state);
        throw new Exception("Attempted to use FakeFoundryBlockEntity as actual block entity!");
    }

    @Override
    protected MeltingModuleInventory createMeltingInventory() {
        MeltingModuleInventory toRet = super.createMeltingInventory();
        toRet.resize(1, dropItem);
        tank.setCapacity(1080);
        return toRet;
    }

    public void updateContentSize(int mb, int items)
    {
        tank.setCapacity(mb);
        meltingInventory.resize(items, dropItem);
    }

    @Override
    protected HeatingStructureMultiblock<?> createMultiblock() {
        return new FakeMultiblock(this);
    }

    @Override
    protected void checkStructure() {
        setStructure(fakeMultiblock.detectMultiblock(null,null,null));
    }

    @Override
    protected void setStructure(@Nullable HeatingStructureMultiblock.StructureData structure) {
        forceSet("structure", structure);
        //super.setStructure(structure);
    }

    FakeMultiblock fakeMultiblock;

    @Override
    public void setFakeMultiblock(FakeMultiblock us) {
        fakeMultiblock = us;
    }

    @Override
    protected void heat() {
        switch (tick % 4) {
            case 2:
                myParent.updateToClients(); // BAD PRACTICE! PLACEHOLDER! TODO: REMOVE
                break;
            case 3:
                return; // skip the final step as to avoid attempting to update the block state
        }
        super.heat();
    }

    @Override
    protected void dropItem(ItemStack stack) {
        // lets not
        //super.dropItem(stack);
    }

    @Override
    public void notifyChange(BlockPos pos, BlockState state) {
        super.notifyChange(pos, state);
        myParent.updateToClients();
    }

    @Override
    public void notifyFluidsChanged(FluidChange type, FluidStack fluid) {
        super.notifyFluidsChanged(type, fluid);
        myParent.updateToClients();
    }

    // called by inventory stuff, quick and dirty way to tell when inventory has changed
    @Override
    public void setChangedFast() {
        if (myParent == null) return;
        myParent.updateToClients();
    }
}
