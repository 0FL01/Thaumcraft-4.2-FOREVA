package thaumcraft.common.entities.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.items.ItemResource;

public class EntityCultist extends net.minecraft.entity.monster.EntityMob {

    public EntityCultist(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.experienceValue = 10;
        PathNavigate nav = this.getNavigator();
        if (nav instanceof PathNavigateGround) {
            PathNavigateGround ground = (PathNavigateGround) nav;
            ground.setBreakDoors(true);
            ground.setEnterDoors(true);
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("HomeD")) {
            this.setHomePosAndDistance(
                    new BlockPos(nbt.getInteger("HomeX"), nbt.getInteger("HomeY"), nbt.getInteger("HomeZ")),
                    nbt.getInteger("HomeD"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.hasHome() && this.getMaximumHomeDistance() > 0.0F) {
            BlockPos home = this.getHomePosition();
            nbt.setInteger("HomeD", (int)this.getMaximumHomeDistance());
            nbt.setInteger("HomeX", home.getX());
            nbt.setInteger("HomeY", home.getY());
            nbt.setInteger("HomeZ", home.getZ());
        }
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

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof EntityCultist || entityIn instanceof EntityCultistLeader || super.isOnSameTeam(entityIn);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (cls == EntityCultistCleric.class || cls == EntityCultistLeader.class || cls == EntityCultistKnight.class) {
            return false;
        }
        return super.canAttackClass(cls);
    }

    // CultistCleric gets ambient chant separately
}
