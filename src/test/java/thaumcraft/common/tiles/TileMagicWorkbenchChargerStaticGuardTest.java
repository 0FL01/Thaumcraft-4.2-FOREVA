package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileMagicWorkbenchChargerStaticGuardTest {

    @Test
    public void chargerShouldExtendVisRelayAndKeepRenderAabbContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMagicWorkbenchCharger.java");

        assertTrue(source.contains("public class TileMagicWorkbenchCharger extends TileVisRelay"));
        assertTrue(source.contains("public short orientation = 0;"));
        assertTrue(source.contains("public AxisAlignedBB getRenderBoundingBox()"));
        assertTrue(source.contains("this.pos.getY() - 1"));
        assertTrue(source.contains("this.pos.getY() + 1"));
    }

    @Test
    public void chargerShouldKeepServerVisChargeFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMagicWorkbenchCharger.java");

        assertTrue(source.contains("super.update();"));
        assertTrue(source.contains("if (this.world == null || this.world.isRemote)"));
        assertTrue(source.contains("this.world.getTileEntity(this.pos.down())"));
        assertTrue(source.contains("below instanceof TileMagicWorkbench"));
        assertTrue(source.contains("ItemStack wand = workbench.getStackInSlot(10);"));
        assertTrue(source.contains("wand.getItem() instanceof ItemWandCasting"));
        assertTrue(source.contains("((ItemWandCasting) wand.getItem()).isStaff(wand)"));
        assertTrue(source.contains("AspectList room = wandItem.getAspectsWithRoom(wand);"));
        assertTrue(source.contains("int drain = Math.min(5, ItemWandCasting.getMaxVis(wand) - ItemWandCasting.getVis(wand, aspect));"));
        assertTrue(source.contains("int consumed = this.consumeVis(aspect, drain);"));
        assertTrue(source.contains("ItemWandCasting.addRealVis(wand, aspect, consumed, true);"));
        assertTrue(source.contains("workbench.onWandVisChanged();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
