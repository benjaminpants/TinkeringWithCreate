package net.mtm101.tinkeringwcreate.registers;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.mtm101.tinkeringwcreate.TinkeringWCreate;
import net.mtm101.tinkeringwcreate.backtanks.BacktankCaseMaterialStats;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankBlockItem;
import net.mtm101.tinkeringwcreate.backtanks.TinkersBacktankItem;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;

public class ModItems
{
    //public static final ModifiableArmorMaterial BACKTANKS = new ModifiableArmorMaterial();

    public static final ItemEntry<Item> CRUSHED_COBALT = REGISTRATE.item("crushed_raw_cobalt", Item::new)
            //.tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
            .register();

    public static final ItemEntry<Item> ANDESITE_ALLOY_NUGGET = REGISTRATE.item("andesite_alloy_nugget", Item::new)
            .register();

    public static final ItemEntry<ToolPartItem> BACKTANK_CASE = REGISTRATE.item("backtank_case", (p) -> new ToolPartItem(p, BacktankCaseMaterialStats.INSTANCE.getId()))
            .register();

    public static final ItemEntry<ToolPartItem> BACKTANK_STRAPS = REGISTRATE.item("backtank_straps", (p) -> new ToolPartItem(p, StatlessMaterialStats.MAILLE.getIdentifier()))
            .register();

    public static final ItemEntry<TinkersBacktankBlockItem> TINKERS_BACKTANK_PLACEABLE = REGISTRATE
            .item("tinkers_backtank_placeable",
                    p -> new TinkersBacktankBlockItem(ModBlocks.TINKERS_BACKTANK.get(), ModItems.TINKERS_BACKTANK::get, p))
            .register();

    @SuppressWarnings("deprecation")
    public static final ItemEntry<TinkersBacktankItem> TINKERS_BACKTANK = REGISTRATE
            .item("tinkers_backtank",
                    p -> new TinkersBacktankItem(p, ToolDefinition.create(new ResourceLocation(TinkeringWCreate.MOD_ID, "tinkers_backtank")),() -> TINKERS_BACKTANK_PLACEABLE.get())) // TODO: PLACEHOLDER! PLATE IS PLACEHOLDER! MAKE CUSTOM ONE!
            //.model(AssetLookup.customGenericItemModel("_", "item"))
            .tag(AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            //.tag(ItemTags.CHEST_ARMOR)
            .register();

    // this is still weird as fuck
    public static void register()
    {

    }
}
