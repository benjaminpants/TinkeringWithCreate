package net.mtm101.tinkeringwcreate.registers;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.resources.ResourceLocation;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;

import static com.simibubi.create.foundation.block.connected.CTSpriteShifter.getCT;

public class ModSpriteShifts {
    public static final CTSpriteShiftEntry SMELTERY_TANK = getShift(AllCTTypes.RECTANGLE, "smeltery_tank"),
            SMELTERY_TANK_TOP = getShift(AllCTTypes.RECTANGLE, "smeltery_tank_top"),
            SMELTERY_TANK_INNER = getShift(AllCTTypes.RECTANGLE, "smeltery_tank_inner");

    private static CTSpriteShiftEntry getShift(CTType type, String blockTextureName) {
        return getCT(type, TinkeringWCreate.asResource("block/" + blockTextureName), TinkeringWCreate.asResource("block/" + blockTextureName + "_connected"));
    }
}
