package thaumcraft.common.entities.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTaintacleSmall extends EntityTaintacle implements thaumcraft.api.entities.ITaintedMob {
    private int lifetime = 200;

    public EntityTaintacleSmall(World world) {
        super(world);
        this.setSize(0.22F, 1.0F);
        this.experienceValue = 0;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (--this.lifetime <= 0) {
            this.attackEntityFrom(DamageSource.STARVE, 10.0F);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    protected Item getDropItem() {
        return Item.getItemById(0);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
    }
}
