package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.InventoryUtils;

/**
 * Vis aspect orb — floats in world, attracted to players with wands that have
 * room for the aspect. On collision, adds vis to the wand and despawns.
 */
public class EntityAspectOrb extends Entity implements IEntityAdditionalSpawnData {

    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    private Aspect aspect;
    private int aspectValue;
    private EntityPlayer closestPlayer;

    public EntityAspectOrb(World world) {
        super(world);
        this.setSize(0.125F, 0.125F);
    }

    public EntityAspectOrb(World world, double x, double y, double z, Aspect aspect, int value) {
        super(world);
        this.setSize(0.125F, 0.125F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (Math.random() * 360.0);
        this.motionX = (float) (Math.random() * 0.4 - 0.2);
        this.motionY = (float) (Math.random() * 0.4);
        this.motionZ = (float) (Math.random() * 0.4 - 0.2);
        this.aspectValue = value;
        this.setAspect(aspect);
    }

    @Override
    protected void entityInit() {}

    @Override
    protected boolean canTriggerWalking() { return false; }

    @Override
    public boolean isInRangeToRenderDist(double dist) {
        double d = this.getEntityBoundingBox().getAverageEdgeLength();
        if (Double.isNaN(d)) d = 1.0D;
        d = d * 64.0D;
        return dist < d * d;
    }

    // ------------------------------------------------------------------
    // Brightness — fullbright glow
    // ------------------------------------------------------------------

    @Override
    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public float getEyeHeight() {
        return this.height / 2.0F;
    }

    @Override
    public boolean handleWaterMovement() {
        return this.world.isMaterialInBB(this.getEntityBoundingBox(), Material.WATER);
    }

    // ------------------------------------------------------------------
    // Main update — physics + player attraction
    // ------------------------------------------------------------------

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.orbCooldown > 0) {
            this.orbCooldown--;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        // Gravity
        this.motionY -= 0.03D;

        // Water splash
        if (this.world.getBlockState(this.getPosition()).getMaterial() == Material.WATER) {
            this.motionY = 0.2D;
            this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);

        // Find closest player with room for this aspect in their wand
        double range = 8.0D;
        if (this.getAspect() != null && this.ticksExisted % 5 == 0 && this.closestPlayer == null) {
            double closestDist = Double.MAX_VALUE;
            for (EntityPlayer player : this.world.playerEntities) {
                if (player.getDistanceSq(this) >= range * range) continue;
                double d = player.getDistanceSq(this);
                if (!(d < closestDist)) continue;
                int slot = InventoryUtils.isWandInHotbarWithRoom(this.getAspect(), this.aspectValue, player);
                if (slot < 0) continue;
                closestDist = d;
                this.closestPlayer = player;
            }
        }

        // Attraction towards closest player
        if (this.closestPlayer != null) {
            double dx = (this.closestPlayer.posX - this.posX) / range;
            double dy = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() - this.posY) / range;
            double dz = (this.closestPlayer.posZ - this.posZ) / range;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double weight = 1.0D - dist;
            if (weight > 0.0D && dist > 1.0E-6D) {
                weight *= weight;
                this.motionX += dx / dist * weight * 0.1D;
                this.motionY += dy / dist * weight * 0.1D;
                this.motionZ += dz / dist * weight * 0.1D;
            }
        }

        // Move
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        // Friction
        float friction = 0.98F;
        if (this.onGround) {
            friction = 0.588F;
        }
        this.motionX *= friction;
        this.motionY *= 0.98D;
        this.motionZ *= friction;

        // Bounce
        if (this.onGround) {
            this.motionY *= -0.9D;
        }

        // Expire
        this.orbAge++;
        if (this.orbAge >= this.orbMaxAge) {
            this.setDead();
        }
    }

    @Override
    protected void dealFireDamage(int amount) {
        this.attackEntityFrom(DamageSource.GENERIC, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.getIsInvulnerable()) return false;
        this.orbHealth = (int) ((float) this.orbHealth - amount);
        if (this.orbHealth <= 0) {
            this.setDead();
        }
        return false;
    }

    @Override
    public boolean canBeCollidedWith() { return false; }

    // ------------------------------------------------------------------
    // On collide with player — add vis to wand
    // ------------------------------------------------------------------

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote) {
            if (this.getAspect() == null || !this.getAspect().isPrimal()) return;
            int slot = InventoryUtils.isWandInHotbarWithRoom(this.getAspect(), this.aspectValue, player);
            if (this.orbCooldown == 0 && slot >= 0) {
                ItemWandCasting wand = (ItemWandCasting) player.inventory.mainInventory.get(slot).getItem();
                wand.addVis(player.inventory.mainInventory.get(slot), this.getAspect(), this.aspectValue);
                this.orbCooldown = 2;
                this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                this.setDead();
            }
        }
    }

    // ------------------------------------------------------------------
    // NBT
    // ------------------------------------------------------------------

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.orbHealth = compound.getShort("Health") & 0xFF;
        this.orbAge = compound.getShort("Age");
        this.aspectValue = compound.getShort("Value");
        this.setAspect(Aspect.getAspect(compound.getString("Aspect")));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short) this.orbHealth);
        compound.setShort("Age", (short) this.orbAge);
        compound.setShort("Value", (short) this.aspectValue);
        if (this.getAspect() != null) {
            compound.setString("Aspect", this.getAspect().getTag());
        }
    }

    // ------------------------------------------------------------------
    // Spawn data — aspect tag
    // ------------------------------------------------------------------

    @Override
    public void writeSpawnData(ByteBuf buf) {
        String tag = this.getAspect() != null ? this.getAspect().getTag() : "";
        buf.writeShort(tag.length());
        for (char c : tag.toCharArray()) {
            buf.writeChar(c);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        try {
            int len = buf.readShort();
            if (len <= 0) {
                this.setAspect(null);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append(buf.readChar());
            }
            this.setAspect(Aspect.getAspect(sb.toString()));
        } catch (Exception e) {
            // ignore
        }
    }

    // ------------------------------------------------------------------
    // Getters/setters
    // ------------------------------------------------------------------

    public int getAspectValue() {
        return this.aspectValue;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }
}
