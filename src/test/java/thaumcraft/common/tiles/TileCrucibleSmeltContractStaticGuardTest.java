package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileCrucibleSmeltContractStaticGuardTest {

    @Test
    public void attemptSmeltKeepsStage9dCrucibleContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileCrucible.java");

        assertTrue("attemptSmelt must keep thrower NBT fallback",
                source.contains("if (entityData.hasKey(\"thrower\"))"));
        assertTrue("attemptSmelt must keep research-gated crucible recipe matcher call",
                source.contains("ThaumcraftCraftingManager.findMatchingCrucibleRecipe(username, this.aspects, item)"));
        assertTrue("attemptSmelt must drain 50mB water on recipe craft",
                source.contains("this.tank.drain(50, true);"));
        assertTrue("attemptSmelt must reject no-aspect items with pickup pop path",
                source.contains("this.world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP"));
        assertTrue("attemptSmelt must stop processing after no-aspect rejection",
                source.contains("return;"));
    }

    @Test
    public void crucibleClientFxHooksStayWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileCrucible.java");

        assertTrue("TileCrucible must keep drawEffects helper call from client update tick",
                source.contains("this.drawEffects();"));
        assertTrue("TileCrucible drawEffects must route froth/bubble into proxy helpers",
                source.contains("Thaumcraft.proxy.crucibleFroth(")
                        && source.contains("Thaumcraft.proxy.crucibleFrothDown(")
                        && source.contains("Thaumcraft.proxy.crucibleBubble("));
        assertTrue("TileCrucible block events must route sparkle/boil FX into proxy helpers",
                source.contains("Thaumcraft.proxy.blockSparkle(")
                        && source.contains("Thaumcraft.proxy.crucibleBoilSound(")
                        && source.contains("Thaumcraft.proxy.crucibleBoil("));
        assertFalse("TileCrucible must not keep Phase 8 crucible FX placeholder comments",
                source.contains("Phase 8: drawEffects()")
                        || source.contains("Phase 8: Thaumcraft.proxy.blockSparkle(...)")
                        || source.contains("Phase 8: Thaumcraft.proxy.crucibleBoilSound(...) + crucibleBoil(...)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
