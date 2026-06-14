package net.mtm101.tinkeringwcreate.smelteryemulation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;

import java.util.List;
import java.util.function.Supplier;

public class FakeMultitankFuelModule extends MultitankFuelModule
{
    public void setTempAndRate(int temperature, int rate)
    {
        this.temperature = temperature;
        this.rate = rate;
    }

    public FakeMultitankFuelModule(MantleBlockEntity parent, Supplier<List<BlockPos>> tankSupplier) {
        super(parent, tankSupplier);
        temperature = 0;
        rate = 0;

    }

    @Override
    public int findFuel(boolean consume) {
        return temperature;
    }

    @Override
    public boolean hasFuel() {
        return temperature != 0;
    }

    @Override
    public void decreaseFuel(int amount) {
        // do nothing
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void ensureTankPresent(BlockEntity be) {

    }

    @Override
    public int getRate() {
        return rate;
    }
}
