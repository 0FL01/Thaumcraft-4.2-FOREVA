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
        int methodStart = source.indexOf("public void onLivingDeath(LivingDeathEvent event)");
        int methodEnd = source.indexOf("private boolean tryConvertTaintedDeath", methodStart);
        String method = source.substring(methodStart, methodEnd);

        assertTrue("onLivingDeath must call ScanManager.generateEntityAspects",
                method.contains("ScanManager.generateEntityAspects("));
        assertTrue("onLivingDeath must call ResearchManager.reduceToPrimals",
                method.contains("ResearchManager.reduceToPrimals("));
        assertTrue("onLivingDeath must spawn EntityAspectOrb",
                method.contains("new EntityAspectOrb("));
        assertTrue("onLivingDeath must use 50% chance (nextBoolean)",
                method.contains("world.rand.nextBoolean()"));
        assertTrue("onLivingDeath must use 1+nextInt(amount) value formula",
                method.contains("1 + event.getEntityLiving().world.rand.nextInt(aspects.getAmount(aspect))"));
        assertTrue("taint conversion must take precedence over aspect-orb eligibility",
                method.indexOf("tryConvertTaintedDeath") < method.indexOf("isAspectOrbEligible"));
        assertTrue("onLivingDeath must use recently-hit eligibility",
                method.contains("isAspectOrbEligible(event.getEntityLiving())"));
        assertFalse("aspect-orb eligibility must not exclude existing tainted mobs",
                method.contains("instanceof ITaintedMob"));
        assertFalse("aspect-orb eligibility must not depend on the final damage source",
                method.contains("getTrueSource()"));
        assertFalse("aspect-orb eligibility must not exclude FakePlayer hits",
                method.contains("instanceof FakePlayer"));
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
