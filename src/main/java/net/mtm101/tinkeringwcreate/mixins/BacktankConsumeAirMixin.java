package net.mtm101.tinkeringwcreate.mixins;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(BacktankUtil.class)
public class BacktankConsumeAirMixin {
    @Inject(method = "consumeAir", at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void consumeAir(LivingEntity entity, ItemStack backtank, float i, CallbackInfo ci)
    {
        if (!(backtank.getItem() instanceof TinkersBacktankItem)) return; // not relevant for us
        if (BacktankUtil.getAir(backtank) >= 10) return;
        ToolStack tool = ToolStack.from(backtank);
        int amount = (int) Math.floor(i);
        if (amount == 0) return;
        for (ModifierEntry entry : tool.getModifierList()) {
            amount = entry.getHook(ModifierHooks.TOOL_DAMAGE).onDamageTool(tool, entry, amount, entity, backtank);
            // if no more damage, done
            if (amount <= 0) {
                if (entity instanceof ServerPlayer player) {
                    AllSoundEvents.CONTROLLER_CLICK.play(player.level(), null, player.blockPosition(), 1f, 1.5f);
                }
                ci.cancel();
                return;
            }
        }
    }
}
