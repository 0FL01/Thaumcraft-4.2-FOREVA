package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneFurnaceNozzleStaticGuardTest {

    @Test
    public void tileArcaneFurnaceNozzleShouldKeepEssentiaDrawAndSuctionContracts() throws IOException {
        String nozzle = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneFurnaceNozzle.java");
        String furnace = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java");

        assertTrue(nozzle.contains("implements ITickable, IEssentiaTransport"));
        assertTrue(nozzle.contains("private EnumFacing facing = EnumFacing.UP;"));
        assertTrue(nozzle.contains("private TileArcaneFurnace furnace = null;"));
        assertTrue(nozzle.contains("this.findFurnace();"));
        assertTrue(nozzle.contains("this.furnace.speedyTime += 600;"));
        assertTrue(nozzle.contains("TileEntity te = ThaumcraftApiHelper.getConnectableTile("));
        assertTrue(nozzle.contains("transport.takeEssentia(Aspect.FIRE, 1, remote) == 1"));
        assertTrue(nozzle.contains("public Aspect getSuctionType(EnumFacing face)"));
        assertTrue(nozzle.contains("return Aspect.FIRE;"));
        assertTrue(nozzle.contains("this.furnace.speedyTime < 40"));
        assertTrue(nozzle.contains("return 128;"));
        assertTrue(nozzle.contains("public boolean canOutputTo(EnumFacing face)"));
        assertTrue(nozzle.contains("return false;"));

        assertTrue(furnace.contains("public int speedyTime = 0;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
