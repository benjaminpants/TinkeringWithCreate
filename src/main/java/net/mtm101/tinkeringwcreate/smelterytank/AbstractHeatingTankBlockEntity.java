package net.mtm101.tinkeringwcreate.smelterytank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.mtm101.tinkeringwcreate.smelteryemulation.IFakeSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;


// plenty of code used from create fluid tanks here.
// TODO: How do I actually detect when multiple controllers have merged into one? Create's functionality seems to be quite hard coded
public abstract class AbstractHeatingTankBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer.Inventory, IMultiBlockEntityContainer.Fluid, IHaveGoggleInformation
{

    protected BlockPos controller;
    protected BlockPos lastKnownPos;

    public int width;
    public int height;
    protected boolean updateConnectivity;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    protected boolean window;
    protected int luminosity;

    protected IFakeSmeltery fakeEntity;

    protected LazyOptional<IItemHandler> itemCapability;
    protected LazyOptional<IFluidHandler> fluidCapability;

    protected boolean updateCapability;

    public AbstractHeatingTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        width = 1;
        height = 1;
        window = true;
        updateConnectivity = false;
        updateCapability = false;
        fakeEntity = createNewFakeEntity();
        itemCapability = LazyOptional.of(() -> getFakeEntity().getMeltingInventory());
        fluidCapability = LazyOptional.of(() -> getFakeEntity().getTank());
        updateSize(1);
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if (fakeEntity != null)
        {
            fakeEntity.setLevel(pLevel);
        }
    }

    // window stuff
    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean)
            window = (boolean) data;
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean windows) {
            windows |= window;
            return windows;
        }
        return data;
    }


    public void refreshCapability()
    {
        itemCapability.invalidate();
        fluidCapability.invalidate();
        itemCapability = LazyOptional.of(() -> getFakeEntity().getMeltingInventory());
        fluidCapability = LazyOptional.of(() -> getFakeEntity().getTank());
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Size", width);
            compound.putInt("Height", height);
            compound.putBoolean("Window", window);
            if (fakeEntity != null) {
                CompoundTag myTag = new CompoundTag();
                fakeEntity.saveSynced(myTag);
                fakeEntity.saveAdditional(myTag);
                compound.put("SmelteryData", myTag);
            }
        }
        super.write(compound, clientPacket);
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        refreshController();
        ConnectivityHandler.formMulti(this);
    }

    public void refreshController()
    {
        if (!isController())
            return;
        updateSize(width * width * height);
        setChanged();
    }

    // TODO: change hardcoded values to refer to a config so modpack devs don't kill me
    public void reCalcHeat()
    {
        if (fakeEntity == null) return;
        int newTemp = 0;
        int newRate = 0;
        int newMinRate = 0;
        var bPos = getBlockPos();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                BlockPos calculatedPos = new BlockPos(bPos.getX() + x,bPos.getY() - 1, bPos.getZ() + z);
                BlazeBurnerBlock.HeatLevel hl = BasinBlockEntity.getHeatLevelOf(level.getBlockState(calculatedPos));
                int heatToChangeTo;
                int rateAdd;
                int minRate;
                switch (hl)
                {
                    case FADING, KINDLED -> { heatToChangeTo = 1000; minRate = 5; rateAdd = 5; }
                    case SEETHING -> { heatToChangeTo = 2000; minRate = 5; rateAdd = 10; }
                    default -> { heatToChangeTo = 0; minRate = 0; rateAdd = 0;}
                }
                // cant handle LOW in a switch
                if (hl == BlazeBurnerBlock.HeatLevel.valueOf("LOW")) {
                    heatToChangeTo = 800;
                    rateAdd = 2;
                    minRate = 6;
                }
                newTemp = max(heatToChangeTo, newTemp);
                newRate += rateAdd;
                newMinRate = max(minRate, newMinRate);
            }
        }
        getFakeEntity().setTempAndRate(newTemp, newRate + newMinRate);
    }

    public void updateToClients()
    {
        sendData();
        //level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        controller = null;
        lastKnownPos = null;

        updateConnectivity = compound.contains("Uninitialized");

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller")) {
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));
        }

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            window = compound.getBoolean("Window");
            if (compound.contains("SmelteryData"))
            {
                if (fakeEntity == null) {
                    fakeEntity = createNewFakeEntity();
                }
                fakeEntity.load(compound.getCompound("SmelteryData"));
            }
        }
        else
        {
            fakeEntity = null; //??
        }

        if (!clientPacket)
            return;

        boolean changeOfController = !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            invalidateRenderBoundingBox();
        }

    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    // what does this even DO
    @Override
    public void writeSafe(CompoundTag compound) {
        if (isController()) {
            compound.putInt("Size", width);
            compound.putInt("Height", height);
            compound.putBoolean("Window", window);
            if (fakeEntity != null) {
                CompoundTag myTag = new CompoundTag();
                fakeEntity.saveSynced(myTag);
                compound.put("SmelteryData", myTag);
            }
        }
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    public abstract IFakeSmeltery createNewFakeEntity();

    public IFakeSmeltery getFakeEntity()
    {
        return isController() ? fakeEntity : getControllerBE().fakeEntity;
    }

    @Override
    public AbstractHeatingTankBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof AbstractHeatingTankBlockEntity)
            return (AbstractHeatingTankBlockEntity)blockEntity;
        return null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    private void onPositionChanged()
    {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide)
            return;
        controller = null;
        width = 1;
        height = 1;
        updateConnectivity = true;
        if ((fakeEntity == null) || (!keepContents)) {
            fakeEntity = createNewFakeEntity();
        }

        BlockState state = getBlockState();
        if (blockstateIsSelf(state)) {
            state = state.setValue(SmelteryTankBlock.BOTTOM, true);
            state = state.setValue(SmelteryTankBlock.TOP, true);
            state = state.setValue(SmelteryTankBlock.SHAPE, window ? FluidTankBlock.Shape.WINDOW : FluidTankBlock.Shape.PLAIN);
            getLevel().setBlock(worldPosition, state, 22);
        }

        refreshCapability();
        setChanged();
        sendData();
        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
            onPositionChanged();
            return;
        }

    }

    public void toggleWindows() {
        AbstractHeatingTankBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.setWindows(!be.window);
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    @Override
    public void tick() {
        super.tick();
        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }

        if (isController() && !level.isClientSide)
        {
            reCalcHeat();
            IFakeSmeltery fakeEnt = getFakeEntity();
            fakeEnt.publicTick();
        }

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (updateConnectivity)
            updateConnectivity();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    public void updateSize(int size)
    {
        IFakeSmeltery fakeEnt = getFakeEntity();
        fakeEnt.updateContentSize(size * getMillibucketsPerBlock(), size * getItemsPerBlock());
        refreshCapability();
    }

    public abstract int getMillibucketsPerBlock();
    public abstract int getItemsPerBlock();
    public abstract boolean blockstateIsSelf(BlockState state);


    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (blockstateIsSelf(state)) { // safety
            state = state.setValue(FluidTankBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(FluidTankBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController()) {
            setWindows(window);
            refreshController();
        }
        setChanged();
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!blockstateIsSelf(blockState))
                        continue;

                    FluidTankBlock.Shape shape = FluidTankBlock.Shape.PLAIN;
                    if (window) {
                        // SIZE 1: Every tank has a window
                        if (width == 1)
                            shape = FluidTankBlock.Shape.WINDOW;
                        // SIZE 2: Every tank has a corner window
                        if (width == 2)
                            shape = xOffset == 0 ? zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SW
                                    : zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SE;
                        // SIZE 3: Tanks in the center have a window
                        if (width == 3 && abs(abs(xOffset) - abs(zOffset)) == 1)
                            shape = FluidTankBlock.Shape.WINDOW;
                    }

                    level.setBlock(pos, blockState.setValue(FluidTankBlock.SHAPE, shape), 22);
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return getMaxHeight();
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    public int getMaxHeight() {
        return AllConfigs.server().fluids.fluidTankMaxHeight.get();
    }  // it seems like this starts running into issues near the end and im not sure why

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    private void initItemCapability() {
        if (itemCapability.isPresent())
            return;
        itemCapability = LazyOptional.of(() -> getFakeEntity().getMeltingInventory());
    }

    private void initFluidCapability() {
        if (fluidCapability.isPresent())
            return;
        fluidCapability = LazyOptional.of(() -> getFakeEntity().getTank());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap)) {
            initItemCapability();
            return itemCapability.cast();
        }
        if (isFluidHandlerCap(cap)) {
            initFluidCapability();
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean hasInventory() {
        return true;
    }

    // goggles

    private static final int TOOLTIP_MAX_ITEMS_SHOWN = 8;

    public abstract String getGoggleName();

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        AbstractHeatingTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null) return false;
        IFakeSmeltery fakeEnt = getFakeEntity();
        if (fakeEnt == null) return false;
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        CreateLang.translate(getGoggleName())
                .forGoggles(tooltip);

        DecimalFormat rateFormat = new DecimalFormat("#.##");

        MultitankFuelModule fuelModule = fakeEnt.getFuelModule();

        // heat display
        if (fuelModule.getTemperature() != 0) {
            CreateLang.translate("gui.goggles.smeltery.temp", fuelModule.getTemperature())
                    .style(ChatFormatting.YELLOW)
                    .forGoggles(tooltip, 1);
            CreateLang.translate("gui.goggles.smeltery.rate",  rateFormat.format((float) fuelModule.getRate() / 10f))
                    .style(ChatFormatting.YELLOW)
                    .forGoggles(tooltip, 1);
        }

        // item display

        int itemAmount = 0;

        MeltingModuleInventory meltInv = fakeEnt.getMeltingInventory();
        for (int i = 0; i < meltInv.getSlots(); i++) {
            MeltingModule module = meltInv.getModule(i);
            if (module.isEmpty()) continue;
            itemAmount++;
            if (itemAmount > TOOLTIP_MAX_ITEMS_SHOWN) continue;

            float timePercentage = (float) module.getCurrentTime() / module.getRequiredTime();

            ChatFormatting barColor = ChatFormatting.WHITE;
            if (module.canHeatItem(fuelModule.getTemperature())) {
                barColor = ChatFormatting.RED;
            }
            else if (!fuelModule.hasFuel() && (module.getCurrentTime() > 0))
            {
                barColor = ChatFormatting.AQUA;
            }
            else if (module.getCurrentTime() == MeltingModule.NO_SPACE)
            {
                timePercentage = 1;
            }

            CreateLang.builder()
                    .add(CreateLang.itemName(module.getStack())
                            .style(ChatFormatting.GRAY))
                    .add(CreateLang.text(" " + TooltipHelper.makeProgressBar(4, (int)floor(4 * timePercentage)))
                            .style(barColor))
                    .forGoggles(tooltip, 1);

        }
        if (itemAmount > TOOLTIP_MAX_ITEMS_SHOWN) {
            CreateLang.translate("gui.goggles.smeltery.andmore", itemAmount - TOOLTIP_MAX_ITEMS_SHOWN)
                    .style(ChatFormatting.DARK_GRAY)
                    .forGoggles(tooltip, 1);
        }

        SmelteryTank<HeatingStructureBlockEntity> tank = fakeEnt.getTank();
        List<FluidStack> tankFluids = tank.getFluids();

        // fluid display
        boolean isEmpty = true;
        int spaceFree = tank.getCapacity();

        for (int i = 0; i < tankFluids.size(); i++) {
            FluidStack fluidStack = tankFluids.get(i);
            if (fluidStack.isEmpty()) continue;
            spaceFree -= fluidStack.getAmount();
            isEmpty = false;
            CreateLang.fluidName(fluidStack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            CreateLang.builder()
                    .add(CreateLang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(tank.getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }

        if (!isEmpty) {
            CreateLang.translate("gui.goggles.smeltery.free")
                    .add(CreateLang.number(spaceFree)
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);
            return true;
        }

        CreateLang.translate("gui.goggles.fluid_container.capacity")
                .add(CreateLang.number(tank.getTankCapacity(0))
                        .add(mb)
                        .style(ChatFormatting.GOLD))
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);
        return true;
    }
}
