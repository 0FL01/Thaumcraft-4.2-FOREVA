package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXWisp extends Particle implements ITCParticle {
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean hasTarget;
    public boolean shrink = false;
    public boolean tinkle = false;
    public int blendmode = 1;
    private final float moteParticleScale;
    private final int moteHalfLife;

    public FXWisp(World world, double x, double y, double z, double tx, double ty, double tz, float size, boolean flag, float speed) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.hasTarget = tx != 0.0D || ty != 0.0D || tz != 0.0D;
        this.particleRed = MathHelper.clamp(speed, 0.0F, 1.0F);
        this.particleGreen = flag ? 0.8F : 0.4F;
        this.particleBlue = 1.0F - this.particleRed * 0.5F;
        this.particleGravity = 0.0F;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleScale *= Math.max(0.2F, size);
        this.moteParticleScale = this.particleScale;
        this.particleMaxAge = (int) (36.0D / (Math.random() * 0.3D + 0.7D));
        this.moteHalfLife = Math.max(1, this.particleMaxAge / 2);
        this.canCollide = true;
        this.setSize(0.1F, 0.1F);

        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 50 : 25;
        if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
    }

    public FXWisp(World world, double x, double y, double z, float size, float red, float green, float blue) {
        this(world, x, y, z, 0.0D, 0.0D, 0.0D, size, false, 0.0F);
        this.setRBGColorF(red, green, blue);
    }

    public FXWisp(World world, double x, double y, double z, float size, int type) {
        this(world, x, y, z, 0.0D, 0.0D, 0.0D, size, false, 0.0F);
        applyTypedColor(world, type);
    }

    public FXWisp(World world, double x, double y, double z, double tx, double ty, double tz, float size, int type) {
        this(world, x, y, z, size, type);
        if (this.particleMaxAge > 0) {
            this.motionX = (tx - this.posX) / (double) this.particleMaxAge;
            this.motionY = (ty - this.posY) / (double) this.particleMaxAge;
            this.motionZ = (tz - this.posZ) / (double) this.particleMaxAge;
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

        this.motionY -= 0.04D * this.particleGravity;
        if (this.canCollide) {
            pushOutOfBlocks(this.posX, this.posY, this.posZ);
        }
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.hasTarget) {
            this.motionX *= 0.985D;
            this.motionY *= 0.985D;
            this.motionZ *= 0.985D;
            double dx = this.targetX - this.posX;
            double dy = this.targetY - this.posY;
            double dz = this.targetZ - this.posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 1.0E-6D) {
                double pull = 0.2D;
                this.motionX += (dx / dist) * pull;
                this.motionY += (dy / dist) * pull;
                this.motionZ += (dz / dist) * pull;
                this.motionX = MathHelper.clamp(this.motionX, -0.2D, 0.2D);
                this.motionY = MathHelper.clamp(this.motionY, -0.2D, 0.2D);
                this.motionZ = MathHelper.clamp(this.motionZ, -0.2D, 0.2D);
            }
        } else {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
            if (this.onGround) {
                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
            }
        }
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

    public void setGravity(float value) {
        this.particleGravity = value;
    }

    public void setNoClip(boolean noClip) {
        this.canCollide = !noClip;
    }

    private void applyTypedColor(World world, int type) {
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
                this.setRBGColorF(1.0F, 1.0F, 1.0F);
        }
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

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float ageScale;
        if (this.shrink) {
            ageScale = (this.particleMaxAge - this.particleAge) / (float) Math.max(1, this.particleMaxAge);
        } else {
            ageScale = this.particleAge / (float) this.moteHalfLife;
            if (ageScale > 1.0F) {
                ageScale = 2.0F - ageScale;
            }
        }

        float scale = 0.5F * this.moteParticleScale * ageScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;
        float uMin = 0.0F;
        float uMax = 0.125F;
        float vMin = 0.875F;
        float vMax = 1.0F;
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
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.5F)
                .lightmap(lightX, lightY)
                .endVertex();
    }
}
