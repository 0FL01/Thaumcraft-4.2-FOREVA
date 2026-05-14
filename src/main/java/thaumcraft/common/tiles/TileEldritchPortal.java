package thaumcraft.common.tiles;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.world.dim.TeleporterThaumcraft;

public class TileEldritchPortal extends TileEntity implements ITickable {
    public int opencount = -1;
    private int count = 0;
    /** Cooldown counter for teleportation (world time). */
    public long lastTeleport = 0;

    @Override
    public void update() {
        if (this.world == null) return;
        this.count++;

        if (this.world.isRemote) {
            if (this.opencount < 30) {
                this.opencount++;
            }
            return;
        }

        if (this.count % 5 != 0) return;

        AxisAlignedBB bounds = new AxisAlignedBB(this.pos).grow(0.5, 1.0, 0.5);
        List<EntityPlayerMP> players = this.world.getEntitiesWithinAABB(EntityPlayerMP.class, bounds);
        for (EntityPlayerMP player : players) {
            transferPlayer(player);
        }
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 9216.0;
    }

    private void transferPlayer(EntityPlayerMP player) {
        if (player.getRidingEntity() != null || player.isBeingRidden()) return;
        if (player.timeUntilPortal > 0) {
            player.timeUntilPortal = 100;
            return;
        }
        if (player.getServer() == null) return;

        int targetDim = player.dimension == Config.dimensionOuterId ? 0 : Config.dimensionOuterId;
        WorldServer targetWorld = player.getServer().getWorld(targetDim);
        if (targetWorld == null) return;

        player.timeUntilPortal = 100;
        player.changeDimension(targetDim, new TeleporterThaumcraft(targetWorld));
        if (targetDim == Config.dimensionOuterId && !ResearchManager.isResearchComplete(player, "ENTEROUTER")) {
            ResearchManager.addResearch(player, "ENTEROUTER");
        }
    }

    @Override
    public void readFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.lastTeleport = compound.getLong("lastTeleport");
    }

    @Override
    public net.minecraft.nbt.NBTTagCompound writeToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setLong("lastTeleport", this.lastTeleport);
        return compound;
    }
}
