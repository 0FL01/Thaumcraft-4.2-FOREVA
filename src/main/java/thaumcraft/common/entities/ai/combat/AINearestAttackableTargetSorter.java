package thaumcraft.common.entities.ai.combat;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class AINearestAttackableTargetSorter implements Comparator<Entity> {
    private final Entity entity;

    public AINearestAttackableTargetSorter(Entity entity) {
        this.entity = entity;
    }

    @Override
    public int compare(Entity a, Entity b) {
        double da = this.entity.getDistanceSq(a);
        double db = this.entity.getDistanceSq(b);
        return da < db ? -1 : (da > db ? 1 : 0);
    }
}
