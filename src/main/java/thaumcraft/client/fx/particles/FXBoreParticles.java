package thaumcraft.client.fx.particles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBoreParticles extends Particle {
    private final Block blockInstance;
    private final IBlockState blockState;
    private final Item itemInstance;
    private final int metadata;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public FXBoreParticles(World world, double x, double y, double z, double tx, double ty, double tz, IBlockState state) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.blockInstance = state.getBlock();
        this.blockState = state;
        this.itemInstance = null;
        this.metadata = 0;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.setParticleTexture(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        this.particleGravity = this.blockInstance.blockParticleGravity;
        this.particleRed = 0.6F;
        this.particleGreen = 0.6F;
        this.particleBlue = 0.6F;
        initMotionAndLifetime();
    }

    public FXBoreParticles(World world, double x, double y, double z, double tx, double ty, double tz, Item item, int meta) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.blockInstance = null;
        this.blockState = null;
        this.itemInstance = item;
        this.metadata = meta;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, meta);
        if (sprite != null) {
            this.setParticleTexture(sprite);
        }
        this.particleGravity = 0.2F;
        this.particleRed = 0.6F;
        this.particleGreen = 0.6F;
        this.particleBlue = 0.6F;
        initMotionAndLifetime();
        if (sprite == null) {
            this.setExpired();
        }
    }

    private void initMotionAndLifetime() {
        this.particleScale = this.rand.nextFloat() * 0.3F + 0.4F;
        this.motionX = this.rand.nextGaussian() * 0.01D;
        this.motionY = this.rand.nextGaussian() * 0.01D;
        this.motionZ = this.rand.nextGaussian() * 0.01D;
        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        int base = Math.max(1, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0D));
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);
        this.canCollide = true;
        applyLocalColor();

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        int visibleDistance = mc.gameSettings.fancyGraphics ? 64 : 32;
        if (player != null && player.getDistance(this.posX, this.posY, this.posZ) > (double) visibleDistance) {
            this.particleMaxAge = 0;
        }
    }

    private void applyLocalColor() {
        if (this.blockState != null) {
            BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
            int color;
            try {
                color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(this.blockState, this.world, pos, 0);
            } catch (Exception ignored) {
                color = 0xFFFFFF;
            }
            applyColorMultiplier(color);
            return;
        }
        if (this.itemInstance != null) {
            try {
                ItemStack stack = new ItemStack(this.itemInstance, 1, this.metadata);
                int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, 0);
                applyColorMultiplier(color);
            } catch (Exception ignored) {
                // Keep default particle tint.
            }
        }
    }

    private void applyColorMultiplier(int color) {
        this.particleRed *= (float) (color >> 16 & 255) / 255.0F;
        this.particleGreen *= (float) (color >> 8 & 255) / 255.0F;
        this.particleBlue *= (float) (color & 255) / 255.0F;
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

        if (this.particleAge++ >= this.particleMaxAge
                || (MathHelper.floor(this.posX) == MathHelper.floor(this.targetX)
                && MathHelper.floor(this.posY) == MathHelper.floor(this.targetY)
                && MathHelper.floor(this.posZ) == MathHelper.floor(this.targetZ))) {
            this.setExpired();
            return;
        }

        if (this.canCollide) {
            pushOutOfBlocks(this.posX, this.posY, this.posZ);
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985D;
        this.motionY *= 0.985D;
        this.motionZ *= 0.985D;

        double dx = this.targetX - this.posX;
        double dy = this.targetY - this.posY;
        double dz = this.targetZ - this.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double accel = 0.3D;
        if (dist < 4.0D) {
            this.particleScale *= 0.9F;
            accel = 0.6D;
        }
        if (dist > 1.0E-6D) {
            this.motionX += dx / dist * accel;
            this.motionY += dy / dist * accel;
            this.motionZ += dz / dist * accel;
        }
        this.motionX = MathHelper.clamp(this.motionX, -0.35D, 0.35D);
        this.motionY = MathHelper.clamp(this.motionY, -0.35D, 0.35D);
        this.motionZ = MathHelper.clamp(this.motionZ, -0.35D, 0.35D);
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
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
