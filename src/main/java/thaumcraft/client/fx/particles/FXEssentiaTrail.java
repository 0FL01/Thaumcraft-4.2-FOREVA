package thaumcraft.client.fx.particles;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXEssentiaTrail extends Particle implements ITCParticle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final int count;
    public int particle = 24;

    public FXEssentiaTrail(World world,
                           double x, double y, double z,
                           double tx, double ty, double tz,
                           int count, int color, float scale) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.count = count;
        this.particleScale = (MathHelper.sin(count / 2.0F) * 0.1F + 1.0F) * Math.max(0.15F, scale);

        double dx = tx - this.posX;
        double dy = ty - this.posY;
        double dz = tz - this.posZ;
        int base = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 30.0F);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        this.motionX = MathHelper.sin(count / 4.0F) * 0.015F + this.rand.nextGaussian() * 0.002D;
        this.motionY = 0.1F + MathHelper.sin(count / 3.0F) * 0.01F;
        this.motionZ = MathHelper.sin(count / 2.0F) * 0.015F + this.rand.nextGaussian() * 0.002D;

        Color tint = new Color(color);
        float redVar = tint.getRed() / 255.0F * 0.2F;
        float greenVar = tint.getGreen() / 255.0F * 0.2F;
        float blueVar = tint.getBlue() / 255.0F * 0.2F;
        this.particleRed = tint.getRed() / 255.0F - redVar + this.rand.nextFloat() * redVar;
        this.particleGreen = tint.getGreen() / 255.0F - greenVar + this.rand.nextFloat() * greenVar;
        this.particleBlue = tint.getBlue() / 255.0F - blueVar + this.rand.nextFloat() * blueVar;

        this.particleGravity = 0.2F;
        this.canCollide = true;

        try {
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase player = mc.player;
            int visibleDistance = mc.gameSettings.fancyGraphics ? 64 : 32;
            if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
                this.particleMaxAge = 0;
            }
        } catch (Exception ignored) {
        }
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

        this.motionY += 0.01D * this.particleGravity;
        if (this.canCollide) {
            pushOutOfBlocks(this.posX, this.posY, this.posZ);
        }
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985D;
        this.motionY *= 0.985D;
        this.motionZ *= 0.985D;
        this.motionX = MathHelper.clamp(this.motionX, -0.05D, 0.05D);
        this.motionY = MathHelper.clamp(this.motionY, -0.05D, 0.05D);
        this.motionZ = MathHelper.clamp(this.motionZ, -0.05D, 0.05D);

        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 2.0D) {
            this.particleScale *= 0.98F;
        }
        if (this.particleScale < 0.2F) {
            this.setExpired();
            return;
        }
        if (dist > 1.0E-6D) {
            double accel = 0.01D / Math.min(1.0D, dist);
            this.motionX += (dx / dist) * accel;
            this.motionY += (dy / dist) * accel;
            this.motionZ += (dz / dist) * accel;
        }
    }

    public void setGravity(float value) {
        this.particleGravity = value;
    }

    protected boolean pushOutOfBlocks(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        double localX = x - pos.getX();
        double localY = y - pos.getY();
        double localZ = z - pos.getZ();
        if (isOpenBlockSpace(pos) || this.world.collidesWithAnyBlock(this.getBoundingBox())) {
            return false;
        }

        boolean westOpen = isOpenBlockSpace(pos.west());
        boolean eastOpen = isOpenBlockSpace(pos.east());
        boolean downOpen = isOpenBlockSpace(pos.down());
        boolean upOpen = isOpenBlockSpace(pos.up());
        boolean northOpen = isOpenBlockSpace(pos.north());
        boolean southOpen = isOpenBlockSpace(pos.south());

        int face = -1;
        double best = 9999.0D;
        if (westOpen && localX < best) {
            best = localX;
            face = 0;
        }
        if (eastOpen && 1.0D - localX < best) {
            best = 1.0D - localX;
            face = 1;
        }
        if (downOpen && localY < best) {
            best = localY;
            face = 2;
        }
        if (upOpen && 1.0D - localY < best) {
            best = 1.0D - localY;
            face = 3;
        }
        if (northOpen && localZ < best) {
            best = localZ;
            face = 4;
        }
        if (southOpen && 1.0D - localZ < best) {
            face = 5;
        }

        float speed = this.rand.nextFloat() * 0.05F + 0.025F;
        float jitter = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
        if (face == 0) {
            this.motionX = -speed;
            this.motionY = jitter;
            this.motionZ = jitter;
        } else if (face == 1) {
            this.motionX = speed;
            this.motionY = jitter;
            this.motionZ = jitter;
        } else if (face == 2) {
            this.motionY = -speed;
            this.motionX = jitter;
            this.motionZ = jitter;
        } else if (face == 3) {
            this.motionY = speed;
            this.motionX = jitter;
            this.motionZ = jitter;
        } else if (face == 4) {
            this.motionZ = -speed;
            this.motionX = jitter;
            this.motionY = jitter;
        } else if (face == 5) {
            this.motionZ = speed;
            this.motionX = jitter;
            this.motionY = jitter;
        }
        return true;
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return this.world.isAirBlock(pos) || !this.world.getBlockState(pos).getMaterial().blocksMovement();
    }

    @Override
    public int getFXLayer() {
        // Vanilla ParticleManager fallback. TC sheet/layer routing is handled by ParticleEngine.
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float bob = MathHelper.sin((this.particleAge - this.count) / 5.0F) * 0.25F + 1.0F;
        float scale = 0.1F * this.particleScale * bob;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;

        float uMin = 0.5625F;
        float uMax = 0.625F;
        float vMin = 0.0625F;
        float vMax = 0.125F;
        addVertex(buffer, x - rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z - rotationYZ * scale - rotationXZ * scale, uMin, vMax, lightX, lightY);
        addVertex(buffer, x - rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z - rotationYZ * scale + rotationXZ * scale, uMax, vMax, lightX, lightY);
        addVertex(buffer, x + rotationX * scale + rotationXY * scale, y + rotationZ * scale,
                z + rotationYZ * scale + rotationXZ * scale, uMax, vMin, lightX, lightY);
        addVertex(buffer, x + rotationX * scale - rotationXY * scale, y - rotationZ * scale,
                z + rotationYZ * scale - rotationXZ * scale, uMin, vMin, lightX, lightY);
    }

    private void addVertex(BufferBuilder buffer, double x, double y, double z, float u, float v, int lightX, int lightY) {
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.5F)
                .lightmap(lightX, lightY)
                .endVertex();
    }
}
