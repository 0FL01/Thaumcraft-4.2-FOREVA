package thaumcraft.common.config;

import org.junit.Test;
import net.minecraft.entity.passive.EntityVillager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class VillagerTradeLevelsStaticGuardTest {

    @Test
    public void thaumcraftVillagerTradesShouldBeFlatAtCareerLevelOne() throws IOException {
        String configEntities = readFile("src/main/java/thaumcraft/common/config/ConfigEntities.java");
        String villagerTrades = readFile("src/main/java/thaumcraft/common/lib/world/ThaumcraftVillagerTrades.java");

        assertTrue("Thaumcraft villager trades must be grouped by Forge career level",
                villagerTrades.contains("public static final EntityVillager.ITradeList[][] WIZARD_TRADE_LEVELS")
                        && villagerTrades.contains("public static final EntityVillager.ITradeList[][] BANKER_TRADE_LEVELS"));
        assertEquals(5, countTradeLevels(villagerTrades, "WIZARD_TRADE_LEVELS"));
        assertEquals(5, countTradeLevels(villagerTrades, "BANKER_TRADE_LEVELS"));

        assertTrue("ConfigEntities must flatten all trade groups into the initially available level",
                configEntities.contains("registerCareerTrades(wizardCareer, ThaumcraftVillagerTrades.WIZARD_TRADE_LEVELS);")
                        && configEntities.contains("registerCareerTrades(bankerCareer, ThaumcraftVillagerTrades.BANKER_TRADE_LEVELS);")
                        && configEntities.contains("career.addTrade(1, trades);"));
        assertFalse("TC4 trades must not be delayed behind Forge career levels",
                configEntities.contains("career.addTrade(i + 1, trades);"));
    }

    @Test
    public void flatteningPreservesEveryTradeAndItsOrder() {
        EntityVillager.ITradeList a = (merchant, recipes, random) -> { };
        EntityVillager.ITradeList b = (merchant, recipes, random) -> { };
        EntityVillager.ITradeList c = (merchant, recipes, random) -> { };
        EntityVillager.ITradeList d = (merchant, recipes, random) -> { };

        EntityVillager.ITradeList[] flat = ConfigEntities.flattenTradeLevels(
                new EntityVillager.ITradeList[][] {{a, b}, null, {}, {c, d}});

        assertEquals(4, flat.length);
        assertSame(a, flat[0]);
        assertSame(b, flat[1]);
        assertSame(c, flat[2]);
        assertSame(d, flat[3]);
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
