package net.mtm101.tinkeringwcreate.smelteryemulation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockResult;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockStructureData;

import java.util.HashSet;
import java.util.function.Consumer;

public class FakeMultiblock extends HeatingStructureMultiblock {

    public StructureData fakeData;
    public FakeMultiblock(HeatingStructureBlockEntity parent) {
        super(parent, false, true, false, 1, 1);
        tanks.add(new BlockPos(0,0,0)); // test
        fakeData = create(new BlockPos(0,0,0), new BlockPos(0,0,0), new HashSet<>());
        // unchecked cast could throw exception BUT we know what we are doing
        ((IFakeSmeltery)parent).setFakeMultiblock(this);
    }

    @Override
    protected boolean isValidBlock(Block block) {
        return true;
    }

    @Override
    protected boolean isValidFloor(Block block) {
        return true;
    }

    @Override
    protected boolean isValidTank(Block block) {
        return false;
    }

    @Override
    protected boolean isValidWall(Block block) {
        return true;
    }


    // suppress updateErrorPos(which will kill us violently) calls by always acting like we can expand
    @Override
    public boolean canExpand(StructureData data, Level world) {
        return true;
    }

    @Override
    public boolean shouldUpdate(Level world, MultiblockStructureData structure, BlockPos pos, BlockState state) {
        return false; // no
    }

    @Override
    public @Nullable StructureData detectMultiblock(Level world, BlockPos master, Direction facing) {
        return fakeData;
    }
}
