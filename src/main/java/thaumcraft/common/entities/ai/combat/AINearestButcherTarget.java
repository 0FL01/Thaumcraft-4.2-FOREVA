package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AINearestButcherTarget extends EntityAITarget {
    private final EntityGolemBase golem;

    public AINearestButcherTarget(EntityGolemBase golem) {
        super(golem, true);
        this.golem = golem;
    }

    @Override
    public boolean shouldExecute() { return false; }
}
