package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelCrabVent;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;

public class TileEldritchCrabSpawnerRenderer extends TileEntitySpecialRenderer<TileEldritchCrabSpawner> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crabvent.png");
    private final ModelCrabVent model = new ModelCrabVent();

    @Override
    public void render(TileEldritchCrabSpawner tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orient(EnumFacing.byIndex(tile.getFacing()));
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TEXTURE);
        model.renderAll();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private static void orient(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
            default:
                break;
        }
    }

}
