package thaumcraft.common.lib.world.dim;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterThaumcraft extends Teleporter {
    public TeleporterThaumcraft(WorldServer ws) {
        super(ws);
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        int x = (int) entity.posX;
        int z = (int) entity.posZ;
        int y = 60;
        entity.setLocationAndAngles(x, y, z, rotationYaw, 0.0f);
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
        placeInPortal(entity, rotationYaw);
        return true;
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }
}
