package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileInfusionMatrixInstabilityStaticGuardTest {

    @Test
    public void tileInfusionMatrixShouldKeepReferenceShapedInstabilityAndEjectFxContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java");

        assertTrue(source.contains("int xpDrainState = this.drainRecipeXP();"));
        assertTrue(source.contains("if (xpDrainState < 0) this.addMissingIngredientInstability(3);"));
        assertTrue(source.contains("this.addMissingIngredientInstability(1 + i);"));
        assertTrue(source.contains("boolean hadTargets = !targets.isEmpty();"));
        assertTrue(source.contains("return hadTargets ? -1 : 0;"));

        assertTrue(source.contains("new PacketFXBlockZap("));
        assertTrue(source.contains("SoundEvents.ENTITY_GENERIC_SWIM"));
        assertTrue(source.contains("SoundEvents.BLOCK_FIRE_EXTINGUISH"));
        assertTrue(source.contains("Thaumcraft.proxy.drawInfusionParticles1("));
        assertTrue(source.contains("Thaumcraft.proxy.drawInfusionParticles2("));
        assertTrue(source.contains("Thaumcraft.proxy.drawInfusionParticles3("));
        assertTrue(source.contains("Thaumcraft.proxy.drawInfusionParticles4("));
        assertTrue(source.contains("Thaumcraft.proxy.nodeBolt("));
        assertFalse(source.contains("Thaumcraft.proxy.beam("));
        assertFalse(source.contains("Thaumcraft.proxy.bolt("));
        assertFalse(source.contains("canPlaceFlux("));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
