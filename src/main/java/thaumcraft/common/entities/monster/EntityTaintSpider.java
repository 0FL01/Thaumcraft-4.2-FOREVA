package thaumcraft.common.entities.monster;

public class EntityTaintSpider extends net.minecraft.entity.monster.EntitySpider implements thaumcraft.api.entities.ITaintedMob {
    public EntityTaintSpider(net.minecraft.world.World world) {
        super(world);
        this.setSize(0.4F, 0.3F);
        this.experienceValue = 2;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override protected float getSoundPitch() { return 0.7f; }

    public float spiderScaleAmount() {
        return 0.4F;
    }

    @Override
    public double getYOffset() {
        return 0.1D;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        if (this.world.rand.nextInt(6) == 0) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 11), this.height / 2.0F);
            } else {
                this.entityDropItem(new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemResource, 1, 12), this.height / 2.0F);
            }
        }
    }
}
