package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerWorldHarvestDropsStaticGuardTest {

    @Test
    public void specialMiningReplacementHooksStayWired() throws IOException {
        String worldHandler = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java");
        String utils = readFile("src/main/java/thaumcraft/common/lib/utils/Utils.java");

        assertTrue("EventHandlerWorld harvest path must gate on elemental/primal/dowsing tools",
                worldHandler.contains("held.getItem() instanceof ItemElementalPickaxe")
                        && worldHandler.contains("held.getItem() instanceof ItemPrimalCrusher")
                        && worldHandler.contains("FocusExcavation.dowsing"));
        assertTrue("EventHandlerWorld harvest path must use Utils.findSpecialMiningResult replacement",
                worldHandler.contains("Utils.findSpecialMiningResult(original, chance, event.getWorld().rand)")
                        && worldHandler.contains("event.getDrops().set(i, replacement)"));
        assertTrue("EventHandlerWorld harvest path must keep treasure/fortune scaling",
                worldHandler.contains("EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held)")
                        && worldHandler.contains("FocusUpgradeType.treasure"));
        assertTrue("Utils must expose special mining result maps and add/find helpers",
                utils.contains("specialMiningResult")
                        && utils.contains("specialMiningChance")
                        && utils.contains("addSpecialMiningResult(")
                        && utils.contains("findSpecialMiningResult("));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
