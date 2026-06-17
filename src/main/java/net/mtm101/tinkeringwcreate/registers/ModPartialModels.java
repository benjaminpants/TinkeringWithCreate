package net.mtm101.tinkeringwcreate.registers;

import com.simibubi.create.Create;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;

public class ModPartialModels {
    public static final PartialModel TINKERS_BACKTANK_SHAFT = block("tinkers_backtank/block_shaft_input");

    // this is still weird as fuck
    public static void register()
    {

    }

    @SuppressWarnings("removal")
    private static PartialModel block(String path) {
        return PartialModel.of(new ResourceLocation(TinkeringWCreate.MOD_ID, "block/" + path));
    }
}
