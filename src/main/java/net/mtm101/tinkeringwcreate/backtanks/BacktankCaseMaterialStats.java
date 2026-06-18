package net.mtm101.tinkeringwcreate.backtanks;

import net.minecraft.network.chat.Component;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;

import java.util.List;

public record BacktankCaseMaterialStats(MaterialStatType<?> getType, float armor, float toughness, float knockbackResistance) implements IMaterialStats {

    private static final RecordLoadable<BacktankCaseMaterialStats> LOADABLE = RecordLoadable.create(
            MaterialStatType.CONTEXT_KEY.requiredField(),
            FloatLoadable.FROM_ZERO.defaultField("armor", 0f, true, BacktankCaseMaterialStats::armor),
            FloatLoadable.FROM_ZERO.defaultField("toughness", 0f, true, BacktankCaseMaterialStats::toughness),
            FloatLoadable.FROM_ZERO.defaultField("knockback_resistance", 0f, true, BacktankCaseMaterialStats::knockbackResistance),
            BacktankCaseMaterialStats::new);

    public static MaterialStatType<BacktankCaseMaterialStats> INSTANCE = new MaterialStatType<BacktankCaseMaterialStats>(new MaterialStatsId(TinkeringWCreate.MOD_ID, "backtank_case"), type -> new BacktankCaseMaterialStats(type, 0,0,0), LOADABLE );

    private static final List<Component> DESCRIPTION = List.of(
            ToolStats.ARMOR.getDescription(),
            ToolStats.ARMOR_TOUGHNESS.getDescription(),
            ToolStats.KNOCKBACK_RESISTANCE.getDescription());

    /**
     * Returns the stat type, which is used for parsing the stat and getting default stats.
     */
    @Override
    public MaterialStatType<?> getType() {
        return getType;
    }

    /**
     * Returns a list containing a String for each player-relevant value.</br>
     * Each line should consist of the name of the value followed by the value itself.</br>
     * Example: "Durability: 25"</br>
     * </br>
     * This is used to display properties of materials to the user.
     */
    @Override
    public List<Component> getLocalizedInfo() {
        Component toughness = ToolStats.ARMOR_TOUGHNESS.formatValue(this.toughness);
        Component knockbackResistance = ToolStats.KNOCKBACK_RESISTANCE.formatValue(this.knockbackResistance * 10); // multiply by 10 as vanilla multiplies toughness by 10 for display
        return List.of(ToolStats.ARMOR.formatValue(this.armor), toughness, knockbackResistance);
    }

    /**
     * Returns a list containing a Text Component describing each player-relevant value.</br>
     * The indices of the lines must line up with the lines from getLocalizedInfo()!</br>
     * *
     * This is used to display properties of materials to the user.
     *
     * @return a list of Text Components
     */
    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    /**
     * Applies this stat to the builder
     *
     * @param builder Builder instance
     * @param scale   Scaling factor for applying these stats, used to allow multiple stats of the same type to exist on one tool
     */
    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        ToolStats.ARMOR.update(builder, armor * scale);
        ToolStats.ARMOR_TOUGHNESS.update(builder, toughness * scale);
        ToolStats.KNOCKBACK_RESISTANCE.update(builder, knockbackResistance * scale);
    }
}
