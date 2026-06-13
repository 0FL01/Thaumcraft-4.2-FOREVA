package thaumcraft.common.lib;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Static guard for TC4 parity aspect-orb drop and hotbar-wide wand pickup.
 */
public class AspectOrbDropStaticGuardTest {

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void onLivingDeathShouldDropAspectOrbsTC4Parity() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue("onLivingDeath must call ScanManager.generateEntityAspects",
                source.contains("ScanManager.generateEntityAspects("));
        assertTrue("onLivingDeath must call ResearchManager.reduceToPrimals",
                source.contains("ResearchManager.reduceToPrimals("));
        assertTrue("onLivingDeath must spawn EntityAspectOrb",
                source.contains("new EntityAspectOrb("));
        assertTrue("onLivingDeath must use 50% chance (nextBoolean)",
                source.contains("world.rand.nextBoolean()"));
        assertTrue("onLivingDeath must use 1+nextInt(amount) value formula",
                source.contains("1 + event.getEntityLiving().world.rand.nextInt(aspects.getAmount(aspect))"));
        assertTrue("onLivingDeath must skip ITaintedMob",
                source.contains("instanceof ITaintedMob"));
        assertTrue("onLivingDeath must check player kill via damage source",
                source.contains("getTrueSource() instanceof EntityPlayer"));
        assertTrue("onLivingDeath must exclude FakePlayer",
                source.contains("instanceof FakePlayer"));
    }

    @Test
    public void isWandInHotbarWithRoomShouldUseAddVisRoomCheck() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java");

        assertTrue("isWandInHotbarWithRoom must use addVis for TC4-parity room check",
                source.contains("ItemWandCasting.addVis(stack, aspect, amount, false) < amount"));
    }

    @Test
    public void entityAspectOrbShouldUseHotbarWideWandSearch() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/EntityAspectOrb.java");

        assertTrue("EntityAspectOrb attraction must use isWandInHotbarWithRoom",
                source.contains("InventoryUtils.isWandInHotbarWithRoom("));
    }
}
