package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPrimalArrow extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> ARROW_TYPE =
        EntityDataManager.createKey(EntityPrimalArrow.class, DataSerializers.VARINT);

    public EntityPrimalArrow(World world) { super(world); }
    public EntityPrimalArrow(World world, EntityLivingBase shooter) {
        super(world, shooter);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ARROW_TYPE, 0);
    }

    public int getArrowType() { return this.dataManager.get(ARROW_TYPE); }
    public void setArrowType(int type) { this.dataManager.set(ARROW_TYPE, type); }

    @Override
    protected ItemStack getArrowStack() { return ItemStack.EMPTY; }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result == null) return;
        // Let EntityArrow handle physical damage + sticking first
        super.onHit(result);

        // Apply primal aspect effects if we hit a living entity
        if (!this.world.isRemote && result.typeOfHit == RayTraceResult.Type.ENTITY
                && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            int type = this.getArrowType();
            switch (type) {
                case 0: // Air — unblockable magic damage
                    target.attackEntityFrom(
                        new EntityDamageSourceIndirect("indirectMagic", this, this.shootingEntity)
                            .setDamageBypassesArmor().setMagicDamage().setProjectile(), 1.0f);
                    break;
                case 1: // Fire — fire damage + ignite
                    target.setFire(5);
                    target.attackEntityFrom(
                        new EntityDamageSourceIndirect("firearrow", this, this.shootingEntity)
                            .setFireDamage().setProjectile(), 1.0f);
                    break;
                case 2: // Water — slowness
                    target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
                    break;
                case 3: // Earth — bonus damage already handled by super.onHit with 1.5x speed
                    break;
                case 4: // Order — weakness
                    target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 4));
                    break;
                case 5: // Entropy — wither
                    target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 0));
                    break;
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("arrowType", this.getArrowType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setArrowType(nbt.getInteger("arrowType"));
    }

    @Override public void writeSpawnData(ByteBuf buf) { buf.writeInt(this.getArrowType()); }
    @Override public void readSpawnData(ByteBuf buf) { this.setArrowType(buf.readInt()); }
}
