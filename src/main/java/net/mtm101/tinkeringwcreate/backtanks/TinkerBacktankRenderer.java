package net.mtm101.tinkeringwcreate.backtanks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.base.IRotate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.mtm101.tinkeringwcreate.registers.ModPartialModels;

public class TinkerBacktankRenderer extends BacktankRenderer {
    public TinkerBacktankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BacktankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        // fuck using the built in kinetic renderer.
        // i dont want to deal with it.
        // i apologize for this shitty rendering code thats hacked together

        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());
        BlockState renderedState = be.getBlockState();

        BlockRenderDispatcher dispatcher = Minecraft.getInstance()
                .getBlockRenderer();
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        BakedModel model = ModPartialModels.TINKERS_BACKTANK_SHAFT.get();
        ModelData data = be.getModelData();

        Direction.Axis axis = ((IRotate) be.getBlockState()
                .getBlock()).getRotationAxis(be.getBlockState());

        ms.translate(0.5,0,0.5);

        ms.mulPose(Axis.YP.rotation(getAngleForBe(be, be.getBlockPos(), axis)));

        ms.translate(-0.5,0,-0.5);
        renderer.renderModel(ms.last(), vc, renderedState, model, 1f, 1f, 1f, light, OverlayTexture.NO_OVERLAY, data, RenderType.cutout());
        //renderer.tesselateBlock(be.getLevel(), model, renderedState, be.getBlockPos(), ms, vc, false, rng, 0, OverlayTexture.NO_OVERLAY, data, RenderType.cutout());
    }

    @Override
    protected RenderType getRenderType(BacktankBlockEntity be, BlockState state) {
        return RenderType.cutout();
    }
}
