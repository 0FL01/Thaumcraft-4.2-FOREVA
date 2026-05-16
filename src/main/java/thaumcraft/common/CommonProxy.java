package thaumcraft.common;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.common.tiles.*;

public class CommonProxy implements IGuiHandler {

    public static final int GUI_GOLEM = 0;
    public static final int GUI_PECH = 1;
    public static final int GUI_TRAVELING_TRUNK = 2;
    public static final int GUI_THAUMATORIUM = 3;
    public static final int GUI_FOCUS_POUCH = 5;
    public static final int GUI_DECONSTRUCTION_TABLE = 8;
    public static final int GUI_ALCHEMY_FURNACE = 9;
    public static final int GUI_RESEARCH_TABLE = 10;
    public static final int GUI_THAUMONOMICON = 12;
    public static final int GUI_ARCANE_WORKBENCH = 13;
    public static final int GUI_ARCANE_BORE = 15;
    public static final int GUI_HAND_MIRROR = 16;
    public static final int GUI_HOVER_HARNESS = 17;
    public static final int GUI_MAGIC_BOX = 18;
    public static final int GUI_SPA = 19;
    public static final int GUI_FOCAL_MANIPULATOR = 20;

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
        BlockPos pos = new BlockPos(x, y, z);
        switch (ID) {
            case GUI_GOLEM: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityGolemBase ? new ContainerGolem(player.inventory, (EntityGolemBase) entity) : null;
            }
            case GUI_PECH: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityPech ? new ContainerPech(player.inventory, world, (EntityPech) entity) : null;
            }
            case GUI_TRAVELING_TRUNK: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityTravelingTrunk ? new ContainerTravelingTrunk(player.inventory, world, (EntityTravelingTrunk) entity) : null;
            }
            case GUI_THAUMATORIUM: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileThaumatorium ? new ContainerThaumatorium(player.inventory, (TileThaumatorium) tile) : null;
            }
            case GUI_FOCUS_POUCH: return new ContainerFocusPouch(player.inventory, world, x, y, z);
            case GUI_DECONSTRUCTION_TABLE: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileDeconstructionTable ? new ContainerDeconstructionTable(player.inventory, (TileDeconstructionTable) tile) : null;
            }
            case GUI_ALCHEMY_FURNACE: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileAlchemyFurnace ? new ContainerAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile) : null;
            }
            case GUI_RESEARCH_TABLE: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileResearchTable ? new ContainerResearchTable(player.inventory, (TileResearchTable) tile) : null;
            }
            case GUI_THAUMONOMICON: return null;
            case GUI_ARCANE_WORKBENCH: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileArcaneWorkbench ? new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile) : null;
            }
            case GUI_ARCANE_BORE: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileArcaneBore ? new ContainerArcaneBore(player.inventory, (TileArcaneBore) tile) : null;
            }
            case GUI_HAND_MIRROR: return new ContainerHandMirror(player.inventory, world, x, y, z);
            case GUI_HOVER_HARNESS: return new ContainerHoverHarness(player.inventory, world, x, y, z);
            case GUI_MAGIC_BOX: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof IInventory ? new ContainerMagicBox(player.inventory, tile) : null;
            }
            case GUI_SPA: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileSpa ? new ContainerSpa(player.inventory, (TileSpa) tile) : null;
            }
            case GUI_FOCAL_MANIPULATOR: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileFocalManipulator ? new ContainerFocalManipulator(player.inventory, (TileFocalManipulator) tile) : null;
            }
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

    public void wispFXEG(World world, double x, double y, double z, Entity target) {
    }

    public void taintLandFX(Entity entity) {
    }

    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
    }

    public int particleCount(int def) {
        return def;
    }

    public void crucibleFroth(World world, float x, float y, float z) {
    }

    public void crucibleFrothDown(World world, float x, float y, float z) {
    }

    public void crucibleBubble(World world, float x, float y, float z, float red, float green, float blue) {
    }

    public void crucibleBoilSound(World world, int x, int y, int z) {
    }

    public void crucibleBoil(World world, int x, int y, int z, TileCrucible crucible, int type) {
    }
}
