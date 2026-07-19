package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityDropContractStaticGuardTest {

    @Test
    public void livingDropsShouldKeepReferenceBrainAndDissolveContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("event.getEntityLiving() instanceof EntityZombie"));
        assertTrue(source.contains("!(event.getEntityLiving() instanceof EntityBrainyZombie)"));
        assertTrue(source.contains("event.isRecentlyHit()"));
        assertTrue(source.contains("world.rand.nextInt(10) - event.getLootingLevel() < 1"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemZombieBrain)"));
        assertTrue(source.contains("event.getEntityLiving() instanceof EntityVillager"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemResource, 1, 18)"));
        assertTrue(source.contains("event.getSource() == DamageSourceThaumcraft.dissolve"));
        assertTrue(source.contains("ScanManager.generateEntityAspects(event.getEntityLiving())"));
        assertTrue(source.contains("if (world.rand.nextBoolean()) continue;"));
        assertTrue(source.contains("int size = 1 + world.rand.nextInt(aspects.getAmount(aspect));"));
        assertTrue(source.contains("size = Math.max(1, size / 2);"));
        assertTrue(source.contains("ConfigItems.itemCrystalEssence.setAspects(crystal, new AspectList().add(aspect, 1))"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
