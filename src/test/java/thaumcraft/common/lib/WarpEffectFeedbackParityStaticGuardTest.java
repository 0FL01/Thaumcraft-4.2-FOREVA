package thaumcraft.common.lib;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WarpEffectFeedbackParityStaticGuardTest {

    @Test
    public void warpPotionsKeepOriginalAmbientParticleAndCurativeContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/WarpEvents.java");

        assertTrue(count(source, "applyPotionWithoutCuratives(player, Config.potion") == 5);
        assertTrue(source.contains("new PotionEffect(potion, duration, amplifier, ambient, true)"));
        assertTrue(count(source, "Config.potionUnnaturalHunger") == 2);
        assertTrue(count(source, "Math.min(3, warp / 15), true, true)") == 2);
        assertTrue(count(source, "pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH))") == 2);
        assertTrue(count(source, "pe.addCurativeItem(new ItemStack(ConfigItems.itemZombieBrain))") == 2);
    }

    @Test
    public void warpPotionsKeepOriginalStatusIconContracts() throws IOException {
        assertIconIndex("src/main/java/thaumcraft/api/potions/PotionFluxTaint.java", 3, 1);
        assertIconIndex("src/main/java/thaumcraft/api/potions/PotionVisExhaust.java", 5, 1);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionInfectiousVisExhaust.java", 6, 1);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionUnnaturalHunger.java", 7, 1);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionWarpWard.java", 3, 2);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionDeathGaze.java", 4, 2);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionBlurredVision.java", 5, 2);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionSunScorned.java", 6, 2);
        assertIconIndex("src/main/java/thaumcraft/common/lib/potions/PotionThaumarhia.java", 7, 2);
    }

    @Test
    public void unnaturalHungerUsesNativeColorConvolveShader() throws IOException {
        String shader = readFile("src/main/resources/assets/minecraft/shaders/post/hunger.json");

        assertTrue(shader.contains("\"name\": \"color_convolve\""));
        assertFalse(shader.contains("color_convolve2"));
        assertTrue(shader.contains("\"values\": [ 1.0, 0.8, 0.8 ]"));
        assertTrue(shader.contains("\"values\": [ 1.1 ]"));
        assertFalse(Files.exists(Paths.get("src/main/resources/assets/minecraft/shaders/program/color_convolve2.json")));
        assertFalse(Files.exists(Paths.get("src/main/resources/assets/minecraft/shaders/program/color_convolve2.fsh")));
    }

    @Test
    public void deathGazeKeepsOriginalTargetAndAggressionFilters() throws IOException {
        String warpEvents = readFile("src/main/java/thaumcraft/common/lib/WarpEvents.java");
        String entityUtils = readFile("src/main/java/thaumcraft/common/lib/utils/EntityUtils.java");

        assertTrue(warpEvents.contains("!entity.canBeCollidedWith()"));
        assertTrue(warpEvents.contains("EntityUtils.isVisibleTo(0.75F, player, entity, range)"));
        assertTrue(warpEvents.contains("!player.canEntityBeSeen(entity)"));
        assertTrue(warpEvents.contains("isPVPEnabled()"));
        assertTrue(warpEvents.contains("living.isPotionActive(MobEffects.WITHER)"));
        assertTrue(warpEvents.contains("living.setRevengeTarget(player);"));
        assertTrue(warpEvents.contains("living.setLastAttackedEntity(player);"));
        assertTrue(entityUtils.contains("target.getEntityBoundingBox().minY + target.height / 2.0F"));
        assertTrue(entityUtils.contains("source.getLook(1.0F).normalize()"));
    }

    @Test
    public void normalWarpMutationsUseOriginalHudAndWhisperRules() throws IOException {
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");
        String packet = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketWarpMessage.java");

        assertTrue(thaumcraft.contains("new PacketWarpMessage(player, (byte) (temporary ? 2 : 0), amount)"));
        assertTrue(thaumcraft.contains("new PacketWarpMessage(player, (byte) 1, amount)"));
        assertTrue(packet.contains("PlayerNotifications.addNotification"));
        assertTrue(packet.contains("this.type == 0 && this.data > 0"));
        assertTrue(packet.contains("this.type == 1"));
        assertTrue(packet.contains("else if (this.data > 0)"));
        assertTrue(count(packet, "playWhisper(world, player);") == 2);
        assertFalse(packet.contains("player.sendMessage("));
        assertFalse(packet.contains("tc.removewarptemp"));
    }

    private static int count(String source, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = source.indexOf(needle, offset)) >= 0) {
            count++;
            offset += needle.length();
        }
        return count;
    }

    private static void assertIconIndex(String path, int x, int y) throws IOException {
        String source = readFile(path);
        assertTrue(path, source.contains("setIconIndex(" + x + ", " + y + ")"));
        assertTrue(path, count(source, "setIconIndex(") == 1);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
