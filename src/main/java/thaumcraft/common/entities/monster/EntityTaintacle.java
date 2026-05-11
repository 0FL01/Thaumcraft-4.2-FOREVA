package thaumcraft.common.entities.monster;

public class EntityTaintacle extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public float flailIntensity = 1.0f;

    public EntityTaintacle(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.66f, 3.0f);
        this.experienceValue = 10;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            // TODO: check biome taint
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
        super.dropFewItems(wasRecentlyHit, looting);
    }

    @Override public int getMaxSpawnedInChunk() { return 200; }
    // TODO: sound events
}
