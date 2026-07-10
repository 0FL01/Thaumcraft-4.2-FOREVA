package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.awt.Color;
import java.util.Calendar;

public class ModelWand extends ModelBase {

    private static final ResourceLocation WAND_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/wand.png");
    private static final ResourceLocation HALLOWEEN_WAND_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/spec_h.png");
    private static final ResourceLocation SCRIPT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/script.png");

    private final ModelRenderer rod;
    private final ModelRenderer focus;
    private final ModelRenderer cap;
    private final ModelRenderer capBottom;

    public ModelWand() {
        textureWidth = 32;
        textureHeight = 32;

        cap = new ModelRenderer(this, 0, 0);
        cap.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);
        cap.setRotationPoint(0.0F, 0.0F, 0.0F);

        capBottom = new ModelRenderer(this, 0, 0);
        capBottom.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);
        capBottom.setRotationPoint(0.0F, 20.0F, 0.0F);

        rod = new ModelRenderer(this, 0, 8);
        rod.addBox(-1.0F, -1.0F, -1.0F, 2, 18, 2);
        rod.setRotationPoint(0.0F, 2.0F, 0.0F);

        focus = new ModelRenderer(this, 0, 0);
        focus.addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6);
        focus.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void render(ItemStack wandStack, float partialTicks, EntityPlayer player) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) {
            return;
        }

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        boolean staff = wand.isStaff(wandStack);
        boolean runes = wand.hasRunes(wandStack);

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(wand.getRod(wandStack).getTexture());

        GlStateManager.pushMatrix();
        if (staff) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        GlStateManager.pushMatrix();
        boolean glowingRod = wand.getRod(wandStack).isGlowing() && player != null;
        float lightX = OpenGlHelper.lastBrightnessX;
        float lightY = OpenGlHelper.lastBrightnessY;
        if (glowingRod) {
            setAnimatedFullbright(player.ticksExisted, 200.0F, 5.0F);
        }
        if (staff) {
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
            GlStateManager.scale(1.2F, 2.0F, 1.2F);
        }
        rod.render(0.0625F);
        if (glowingRod) {
            restoreLight(lightX, lightY);
        }
        GlStateManager.popMatrix();

        mc.getTextureManager().bindTexture(wand.getCap(wandStack).getTexture());
        GlStateManager.pushMatrix();
        if (staff) {
            GlStateManager.scale(1.3F, 1.1F, 1.3F);
        } else {
            GlStateManager.scale(1.2F, 1.0F, 1.2F);
        }
        if (ItemWandCasting.isSceptre(wandStack)) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.3F, 1.3F, 1.3F);
            cap.render(0.0625F);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            GlStateManager.scale(1.0F, 0.66F, 1.0F);
            cap.render(0.0625F);
            GlStateManager.popMatrix();
        } else {
            cap.render(0.0625F);
        }
        if (staff) {
            GlStateManager.translate(0.0F, 0.225F, 0.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, 0.66F, 1.0F);
            cap.render(0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.65F, 0.0F);
        }
        capBottom.render(0.0625F);
        GlStateManager.popMatrix();

        if (wand.getFocus(wandStack) != null) {
            renderFocus(wand, wandStack, focusStack, staff, player);
        }
        if (ItemWandCasting.isSceptre(wandStack)) {
            renderSceptreRunes(player);
        }
        if (runes) {
            renderRodRunes(player);
        }

        GlStateManager.popMatrix();
    }

    private void renderFocus(ItemWandCasting wand, ItemStack wandStack, ItemStack focusStack, boolean staff, EntityPlayer player) {
        ItemFocusBasic focusItem = wand.getFocus(wandStack);
        if (focusItem == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        TextureAtlasSprite ornament = focusItem.getOrnament(focusStack);
        if (ornament != null) {
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.25F, -0.1F, 0.0275F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            renderOrnament(ornament);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.25F, -0.1F, 0.0275F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            renderOrnament(ornament);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }

        TextureAtlasSprite depthSprite = focusItem.getFocusDepthLayerIcon(focusStack);
        if (depthSprite != null) {
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            if (staff) {
                GlStateManager.translate(0.0F, -0.15F, 0.0F);
                GlStateManager.scale(0.165F, 0.1765F, 0.165F);
            } else {
                GlStateManager.translate(0.0F, -0.09F, 0.0F);
                GlStateManager.scale(0.16F, 0.16F, 0.16F);
            }
            renderDepthCube(depthSprite);
            GlStateManager.popMatrix();
        }

        mc.getTextureManager().bindTexture(isHalloween() ? HALLOWEEN_WAND_TEXTURE : WAND_TEXTURE);
        GlStateManager.pushMatrix();
        if (staff) {
            GlStateManager.translate(0.0F, -0.0475F, 0.0F);
            GlStateManager.scale(0.525F, 0.5525F, 0.525F);
        } else {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }
        Color color = new Color(focusItem.getFocusColor(focusStack));
        float alpha = depthSprite != null ? 0.6F : 0.95F;
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha);
        if (player != null) {
            float lightX = OpenGlHelper.lastBrightnessX;
            float lightY = OpenGlHelper.lastBrightnessY;
            setAnimatedFullbright(player.ticksExisted / 3.0F, 195.0F, 10.0F);
            focus.render(0.0625F);
            restoreLight(lightX, lightY);
        } else {
            focus.render(0.0625F);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderOrnament(TextureAtlasSprite sprite) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        float depth = 0.1F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        addQuad(buffer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                minU, minV, maxU, maxV, 0.0F, 0.0F, 1.0F);
        addQuad(buffer, 1.0F, 1.0F, -depth, 0.0F, 1.0F, -depth, 0.0F, 0.0F, -depth, 1.0F, 0.0F, -depth,
                minU, minV, maxU, maxV, 0.0F, 0.0F, -1.0F);
        addQuad(buffer, 0.0F, 1.0F, -depth, 1.0F, 1.0F, -depth, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F,
                minU, minV, maxU, maxV, 0.0F, 1.0F, 0.0F);
        addQuad(buffer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, -depth, 0.0F, 0.0F, -depth,
                minU, minV, maxU, maxV, 0.0F, -1.0F, 0.0F);
        addQuad(buffer, 0.0F, 1.0F, -depth, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -depth,
                minU, minV, maxU, maxV, -1.0F, 0.0F, 0.0F);
        addQuad(buffer, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, -depth, 1.0F, 0.0F, -depth, 1.0F, 0.0F, 0.0F,
                minU, minV, maxU, maxV, 1.0F, 0.0F, 0.0F);
        Tessellator.getInstance().draw();
    }

    private void renderDepthCube(TextureAtlasSprite sprite) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        float half = 0.5F;
        float min = -half;
        float max = half;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        addQuad(buffer, min, min, max, max, max, max, max, min, max, min, min, max, minU, minV, maxU, maxV, 0.0F, 0.0F, 1.0F);
        addQuad(buffer, max, min, min, min, max, min, min, min, min, max, min, min, minU, minV, maxU, maxV, 0.0F, 0.0F, -1.0F);
        addQuad(buffer, min, max, min, min, max, max, max, max, max, max, max, min, minU, minV, maxU, maxV, 0.0F, 1.0F, 0.0F);
        addQuad(buffer, min, min, max, min, min, min, max, min, min, max, min, max, minU, minV, maxU, maxV, 0.0F, -1.0F, 0.0F);
        addQuad(buffer, min, min, min, min, min, max, min, max, max, min, max, min, minU, minV, maxU, maxV, -1.0F, 0.0F, 0.0F);
        addQuad(buffer, max, min, max, max, min, min, max, max, min, max, max, max, minU, minV, maxU, maxV, 1.0F, 0.0F, 0.0F);
        Tessellator.getInstance().draw();
    }

    private static void addQuad(BufferBuilder buffer,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float minU, float minV, float maxU, float maxV,
                                float nx, float ny, float nz) {
        buffer.pos(x1, y1, z1).tex(minU, maxV).normal(nx, ny, nz).endVertex();
        buffer.pos(x2, y2, z2).tex(maxU, maxV).normal(nx, ny, nz).endVertex();
        buffer.pos(x3, y3, z3).tex(maxU, minV).normal(nx, ny, nz).endVertex();
        buffer.pos(x4, y4, z4).tex(minU, minV).normal(nx, ny, nz).endVertex();
    }

    private void renderSceptreRunes(EntityPlayer player) {
        if (player == null) {
            return;
        }
        float lightX = OpenGlHelper.lastBrightnessX;
        float lightY = OpenGlHelper.lastBrightnessY;
        GlStateManager.pushMatrix();
        setFixedFullbright(200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        for (int rot = 0; rot < 10; rot++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(36.0F * rot + player.ticksExisted, 0.0F, 1.0F, 0.0F);
            drawRune(0.16D, -0.01F, -0.125D, rot, player);
            GlStateManager.popMatrix();
        }
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        restoreLight(lightX, lightY);
        GlStateManager.popMatrix();
    }

    private void renderRodRunes(EntityPlayer player) {
        if (player == null) {
            return;
        }
        float lightX = OpenGlHelper.lastBrightnessX;
        float lightY = OpenGlHelper.lastBrightnessY;
        GlStateManager.pushMatrix();
        setFixedFullbright(200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        for (int rot = 0; rot < 4; rot++) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            for (int a = 0; a < 14; a++) {
                int rune = (a + rot * 3) % 16;
                drawRune(0.36D + a * 0.14D, -0.01F, -0.08D, rune, player);
            }
        }
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        restoreLight(lightX, lightY);
        GlStateManager.popMatrix();
    }

    private void drawRune(double x, float y, double z, int rune, EntityPlayer player) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SCRIPT_TEXTURE);
        float red = MathHelper.sin((player.ticksExisted + rune * 5) / 5.0F) * 0.1F + 0.88F;
        float green = MathHelper.sin((player.ticksExisted + rune * 5) / 7.0F) * 0.1F + 0.63F;
        float alpha = MathHelper.sin((player.ticksExisted + rune * 5) / 10.0F) * 0.2F + 0.6F;

        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(x, y, z);
        GlStateManager.color(red, green, 0.2F, alpha);

        float minU = rune * 0.0625F;
        float maxU = minU + 0.0625F;
        float minV = 0.0F;
        float maxV = 1.0F;
        float size = 0.06F + alpha / 40.0F;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-size, size, 0.0D).tex(maxU, maxV).color(red, green, 0.2F, alpha).endVertex();
        buffer.pos(size, size, 0.0D).tex(maxU, minV).color(red, green, 0.2F, alpha).endVertex();
        buffer.pos(size, -size, 0.0D).tex(minU, minV).color(red, green, 0.2F, alpha).endVertex();
        buffer.pos(-size, -size, 0.0D).tex(minU, maxV).color(red, green, 0.2F, alpha).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void setAnimatedFullbright(float ticks, float base, float amplitude) {
        int packed = (int) (base + MathHelper.sin(ticks) * amplitude + amplitude);
        int sky = packed % 65536;
        int block = packed / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sky, block);
    }

    private static void setFixedFullbright(int packed) {
        int sky = packed % 65536;
        int block = packed / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sky, block);
    }

    private static void restoreLight(float lightX, float lightY) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

    private static boolean isHalloween() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER
                && calendar.get(Calendar.DAY_OF_MONTH) == 31;
    }
}
