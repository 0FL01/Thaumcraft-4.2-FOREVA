package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileAlembicStaticGuardTest {

    @Test
    public void tileAlembicShouldKeepNonTickingAppearanceAndRenderBoundsContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileAlembic.java");

        assertTrue(source.contains("public class TileAlembic extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, IWandable"));
        assertTrue(!source.contains("implements ITickable"));
        assertTrue(source.contains("public void getAppearance()"));
        assertTrue(source.contains("this.aboveAlembic = false;"));
        assertTrue(source.contains("this.aboveFurnace = false;"));
        assertTrue(source.contains("public AxisAlignedBB getRenderBoundingBox()"));
        assertTrue(source.contains("this.pos.getX() - 1"));
        assertTrue(source.contains("this.pos.getX() + 2"));
        assertTrue(source.contains("public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)"));
        assertTrue(source.contains("this.getAppearance();"));
        assertTrue(source.contains("public void handleUpdateTag(NBTTagCompound tag)"));
        assertTrue(source.contains("public void onLoad()"));
    }

    @Test
    public void tileAlembicShouldKeepEssentiaOutputContractAndFacingRules() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileAlembic.java");

        assertTrue(source.contains("public boolean doesContainerAccept(Aspect tag)"));
        assertTrue(source.contains("return true;"));
        assertTrue(source.contains("if (!this.doesContainerAccept(tag)) return requested;"));
        assertTrue(source.contains("int add = Math.min(requested, this.maxAmount - this.amount);"));
        assertTrue(source.contains("return face != EnumFacing.byIndex(this.facing) && face != EnumFacing.DOWN;"));
        assertTrue(source.contains("public boolean canInputFrom(EnumFacing face)"));
        assertTrue(source.contains("return false;"));
        assertTrue(source.contains("public int takeEssentia(Aspect aspect, int amount, EnumFacing face)"));
        assertTrue(source.contains("this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0"));
        assertTrue(source.contains("return true;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
