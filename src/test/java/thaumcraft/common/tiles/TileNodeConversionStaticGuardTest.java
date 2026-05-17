package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
