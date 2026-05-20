package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ChampionClientFxStaticGuardTest {

    @Test
    public void renderEventHandlerShouldDriveChampionClientFx() throws IOException {
        String source = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");

        assertTrue(source.contains("event.getEntityLiving() instanceof EntityMob && !event.getEntityLiving().isDead"));
        assertTrue(source.contains("IAttributeInstance mod = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);"));
        assertTrue(source.contains("int type = (int) mod.getAttributeValue();"));
        assertTrue(source.contains("if (type >= 0 && type < ChampionModifier.mods.length)"));
        assertTrue(source.contains("ChampionModifier.mods[type].effect.showFX((EntityLivingBase) mob);"));
        assertTrue(source.contains("if (player == null || event.getEntityLiving().getEntityId() != player.getEntityId())"));
        assertTrue(source.contains("clearScanState();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
