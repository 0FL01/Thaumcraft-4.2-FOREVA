package thaumcraft.common.entities.monster;

public class EntityMindSpider extends net.minecraft.entity.monster.EntitySpider {
    private int harmlessTicks = 1200;

    public EntityMindSpider(net.minecraft.world.World world) { super(world); this.setSize(0.3f, 0.3f); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.harmlessTicks > 0) {
            this.harmlessTicks--;
            if (this.harmlessTicks == 0 && !this.world.isRemote) this.setDead();
        }
    }

    @Override protected float getSoundPitch() { return 0.7f; }

    @Override public boolean isAIDisabled() { return this.harmlessTicks > 0; }

    @Override public boolean isEntityInvulnerable(net.minecraft.util.DamageSource src) { return this.harmlessTicks > 0; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {}
    @Override public int getMaxSpawnedInChunk() { return 200; }

    // ---- WarpEvents phantom spider support ----

    private String viewer = null;

    public void setViewer(String name) {
        this.viewer = name;
    }

    public String getViewer() {
        return this.viewer == null ? "" : this.viewer;
    }

    public void setHarmless(boolean harmless) {
        this.harmlessTicks = harmless ? 1200 : 0;
    }
}
