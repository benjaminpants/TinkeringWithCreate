package net.mtm101.tinkeringwcreate.foundrytank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mtm101.tinkeringwcreate.registers.ModBlockEntities;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;

public class FoundryTankBlock extends Block implements IWrenchable, IBE<FoundryTankBlockEntity>
{
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<FluidTankBlock.Shape> SHAPE = EnumProperty.create("shape", FluidTankBlock.Shape.class);
    public static final BooleanProperty IN_STRUCTURE = ControllerBlock.IN_STRUCTURE;

    public static final int ITEMS_PER_BLOCK = 8;
    public static final int MB_PER_BLOCK = 2160;


    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        boolean x = mirror == Mirror.FRONT_BACK;
        switch (state.getValue(SHAPE)) {
            case WINDOW_NE:
                return state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SE);
            case WINDOW_NW:
                return state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SW);
            case WINDOW_SE:
                return state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_SW : FluidTankBlock.Shape.WINDOW_NE);
            case WINDOW_SW:
                return state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_SE : FluidTankBlock.Shape.WINDOW_NW);
            default:
                return state;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        switch (state.getValue(SHAPE)) {
            case WINDOW_NE:
                return state.setValue(SHAPE, FluidTankBlock.Shape.WINDOW_SE);
            case WINDOW_NW:
                return state.setValue(SHAPE, FluidTankBlock.Shape.WINDOW_NE);
            case WINDOW_SE:
                return state.setValue(SHAPE, FluidTankBlock.Shape.WINDOW_SW);
            case WINDOW_SW:
                return state.setValue(SHAPE, FluidTankBlock.Shape.WINDOW_NW);
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_)
    {
        p_206840_1_.add(TOP, BOTTOM, SHAPE, IN_STRUCTURE);
    }

    public FoundryTankBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, FluidTankBlock.Shape.WINDOW)
                .setValue(IN_STRUCTURE, true));
    }

    public static boolean isFoundryTank(BlockState state) {
        return state.getBlock() instanceof FoundryTankBlock;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    public Class<FoundryTankBlockEntity> getBlockEntityClass() {
        return FoundryTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FoundryTankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FOUNDRY_TANK_BLOCK_ENTITY.get();
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, FoundryTankBlockEntity::updateConnectivity);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData()
                .contains("SilenceTankSound"))
            return FluidTankBlock.SILENCED_METAL;
        return soundType;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FoundryTankBlockEntity tankBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }
}
