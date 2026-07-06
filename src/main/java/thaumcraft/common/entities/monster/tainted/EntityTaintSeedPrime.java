package thaumcraft.common.entities.monster.tainted;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/** Minimal TC6 package-compatible prime taint seed entity. */
public class EntityTaintSeedPrime extends EntityTaintSeed {

    public EntityTaintSeedPrime(World world) {
        super(world);
        this.setSize(1.2F, 2.0F);
        this.experienceValue = 16;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
    }
}
