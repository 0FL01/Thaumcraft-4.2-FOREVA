package thaumcraft.common;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
            case 0: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileArcaneWorkbench ? new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile) : null;
            }
            case 1: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileResearchTable ? new ContainerResearchTable(player.inventory, (TileResearchTable) tile) : null;
            }
            case 2: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileArcaneBore ? new ContainerArcaneBore(player.inventory, (TileArcaneBore) tile) : null;
            }
            case 3: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileAlchemyFurnace ? new ContainerAlchemyFurnace(player.inventory, (TileAlchemyFurnace) tile) : null;
            }
            case 4: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileDeconstructionTable ? new ContainerDeconstructionTable(player.inventory, (TileDeconstructionTable) tile) : null;
            }
            case 5: return new ContainerFocusPouch(player.inventory, world, x, y, z);
            case 6: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityGolemBase ? new ContainerGolem(player.inventory, (EntityGolemBase) entity) : null;
            }
            case 7: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityPech ? new ContainerPech(player.inventory, world, (EntityPech) entity) : null;
            }
            case 8: {
                Entity entity = world.getEntityByID(x);
                return entity instanceof EntityTravelingTrunk ? new ContainerTravelingTrunk(player.inventory, world, (EntityTravelingTrunk) entity) : null;
            }
            case 9: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileThaumatorium ? new ContainerThaumatorium(player.inventory, (TileThaumatorium) tile) : null;
            }
            case 10: return new ContainerHandMirror(player.inventory, world, x, y, z);
            case 11: return new ContainerHoverHarness(player.inventory, world, x, y, z);
            case 12: {
                TileEntity tile = world.getTileEntity(pos);
                return tile != null ? new ContainerMagicBox(player.inventory, tile) : null;
            }
            case 13: {
                TileEntity tile = world.getTileEntity(pos);
                return tile instanceof TileSpa ? new ContainerSpa(player.inventory, (TileSpa) tile) : null;
            }
            case 14: {
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

    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
    }

    public int particleCount(int def) {
        return def;
    }
}
