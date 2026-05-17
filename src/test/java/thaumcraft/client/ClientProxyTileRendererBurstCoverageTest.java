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
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeEnergized.class, new TileNodeEnergizedRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeStabilizer.class, new TileNodeStabilizerRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileNodeConverter.class, new TileNodeConverterRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileVisRelay.class, new TileVisRelayRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileAlchemyFurnace.class, new TileAlchemyFurnaceAdvancedRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileBellows.class, new TileBellowsRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileTable.class, new TileTableRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileCrucible.class, new TileCrucibleRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileAlembic.class, new TileAlembicRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new TilePedestalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileWandPedestal.class, new TileWandPedestalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionMatrix.class, new TileRunicMatrixRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileThaumatorium.class, new TileThaumatoriumRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneBore.class, new TileArcaneBoreRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(thaumcraft.common.tiles.TileArcaneBoreBase.class, new TileArcaneBoreBaseRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileFocalManipulator.class, new TileFocalManipulatorRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.class, new TileBannerRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileHole.class, new TileHoleRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileWarded.class, new TileWardedRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneLamp.class, lampRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneLampGrowth.class, lampRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneWorkbench.class, new TileArcaneWorkbenchRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileDeconstructionTable.class, new TileDeconstructionTableRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileResearchTable.class, new TileResearchTableRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileMirror.class, mirrorRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileMirrorEssentia.class, mirrorRenderer);"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileTubeBuffer.class, new TileTubeBufferRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileTubeValve.class, new TileTubeValveRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileTubeOneway.class, new TileTubeOnewayRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileCentrifuge.class, new TileCentrifugeRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileChestHungry.class, new TileChestHungryRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileCrystal.class, new TileCrystalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchCrystal.class, new TileEldritchCrystalRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchLock.class, new TileEldritchLockRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileEldritchNothing.class, new TileEldritchNothingRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaReservoir.class, new TileEssentiaReservoirRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileEtherealBloom.class, new TileEtherealBloomRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileFluxScrubber.class, new TileFluxScrubberRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileManaPod.class, new TileManaPodRenderer());"));
        assertTrue(source.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileMagicWorkbenchCharger.class, new TileMagicWorkbenchChargerRenderer());"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
