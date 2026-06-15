package net.mtm101.tinkeringwcreate.registers;
import com.simibubi.create.AllTags.AllItemTags;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static net.mtm101.tinkeringwcreate.TinkeringWCreate.REGISTRATE;

public class ModItems
{
    public static final ItemEntry<Item> CRUSHED_COBALT = REGISTRATE.item("crushed_raw_cobalt", Item::new)
            //.tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
            .register();

    public static final ItemEntry<Item> ANDESITE_ALLOY_NUGGET = REGISTRATE.item("andesite_alloy_nugget", Item::new)
            .register();

    // this is still weird as fuck
    public static void register()
    {

    }
}
