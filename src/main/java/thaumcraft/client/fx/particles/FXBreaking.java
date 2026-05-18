package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBreaking extends Particle {
    private final Item item;
    private final int itemMeta;

    public FXBreaking(World world, double x, double y, double z, Item item) {
        this(world, x, y, z, 0.0D, 0.0D, 0.0D, item, 0);
    }

    public FXBreaking(World world, double x, double y, double z, double mx, double my, double mz, Item item) {
        this(world, x, y, z, mx, my, mz, item, 0);
    }

    public FXBreaking(World world, double x, double y, double z, double mx, double my, double mz, Item item, int meta) {
        super(world, x, y, z, mx, my, mz);
        this.item = item;
        this.itemMeta = meta;
        this.motionX += mx;
        this.motionY += my;
        this.motionZ += mz;
        this.particleScale *= 0.5F;
        this.particleGravity = 0.06F;
        this.particleMaxAge = 12 + this.rand.nextInt(8);
        this.canCollide = false;
    }

    public void setParticleMaxAge(int particleMaxAge) {
        this.particleMaxAge = Math.max(1, particleMaxAge);
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
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }

        if (this.item != null) {
            this.world.spawnParticle(
                    EnumParticleTypes.ITEM_CRACK,
                    this.posX, this.posY, this.posZ,
                    this.motionX * 0.1D, this.motionY * 0.1D, this.motionZ * 0.1D,
                    Item.getIdFromItem(this.item), this.itemMeta);
        }
        this.world.spawnParticle(
                EnumParticleTypes.REDSTONE,
                this.posX, this.posY, this.posZ,
                this.particleRed, this.particleGreen, this.particleBlue);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
