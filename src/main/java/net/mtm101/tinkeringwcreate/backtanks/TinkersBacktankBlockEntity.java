package net.mtm101.tinkeringwcreate.backtanks;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.openjdk.nashorn.internal.objects.annotations.Getter;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import javax.annotation.Nonnull;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.nbt.ToolStack.TAG_MATERIALS;

public class TinkersBacktankBlockEntity extends BacktankBlockEntity {
    @Nonnull
    private MaterialIdNBT material = MaterialIdNBT.EMPTY;

    public TinkersBacktankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        requestModelDataUpdate();
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(ModelProperties.MATERIALS, material).build();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        material = Objects.requireNonNullElse(MaterialIdNBT.readFromNBT(getVanillaTag().get(TAG_MATERIALS)), MaterialIdNBT.EMPTY);
        RetexturedHelper.onTextureUpdated(this);
    }
}
