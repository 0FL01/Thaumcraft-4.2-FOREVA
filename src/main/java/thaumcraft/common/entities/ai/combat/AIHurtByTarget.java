package thaumcraft.common.entities.ai.combat;

import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIHurtByTarget extends AITarget {
    private final EntityGolemBase golem;

    public AIHurtByTarget(EntityGolemBase golem) {
        super(golem, false);
        this.golem = golem;
    }

    @Override
    public boolean shouldExecute() { return false; }
}
