package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.renderers.models.ModelAlembic;
import thaumcraft.client.renderers.models.ModelBoreBase;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileTube;

public class TileAlembicRenderer extends TileEntitySpecialRenderer<TileAlembic> {

    private static final ResourceLocation ALEMBIC_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/alembic.png");
    private static final ResourceLocation LABEL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/label.png");
    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bore.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelAlembic model = new ModelAlembic();
    private final ModelBoreBase modelBore = new ModelBoreBase();

    @Override
    public void render(TileAlembic tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        renderAlembicModel(tile, x, y, z);
        renderEssentiaColumn(tile, x, y, z);

        if (tile.aspectFilter != null) {
            renderAspectLabel(tile, x, y, z, tile.aspectFilter);
        }
        if (tile.getWorld() != null) {
            renderOutputNozzles(tile, x, y, z);
        }
    }

    private void renderAlembicModel(TileAlembic tile, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(ALEMBIC_TEXTURE);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);

        if (tile.getWorld() != null) {
            rotateByFacing(tile.facing);
            if (tile.aboveFurnace) {
                model.renderTubeMain();
                model.renderLegs();
            } else if (tile.aboveAlembic) {
                model.renderTubeMain();
                model.renderTubeSmall();
            } else {
                model.renderLegs();
            }
        } else {
            GlStateManager.translate(0.0F, 0.0F, -0.4F);
            model.renderLegs();
        }
        model.renderPot();
        model.renderPanel();
        GlStateManager.popMatrix();
    }

    private void renderOutputNozzles(TileAlembic tile, double x, double y, double z) {
        bindTexture(BORE_TEXTURE);
        for (EnumFacing dir : EnumFacing.values()) {
            if (!tile.canOutputTo(dir)) continue;
            TileEntity target = ThaumcraftApiHelper.getConnectableTile(
                    tile.getWorld(),
                    tile.getPos().getX(),
                    tile.getPos().getY(),
                    tile.getPos().getZ(),
                    dir);
            if (!(target instanceof IEssentiaTransport) || target instanceof TileTube) continue;
            renderNozzle(x, y, z, dir);
        }
    }

    private void renderNozzle(double x, double y, double z, EnumFacing dir) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        orientNozzle(dir);
        modelBore.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();
    }

    private static void orientNozzle(EnumFacing dir) {
        switch (dir) {
            case DOWN:
                GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
                break;
            case UP:
                GlStateManager.translate(0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case NORTH:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
            default:
                break;
        }
    }

    private void renderEssentiaColumn(TileAlembic tile, double x, double y, double z) {
        if (tile.amount <= 0 || tile.aspect == null) {
            return;
        }
        float level = 0.1F + 0.72F * TileRenderHelper.clamp01((float) tile.amount / (float) tile.maxAmount);
        int color = 0xCC000000 | (tile.aspect.getColor() & 0x00FFFFFF);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + level, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawSolidHorizontalQuad(0.20F, color);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderAspectLabel(TileAlembic tile, double x, double y, double z, Aspect aspect) {
        EnumFacing facing = EnumFacing.byIndex(tile.facing);
        if (facing == null || facing.getAxis().isVertical()) {
            facing = EnumFacing.NORTH;
        }
        float lx = (float) (x + 0.5D + facing.getXOffset() * 0.409D);
        float ly = (float) (y + 0.468D);
        float lz = (float) (z + 0.5D + facing.getZOffset() * 0.409D);
        float yaw = -facing.getHorizontalAngle();

        GlStateManager.pushMatrix();
        GlStateManager.translate(lx, ly, lz);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(LABEL_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.135F, 0xDDFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        bindTexture(aspect.getImage());
        GlStateManager.translate(0.0F, 0.0F, 0.001F);
        TileRenderHelper.drawTexturedQuad(0.06F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void rotateByFacing(int facingIdx) {
        EnumFacing facing = EnumFacing.byIndex(facingIdx);
        if (facing == EnumFacing.EAST) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        } else if (facing == EnumFacing.SOUTH) {
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (facing == EnumFacing.NORTH) {
            GlStateManager.rotate(270.0F, 0.0F, 0.0F, 1.0F);
        }
    }
}
