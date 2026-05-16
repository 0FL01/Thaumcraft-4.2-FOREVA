package thaumcraft.common.entities.monster.boss;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityCultistPortalLootPlacementContractTest {

    @Test
    public void cultistPortalUsesThaumcraftLootBlocksInsteadOfVanillaChestPlaceholders() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java");

        assertTrue("EntityCultistPortal must place Thaumcraft loot crates in stage 0",
                source.contains("ConfigBlocks.blockLootCrate.getStateFromMeta"));
        assertTrue("EntityCultistPortal should not place vanilla chest blocks directly",
                !source.contains("Blocks.CHEST") && !source.contains("minecraft:chest"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
