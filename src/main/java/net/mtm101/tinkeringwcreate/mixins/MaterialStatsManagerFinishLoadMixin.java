package net.mtm101.tinkeringwcreate.mixins;


import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.mtm101.tinkeringwcreate.backtanks.BacktankCaseMaterialStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsManager;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;

import java.util.Map;


@Mixin(MaterialStatsManager.class)
public class MaterialStatsManagerFinishLoadMixin {

    @Inject(method = "finishLoad", at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void finishLoadMixin(Map<ResourceLocation, Map<ResourceLocation, JsonObject>> map, ResourceManager manager, CallbackInfo ci)
    {
        map.forEach((res, subMap) -> {
            if (subMap.containsKey(BacktankCaseMaterialStats.INSTANCE.getId())) return; // material has manual specifications for backtank case, ignore.
            if (!subMap.containsKey(PlatingMaterialStats.CHESTPLATE.getId())) return;
            System.out.println("Found chestplate for: " + res.toString());
            // any material that supports chestplate automatically supports Backtank Case
            subMap.put(BacktankCaseMaterialStats.INSTANCE.getId(), subMap.get(PlatingMaterialStats.CHESTPLATE.getId())); // make a copy
            map.put(res, subMap); // ??
        });
    }
}
