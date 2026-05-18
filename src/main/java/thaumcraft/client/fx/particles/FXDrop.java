package thaumcraft.client.fx.particles;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXDrop extends Particle {
    private int bobTimer;

    public FXDrop(World world, double x, double y, double z, float r, float g, float b) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
        this.setParticleTextureIndex(113);
        this.particleGravity = 0.06f;
        this.bobTimer = 40;
        this.particleMaxAge = (int) (64.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 257;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= this.particleGravity;

        if (this.bobTimer-- > 0) {
            this.motionX *= 0.02;
            this.motionY *= 0.02;
            this.motionZ *= 0.02;
            this.setParticleTextureIndex(113);
        } else {
            this.setParticleTextureIndex(112);
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;

        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
            return;
        }

        if (this.onGround) {
            this.setParticleTextureIndex(114);
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }

        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
        Material material = this.world.getBlockState(pos).getMaterial();
        if (material != Material.AIR && (material.isLiquid() || material.isSolid())) {
            double fluidTop = pos.getY() + 1;
            if (this.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                Integer level = this.world.getBlockState(pos).getValue(BlockLiquid.LEVEL);
                fluidTop = pos.getY() + 1 - BlockLiquid.getLiquidHeightPercent(level);
            }
            if (this.posY < fluidTop) {
                this.setExpired();
            }
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
