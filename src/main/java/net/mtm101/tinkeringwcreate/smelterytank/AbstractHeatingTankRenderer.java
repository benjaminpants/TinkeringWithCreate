package net.mtm101.tinkeringwcreate.smelterytank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import slimeknights.tconstruct.smeltery.client.render.SmelteryTankRenderer;

public class AbstractHeatingTankRenderer extends SafeBlockEntityRenderer<AbstractHeatingTankBlockEntity> {
    public AbstractHeatingTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(AbstractHeatingTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;
        if (!be.window)
            return;

        float tankHullWidth = 1 / 16f + 1 / 128f;
        float capHeight = 1 / 4f;

        BlockPos bp = be.getBlockPos();
        ms.pushPose();
        ms.translate(tankHullWidth, capHeight, tankHullWidth);
        ms.scale(1 - ((tankHullWidth*2)/be.width), 1-((capHeight*2)/be.height), 1 - ((tankHullWidth*2)/be.width));
        SmelteryTankRenderer.renderFluids(ms, bufferSource, be.fakeEntity.getTank(), be.getBlockPos(), new BlockPos(bp.getX() + (be.width - 1),bp.getY() + (be.height - 1),bp.getZ() + (be.width - 1)), light);
        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(AbstractHeatingTankBlockEntity be) {
        return be.isController();
    }
}
