package net.mtm101.tinkeringwcreate.foundrytank;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.mtm101.tinkeringwcreate.smelteryemulation.FakeFoundryBlockEntity;
import net.mtm101.tinkeringwcreate.smelteryemulation.FakeSmelteryBlockEntity;
import net.mtm101.tinkeringwcreate.smelteryemulation.IFakeSmeltery;
import net.mtm101.tinkeringwcreate.smelterytank.AbstractHeatingTankBlockEntity;
import net.mtm101.tinkeringwcreate.smelterytank.SmelteryTankBlock;


// plenty of code used from create fluid tanks here.
// quite literally my first ever block entity and it is something of this magnitude.
public class FoundryTankBlockEntity extends AbstractHeatingTankBlockEntity
{

    public FoundryTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public IFakeSmeltery createNewFakeEntity() {
        return new FakeFoundryBlockEntity(this);
    }

    @Override
    public int getMillibucketsPerBlock() {
        return FoundryTankBlock.MB_PER_BLOCK;
    }

    @Override
    public int getItemsPerBlock() {
        return FoundryTankBlock.ITEMS_PER_BLOCK;
    }

    @Override
    public boolean blockstateIsSelf(BlockState state) {
        return FoundryTankBlock.isFoundryTank(state);
    }

    @Override
    public String getGoggleName() {
        return "gui.goggles.foundry";
    }
}
