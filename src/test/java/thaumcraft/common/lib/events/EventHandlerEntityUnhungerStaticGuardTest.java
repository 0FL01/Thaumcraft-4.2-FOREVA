package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityUnhungerStaticGuardTest {

    @Test
    public void itemUseFinishShouldKeepUnnaturalHungerContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(source.contains("Potion.getPotionById(Config.potionUnHungerID)"));
        assertTrue(source.contains("used.getItem() == Items.ROTTEN_FLESH || used.getItem() == ConfigItems.itemZombieBrain"));
        assertTrue(source.contains("player.removePotionEffect(unHunger);"));
        assertTrue(source.contains("reduced.getCurativeItems().add(new ItemStack(Items.ROTTEN_FLESH));"));
        assertTrue(source.contains("new TextComponentTranslation(\"warp.text.hunger.2\")"));
        assertTrue(source.contains("new TextComponentTranslation(\"warp.text.hunger.1\")"));
        assertTrue(lang.contains("warp.text.hunger.1=Your hunger cannot be satisfied with normal food."));
        assertTrue(lang.contains("warp.text.hunger.2=You hunger begins to fade."));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
