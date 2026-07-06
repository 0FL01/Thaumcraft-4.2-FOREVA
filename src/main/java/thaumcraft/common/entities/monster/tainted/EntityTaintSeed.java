package thaumcraft.common.entities.monster.tainted;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;

/** Minimal TC6 package-compatible taint seed entity. */
public class EntityTaintSeed extends EntityMob implements ITaintedMob {

    public EntityTaintSeed(World world) {
        super(world);
        this.setSize(0.9F, 1.5F);
        this.experienceValue = 8;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
    }
}
