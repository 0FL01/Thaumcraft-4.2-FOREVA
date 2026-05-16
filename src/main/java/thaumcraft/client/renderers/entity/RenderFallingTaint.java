package thaumcraft.client.renderers.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.entities.EntityFallingTaint;

import javax.annotation.Nullable;

public class RenderFallingTaint extends Render<EntityFallingTaint> {

    public RenderFallingTaint(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
    }

    @Override
    public void doRender(EntityFallingTaint entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.block == null) {
            return;
        }

        World world = entity.getEntityWorld();
        BlockPos blockPos = new BlockPos(entity);
        Block block = entity.block;
        IBlockState blockState = block.getStateFromMeta(entity.metadata);
        IBlockState worldState = world.getBlockState(blockPos);
        if (blockState == worldState || blockState.getRenderType() != EnumBlockRenderType.MODEL) {
            return;
        }

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-blockPos.getX() - 0.5D, -blockPos.getY(), -blockPos.getZ() - 0.5D);

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        dispatcher.getBlockModelRenderer().renderModel(
                world,
                dispatcher.getModelForState(blockState),
                blockState,
                blockPos,
                buffer,
                false,
                MathHelper.getPositionRandom(blockPos)
        );

        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFallingTaint entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
