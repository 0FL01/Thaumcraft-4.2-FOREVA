package thaumcraft.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BrainJarAndPechClientFeedbackStaticGuardTest {

    @Test
    public void tileJarBrainShouldKeepClientAnimationAndSighLoop() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileJarBrain.java");

        assertTrue(source.contains("if (this.world.isRemote)"));
        assertTrue(source.contains("this.rotb = this.rota;"));
        assertTrue(source.contains("this.world.getClosestPlayer("));
        assertTrue(source.contains("TCSounds.BRAIN"));
        assertTrue(source.contains("SoundCategory.AMBIENT"));
        assertTrue(source.contains("this.lastsigh = System.currentTimeMillis() + 5000L + this.world.rand.nextInt(25000);"));
        assertTrue(source.contains("this.field_40066_q = (float) Math.atan2(d1, d);"));
        assertTrue(source.contains("this.rota += f * 0.04F;"));
        assertTrue(source.contains("/ 25.0D"));
        assertTrue(source.contains("strength * 0.3"));
        assertTrue(source.contains("strength * 0.5"));
        assertTrue(source.contains(".grow(8.0, 8.0, 8.0)"));
    }

    @Test
    public void blockJarShouldKeepBrainJarServerOnlyXpDropAndJarClickSound() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockJar.java");

        assertTrue(source.contains("if (meta == 1 && !worldIn.isRemote)"));
        assertTrue(source.contains("new EntityXPOrb(worldIn, pos.getX() + 0.5"));
        assertTrue(source.contains("TCSounds.JAR"));
    }

    @Test
    public void emptyingJarUsesTheTc4JarAndLiquidSoundsRatherThanGlassBreakage() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockJar.java");
        int emptyJarStart = source.indexOf("// Sneak + empty hand = empty jar");
        int nextInteraction = source.indexOf("// Apply label", emptyJarStart);
        String emptyJarInteraction = source.substring(emptyJarStart, nextInteraction);

        assertTrue(emptyJarInteraction.contains("TCSounds.JAR"));
        assertTrue(emptyJarInteraction.contains("SoundEvents.ENTITY_PLAYER_SWIM"));
        assertFalse(emptyJarInteraction.contains("SoundEvents.BLOCK_GLASS_BREAK"));
    }

    @Test
    public void entityPechShouldKeepClientStatusFeedbackForTradeAndAnger() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/EntityPech.java");

        assertTrue(source.contains("public float mumble = 0.0F;"));
        assertTrue(source.contains("public void handleStatusUpdate(byte id)"));
        assertTrue(source.contains("this.mumble = (float) Math.PI;"));
        assertTrue(source.contains("this.mumble = (float) Math.PI * 2.0F;"));
        assertTrue(source.contains("spawnReactionParticles(80);"));
        assertTrue(source.contains("spawnReactionParticles(81);"));
        assertTrue(source.contains("Thaumcraft.proxy.drawGenericParticles("));
        assertTrue(source.contains("false, start, 1, 1, 16, 0, 1.5F, 1);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
