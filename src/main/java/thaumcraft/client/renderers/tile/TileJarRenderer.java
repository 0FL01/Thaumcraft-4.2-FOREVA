package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.renderers.models.ModelBrain;
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
    private static final String LIQUID_TEXTURE = "thaumcraft:blocks/animatedglow";
    private static final float MODEL_SCALE = 0.0625F;
    private final ModelJar model = new ModelJar();
    private final ModelBrain brain = new ModelBrain();

    @Override
    public void render(TileJar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        boolean renderShell = tile.getWorld() == null;
        float shellScale = 1.0F;
        if (tile instanceof TileJarNode) {
            TileJarNode node = (TileJarNode) tile;
            long now = System.currentTimeMillis();
            if (node.animate > now) {
                renderShell = true;
                shellScale = 1.0F + 2.0F * (float) (node.animate - now) / 1000.0F;
            } else if (node.animate > 0L) {
                node.animate = 0L;
            }
        }

        if (tile instanceof TileJarNode) {
            TileNodeRenderer.renderNodeAt((TileJarNode) tile, x + 0.5D, y + 0.4D, z + 0.5D, partialTicks, 0.7F);
        }

        if (tile instanceof TileJarBrain) {
            renderBrain((TileJarBrain) tile, x, y, z, partialTicks);
        } else if (tile instanceof TileJarFillable) {
            renderFillable((TileJarFillable) tile, x, y, z);
        }

        if (renderShell) {
            renderJarShell(tile, x, y, z, shellScale);
        }
    }

    private void renderJarShell(TileJar tile, double x, double y, double z, float scale) {
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
        if (scale != 1.0F) {
            GlStateManager.scale(scale, scale, scale);
        }
        model.renderAll(MODEL_SCALE);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderFillable(TileJarFillable tile, double x, double y, double z) {
        renderLiquid(tile, x, y, z);

        if (tile.aspectFilter != null) {
            renderAspectLabel(tile, x, y, z, tile.aspectFilter);
        }
    }

    private void renderLiquid(TileJarFillable tile, double x, double y, double z) {
        if (tile.amount <= 0 || tile.aspect == null) {
            return;
        }

        TextureAtlasSprite liquid = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(LIQUID_TEXTURE);
        if (liquid == null) {
            return;
        }

        float fill = TileRenderHelper.clamp01((float) tile.amount / (float) Math.max(1, tile.maxAmount));
        float minX = (float) x + 4.0F / 16.0F;
        float maxX = (float) x + 12.0F / 16.0F;
        float minZ = (float) z + 4.0F / 16.0F;
        float maxZ = (float) z + 12.0F / 16.0F;
        float minY = (float) y + 1.0F / 16.0F;
        float maxY = minY + 10.0F / 16.0F * fill;
        int color = 0xFF000000 | (tile.aspect.getColor() & 0x00FFFFFF);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        TileRenderHelper.drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ, liquid, color);
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
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
        float bob = MathHelper.sin(ticks / 14.0F) * 0.03F + 0.03F;
        float delta = tile.rota - tile.rotb;
        while (delta >= (float) Math.PI) {
            delta -= (float) (Math.PI * 2.0D);
        }
        while (delta < (float) -Math.PI) {
            delta += (float) (Math.PI * 2.0D);
        }
        float rot = tile.rotb + delta * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.01D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.8F + bob, 0.0F);
        GlStateManager.rotate(rot * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        bindTexture(BRAIN_TEXTURE);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        brain.render(MODEL_SCALE);
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
