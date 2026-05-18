package thaumcraft.client.fx.beams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.nodes.IRevealer;

@SideOnly(Side.CLIENT)
public class FXBeamPower extends FXBeam {
    private static final ResourceLocation FLARE = new ResourceLocation("thaumcraft", "textures/misc/p_large.png");
    private float opacity = 0.3F;

    public FXBeamPower(World world,
                       double px, double py, double pz,
                       double tx, double ty, double tz,
                       float red, float green, float blue,
                       int age, boolean flicker, int density) {
        super(world, px, py, pz, tx, ty, tz, red, green, blue, age, flicker, density);
    }

    public void updateBeam(double px, double py, double pz, double tx, double ty, double tz) {
        super.updateBeam(px, py, pz, tx, ty, tz);
    }

    public void setPulse(boolean pulse, float red, float green, float blue) {
        setPulse(pulse);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        if (pulse) {
            this.opacity = 0.8F;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.opacity > 0.3F) {
            this.opacity -= 0.025F;
        } else if (this.opacity < 0.3F) {
            this.opacity = 0.3F;
        }
    }

    @Override
    protected void renderImpact(float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(FLARE);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        float opmod = 0.2F;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        if (viewer instanceof EntityPlayer) {
            ItemStack helm = ((EntityPlayer) viewer).inventory.armorInventory.get(3);
            if (!helm.isEmpty() && helm.getItem() instanceof IRevealer) {
                opmod = 1.0F;
            }
        }

        int part = this.particleAge % 16;
        float u0 = part / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.3125F;
        float v1 = v0 + 0.0624375F;
        float size = 0.66F * this.opacity;

        float tx = (float) (this.ptX + (this.tX - this.ptX) * partialTicks - interpPosX);
        float ty = (float) (this.ptY + (this.tY - this.ptY) * partialTicks - interpPosY);
        float tz = (float) (this.ptZ + (this.tZ - this.ptZ) * partialTicks - interpPosZ);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addFlareVertex(buf, tx - rotationX * size - rotationXY * size, ty - rotationZ * size, tz - rotationYZ * size - rotationXZ * size, u1, v1, this.opacity * opmod);
        addFlareVertex(buf, tx - rotationX * size + rotationXY * size, ty + rotationZ * size, tz - rotationYZ * size + rotationXZ * size, u1, v0, this.opacity * opmod);
        addFlareVertex(buf, tx + rotationX * size + rotationXY * size, ty + rotationZ * size, tz + rotationYZ * size + rotationXZ * size, u0, v0, this.opacity * opmod);
        addFlareVertex(buf, tx + rotationX * size - rotationXY * size, ty - rotationZ * size, tz + rotationYZ * size - rotationXZ * size, u0, v1, this.opacity * opmod);
        tess.draw();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private void addFlareVertex(BufferBuilder buf, double x, double y, double z, double u, double v, float alpha) {
        buf.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
                .lightmap(240, 240)
                .endVertex();
    }
}
