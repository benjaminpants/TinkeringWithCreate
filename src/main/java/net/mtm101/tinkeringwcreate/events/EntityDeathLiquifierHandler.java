package net.mtm101.tinkeringwcreate.events;

import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static net.minecraftforge.fluids.capability.IFluidHandler.*;

public class EntityDeathLiquifierHandler {
    public static int BLOCKS_TO_CHECK = 6;


    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event)
    {
        LivingEntity ent = event.getEntity();
        Level level = ent.level();
        if (level.isClientSide()) return; // dont
        if ((ent.wasOnFire || ent.isOnFire()) || ent.fireImmune()) {
            EntityMeltingRecipe recipe = findRecipe(level, ent.getType());
            if (recipe == null) return; // mob has no melt recipe, do not bother
            ArrayList<BlockPos> potentialFluidDrains = new ArrayList<>();
            // check a 3x3 area around where the mob died
            BlockPos onPos = ent.getOnPos();
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    SearchDown(level, new BlockPos(onPos.getX() + x, onPos.getY(), onPos.getZ() + z), potentialFluidDrains);
                }
            }
            if (potentialFluidDrains.isEmpty()) return; // no drains
            FluidStack fluid = recipe.getOutput(ent);
            RandomSource random = level.getRandom();
            while ((!fluid.isEmpty()) && !potentialFluidDrains.isEmpty())
            {
                BlockPos chosenFluidDrain = potentialFluidDrains.get(random.nextInt(potentialFluidDrains.size()));
                ItemDrainBlockEntity blockEnt = (ItemDrainBlockEntity)level.getBlockEntity(chosenFluidDrain);
                SmartFluidTankBehaviour smartTank = blockEnt.getBehaviour(SmartFluidTankBehaviour.TYPE);
                smartTank.allowInsertion();
                // only insert a bit at a time
                FluidStack amountToInsert = new FluidStack(fluid, random.nextIntBetweenInclusive(Math.min(5,fluid.getAmount()),fluid.getAmount()));

                int amountAbleToBeFilled = smartTank.getPrimaryHandler().fill(amountToInsert, FluidAction.SIMULATE);
                if (amountAbleToBeFilled != 0) {
                    int amountFilled = smartTank.getPrimaryHandler().fill(new FluidStack(fluid, amountAbleToBeFilled), FluidAction.EXECUTE);
                    fluid.setAmount(fluid.getAmount() - amountFilled);
                }
                else
                {
                    // block refused any fill, assume full and don't try again
                    potentialFluidDrains.remove(chosenFluidDrain);
                }
                smartTank.forbidInsertion();
            }
        }
    }

    private static EntityMeltingRecipe findRecipe(Level level, EntityType<?> type) {
        return EntityMeltingRecipeCache.findRecipe(level.getRecipeManager(), type);
    }

    private static void SearchDown(Level level, BlockPos startPos, ArrayList<BlockPos> potentialFluidDrains)
    {
        int tried = 0;
        while (tried < BLOCKS_TO_CHECK)
        {
            BlockPos currentPos = new BlockPos(startPos.getX(), startPos.getY() - tried, startPos.getZ());
            BlockState curState = level.getBlockState(currentPos);
            tried++;
            if (curState.canBeReplaced()) continue; // pointless air blocks/torches
            if (curState.getBlock() instanceof ItemDrainBlock)
            {
                potentialFluidDrains.add(currentPos);
            }
            return;
        }
    }
}
