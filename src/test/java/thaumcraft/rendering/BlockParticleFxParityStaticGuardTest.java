package thaumcraft.rendering;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockParticleFxParityStaticGuardTest {

    @Test
    public void tc4HitAndDestroyHooksRemainConnected() throws IOException {
        String airy = read("src/main/java/thaumcraft/common/blocks/BlockAiry.java");
        String ore = read("src/main/java/thaumcraft/common/blocks/BlockCustomOre.java");
        String wardedGlass = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticOpaque.java");
        String warded = read("src/main/java/thaumcraft/common/blocks/BlockWarded.java");
        String solid = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java");
        String log = read("src/main/java/thaumcraft/common/blocks/BlockMagicalLog.java");
        String jar = read("src/main/java/thaumcraft/common/blocks/BlockJar.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(airy.contains("addHitEffects") && airy.contains("addDestroyEffects")
                && airy.contains("infusedStoneSparkle") && airy.contains("TCSounds.CRAFTFAIL"));
        assertTrue(ore.contains("addHitEffects") && ore.contains("meta != 0 && meta < 6")
                && ore.contains("infusedStoneSparkle"));
        assertTrue(wardedGlass.contains("addHitEffects") && wardedGlass.contains("target.hitVec.x - pos.getX()")
                && wardedGlass.contains("return true;"));
        assertTrue(warded.contains("addHitEffects") && warded.contains("Thaumcraft.proxy.blockWard"));
        assertTrue(solid.contains("addDestroyEffects") && solid.contains("== 8")
                && solid.contains("TCSounds.CRAFTFAIL"));
        assertTrue(log.contains("addDestroyEffects") && log.contains("state.getValue(TYPE) == 2")
                && log.contains("TCSounds.CRAFTFAIL"));
        assertTrue(jar.contains("addDestroyEffects")
                && jar.contains("this.getMetaFromState(world.getBlockState(pos)) != 15"));
        assertTrue(commonProxy.contains("void infusedStoneSparkle"));
        assertTrue(clientProxy.contains("void infusedStoneSparkle")
                && clientProxy.contains("1.75F, color, 3.0F + world.rand.nextInt(3)")
                && clientProxy.contains("fx.setGravity(0.1F)"));
    }

    @Test
    public void infusionUsesBlockParticlesForEveryItemBlockMetadata() throws IOException {
        String matrix = read("src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java");
        String boreParticle = read("src/main/java/thaumcraft/client/fx/particles/FXBoreParticles.java");
        assertTrue(matrix.contains("if (item instanceof ItemBlock)")
                && !matrix.contains("meta == 0 && item instanceof ItemBlock"));
        assertTrue(boreParticle.contains("if (sprite != null)")
                && boreParticle.contains("if (sprite == null)")
                && boreParticle.contains("this.setExpired();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
