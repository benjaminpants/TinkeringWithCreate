package net.mtm101.tinkeringwcreate.mixins;

import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.state.BlockState;
import net.mtm101.tinkeringwcreate.registers.ModBlocks;
import net.mtm101.tinkeringwcreate.registers.ModPartialModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BacktankRenderer.class)
public class GetShaftModelMixin {
    @Inject(method = "getShaftModel", at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void getShaftModelMixin(BlockState state, CallbackInfoReturnable<PartialModel> cir) {
        if (ModBlocks.TINKERS_BACKTANK.has(state))
        {
            cir.setReturnValue(ModPartialModels.TINKERS_BACKTANK_SHAFT);
        }
    }

}
