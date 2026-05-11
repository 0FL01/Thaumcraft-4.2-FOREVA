package thaumcraft.common.entities.monster;

public class EntityBrainyZombie extends net.minecraft.entity.monster.EntityZombie {
    public EntityBrainyZombie(net.minecraft.world.World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        int v = super.getMaxSpawnedInChunk() + 3;
        if (v > 20) v = 20;
        return v;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        for (int a = 0; a < 3; ++a) {
            if (!this.world.rand.nextBoolean()) continue;
            this.dropItem(net.minecraft.init.Items.ROTTEN_FLESH, 1);
        }
        if (this.world.rand.nextInt(10) - looting <= 4) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemZombieBrain), 1.5f);
        }
    }
}
