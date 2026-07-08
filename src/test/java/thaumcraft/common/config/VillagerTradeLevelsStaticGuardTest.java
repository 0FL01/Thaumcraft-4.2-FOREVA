package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VillagerTradeLevelsStaticGuardTest {

    @Test
    public void thaumcraftVillagerTradesShouldBeCareerLevelGated() throws IOException {
        String configEntities = readFile("src/main/java/thaumcraft/common/config/ConfigEntities.java");
        String villagerTrades = readFile("src/main/java/thaumcraft/common/lib/world/ThaumcraftVillagerTrades.java");

        assertTrue("Thaumcraft villager trades must be grouped by Forge career level",
                villagerTrades.contains("public static final EntityVillager.ITradeList[][] WIZARD_TRADE_LEVELS")
                        && villagerTrades.contains("public static final EntityVillager.ITradeList[][] BANKER_TRADE_LEVELS"));
        assertTrue("Wizard trade data must include multiple career levels",
                countTradeLevels(villagerTrades, "WIZARD_TRADE_LEVELS") > 1);
        assertTrue("Banker trade data must include multiple career levels",
                countTradeLevels(villagerTrades, "BANKER_TRADE_LEVELS") > 1);
        assertFalse("Wizard trades must not all be registered as a single level-1 flat list",
                villagerTrades.contains("WIZARD_TRADES"));
        assertFalse("Banker trades must not all be registered as a single level-1 flat list",
                villagerTrades.contains("BANKER_TRADES"));

        assertTrue("ConfigEntities must register every populated trade group at its own career level",
                configEntities.contains("registerCareerTrades(wizardCareer, ThaumcraftVillagerTrades.WIZARD_TRADE_LEVELS);")
                        && configEntities.contains("registerCareerTrades(bankerCareer, ThaumcraftVillagerTrades.BANKER_TRADE_LEVELS);")
                        && configEntities.contains("career.addTrade(i + 1, trades);"));
        assertFalse("TC villagers should not unlock every trade through a single addTrade(1, ...) call",
                configEntities.contains(".addTrade(1, ThaumcraftVillagerTrades.WIZARD")
                        || configEntities.contains(".addTrade(1, ThaumcraftVillagerTrades.BANKER"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int countTradeLevels(String source, String fieldName) {
        int fieldStart = source.indexOf(fieldName + " = new EntityVillager.ITradeList[][] {");
        if (fieldStart < 0) {
            return 0;
        }
        int openBrace = source.indexOf('{', fieldStart);
        int depth = 1;
        int levels = 0;
        for (int i = openBrace + 1; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (ch == '{') {
                if (depth == 1) {
                    levels++;
                }
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    return levels;
                }
            }
        }
        return 0;
    }
}
