package thaumcraft.client.fx.particles;

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

@SideOnly(Side.CLIENT)
public class FXWispEG extends Particle {
    private final Entity target;
    public int blendmode = 771;

    public FXWispEG(World world, double x, double y, double z, Entity target) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.motionX = this.rand.nextGaussian() * 0.03D;
        this.motionY = -0.05D;
        this.motionZ = this.rand.nextGaussian() * 0.03D;
        this.particleScale *= 0.4F;
        this.particleMaxAge = (int) (40.0D / (Math.random() * 0.3D + 0.7D));
        this.canCollide = true;
        this.setSize(0.01F, 0.01F);
        this.particleRed = this.rand.nextFloat() * 0.05F;
        this.particleGreen = this.rand.nextFloat() * 0.05F;
        this.particleBlue = this.rand.nextFloat() * 0.05F;

        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 50 : 25;
        if (player != null && player.getDistance(x, y, z) > visibleDistance) {
            this.particleMaxAge = 0;
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

        if (this.target != null && !this.onGround) {
            this.posX += this.target.motionX;
            this.posZ += this.target.motionZ;
        }
        pushOutOfBlocks(this.posX, this.posY, this.posZ);
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
        if (this.onGround) {
            this.motionX *= 0.85D;
            this.motionZ *= 0.85D;
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

    @Override
    public int getFXLayer() {
        // FXWispEG also animates against the misc particle sheet with
        // setParticleTextureIndex(...), so it must not be routed through the
        // 1.12.2 terrain-texture layer.
        return 0;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        EntityLivingBase player = Minecraft.getMinecraft().player;
        float ageScale = 1.0F - this.particleAge / (float) Math.max(1, this.particleMaxAge);
        float viewFade = 1.0F;
        if (player != null) {
            float maxDist = 1024.0F;
            double dx = this.posX - player.posX;
            double dy = this.posY - player.posY;
            double dz = this.posZ - player.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            viewFade = (float) (1.0D - Math.min(maxDist, distSq) / maxDist);
        }

        float alpha = this.particleAlpha;
        this.particleAlpha = 0.2F * ageScale * viewFade;
        this.setParticleTextureIndex(48 + (this.particleAge % 13));
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        this.particleAlpha = alpha;
    }
}
