package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FXSwarm extends Particle implements ITCParticle {
    private static final float MAX_SPEED = 0.35F;
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;
    private static final List<Long> buzzcount = new ArrayList<>();

    private final Entity target;
    private float turnSpeed = 10.0F;
    private float speed = 0.2F;
    private int deathTimer = 0;
    private float pitch = 0.0F;
    public int particle = 40;

    public FXSwarm(World world, double x, double y, double z, Entity target, float red, float green, float blue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleScale = this.rand.nextFloat() * 0.5F + 1.0F;
        this.particleGravity = 0.1F;
        this.canCollide = false;
        this.particleMaxAge = 200 + this.rand.nextInt(80);

        float spread = 0.2F;
        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
        this.motionY = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * spread;

        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 64 : 32;
        if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.setExpired();
        }
    }

    public FXSwarm(World world,
                   double x, double y, double z,
                   Entity target,
                   float red, float green, float blue,
                   float speed, float turnSpeed, float particleGravity) {
        this(world, x, y, z, target, red, green, blue);
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.particleGravity = particleGravity;
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

        this.particleAge++;

        boolean targetAlive = this.target != null
                && !this.target.isDead
                && (!(this.target instanceof EntityLivingBase) || ((EntityLivingBase) this.target).deathTime <= 0);
        boolean targetHurt = this.target instanceof EntityLivingBase && ((EntityLivingBase) this.target).hurtTime > 0;

        if (!targetAlive) {
            this.deathTimer++;
            this.motionY -= this.particleGravity * 0.5F;
            if (this.deathTimer > 50) {
                this.setExpired();
                return;
            }
        } else {
            this.motionY += this.particleGravity;
        }

        pushOutOfBlocks(this.posX, this.posY, this.posZ);
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985D;
        this.motionY *= 0.985D;
        this.motionZ *= 0.985D;

        if (targetAlive) {
            steerTowardsTarget(targetHurt);
        }

        if (this.world.rand.nextInt(50) == 0) {
            EntityLivingBase player = Minecraft.getMinecraft().player;
            if (player != null && player.getDistance(this.posX, this.posY, this.posZ) < 8.0D && buzzcount.size() < 3) {
                this.world.playSound(this.posX, this.posY, this.posZ, TCSounds.FLY, SoundCategory.AMBIENT,
                        0.03F, 0.5F + this.rand.nextFloat() * 0.4F, false);
                buzzcount.add(System.nanoTime() + 1_500_000L);
            }
        }
        if (buzzcount.size() >= 3 && buzzcount.get(0) < System.nanoTime()) {
            buzzcount.remove(0);
        }
    }

    private void steerTowardsTarget(boolean targetHurt) {
        double tx = this.target.posX - this.posX;
        double ty = (this.target.getEntityBoundingBox().minY + this.target.getEntityBoundingBox().maxY) * 0.5D
                - (this.getBoundingBox().minY + this.getBoundingBox().maxY) * 0.5D;
        double tz = this.target.posZ - this.posZ;
        double distSq = tx * tx + ty * ty + tz * tz;
        if (distSq < 1.0E-6D) {
            return;
        }

        float halfTurn = Math.max(1.0F, this.turnSpeed * 0.5F);
        int randomTurn = Math.max(1, (int) halfTurn);
        float maxYaw = halfTurn + this.rand.nextInt(randomTurn);
        float maxPitch = halfTurn + this.rand.nextInt(randomTurn);
        if (distSq > this.target.width && !targetHurt) {
            rotateToward(tx, ty, tz, maxYaw, maxPitch);
        } else {
            rotateToward(-tx, -ty, -tz, maxYaw, maxPitch);
        }
    }

    private void rotateToward(double tx, double ty, double tz, float maxYaw, float maxPitch) {
        double flat = Math.sqrt(tx * tx + tz * tz);
        float targetYaw = (float) (Math.atan2(tz, tx) * 180.0D / Math.PI) - 90.0F;
        float targetPitch = (float) (-(Math.atan2(ty, flat) * 180.0D / Math.PI));

        this.particleAngle = updateRotation(this.particleAngle, targetYaw, maxYaw);
        this.pitch = updateRotation(this.pitch, targetPitch, maxPitch);

        float yawRad = this.particleAngle * 0.017453292F;
        float pitchRad = this.pitch * 0.017453292F;
        double headingX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double headingZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);
        double headingY = -MathHelper.sin(pitchRad);
        setHeading(headingX, headingY, headingZ, Math.max(0.01F, this.speed), 15.0F);
    }

    private float updateRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > maxDelta) delta = maxDelta;
        if (delta < -maxDelta) delta = -maxDelta;
        return current + delta;
    }

    private void setHeading(double x, double y, double z, float speed, float variance) {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1.0E-6D) {
            return;
        }
        x /= len;
        y /= len;
        z /= len;
        double jitter = 0.0075D * variance;
        x += this.rand.nextGaussian() * jitter;
        y += this.rand.nextGaussian() * jitter;
        z += this.rand.nextGaussian() * jitter;

        this.motionX = MathHelper.clamp((float) (x * speed), -MAX_SPEED, MAX_SPEED);
        this.motionY = MathHelper.clamp((float) (y * speed), -MAX_SPEED, MAX_SPEED);
        this.motionZ = MathHelper.clamp((float) (z * speed), -MAX_SPEED, MAX_SPEED);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float bob = MathHelper.sin(this.particleAge / 3.0F) * 0.25F + 1.0F;
        int frame = 7 + this.particleAge % 8;
        float u0 = frame / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.25F;
        float v1 = v0 + 0.0624375F;
        float size = 0.1F * this.particleScale * bob;
        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        float alpha = (50.0F - this.deathTimer) / 50.0F;
        float red = this.particleRed;
        float green = this.particleGreen;
        float blue = this.particleBlue;
        if (this.target instanceof EntityLivingBase && ((EntityLivingBase) this.target).hurtTime > 0) {
            green *= 0.5F;
            blue *= 0.5F;
        }

        addLitVertex(buffer, px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size, u1, v1, red, green, blue, alpha);
        addLitVertex(buffer, px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size, u1, v0, red, green, blue, alpha);
        addLitVertex(buffer, px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size, u0, v0, red, green, blue, alpha);
        addLitVertex(buffer, px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size, u0, v1, red, green, blue, alpha);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getTCParticleLayer() {
        return 1;
    }

    protected boolean pushOutOfBlocks(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        double localX = x - pos.getX();
        double localY = y - pos.getY();
        double localZ = z - pos.getZ();
        if (this.world.getBlockState(pos).getBlock() == ConfigBlocks.blockTaintFibres
                || this.world.isAirBlock(pos)
                || this.world.collidesWithAnyBlock(this.getBoundingBox())) {
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
    public int getBrightnessForRender(float partialTicks) {
        return LIGHTMAP_FULLBRIGHT;
    }

    private void addLitVertex(BufferBuilder buffer, double x, double y, double z, double u, double v,
                              float red, float green, float blue, float alpha) {
        int lightU = LIGHTMAP_FULLBRIGHT & 0xFFFF;
        int lightV = LIGHTMAP_FULLBRIGHT >> 16 & 0xFFFF;
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(red, green, blue, alpha)
                .lightmap(lightU, lightV)
                .endVertex();
    }
}
