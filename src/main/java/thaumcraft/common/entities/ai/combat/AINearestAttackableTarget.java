package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AINearestAttackableTarget extends EntityAITarget {
    private final EntityGolemBase golem;
    private final Class targetClass;

    public AINearestAttackableTarget(EntityGolemBase golem, Class targetClass, boolean checkSight) {
        super(golem, checkSight);
        this.golem = golem;
        this.targetClass = targetClass;
    }

    @Override
    public boolean shouldExecute() { return false; }
}
