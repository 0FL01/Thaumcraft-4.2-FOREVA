package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneLampStaticGuardTest {

    @Test
    public void tileArcaneLampShouldKeepAiryLightPlacementContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneLamp.java");

        assertTrue(source.contains("public EnumFacing facing = EnumFacing.byIndex(0);"));
        assertTrue(source.contains("if (this.world == null || this.world.isRemote)"));
        assertTrue(source.contains("this.world.rand.nextInt(16) - this.world.rand.nextInt(16)"));
        assertTrue(source.contains("this.world.getHeight(new BlockPos(x, 0, z)).getY() + 4"));
        assertTrue(source.contains("if (y < 5)"));
        assertTrue(source.contains("this.world.isAirBlock(target)"));
        assertTrue(source.contains("this.world.getLight(target) < 9"));
        assertTrue(source.contains("ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 3)"));
    }

    @Test
    public void tileArcaneLampShouldKeepOrientationAndRemoveLightsContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneLamp.java");

        assertTrue(source.contains("this.facing = EnumFacing.byIndex(nbt.getInteger(\"orientation\"));"));
        assertTrue(source.contains("nbt.setInteger(\"orientation\", this.facing.getIndex());"));
        assertTrue(source.contains("public void removeLights()"));
        assertTrue(source.contains("for (int x = -15; x <= 15; x++)"));
        assertTrue(source.contains("for (int y = -15; y <= 15; y++)"));
        assertTrue(source.contains("for (int z = -15; z <= 15; z++)"));
        assertTrue(source.contains("this.world.getBlockState(check).getBlock() == ConfigBlocks.blockAiry"));
        assertTrue(source.contains("this.world.getBlockState(check).getValue(BlockAiry.TYPE) == 3"));
    }

    @Test
    public void blockMetalDeviceShouldKeepArcaneLampNeighborAndBreakHooks() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");

        assertTrue(source.contains("else if (te instanceof TileArcaneLamp)"));
        assertTrue(source.contains("((TileArcaneLamp) te).removeLights();"));
        assertTrue(source.contains("if (meta == 7)"));
        assertTrue(source.contains("TileArcaneLamp lamp = (TileArcaneLamp) te;"));
        assertTrue(source.contains("worldIn.isAirBlock(pos.offset(lamp.facing))"));
        assertTrue(source.contains("worldIn.destroyBlock(pos, true);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
