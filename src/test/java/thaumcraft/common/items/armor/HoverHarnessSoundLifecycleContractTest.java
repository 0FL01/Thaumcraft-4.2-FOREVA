package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HoverHarnessSoundLifecycleContractTest {

    private static final String SOURCE = "src/main/java/thaumcraft/common/items/armor/Hover.java";

    @Test
    public void operationAndToggleSoundsShouldMatchTc4Contract() throws IOException {
        String source = read(SOURCE);
        String localGate = between(source, "private static boolean isLocalClientPlayer", "private static void notifyClientHoverChange");
        String notify = between(source, "private static void notifyClientHoverChange", "private static float getHoverMotionModifier");

        assertTrue(localGate.contains("player.world.isRemote")
                && localGate.contains("Thaumcraft.proxy.getClientPlayer() == player"));
        assertTrue(source.contains("currentTime + 1200L")
                && source.contains("TCSounds.JACOBS")
                && source.contains("SoundCategory.MASTER, 0.05F")
                && source.contains("1.0F + player.world.rand.nextFloat() * 0.05F"));
        assertTrue(notify.contains("new PacketFlyToServer(player, hover)")
                && notify.contains("hover ? TCSounds.HHON : TCSounds.HHOFF")
                && notify.contains("SoundCategory.MASTER, 0.33F, 1.0F, false"));
        assertFalse(source.contains("SoundCategory.PLAYERS") || source.contains("player.playSound("));
    }

    @Test
    public void exhaustionAndFlightStateShouldUseOneCanonicalStopTransition() throws IOException {
        String source = read(SOURCE);
        String handle = between(source, "public static void handleHoverArmor", "private static NBTTagCompound ensureTag");
        String expend = between(source, "public static boolean expendCharge", "private static void applyClientHoverMotion");

        assertTrue(handle.contains("boolean hover = tag.getBoolean(\"hover\");")
                && handle.contains("setHover(player, harness, false);")
                && handle.contains("notifyClientHoverChange(player, false);")
                && handle.contains("boolean abilitiesChanged")
                && handle.contains("player.capabilities.allowFlying = hover;")
                && handle.contains("player.capabilities.isFlying = hover;")
                && handle.contains("if (abilitiesChanged && !player.world.isRemote"));
        assertTrue(expend.indexOf("if (charge < threshold)") < expend.indexOf("charge + 1")
                && expend.indexOf("charge + 1") < expend.indexOf("consumeEnergyUnit(harness, jar)"));
        assertTrue(expend.contains("isNormalEnergyJar(jar)")
                && expend.contains("aspects.getAmount(Aspect.ENERGY) - 1")
                && expend.contains("new AspectList().add(Aspect.ENERGY, energy)"));
        assertFalse(expend.contains("capabilities.isCreativeMode"));
    }

    @Test
    public void soundAssetsShouldRemainExactTc4Copies() throws IOException {
        assertTc4Asset("jacobs.ogg");
        assertTc4Asset("hhon.ogg");
        assertTc4Asset("hhoff.ogg");
    }

    private static void assertTc4Asset(String name) throws IOException {
        assertArrayEquals(
                Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/sounds/" + name)),
                Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/sounds/" + name)));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String between(String source, String start, String end) {
        int startIndex = source.indexOf(start);
        int endIndex = source.indexOf(end, startIndex);
        assertTrue("Missing source section: " + start + " -> " + end, startIndex >= 0 && endIndex > startIndex);
        return source.substring(startIndex, endIndex);
    }
}
