package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyTileRendererBurstCoverageTest {

    @Test
    public void setupTileRenderersBindsCoreStage8cTesrBurstSet() throws IOException {
        String source = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(source.contains("private void setupTileRenderers()"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileJarFillable.class, jarRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileJarFillableVoid.class, jarRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileJarBrain.class, jarRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileJarNode.class, jarRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNode.class, nodeRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeEnergized.class, nodeRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeStabilizer.class, new TileNodeStabilizerRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeConverter.class, new TileNodeConverterRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileVisRelay.class, new TileVisRelayRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileCrucible.class, new TileCrucibleRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileAlembic.class, new TileAlembicRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new TilePedestalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileWandPedestal.class, new TilePedestalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionMatrix.class, new TileRunicMatrixRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileThaumatorium.class, new TileThaumatoriumRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneBore.class, new TileArcaneBoreRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileArcaneBoreBase.class, new TileArcaneBoreBaseRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileFocalManipulator.class, new TileFocalManipulatorRenderer());"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
