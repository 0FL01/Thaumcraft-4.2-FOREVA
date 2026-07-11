package thaumcraft.common;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.*;
import thaumcraft.common.entities.ContainerPech;
import thaumcraft.common.entities.golems.ContainerGolem;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.golems.ContainerTravelingTrunk;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.items.wands.WandManager;
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
    public final WandManager wandManager = new WandManager();

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

    public void registerEntityRenders() {
    }

    public void registerModelLocations() {
    }

    public void registerKeyBindings() {
    }

    public boolean isShiftKeyDown() {
        return false;
    }

    public void registerHandlers() {
    }

    public void scheduleClientTask(Runnable task) {
    }

    public void notifyThaumometerUnknownObject() {
    }

    public void notifyThaumometerDiscoveryError(@Nullable Aspect missingAspect) {
    }

    public void notifyThaumometerAspectDiscovery(@Nullable Aspect aspect) {
    }

    public void notifyThaumometerAspectPool(@Nullable Aspect aspect, int amount) {
    }

    @Nullable
    public EntityPlayer getClientPlayer() {
        return null;
    }

    @Nullable
    public World getClientWorld() {
        return null;
    }

    // ---- FX stubs (ClientProxy overrides with actual GL calls) ----

    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
    }

    public void blockWard(World world, double x, double y, double z, EnumFacing side, float red, float green, float blue) {
    }

    public void refreshWardedBlockRender(World world, BlockPos pos) {
    }

    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
    }

    public void beamPulseFX(World world, Entity source, Entity target, int color) {
    }

    public void beamPulseGolemBossFX(World world, EntityLivingBase source, Entity target) {
    }

    public void excavateFX(World world, BlockPos pos, EntityPlayer player, int progress) {
    }

    public Object beamCont(World world,
                           EntityPlayer player,
                           double tx, double ty, double tz,
                           int type, int color,
                           boolean reverse, float endmod,
                           Object input, int impact) {
        return null;
    }

    public Object beamBore(World world,
                           double px, double py, double pz,
                           double tx, double ty, double tz,
                           int type, int color,
                           boolean reverse, float endmod,
                           Object input, int impact) {
        return null;
    }

    public Object beamPower(World world,
                            double px, double py, double pz,
                            double tx, double ty, double tz,
                            float red, float green, float blue,
                            boolean pulse, Object input) {
        return null;
    }

    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
    }

    public void bolt(World world, Entity sourceEntity, Entity targetedEntity) {
    }

    public void nodeBolt(World world, float x, float y, float z, Entity target) {
    }

    public void nodeBolt(World world, float x, float y, float z, float tx, float ty, float tz) {
    }

    public void sourceStreamFX(World world, double sx, double sy, double sz, float tx, float ty, float tz, int color) {
    }

    public void essentiaTrailFx(World world, int x, int y, int z, int tx, int ty, int tz, int count, int color, float scale) {
    }

    public void visDrainFx(World world, BlockPos from, BlockPos to, int color) {
    }

    public void blockRunes(World world, double x, double y, double z, float red, float green, float blue, int duration, float gravity) {
    }

    public void arcLightning(World world,
                             double x, double y, double z,
                             double tx, double ty, double tz,
                             float red, float green, float blue,
                             float height) {
    }

    public void drawInfusionParticles1(World world,
                                       double x, double y, double z,
                                       int tx, int ty, int tz,
                                       Item item, int meta) {
    }

    public void drawInfusionParticles2(World world,
                                       double x, double y, double z,
                                       int tx, int ty, int tz,
                                       IBlockState state) {
    }

    public void drawInfusionParticles3(World world, double x, double y, double z, int tx, int ty, int tz) {
    }

    public void drawInfusionParticles4(World world, double x, double y, double z, int tx, int ty, int tz) {
    }

    public void burst(World world, double x, double y, double z, float scale) {
    }

    public void sonicFX(World world, Entity source, int age) {
    }

    public void shieldRunesFX(World world, Entity source, int age, float yaw, float pitch) {
    }

    public void zapFX(World world, Entity source, Entity target) {
    }

    public void focusShockBolt(World world, EntityLivingBase source, double tx, double ty, double tz) {
    }

    public void wispFX(World world, double x, double y, double z, float size, float red, float green, float blue) {
    }

    public void wispFX3(World world, double x, double y, double z, double tx, double ty, double tz, float size, int count, boolean flag, float speed) {
    }

    public void wispFX2(World world, double x, double y, double z, float size, int type, boolean shrink, boolean clip, float gravity) {
    }

    public void wispFXEG(World world, double x, double y, double z, Entity target) {
    }

    public void taintLandFX(Entity entity) {
    }

    public void slimeJumpFX(Entity entity, int size) {
    }

    public Object swarmParticleFX(World world, Entity targetedEntity, float speed, float turnSpeed, float particleGravity) {
        return null;
    }

    public boolean isParticleAlive(Object particle) {
        return false;
    }

    public void splooshFX(Entity entity) {
    }

    public void taintsplosionFX(Entity entity) {
    }

    public void tentacleAriseFX(Entity entity) {
    }

    public void golemFishingSplashFX(Entity entity, int kind) {
    }

    public void spark(float x, float y, float z, float size, float red, float green, float blue, float alpha) {
    }

    public void bottleTaintBreak(World world, double x, double y, double z) {
    }

    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale) {
    }

    public void drawGenericParticles(World world, double x, double y, double z,
                                     double mx, double my, double mz,
                                     float red, float green, float blue, float alpha,
                                     boolean loop, int start, int num, int inc, int age, int delay, float scale,
                                     int count) {
    }

    public void boreDigFx(World world,
                          double x, double y, double z,
                          double tx, double ty, double tz,
                          IBlockState state,
                          @Nullable Item item,
                          int meta) {
    }

    public void drawVentParticles(World world, double x, double y, double z,
                                  double mx, double my, double mz, int color) {
    }

    public void drawVentParticles(World world, double x, double y, double z,
                                  double mx, double my, double mz, int color, float scale) {
    }

    public void sparkle(float x, float y, float z, float scale, int type, float speed) {
    }

    public void sparkle(float x, float y, float z, int color) {
    }

    public void smokeSpiral(World world, double x, double y, double z, float radius, int start, int miny, int color) {
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

    public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius) {
    }
}
