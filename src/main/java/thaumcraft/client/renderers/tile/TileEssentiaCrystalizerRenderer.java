package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelCrystalizer;
import thaumcraft.client.renderers.models.ModelVisRelay;
import thaumcraft.common.tiles.TileEssentiaCrystalizer;

public class TileEssentiaCrystalizerRenderer extends TileEntitySpecialRenderer<TileEssentiaCrystalizer> {
    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crystalizer.png");
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelCrystalizer baseModel = new ModelCrystalizer();
    private final ModelVisRelay model = new ModelVisRelay();

    @Override
    public void render(TileEssentiaCrystalizer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = 0.0F;
        if (Minecraft.getMinecraft().player != null) {
            ticks = Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        }
        GlStateManager.pushMatrix();
        orientByFace(x, y, z, tile.facing);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(BASE_TEXTURE);
        baseModel.renderBase();
        baseModel.renderTop();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(CRYSTAL_TEXTURE);
        GlStateManager.color(tile.cr, tile.cg, tile.cb, 1.0F);
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            float glow = MathHelper.sin((ticks + i * 10.0F) / 2.0F) * 0.05F + 0.95F;
            int light = 50 + (int) (150.0F * glow);
            int low = light % 65536;
            int high = light / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
            GlStateManager.rotate(90.0F * i, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.34F, 0.0F, 1.2125F);
            GlStateManager.rotate(tile.spin + tile.spinInc * partialTicks, 0.0F, 0.0F, 1.0F);
            model.renderCrystal(MODEL_SCALE);
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    private static void orientByFace(double x, double y, double z, EnumFacing facing) {
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (facing == EnumFacing.DOWN) {
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (facing == EnumFacing.UP) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (facing == EnumFacing.SOUTH) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.WEST) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.EAST) {
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0D, 0.0D, -0.5D);
    }
}
