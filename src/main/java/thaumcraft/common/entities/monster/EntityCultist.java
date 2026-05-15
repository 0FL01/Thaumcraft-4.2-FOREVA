package thaumcraft.common.entities.monster;

import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;

public class EntityCultist extends net.minecraft.entity.monster.EntityMob {
    private int homeX, homeY, homeZ;

    public EntityCultist(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(8.0);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.homeX = nbt.getInteger("homeX");
        this.homeY = nbt.getInteger("homeY");
        this.homeZ = nbt.getInteger("homeZ");
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("homeX", this.homeX);
        nbt.setInteger("homeY", this.homeY);
        nbt.setInteger("homeZ", this.homeZ);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        int roll = this.rand.nextInt(10);
        if (roll == 0) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT), 1.5F);
        } else if (roll <= 1) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_VOID_SEED), 1.5F);
        } else if (roll <= 3 + looting) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_COIN), 1.5F);
        }
        super.dropFewItems(wasRecentlyHit, looting);
        if (wasRecentlyHit && this.rand.nextInt(200) - looting < 5) {
            this.entityDropItem(new ItemStack(ConfigItems.itemEldritchObject, 1, 1), 1.0F);
        }
    }

    @Override public net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }

    // CultistCleric gets ambient chant separately
}
