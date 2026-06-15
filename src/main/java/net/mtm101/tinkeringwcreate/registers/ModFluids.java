package net.mtm101.tinkeringwcreate.registers;

import com.simibubi.create.AllFluids;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;
import org.joml.Vector3f;
import slimeknights.mantle.block.fluid.BurningLiquidBlock;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.FluidDeferredRegisterExtension;

import java.util.function.Supplier;

import static net.mtm101.tinkeringwcreate.TinkeringWCreate.MOD_ID;
import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;
import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.fluids.block.BurningLiquidBlock.createBurning;
import static slimeknights.tconstruct.fluids.block.MobEffectLiquidBlock.createEffect;

// I HATE YOU
public class ModFluids {
    public static final FluidDeferredRegisterExtension fluidRegister = new FluidDeferredRegisterExtension(MOD_ID);

    public static final FlowingFluidObject<ForgeFlowingFluid> moltenAndesiteAlloy = fluidRegister.registerMetal("molten_andesite_alloy").type(hot("molten_andesite_alloy").temperature(600).lightLevel(12)).block(createBurning(MapColor.RAW_IRON, 12, 10, 5f)).bucket().commonTag().flowing();

    /*
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_ANDESITE_ALLOY =
            REGISTRATE.standardFluid("molten_andesite_alloy", createHot("molten_andesite_alloy"))
                    .source(ForgeFlowingFluid.Source::new)
                    .block()
                    .build()
                    .bucket()
                    .build()
                    .register();
    */
    public static void register()
    {

    }


    /*
    private static FluidBuilder.FluidTypeFactory createHot(String name) {
        return (p, s, f) -> {
            FluidType fluidType = new FluidType(hot(name));
            BurningLiquidBlock.createBurning(MapColor.COLOR_BLUE, 12, 10, 5f);
            return fluidType;
        };
    }*/

    private static FluidType.Properties hot(String name) {
        return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000)
                .descriptionId("fluid." + TinkeringWCreate.MOD_ID + "." + name)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                // from forge lava type
                .motionScale(0.0023333333333333335D)
                .canSwim(false).canDrown(false)
                .pathType(BlockPathTypes.LAVA).adjacentPathType(null);
    }
}
