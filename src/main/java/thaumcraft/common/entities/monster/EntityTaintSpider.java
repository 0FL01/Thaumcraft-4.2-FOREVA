package thaumcraft.common.entities.monster;

public class EntityTaintSpider extends net.minecraft.entity.monster.EntitySpider implements thaumcraft.api.entities.ITaintedMob {
    public EntityTaintSpider(net.minecraft.world.World world) {
        super(world);
        this.setSize(1.4f, 0.9f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override protected float getSoundPitch() { return 0.7f; }

    public float spiderScaleAmount() {
        return 0.4F;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0f);
        } else {
            this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0f);
        }
    }
}
