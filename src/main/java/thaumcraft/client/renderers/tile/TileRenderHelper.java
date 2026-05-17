package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

final class TileRenderHelper {

    private TileRenderHelper() {}

    static float worldTicks(World world, float partialTicks) {
        return (world == null ? 0.0F : world.getTotalWorldTime()) + partialTicks;
    }

    static void renderFloatingItem(ItemStack stack, float ticks, float yOffset, float scale) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, yOffset + MathHelper.sin(ticks / 16.0F) * 0.05F, 0.0F);
        GlStateManager.rotate(ticks * 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    static void orientBillboardToPlayer() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rm.playerViewX, 1.0F, 0.0F, 0.0F);
    }

    static void drawTexturedQuad(float half, int argb, float u0, float u1, float v0, float v1) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(-half, -half, 0.0D).tex(u0, v1).color(r, g, b, a).endVertex();
        buf.pos(half, -half, 0.0D).tex(u1, v1).color(r, g, b, a).endVertex();
        buf.pos(half, half, 0.0D).tex(u1, v0).color(r, g, b, a).endVertex();
        buf.pos(-half, half, 0.0D).tex(u0, v0).color(r, g, b, a).endVertex();
        tess.draw();
    }

    static void drawSolidHorizontalQuad(float half, int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(-half, 0.0D, -half).color(r, g, b, a).endVertex();
        buf.pos(-half, 0.0D, half).color(r, g, b, a).endVertex();
        buf.pos(half, 0.0D, half).color(r, g, b, a).endVertex();
        buf.pos(half, 0.0D, -half).color(r, g, b, a).endVertex();
        tess.draw();
    }

    static Vec3d rgb(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        return new Vec3d(r, g, b);
    }

    static float clamp01(float value) {
        return MathHelper.clamp(value, 0.0F, 1.0F);
    }

    static float ticks(TileEntity tile, float partialTicks) {
        return worldTicks(tile == null ? null : tile.getWorld(), partialTicks);
    }
}
