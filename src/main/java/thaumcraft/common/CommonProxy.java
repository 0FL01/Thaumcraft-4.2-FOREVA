package thaumcraft.common;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thaumcraft.common.container.*;
import thaumcraft.common.entities.ContainerPech;
import thaumcraft.common.entities.golems.ContainerGolem;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.golems.ContainerTravelingTrunk;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.lib.research.ResearchManager;

public class CommonProxy implements IGuiHandler {

    // Capability-based player data accessors

    /**
     * Get the IPlayerKnowledge capability for a player.
     */
    public static IPlayerKnowledge getPlayerKnowledge(EntityPlayer player) {
        if (player == null) return null;
        return player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
    }

    /**
     * Get the research manager (delegates to capability system).
     */
    public ResearchManager getResearchManager() {
        return new ResearchManager();
    }

    // ---- IGuiHandler ----

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Entity entity = world.getEntityByID(x);
        switch (ID) {
            case 0: return new ContainerArcaneWorkbench();
            case 1: return new ContainerResearchTable();
            case 2: return new ContainerArcaneBore();
            case 3: return new ContainerAlchemyFurnace();
            case 4: return new ContainerDeconstructionTable();
            case 5: return new ContainerFocusPouch();
            case 6: return entity instanceof EntityGolemBase ? new ContainerGolem(player.inventory, (EntityGolemBase) entity) : null;
            case 7: return entity instanceof EntityPech ? new ContainerPech(player.inventory, world, (EntityPech) entity) : null;
            case 8: return entity instanceof EntityTravelingTrunk ? new ContainerTravelingTrunk(player.inventory, world, (EntityTravelingTrunk) entity) : null;
            case 9: return new ContainerThaumatorium();
            case 10: return new ContainerHandMirror();
            case 11: return new ContainerHoverHarness();
            case 12: return new ContainerMagicBox();
            case 13: return new ContainerSpa();
            case 14: return new ContainerFocalManipulator();
            default: return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    // ---- Registration stubs (overridden by ClientProxy) ----

    public void registerDisplayInformation() {
    }

    public void registerKeyBindings() {
    }

    public void registerHandlers() {
    }

    // ---- FX stubs (ClientProxy overrides with actual GL calls) ----

    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
    }

    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
    }

    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
    }

    public void burst(World world, double x, double y, double z, float scale) {
    }

    public void wispFX3(World world, double x, double y, double z, double tx, double ty, double tz, float size, int count, boolean flag, float speed) {
    }

    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
    }

    public int particleCount(int def) {
        return def;
    }
}
