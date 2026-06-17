package net.mtm101.tinkeringwcreate.backtanks;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.util.function.Supplier;

public class TinkersBacktankBlockItem extends BacktankItem.BacktankBlockItem {
    public TinkersBacktankBlockItem(Block block, Supplier<Item> actualItem, Properties properties) {
        super(block, actualItem, properties);
    }
}
