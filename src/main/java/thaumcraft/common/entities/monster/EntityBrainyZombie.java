package thaumcraft.common.entities.monster;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

import javax.annotation.Nullable;

public class EntityBrainyZombie extends net.minecraft.entity.monster.EntityZombie {
    public EntityBrainyZombie(World world) {
        super(world);
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        if (this.getEntityAttribute(EntityZombie.SPAWN_REINFORCEMENTS_CHANCE) != null) {
            this.getEntityAttribute(EntityZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
        }
    }

    @Override
    public int getMaxSpawnedInChunk() {
        int v = super.getMaxSpawnedInChunk() + 3;
        if (v > 20) v = 20;
        return v;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        if (this.isChild()) {
            return;
        }
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
        if (wasRecentlyHit) {
            int rareRoll = this.rand.nextInt(200) - lootingModifier;
            if (rareRoll < 5) {
                this.dropRareDrop(rareRoll <= 0 ? 1 : 0);
            }
        }
    }

    protected void dropRareDrop(int chance) {
        switch (this.rand.nextInt(3)) {
            case 0:
                this.dropItem(Items.IRON_INGOT, 1);
                break;
            case 1:
                this.dropItem(Items.CARROT, 1);
                break;
            default:
                this.dropItem(Items.POTATO, 1);
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        for (int i = 0; i < 3; ++i) {
            if (this.world.rand.nextBoolean()) {
                this.dropItem(Items.ROTTEN_FLESH, 1);
            }
        }
        if (this.world.rand.nextInt(10) - looting <= 4) {
            this.entityDropItem(new ItemStack(ConfigItems.itemZombieBrain), 1.5F);
        }
    }
}
