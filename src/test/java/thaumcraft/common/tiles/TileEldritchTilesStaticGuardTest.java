package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileEldritchTilesStaticGuardTest {

    @Test
    public void eldritchCapAndNothingShouldKeepRenderDistanceAndBoundsContracts() throws IOException {
        String cap = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchCap.java");
        String nothing = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchNothing.java");

        assertTrue(cap.contains("public class TileEldritchCap extends TileEntity"));
        assertTrue(cap.contains("public double getMaxRenderDistanceSquared()"));
        assertTrue(cap.contains("return 9216.0;"));

        assertTrue(nothing.contains("public class TileEldritchNothing extends TileEntity"));
        assertTrue(nothing.contains("public double getMaxRenderDistanceSquared()"));
        assertTrue(nothing.contains("public AxisAlignedBB getRenderBoundingBox()"));
        assertTrue(nothing.contains("this.pos.getY() + 1"));
    }

    @Test
    public void eldritchObeliskShouldKeepAuraBuffAndClientFxContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchObelisk.java");

        assertTrue(source.contains("public class TileEldritchObelisk extends TileThaumcraft implements ITickable"));
        assertTrue(source.contains("private int counter = 0;"));
        assertTrue(source.contains("EntityUtils.getEntitiesInRange("));
        assertTrue(source.contains("this.counter % 20 == 0"));
        assertTrue(source.contains("e instanceof IEldritchMob"));
        assertTrue(source.contains("MobEffects.REGENERATION"));
        assertTrue(source.contains("MobEffects.RESISTANCE"));
        assertTrue(source.contains("Thaumcraft.proxy.wispFXEG("));
        assertTrue(source.contains("this.pos.getY() + 5"));
        assertTrue(source.contains("return 20736.0;"));
    }

    @Test
    public void eldritchLockShouldKeepAiryCleanupSparkleBroadcastContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchLock.java");

        assertTrue(source.contains("private void clearNearbyAiryBlocks()"));
        assertTrue(source.contains("if (this.world.getBlockState(target).getBlock() == ConfigBlocks.blockAiry)"));
        assertTrue(source.contains("new PacketFXBlockSparkle(target.getX(), target.getY(), target.getZ(), 0x400040)"));
        assertTrue(source.contains("new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), target.getX(), target.getY(), target.getZ(), 32.0)"));
    }

    @Test
    public void eldritchPortalShouldKeepAmbientBoundsAndTransferContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchPortal.java");

        assertTrue(source.contains("this.count % 250 == 0"));
        assertTrue(source.contains("TCSounds.EVILPORTAL"));
        assertTrue(source.contains("this.opencount < 30"));
        assertTrue(source.contains("public AxisAlignedBB getRenderBoundingBox()"));
        assertTrue(source.contains("this.pos.add(-1, -1, -1)"));
        assertTrue(source.contains("this.pos.add(2, 2, 2)"));
        assertTrue(source.contains("transferPlayerToDimension(player, targetDim, new TeleporterThaumcraft(targetWorld))"));
        assertTrue(source.contains("ResearchManager.addResearch(player, \"ENTEROUTER\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
