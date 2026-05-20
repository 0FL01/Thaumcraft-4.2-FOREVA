package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXWispArcing extends Particle {
    private final double anchorX;
    private final double anchorY;
    private final double anchorZ;
    private final float moteParticleScale;
    private final int moteHalfLife;
    public boolean tinkle = false;
    public int blendmode = 1;

    public FXWispArcing(World world,
                        double x, double y, double z,
                        double tx, double ty, double tz,
                        float scale,
                        float red, float green, float blue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.anchorX = x;
        this.anchorY = y;
        this.anchorZ = z;
        this.particleRed = red == 0.0F ? 1.0F : red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleGravity = 0.0F;
        this.particleScale *= Math.max(0.1F, scale);
        this.moteParticleScale = this.particleScale;
        this.particleMaxAge = (int) (36.0D / (Math.random() * 0.3D + 0.7D));
        this.moteHalfLife = Math.max(1, this.particleMaxAge / 2);
        this.canCollide = true;
        this.setSize(0.01F, 0.01F);

        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 50 : 25;
        if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }

        this.motionX = tx - x;
        this.motionY = ty - y;
        this.motionZ = tz - z;
        this.setPosition(tx, ty, tz);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.world == null || !this.world.isRemote) {
            this.setExpired();
            return;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        float progress = this.particleAge / (float) this.particleMaxAge;
        float curve = this.particleAge / (this.particleMaxAge / 2.0F);
        progress = 1.0F - progress;
        curve = 1.0F - curve;
        curve *= curve;
        this.posX = this.anchorX + this.motionX * progress;
        this.posY = this.anchorY + this.motionY * progress - curve + 1.0D;
        this.posZ = this.anchorZ + this.motionZ * progress;
    }

    @Override
    public int getFXLayer() {
        // This particle animates through setParticleTextureIndex(...) on the
        // misc particle sheet, so it cannot use the terrain-texture layer.
        return 0;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float ageScale = this.particleAge / (float) this.moteHalfLife;
        if (ageScale > 1.0F) {
            ageScale = 2.0F - ageScale;
        }
        float scale = this.particleScale;
        float alpha = this.particleAlpha;
        this.particleScale = this.moteParticleScale * ageScale;
        this.particleAlpha = 0.5F;
        this.setParticleTextureIndex(240 + (this.particleAge % 2));
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        this.particleScale = scale;
        this.particleAlpha = alpha;
    }

    public void setGravity(float value) {
        this.particleGravity = value;
    }
}
