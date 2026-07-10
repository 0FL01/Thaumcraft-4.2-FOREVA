package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcaneBellowsParityStaticGuardTest {

    @Test
    public void bellowsKeepsOriginalCycleSoundAndRenderNormals() throws IOException {
        String bellows = read("src/main/java/thaumcraft/common/tiles/TileBellows.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java");

        assertTrue(bellows.contains("SoundEvents.ENTITY_GHAST_SHOOT")
                && bellows.contains("SoundCategory.BLOCKS, 0.01F")
                && bellows.contains("* 0.2F, false"));
        assertTrue(renderer.contains("GlStateManager.enableNormalize();")
                && renderer.contains("GlStateManager.disableNormalize();")
                && renderer.contains("finally {"));
    }

    @Test
    public void consumersKeepTheirReferenceBellowsRules() throws IOException {
        String bellows = read("src/main/java/thaumcraft/common/tiles/TileBellows.java");
        String arcaneFurnace = read("src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java");
        String crucible = read("src/main/java/thaumcraft/common/tiles/TileCrucible.java");
        String metalDevice = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");

        assertTrue(bellows.contains("bellowsTile.orientation == opposite && !bellowsTile.gettingPower()"));
        assertFalse(bellows.contains("bellowsTile.orientation == 0 ||"));
        assertTrue(arcaneFurnace.contains("this.world.isBlockPowered(bellowsPos)"));
        assertFalse(arcaneFurnace.contains("this.world.isAirBlock(bellowsPos)"));
        assertTrue(crucible.contains("for (EnumFacing dir : EnumFacing.HORIZONTALS)")
                && crucible.contains("state.getBlock() == ConfigBlocks.blockWoodenDevice")
                && crucible.contains("ConfigBlocks.blockWoodenDevice.getMetaFromState(state) == 0"));
        assertFalse(crucible.contains("TileBellows.getBellows(this.world, this.pos, EnumFacing.HORIZONTALS)"));
        assertTrue(metalDevice.contains("neighborTile instanceof TileCrucible")
                && metalDevice.contains("((TileCrucible) neighborTile).getBellows();"));
    }

    @Test
    public void placementNotifiesConsumersAfterBellowsOrientationIsStored() throws IOException {
        String item = read("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockWoodenDeviceItem.java");
        int orientation = item.indexOf("bellows.orientation = (byte) out.getIndex();");
        int notification = item.indexOf("world.notifyNeighborsOfStateChange(pos, newState.getBlock(), false);");

        assertTrue(orientation >= 0);
        assertTrue(notification > orientation);
        assertTrue(item.substring(orientation, notification).contains("bellows.markDirty();"));
        assertTrue(item.substring(orientation, notification).contains("if (!world.isRemote)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
