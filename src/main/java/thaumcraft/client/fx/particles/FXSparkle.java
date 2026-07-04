package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;
import thaumcraft.client.fx.ParticleEngine;

@SideOnly(Side.CLIENT)
public class FXSparkle extends Particle implements ITCParticle {
    public boolean leyLineEffect = false;
    public int multiplier = 2;
    public boolean shrink = true;
    public int particle = 16;
    public boolean tinkle = false;
    public int blendmode = 1;
    public boolean slowdown = true;
    public int currentColor = 0;
    private final float initialScale;

    public FXSparkle(World world, double x, double y, double z, float scale, int type, float speed) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleGravity = 0.0F;
        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F * Math.max(0.5F, Math.abs(speed));
        this.motionY = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F * Math.max(0.5F, Math.abs(speed));
        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.01F * Math.max(0.5F, Math.abs(speed));
        this.particleScale *= scale;
        this.initialScale = this.particleScale;
        this.multiplier = Math.max(1, Math.round(Math.max(1.0F, Math.abs(speed) * 6.0F)));
        this.particleMaxAge = 3 * this.multiplier;
        this.setSize(0.01F, 0.01F);
        this.canCollide = false;
        this.currentColor = type;
        applyColorByType(world, type);
    }

    private void applyColorByType(World world, int type) {
        switch (type) {
            case 0:
                this.setRBGColorF(0.75F + world.rand.nextFloat() * 0.25F, 0.25F + world.rand.nextFloat() * 0.25F, 0.75F + world.rand.nextFloat() * 0.25F);
                return;
            case 1:
                this.setRBGColorF(0.5F + world.rand.nextFloat() * 0.3F, 0.5F + world.rand.nextFloat() * 0.3F, 0.2F);
                return;
            case 2:
                this.setRBGColorF(0.2F, 0.2F, 0.7F + world.rand.nextFloat() * 0.3F);
                return;
            case 3:
                this.setRBGColorF(0.2F, 0.7F + world.rand.nextFloat() * 0.3F, 0.2F);
                return;
            case 4:
                this.setRBGColorF(0.7F + world.rand.nextFloat() * 0.3F, 0.2F, 0.2F);
                return;
            case 5:
                this.blendmode = 771;
                this.setRBGColorF(world.rand.nextFloat() * 0.1F, world.rand.nextFloat() * 0.1F, world.rand.nextFloat() * 0.1F);
                return;
            case 6:
                this.setRBGColorF(0.8F + world.rand.nextFloat() * 0.2F, 0.8F + world.rand.nextFloat() * 0.2F, 0.8F + world.rand.nextFloat() * 0.2F);
                return;
            case 7:
                this.setRBGColorF(0.2F, 0.5F + world.rand.nextFloat() * 0.3F, 0.6F + world.rand.nextFloat() * 0.3F);
                return;
            default:
                float r = (type & 0xFF) / 255.0F;
                float g = ((type >> 8) & 0xFF) / 255.0F;
                float b = ((type >> 16) & 0xFF) / 255.0F;
                if (r == 0.0F && g == 0.0F && b == 0.0F) {
                    r = 0.8F;
                    g = 0.8F;
                    b = 1.0F;
                }
                this.setRBGColorF(r, g, b);
        }
    }

    public void setGravity(float value) {
        this.particleGravity = value;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.world == null || !this.world.isRemote || this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        if (this.particleAge == 1 && this.tinkle && this.rand.nextInt(10) == 0) {
            this.world.playSound(this.posX, this.posY, this.posZ, net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    net.minecraft.util.SoundCategory.AMBIENT, 0.02F,
                    0.7F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.6F + 2.0F), false);
        }
        this.motionY -= 0.04D * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.slowdown) {
            this.motionX *= 0.9080000019073486D;
            this.motionY *= 0.9080000019073486D;
            this.motionZ *= 0.9080000019073486D;
            if (this.onGround) {
                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
            }
        }
        if (this.leyLineEffect && this.world.rand.nextBoolean()) {
            FXSparkle fx = new FXSparkle(
                    this.world,
                    this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F,
                    this.prevPosY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F,
                    this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F,
                    1.0F,
                    this.currentColor,
                    0.5F);
            fx.canCollide = false;
            ParticleEngine.addEffect(this.world, fx);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        int part = this.particle + this.particleAge / this.multiplier;
        float uMin = (float) (part % 4) / 16.0F;
        float uMax = uMin + 0.0624375F;
        float vMin = 0.25F;
        float vMax = vMin + 0.0624375F;
        float scale = 0.1F * this.initialScale;
        if (this.shrink && this.particleMaxAge > 0) {
            scale *= (float) (this.particleMaxAge - this.particleAge + 1) / (float) this.particleMaxAge;
        }
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;
        addVertex(buffer, x - rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z - rotationYZ * scale - rotationXZ * scale, uMax, vMax, lightX, lightY);
        addVertex(buffer, x - rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z - rotationYZ * scale + rotationXZ * scale, uMax, vMin, lightX, lightY);
        addVertex(buffer, x + rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z + rotationYZ * scale + rotationXZ * scale, uMin, vMin, lightX, lightY);
        addVertex(buffer, x + rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z + rotationYZ * scale - rotationXZ * scale, uMin, vMax, lightX, lightY);
    }

    private void addVertex(BufferBuilder buffer, double x, double y, double z, float u, float v, int lightX, int lightY) {
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(lightX, lightY)
                .endVertex();
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public int getFXLayer() {
        // Vanilla ParticleManager layer. TC sheet/blend routing is handled by ParticleEngine.
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return this.blendmode == 1 ? 0 : 1;
    }
}
