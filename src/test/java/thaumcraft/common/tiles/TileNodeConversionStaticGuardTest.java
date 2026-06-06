package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileNodeConversionStaticGuardTest {

    @Test
    public void tileNodeConverterShouldKeepNodeToEnergizedAndBackContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileNodeConverter.java");
        String energized = readFile("src/main/java/thaumcraft/common/tiles/TileNodeEnergized.java");

        assertTrue(source.contains("public int count = -1;"));
        assertTrue(source.contains("public int status = 0;"));
        assertTrue(source.contains("this.world.setBlockState(this.pos.down(),"));
        assertTrue(source.contains("withProperty(BlockAiry.TYPE, 5)"));
        assertTrue(source.contains("((TileNodeEnergized) tileNew).setNodeModifier(mod);"));
        assertTrue(source.contains("((TileNodeEnergized) tileNew).setupNode();"));
        assertTrue(source.contains("withProperty(BlockAiry.TYPE, 0)"));
        assertTrue(source.contains("node.takeFromContainer(a, node.getAspects().getAmount(a));"));
        assertTrue(source.contains("BlockAiry.explodify(this.getWorld(), this.pos.getX(), this.pos.getY() - 1, this.pos.getZ());"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos, this.getBlockType(), 10, 10);"));
        assertTrue(source.contains("Thaumcraft.proxy.burst(this.world, this.pos.getX() + 0.5D, this.pos.getY() - 0.5D, this.pos.getZ() + 0.5D, 1.0F);"));
        assertTrue(source.contains("TCSounds.CRAFTFAIL"));

        assertTrue(energized.contains("public class TileNodeEnergized extends TileVisNode implements IAspectContainer"));
        assertTrue(energized.contains("public void setupNode()"));
        assertTrue(energized.contains("ResearchManager.reduceToPrimals(this.getAuraBase(), true)"));
        assertTrue(energized.contains("amt = MathHelper.floor(MathHelper.sqrt(amt));"));
        assertTrue(energized.contains("nbt.setTag(\"AEB\", tlist);"));
    }

    @Test
    public void tileNodeConverterShouldKeepPowerStateAndStabilizerContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileNodeConverter.java");

        assertTrue(source.contains("this.world.isBlockPowered(this.pos)"));
        assertTrue(source.contains("private boolean hasStabilizer()"));
        assertTrue(source.contains("te instanceof TileNodeStabilizer"));
        assertTrue(source.contains("Thaumcraft.proxy.bolt(this.world,"));
        assertTrue(source.contains("nbt.setInteger(\"status\", this.status);"));
        assertTrue(source.contains("nbt.setInteger(\"count\", this.count);"));
    }

    @Test
    public void tileNodeStabilizerAndBlockAiryShouldKeepConversionSupportContracts() throws IOException {
        String stabilizer = readFile("src/main/java/thaumcraft/common/tiles/TileNodeStabilizer.java");
        String airy = readFile("src/main/java/thaumcraft/common/blocks/BlockAiry.java");
        String stoneDevice = readFile("src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java");

        assertTrue(stabilizer.contains("public int count = 0;"));
        assertTrue(stabilizer.contains("public int lock = 0;"));
        assertTrue(stabilizer.contains("this.world.getBlockState(above).getBlock() == ConfigBlocks.blockAiry"));
        assertTrue(stabilizer.contains("this.world.getBlockState(above).getValue(BlockAiry.TYPE) == 5"));
        assertTrue(stabilizer.contains("public AxisAlignedBB getRenderBoundingBox()"));

        assertTrue(airy.contains("public static void explodify(World world, int x, int y, int z)"));
        assertTrue(airy.contains("world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 3.0F, false);"));
        assertTrue(airy.contains("ConfigBlocks.blockFluxGoo.getStateFromMeta(8)"));
        assertTrue(airy.contains("ConfigBlocks.blockFluxGas.getStateFromMeta(8)"));

        assertTrue(stoneDevice.contains("else if (te instanceof TileNodeConverter)"));
        assertTrue(stoneDevice.contains("((TileNodeConverter) te).checkStatus();"));
    }

    @Test
    public void tileNodeShouldKeepDischargeLockAndRechargeDelayContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileNode.java");

        assertTrue(source.contains("private int wait = 0;"));
        assertTrue(source.contains("private byte nodeLock = 0;"));
        assertTrue(source.contains("public byte getLock()"));
        assertTrue(source.contains("checkLock();"));
        assertTrue(source.contains("changed |= handleDischarge();"));
        assertTrue(source.contains("this.regeneration > 0 && this.wait == 0 && this.count % this.regeneration == 0"));
        assertTrue(source.contains("private void checkLock()"));
        assertTrue(source.contains("this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockStoneDevice"));
        assertTrue(source.contains("if (meta == 9)"));
        assertTrue(source.contains("} else if (meta == 10)"));
        assertTrue(source.contains("private boolean handleDischarge()"));
        assertTrue(source.contains("this.world.getBlockState(this.pos).getBlock() != ConfigBlocks.blockAiry || this.getLock() == 1"));
        assertTrue(source.contains("targetNode.wait = targetNode.regeneration / 2;"));
        assertTrue(source.contains("new PacketFXBlockZap("));
        assertTrue(source.contains("new NetworkRegistry.TargetPoint("));
    }

    @Test
    public void tileNodeShouldKeepTc42DirectWandDrainContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileNode.java");
        String renderer = readFile("src/main/java/thaumcraft/client/renderers/tile/TileNodeRenderer.java");
        String utils = readFile("src/main/java/thaumcraft/client/lib/UtilsFX.java");

        assertTrue(source.contains("implements ITickable, INode, IAspectContainer, IWandable"));
        assertTrue(source.contains("public Entity drainEntity = null;"));
        assertTrue(source.contains("public RayTraceResult drainCollision = null;"));
        assertTrue(source.contains("public int drainColor = 0xFFFFFF;"));
        assertTrue(source.contains("public Color targetColor = new Color(0xFFFFFF);"));
        assertTrue(source.contains("public Color color = new Color(0xFFFFFF);"));
        assertTrue(source.contains("public int drainBeamAge = 0;"));
        assertFalse(source.contains("drainBeamGrace"));
        assertFalse(source.contains("DRAIN_BEAM_INACTIVE_GRACE_TICKS"));
        assertTrue(source.contains("nbttagcompound.setString(\"drainer\", this.drainEntity.getName());"));
        assertTrue(source.contains("nbttagcompound.setInteger(\"draincolor\", this.drainColor);"));

        assertTrue(source.contains("public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md)"));
        assertTrue(source.contains("return -1;"));
        assertTrue(source.contains("public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player)"));
        assertTrue(source.contains("setActiveWandHand(player, wandstack);"));
        assertTrue(source.contains("setObjectInUse(wandstack, this.pos.getX(), this.pos.getY(), this.pos.getZ());"));
        assertTrue(source.contains("player.stopActiveHand();"));
        assertTrue(source.contains("if (count % 5 == 0)"));
        assertTrue(source.contains("ResearchManager.isResearchComplete(player, \"NODETAPPER1\")"));
        assertTrue(source.contains("ResearchManager.isResearchComplete(player, \"NODETAPPER2\")"));
        assertTrue(source.contains("ResearchManager.isResearchComplete(player, \"NODEPRESERVE\")"));
        assertTrue(source.contains("!player.isSneaking()"));
        assertTrue(source.contains("!\"wood\".equals(rod.getTag())"));
        assertTrue(source.contains("!\"iron\".equals(cap.getTag())"));
        assertTrue(source.contains("private Aspect chooseRandomFilteredFromSource(AspectList room, boolean preserve)"));
        assertTrue(source.contains("int min = preserve ? 1 : 0;"));
        assertTrue(source.contains("room.getAmount(aspect) > 0 && this.aspects.getAmount(aspect) > min"));
        assertTrue(source.contains("if (preserve && tap == currentAmount)"));
        assertTrue(source.contains("ItemWandCasting.addVis(wandstack, aspect, tap, !this.world.isRemote)"));
        assertTrue(source.contains("this.takeFromContainer(aspect, tap - remainder)"));
        assertTrue(source.contains("syncDrainChange();"));
        assertTrue(source.contains("clearDrainVisualAndSync();"));
        assertTrue(source.contains("clearDrainVisualOnServer();"));
        assertTrue(source.contains("if (world != null && !world.isRemote)"));
        assertTrue(source.contains("private void updateDrainBeamVisual()"));
        assertTrue(source.contains("++this.drainBeamAge;"));
        assertFalse(source.contains("isHandActive()"));
        assertTrue(source.contains("private void syncDrainChange()"));
        assertTrue(source.contains("this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);"));
        assertTrue(source.contains("this.color = new Color(red, green, blue);"));

        assertTrue(renderer.contains("renderDrainBeam((TileNode) tile, partialTicks);"));
        assertFalse(renderer.contains("getItemInUseMaxCount()"));
        assertTrue(renderer.contains("float beamAge = node.drainBeamAge + partialTicks;"));
        assertTrue(renderer.contains("MathHelper.sin(beamAge / 10.0F) * 10.0F"));
        assertTrue(renderer.contains("new Vec3d(-0.1D, -0.1D, 0.5D)"));
        assertTrue(renderer.contains("offset = offset.rotateYaw(-wobble * 0.01F);"));
        assertTrue(renderer.contains("offset = offset.rotatePitch(-wobble * 0.015F);"));
        assertTrue(renderer.contains("node.color == null ? node.drainColor : node.color.getRGB()"));
        assertTrue(renderer.contains("UtilsFX.drawFloatyLine(sourceWorldX, sourceWorldY, sourceWorldZ,"));
        assertTrue(renderer.contains("targetWorldX, targetWorldY, targetWorldZ,"));
        assertTrue(renderer.contains("partialTicks, color, \"textures/misc/wispy.png\", -0.02F, Math.min(beamAge, 10.0F) / 10.0F)"));

        assertTrue(utils.contains("public static void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2,"));
        assertTrue(utils.contains("drawFloatyLine(x, y, z, x2, y2, z2, partialTicks, color, texture, speed, distance, 0.15F);"));
        assertTrue(utils.contains("long timeLong = (System.nanoTime() / 30000000L) % 32767L;"));
        assertTrue(utils.contains("float time = (float) timeLong;"));
        assertTrue(utils.contains("GlStateManager.depthMask(false);"));
        assertTrue(utils.contains("GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);"));
        assertTrue(utils.contains("GlStateManager.disableCull();"));
        assertTrue(utils.contains("buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);"));
        assertTrue(utils.contains("drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, true);"));
        assertTrue(utils.contains("drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, false);"));
        assertTrue(utils.contains(".color(red, green, blue, centerWeight).endVertex();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
