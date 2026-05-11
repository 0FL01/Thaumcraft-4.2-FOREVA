package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.monster.EntityCultist;

public class AICultistHurtByTarget extends EntityAITarget {
    private final EntityCultist cultist;

    public AICultistHurtByTarget(EntityCultist cultist) {
        super(cultist, true);
        this.cultist = cultist;
    }

    @Override
    public boolean shouldExecute() { return false; }
}
