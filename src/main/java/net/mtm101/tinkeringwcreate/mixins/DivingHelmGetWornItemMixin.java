package net.mtm101.tinkeringwcreate.mixins;

import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.mtm101.tinkeringwcreate.divinghelmet.TinkersDivingHelmetItem;
import net.mtm101.tinkeringwcreate.registers.ModBlocks;
import net.mtm101.tinkeringwcreate.registers.ModPartialModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;

@Mixin(DivingHelmetItem.class)
public class DivingHelmGetWornItemMixin {
    @Inject(method = "getWornItem", at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private static void getWornItemMixin(Entity entity, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof TinkersDivingHelmetItem)) return;
        if (ToolDamageUtil.isBroken(cir.getReturnValue())) { cir.setReturnValue(ItemStack.EMPTY); }
    }
}
