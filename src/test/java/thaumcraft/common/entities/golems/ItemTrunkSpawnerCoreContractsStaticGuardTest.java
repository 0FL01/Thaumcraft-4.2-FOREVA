package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemTrunkSpawnerCoreContractsStaticGuardTest {

    @Test
    public void trunkSpawnerKeepsReferenceUseAndTooltipContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/ItemTrunkSpawner.java");

        assertTrue("ItemTrunkSpawner must keep upgrade/inventory tooltip localization contracts",
                source.contains("I18n.translateToLocal(\"item.ItemGolemUpgrade.\" + upgrade + \".name\")")
                        && source.contains("I18n.translateToLocal(\"item.TrunkSpawner.text.1\")"));
        assertTrue("ItemTrunkSpawner must keep server consume-success result semantics",
                source.contains("if (world.isRemote) {")
                        && source.contains("return EnumActionResult.SUCCESS;")
                        && source.contains("stack.shrink(1);")
                        && source.contains("return EnumActionResult.SUCCESS;"));
        assertTrue("ItemTrunkSpawner must keep trunk spawn-initialization hooks",
                source.contains("trunk.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(trunk)), (IEntityLivingData) null);")
                        && source.contains("boolean spawned = world.spawnEntity(trunk);")
                        && source.contains("trunk.playLivingSound();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
