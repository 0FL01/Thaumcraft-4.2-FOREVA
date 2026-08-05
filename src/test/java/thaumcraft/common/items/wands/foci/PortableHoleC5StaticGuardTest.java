package thaumcraft.common.items.wands.foci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PortableHoleC5StaticGuardTest {

    @Test
    public void creationAndRestorationKeepNonNotifyingOrderedTransitions() throws Exception {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java");
        String tile = read("src/main/java/thaumcraft/common/tiles/TileHole.java");

        int air = focus.indexOf("world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0)");
        int hole = focus.indexOf("world.setBlockState(pos, ConfigBlocks.blockHole.getDefaultState(), 0)");
        int initialized = focus.indexOf("((TileHole) placed).setStoredBlock");
        int update = focus.indexOf("world.notifyBlockUpdate(pos, state, ConfigBlocks.blockHole.getDefaultState(), 2)");
        int sparkle = focus.indexOf("new PacketFXBlockSparkle(x, y, z, 0x400040)");
        assertTrue(air >= 0 && air < hole && hole < initialized && initialized < update && update < sparkle);
        assertEquals(1, count(focus, "world.notifyBlockUpdate("));
        assertTrue(focus.indexOf("restoreOriginal(world, pos, state, tileData)", hole) > hole);
        assertTrue(!focus.contains("setBlockToAir") && !focus.contains("notifyNeighbors"));

        int restore = tile.indexOf("world.setBlockState(pos, restoreState, 0)");
        int tileRestore = tile.indexOf("tile.readFromNBT(tileData)", restore);
        int restoreUpdate = tile.indexOf("world.notifyBlockUpdate(pos, oldState, restoreState, 2)", restore);
        int scheduled = tile.indexOf("world.scheduleUpdate(pos, restoreState.getBlock(), 2)", restore);
        assertTrue(restore >= 0 && restore < tileRestore && tileRestore < restoreUpdate && restoreUpdate < scheduled);
        assertEquals(1, count(tile, "world.notifyBlockUpdate("));
        assertTrue(!tile.contains("notifyNeighbors") && !tile.contains("setBlockToAir"));
        assertTrue(!focus.contains("getDefaultState(), 3)"));
        assertTrue(!tile.contains("restoreState, 3"));
    }

    @Test
    public void actorAuthorityAndOwnerPropagationRemainNarrowAndExact() throws Exception {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java");
        String tile = read("src/main/java/thaumcraft/common/tiles/TileHole.java");

        assertTrue(focus.contains("return createHole(world, x, y, z, side, count, max, null);"));
        assertTrue(focus.contains("player != null && !world.isBlockModifiable(player, pos)"));
        assertTrue(focus.contains("&& world.isBlockModifiable(player, cursor)"));
        assertTrue(focus.contains("player == null ? null : player.getUniqueID()"));
        assertTrue(focus.contains("scaleCost(this.getVisCost(focusStack), distance)"));
        assertTrue(focus.contains("base.getAmount(aspect) * multiplier"));

        assertTrue(tile.contains("this.world.getPlayerEntityByUUID(this.ownerUUID)"));
        assertTrue(tile.contains("if (owner == null || owner.isDead)"));
        assertTrue(tile.contains("this.count = 0;"));
        assertTrue(count(tile, "this.countdownmax, owner)") == 4);
        assertTrue(tile.contains("nbt.hasUniqueId(\"ownerUUID\")"));
        assertTrue(tile.contains("nbt.setUniqueId(\"ownerUUID\", this.ownerUUID)"));
    }

    @Test
    public void allSixOpeningOrientationsRemainFunctional() throws Exception {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileHole.java");
        for (int direction = 0; direction < 6; direction++) {
            assertTrue("missing direction " + direction, tile.contains("case " + direction + ":"));
        }
        assertTrue(tile.contains("this.pos.add(x, 0, z)"));
        assertTrue(tile.contains("this.pos.add(x, y, 0)"));
        assertTrue(tile.contains("this.pos.add(0, y, z)"));
        assertTrue(tile.contains("if (x == 0 && z == 0) continue;"));
        assertTrue(tile.contains("if (x == 0 && y == 0) continue;"));
        assertTrue(tile.contains("if (y == 0 && z == 0) continue;"));
        assertTrue(tile.contains("EnumFacing.byIndex(this.direction).getOpposite()"));
    }

    @Test
    public void protectedRendererBatchingFrameAndClickThroughContractsStayFrozen() throws Exception {
        assertEquals("d4c6aa42ab264416765029534a5b4e8aa8750b52fb2b924f6386bf8e1e9ecb8d",
                sha256("src/main/java/thaumcraft/client/renderers/tile/TileHoleRenderer.java"));
        assertEquals("edeb7cedcf53d687a858ff4515576a164e200d6406ce7a1cab397a441c8d4134",
                sha256("src/main/java/thaumcraft/client/renderers/tile/HoleRenderBatchCache.java"));
        assertEquals("5df6a7797e52b6e4ced8f0182eb0df7216ed37ac3d24d1b92e4eeb2cc1eafc32",
                sha256("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java"));

        String frame = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String block = read("src/main/java/thaumcraft/common/blocks/BlockHole.java");
        assertTrue(frame.contains("HoleRenderBatchCache.nextFrame();"));
        assertTrue(block.contains("private static final AxisAlignedBB ZERO_AABB"));
        assertTrue(block.contains("return ZERO_AABB;"));
        assertTrue(block.contains("public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)"));
        assertTrue(block.contains("return false;"));
    }

    @Test
    public void mountedDepthSpriteUsesTheExistingOriginalAssetOnly() throws Exception {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        assertTrue(focus.contains("getFocusDepthLayerIcon")
                && focus.contains("thaumcraft:items/focus_portablehole_depth")
                && focus.contains("getTextureMapBlocks().getAtlasSprite"));
        assertTrue(registry.contains("registerSprite(FOCUS_PORTABLE_HOLE_DEPTH_SPRITE)"));

        Path original = Paths.get("thaumcraft_src/assets/thaumcraft/textures/items/focus_portablehole_depth.png");
        Path runtime = Paths.get("src/main/resources/assets/thaumcraft/textures/items/focus_portablehole_depth.png");
        assertArrayEquals(Files.readAllBytes(original), Files.readAllBytes(runtime));
        BufferedImage image = ImageIO.read(runtime.toFile());
        assertNotNull(image);
        assertEquals(8, image.getWidth());
        assertEquals(128, image.getHeight());
    }

    private static int count(String source, String needle) {
        int count = 0;
        int at = 0;
        while ((at = source.indexOf(needle, at)) >= 0) {
            count++;
            at += needle.length();
        }
        return count;
    }

    private static String sha256(String path) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(Paths.get(path)));
        StringBuilder out = new StringBuilder();
        for (byte value : digest) {
            out.append(String.format("%02x", value & 0xff));
        }
        return out.toString();
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
