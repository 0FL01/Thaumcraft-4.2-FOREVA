package thaumcraft.common.items.wands;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WandRodPrimalOnUpdateStaticGuardTest {

    @Test
    public void primalRodUpdateShouldKeepReferenceRechargeCadenceContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.java");

        assertTrue(source.contains("if (player.ticksExisted % 200 == 0"));
        assertTrue(source.contains("wand.getVis(itemstack, this.aspect) < wand.getMaxVis(itemstack) / 10"));
        assertTrue(source.contains("wand.addVis(itemstack, this.aspect, 1, true);"));
        assertTrue(source.contains("if (player.ticksExisted % 50 == 0 && this.primals != null)"));
        assertTrue(source.contains("ArrayList<Aspect> candidates = new ArrayList<>();"));
        assertTrue(source.contains("if (wand.getVis(itemstack, primal) < wand.getMaxVis(itemstack) / 10)"));
        assertTrue(source.contains("candidates.get(player.world.rand.nextInt(candidates.size()))"));
        assertTrue(source.contains("wand.addVis(itemstack, candidates.get(player.world.rand.nextInt(candidates.size())), 1, true);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
