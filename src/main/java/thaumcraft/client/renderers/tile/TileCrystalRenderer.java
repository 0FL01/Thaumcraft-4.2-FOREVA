package thaumcraft.client.renderers.tile;

import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelCrystal;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.tiles.TileCrystal;

public class TileCrystalRenderer extends TileEntitySpecialRenderer<TileCrystal> {
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crystal.png");

    private final ModelCrystal model = new ModelCrystal();

    @Override
    public void render(TileCrystal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();

        int md = tile.getBlockMetadata();
        int color = getCrystalColor(md);
        bindTexture(CRYSTAL_TEXTURE);
        BlockPos pos = tile.getPos() == null ? BlockPos.ORIGIN : tile.getPos();
        Random rand = new Random(tile.getBlockMetadata()
                + pos.getX()
                + pos.getY() * pos.getZ());
        drawCrystal(tile.orientation, x, y, z,
                (rand.nextFloat() - rand.nextFloat()) * 5.0F,
                (rand.nextFloat() - rand.nextFloat()) * 5.0F,
                rand, color, 1.1F);
        for (int i = 1; i < 6; i++) {
            if (md == 6) {
                color = getBalancedSubColor(i);
            }
            int angle1 = rand.nextInt(36) + 72 * i;
            int angle2 = 15 + rand.nextInt(15);
            drawCrystal(tile.orientation, x, y, z, angle1, angle2, rand, color, 0.8F);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    public void renderItemCluster(int metadata) {
        GlStateManager.pushMatrix();

        int color = getCrystalColor(metadata);
        bindTexture(CRYSTAL_TEXTURE);
        Random rand = new Random(metadata);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.8F, 0.0F);
        drawItemCrystal((rand.nextFloat() - rand.nextFloat()) * 5.0F,
                (rand.nextFloat() - rand.nextFloat()) * 5.0F,
                rand, color, 1.1F);
        for (int i = 1; i < 6; i++) {
            if (metadata == 6) {
                color = getBalancedSubColor(i);
            }
            int angle1 = rand.nextInt(36) + 72 * i;
            int angle2 = 15 + rand.nextInt(15);
            drawItemCrystal(angle1, angle2, rand, color, 0.8F);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    private static int getCrystalColor(int metadata) {
        if (metadata >= 0 && metadata < ItemShard.colors.length) {
            return ItemShard.colors[metadata];
        }
        return 0xEECCFF; // fallback: Order purple
    }

    /**
     * Returns the sub-crystal color for balanced clusters (metadata 6).
     * Original 1.7.10 used BlockCustomOreItem.colors[a==5 ? 6 : a]
     * where a=1..5. With our re-indexed array (direct meta→color):
     *   a=1→Air, a=2→Fire, a=3→Water, a=4→Earth, a=5→Entropy.
     */
    private static int getBalancedSubColor(int index) {
        // index runs 1..5 in the original loop
        // Original: colors[a==5 ? 6 : a] → with old array: Air(1),Fire(2),Water(3),Earth(4),Entropy(6)
        // New array direct: Air(0),Fire(1),Water(2),Earth(3),Entropy(5)
        if (index == 5) {
            return ItemShard.colors[5]; // Entropy
        }
        return ItemShard.colors[index - 1]; // Air=0, Fire=1, Water=2, Earth=3
    }

    private void drawCrystal(short orientation, double x, double y, double z, float yaw, float pitch, Random rand, int color, float size) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        float ticks = player == null ? 0.0F : player.ticksExisted;
        float shade = MathHelper.sin((ticks + rand.nextInt(10)) / (5.0F + rand.nextFloat())) * 0.075F + 0.925F;
        Color tint = new Color(color);
        float r = tint.getRed() / 220.0F;
        float g = tint.getGreen() / 220.0F;
        float b = tint.getBlue() / 220.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        translateFromOrientation((float) x, (float) y, (float) z, orientation);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        float sx = (0.15F + rand.nextFloat() * 0.075F) * size;
        float sy = (0.5F + rand.nextFloat() * 0.1F) * size;
        float sz = (0.15F + rand.nextFloat() * 0.05F) * size;
        GlStateManager.scale(sx, sy, sz);

        float prevX = OpenGlHelper.lastBrightnessX;
        float prevY = OpenGlHelper.lastBrightnessY;
        int glow = (int) (210.0F * shade);
        int lightU = glow % 65536;
        int lightV = glow / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);

        GlStateManager.color(r, g, b, 1.0F);
        model.render();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void drawItemCrystal(float yaw, float pitch, Random rand, int color, float size) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        float ticks = player == null ? 0.0F : player.ticksExisted;
        float shade = MathHelper.sin((ticks + rand.nextInt(10)) / (5.0F + rand.nextFloat())) * 0.075F + 0.925F;
        Color tint = new Color(color);
        float r = tint.getRed() / 220.0F;
        float g = tint.getGreen() / 220.0F;
        float b = tint.getBlue() / 220.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        float sx = (0.15F + rand.nextFloat() * 0.075F) * size;
        float sy = (0.5F + rand.nextFloat() * 0.1F) * size;
        float sz = (0.15F + rand.nextFloat() * 0.05F) * size;
        GlStateManager.scale(sx, sy, sz);

        float prevX = OpenGlHelper.lastBrightnessX;
        float prevY = OpenGlHelper.lastBrightnessY;
        int glow = (int) (210.0F * shade);
        int lightU = glow % 65536;
        int lightV = glow / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);

        GlStateManager.color(r, g, b, 1.0F);
        model.render();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void translateFromOrientation(float x, float y, float z, int orientation) {
        if (orientation == 0) {
            GlStateManager.translate(x + 0.5F, y + 1.3F, z + 0.5F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.translate(x + 0.5F, y - 0.3F, z + 0.5F);
        } else if (orientation == 2) {
            GlStateManager.translate(x + 0.5F, y + 0.5F, z + 1.3F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 3) {
            GlStateManager.translate(x + 0.5F, y + 0.5F, z - 0.3F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.translate(x + 1.3F, y + 0.5F, z + 0.5F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 5) {
            GlStateManager.translate(x - 0.3F, y + 0.5F, z + 0.5F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
        }
    }
}
