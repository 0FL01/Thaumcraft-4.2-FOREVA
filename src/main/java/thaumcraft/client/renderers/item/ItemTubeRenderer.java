package thaumcraft.client.renderers.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.client.renderers.tile.TileEssentiaCrystalizerRenderer;
import thaumcraft.client.renderers.tile.TileTubeValveRenderer;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileEssentiaCrystalizer;
import thaumcraft.common.tiles.TileTubeValve;

public class ItemTubeRenderer extends TileEntityItemStackRenderer {

    private final TileEssentiaCrystalizerRenderer crystalizerRenderer = new TileEssentiaCrystalizerRenderer();
    private final TileTubeValveRenderer valveRenderer = new TileTubeValveRenderer();

    public ItemTubeRenderer() {
        crystalizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        valveRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        GlStateManager.pushMatrix();
        try {
            if (meta == 7) {
                TileEssentiaCrystalizer crystalizer = new TileEssentiaCrystalizer();
                crystalizerRenderer.render(crystalizer, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 1) {
                renderBakedShell(meta);
            }
            if (meta == 1) {
                TileTubeValve valve = new TileTubeValve();
                valve.facing = EnumFacing.EAST;
                valveRenderer.render(valve, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void renderBakedShell(int meta) {
        Minecraft minecraft = Minecraft.getMinecraft();
        IBlockState state = ConfigBlocks.blockTube.getStateFromMeta(meta);
        BlockRendererDispatcher dispatcher = minecraft.getBlockRendererDispatcher();
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        try {
            minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            dispatcher.getBlockModelRenderer().renderModelBrightnessColor(
                    state, dispatcher.getModelForState(state), 1.0F, 1.0F, 1.0F, 1.0F);
        } finally {
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (cullEnabled) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
