package thaumcraft.common.entities.ai.combat;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class AIOldestAttackableTargetSorter implements Comparator<Entity> {
    public AIOldestAttackableTargetSorter(Entity entity) {
    }

    @Override
    public int compare(Entity a, Entity b) {
        int ageA = a.ticksExisted;
        int ageB = b.ticksExisted;
        return ageA > ageB ? -1 : (ageA < ageB ? 1 : 0);
    }
}
