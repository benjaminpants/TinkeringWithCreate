package net.mtm101.tinkeringwcreate.foundrytank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.mtm101.tinkeringwcreate.registers.ModBlockEntities;

public class FoundryTankItem extends BlockItem {
    public FoundryTankItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos p_195943_1_, Level p_195943_2_, Player p_195943_3_,
                                                 ItemStack p_195943_4_, BlockState p_195943_5_) {
        MinecraftServer minecraftserver = p_195943_2_.getServer();
        if (minecraftserver == null)
            return false;
        CompoundTag nbt = p_195943_4_.getTagElement("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
        }
        return super.updateCustomBlockEntityTag(p_195943_1_, p_195943_2_, p_195943_3_, p_195943_4_, p_195943_5_);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        if (!face.getAxis()
                .isVertical())
            return;
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        /*if (!SmelteryTankBlock.isTank(placedOnState))
            return;*/
        if (SymmetryWandItem.presentInHotbar(player))
            return;
        FoundryTankBlockEntity tankAt = ConnectivityHandler.partAt(
                ModBlockEntities.FOUNDRY_TANK_BLOCK_ENTITY.get(), world, placedOnPos);
        if (tankAt == null)
            return;
        FoundryTankBlockEntity controllerBE = (FoundryTankBlockEntity)tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.width;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerBE.getBlockPos()
                .below()
                : controllerBE.getBlockPos()
                .above(controllerBE.height);

        if (startPos.getY() != pos.getY())
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (FoundryTankBlock.isFoundryTank(blockState))
                    continue;
                if (!blockState.canBeReplaced())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (FoundryTankBlock.isFoundryTank(blockState))
                    continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
                player.getPersistentData()
                        .putBoolean("SilenceTankSound", true);
                super.place(context);
                player.getPersistentData()
                        .remove("SilenceTankSound");
            }
        }
    }
}
