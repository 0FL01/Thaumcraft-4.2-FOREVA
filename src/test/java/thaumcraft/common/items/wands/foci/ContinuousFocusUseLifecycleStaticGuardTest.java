package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContinuousFocusUseLifecycleStaticGuardTest {

    @Test
    public void continuousFociShouldKeepClientActiveUntilAuthoritativeServerStop() throws IOException {
        String fire = read("src/main/java/thaumcraft/common/items/wands/foci/FocusFire.java");
        String shock = read("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");
        String excavation = read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java");
        String clientTicks = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");
        String wandBeam = read("src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java");

        assertAuthoritativeStop(fire);
        assertAuthoritativeStop(shock);
        assertAuthoritativeStop(excavation);
        assertTrue("client must send release on physical key-up even if active-hand sync was lost",
                clientTicks.contains("private boolean wandUseReleasePending;")
                        && clientTicks.contains("this.ensureWandUseRelease(Minecraft.getMinecraft());")
                        && clientTicks.contains("mc.gameSettings.keyBindUseItem.isKeyDown()")
                        && clientTicks.contains("mc.playerController.onStoppedUsingItem(mc.player);"));
        assertTrue("continuous wand beams must die as soon as active use ends",
                wandBeam.contains("|| !this.player.isHandActive()")
                        && wandBeam.contains("this.setExpired();"));
    }

    @Test
    public void continuousFociShouldUseThePhysicalHandHoldingTheWand() throws IOException {
        String wand = read("src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java");
        String fire = read("src/main/java/thaumcraft/common/items/wands/foci/FocusFire.java");
        String shock = read("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");
        String excavation = read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java");

        assertTrue(wand.contains("public static EnumHand getHandHoldingWand(EntityPlayer player, ItemStack wandStack)"));
        assertTrue(wand.contains("if (main == wandStack) return EnumHand.MAIN_HAND;"));
        assertTrue(wand.contains("if (off == wandStack) return EnumHand.OFF_HAND;"));
        assertTrue(fire.contains("player.setActiveHand(hand);"));
        assertTrue(shock.contains("player.setActiveHand(hand);"));
        assertTrue(excavation.contains("player.setActiveHand(ItemWandCasting.getHandHoldingWand(player, wandStack));"));
    }

    private static void assertAuthoritativeStop(String source) {
        assertFalse("client-side reset loses vanilla's release-use packet",
                source.contains("player.resetActiveHand();"));
        assertTrue("resource exhaustion must stop through the server item lifecycle",
                source.contains("if (!player.world.isRemote)")
                        && source.contains("player.stopActiveHand();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
