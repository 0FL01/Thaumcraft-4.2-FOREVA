package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WardedHoleRendererFidelityStaticGuardTest {

    @Test
    public void wardedAndHoleRenderersKeepConnectedAndLayeredContracts() throws IOException {
        String warded = read("src/main/java/thaumcraft/client/renderers/tile/TileWardedRenderer.java");
        String hole = read("src/main/java/thaumcraft/client/renderers/tile/TileHoleRenderer.java");
        String holeBatch = read("src/main/java/thaumcraft/client/renderers/tile/HoleRenderBatchCache.java");
        String nothing = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchNothingRenderer.java");
        String obelisk = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java");
        String connectedTextures = read("src/main/java/thaumcraft/common/lib/utils/ConnectedTextureUtils.java");

        assertTrue("TileWardedRenderer should keep warded connected-texture matrix routing",
                connectedTextures.contains("CONNECTED_TEXTURE_REF_BY_ID")
                        && warded.contains("ConnectedTextureUtils.getTextureIndex")
                        && warded.contains("ICON_CACHE")
                        && warded.contains("(worldTime + side) % 10L != 0L")
                        && warded.contains("warded_glass_")
                        && warded.contains("face.getOpposite().getIndex()")
                        && warded.contains("isConnectedBlock(")
                        && warded.contains("ConfigBlocks.blockWarded"));

        assertTrue("TileWardedRenderer should keep warding-focus visibility gating",
                warded.contains("focus instanceof FocusWarding")
                        && warded.contains("isWardingWandHeld()"));

        assertTrue("TileWardedRenderer should render the stored facade through the baked block-model dispatcher before drawing the ward overlay",
                warded.contains("renderStoredFacade(tile, x, y, z);")
                        && warded.contains("tile.getStoredState()")
                        && warded.contains("storedState.getRenderType() != EnumBlockRenderType.MODEL")
                        && warded.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && warded.contains("dispatcher.getBlockModelRenderer().renderModel(")
                        && warded.contains("MathHelper.getPositionRandom(tile.getPos())"));

        assertTrue("TileWardedRenderer should preserve render layers and invalidate its dimension-aware connected-texture cache",
                warded.contains("for (BlockRenderLayer layer : BlockRenderLayer.values())")
                        && warded.contains("block.canRenderInLayer(actualState, layer)")
                        && warded.contains("ForgeHooksClient.setRenderLayer(layer)")
                        && warded.contains("ForgeHooksClient.setRenderLayer(null)")
                        && warded.contains("new StoredBlockAccess(tile.getWorld(), tile.getPos(), storedState)")
                        && warded.contains("private final int dimension;")
                        && warded.contains("public static void invalidate(World world, BlockPos pos)")
                        && warded.contains("public static void onWorldUnload(WorldEvent.Unload event)")
                        && warded.contains("ICON_CACHE.clear()"));

        assertTrue("TileWardedRenderer should only cull shared faces for the same stored block and metadata",
                warded.contains("hasSameStoredBlock(tile, (TileWarded) neighbor)")
                        && warded.contains("left.block == right.block")
                        && warded.contains("(left.blockMd & 255) == (right.blockMd & 255)"));

        assertTrue("TileHoleRenderer should batch connected holes and keep layered tunnel routing through the shared helper",
                hole.contains("HoleRenderBatchCache.getGroup(tile)")
                        && hole.contains("group.markRenderedThisFrame()")
                        && hole.contains("LayeredFieldPlaneHelper.renderLayeredFaceRect("));

        assertTrue("HoleRenderBatchCache should discover connected hole groups and merge unit faces into rectangles",
                holeBatch.contains("ArrayDeque<BlockPos>")
                        && holeBatch.contains("ConfigBlocks.blockHole")
                        && holeBatch.contains("GROUP_BY_MEMBER")
                        && holeBatch.contains("mergeCells(")
                        && holeBatch.contains("shouldRenderFace"));

        assertTrue("TileEldritchNothingRenderer should keep layered tunnel field routing through the shared helper",
                (nothing.contains("LayeredFieldPlaneHelper.addInRangeBatchFace(")
                        || nothing.contains("LayeredFieldPlaneHelper.renderLayeredFace("))
                        && nothing.contains("shouldRenderFace"));

        assertTrue("LayeredFieldPlaneHelper should keep shared tunnel-field texgen and camera-parallax contracts",
                helper.contains("textures/misc/tunnel.png")
                        && helper.contains("textures/misc/particlefield.png")
                        && helper.contains("textures/misc/particlefield32.png")
                        && helper.contains("for (int i = 0; i < 16; i++)")
                        && helper.contains("FIELD_COLOR_SEED = 31100L")
                        && helper.contains("ActiveRenderInfo.getRotationX()")
                        && helper.contains("GL11.glTexGen(")
                        && helper.contains("GlStateManager.matrixMode(5890)"));

        assertTrue("TileEldritchObeliskRenderer should keep layered side fields with camera-parallax contracts",
                obelisk.contains("LayeredFieldPlaneHelper.renderLayeredFaceRect(")
                        && obelisk.contains("-0.5F,")
                        && obelisk.contains("0.5F,")
                        && obelisk.contains("0.0F,")
                        && obelisk.contains("3.0F")
                        && obelisk.contains("for (EnumFacing facing : EnumFacing.HORIZONTALS)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
