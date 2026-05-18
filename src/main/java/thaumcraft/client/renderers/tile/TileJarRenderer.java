package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.renderers.models.ModelJar;
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarBrain;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarNode;

public class TileJarRenderer extends TileEntitySpecialRenderer<TileJar> {

    private static final ResourceLocation LABEL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/label.png");
    private static final ResourceLocation JAR_VOID_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jar_void.png");
    private static final ResourceLocation BRAIN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/brain2.png");
    private static final ResourceLocation BRINE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jarbrine.png");
    private static final float MODEL_SCALE = 0.0625F;
    private final ModelJar model = new ModelJar();

    @Override
    public void render(TileJar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        if (tile instanceof TileJarNode) {
            TileNodeRenderer.renderNodeAt((TileJarNode) tile, x + 0.5D, y + 0.4D, z + 0.5D, partialTicks, 0.7F);
        }

        if (tile instanceof TileJarBrain) {
            renderBrain((TileJarBrain) tile, x, y, z, partialTicks);
        } else if (tile instanceof TileJarFillable) {
            renderFillable((TileJarFillable) tile, x, y, z);
        }

        renderJarShell(tile, x, y, z);
    }

    private void renderJarShell(TileJar tile, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate(x + 0.5D, y + 0.01D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (tile instanceof thaumcraft.common.tiles.TileJarFillableVoid) {
            bindTexture(JAR_VOID_TEXTURE);
        } else {
            bindTexture(tile.getTexture());
        }
        if (tile instanceof TileJarNode) {
            TileJarNode node = (TileJarNode) tile;
            long now = System.currentTimeMillis();
            if (node.animate > now) {
                float size = 1.0F + 2.0F * (float) (node.animate - now) / 1000.0F;
                GlStateManager.scale(size, size, size);
            } else if (node.animate > 0L) {
                node.animate = 0L;
            }
        }
        model.renderAll(MODEL_SCALE);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderFillable(TileJarFillable tile, double x, double y, double z) {
        if (tile.amount > 0 && tile.aspect != null) {
            float level = 0.12F + 0.62F * TileRenderHelper.clamp01((float) tile.amount / (float) tile.maxAmount);
            int color = 0xCC000000 | (tile.aspect.getColor() & 0x00FFFFFF);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + level, z + 0.5D);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawSolidHorizontalQuad(0.24F, color);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }

        if (tile.aspectFilter != null) {
            renderAspectLabel(tile, x, y, z, tile.aspectFilter);
        }
    }

    private void renderAspectLabel(TileJarFillable tile, double x, double y, double z, Aspect aspect) {
        EnumFacing facing = EnumFacing.byIndex(tile.facing);
        if (facing == null || facing.getAxis().isVertical()) {
            facing = EnumFacing.NORTH;
        }

        float lx = (float) (x + 0.5D + facing.getXOffset() * 0.315D);
        float ly = (float) (y + 0.60D);
        float lz = (float) (z + 0.5D + facing.getZOffset() * 0.315D);
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

    private void renderBrain(TileJarBrain tile, double x, double y, double z, float partialTicks) {
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = (float) Math.sin(ticks / 14.0F) * 0.03F + 0.03F;
        float scale = 0.4F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.21D + bob, z + 0.5D);
        float rot = tile.rotb + (tile.rota - tile.rotb) * partialTicks;
        GlStateManager.rotate((float) Math.toDegrees(rot) - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(BRAIN_TEXTURE);
        TileRenderHelper.drawTexturedQuad(scale, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.01D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(BRINE_TEXTURE);
        model.renderBrine(MODEL_SCALE);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
