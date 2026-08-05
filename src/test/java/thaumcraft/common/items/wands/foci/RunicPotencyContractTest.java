package thaumcraft.common.items.wands.foci;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Bootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RunicPotencyContractTest {
    private LinkedHashMap<String, WandRod> oldRods;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void saveRods() {
        this.oldRods = new LinkedHashMap<>(WandRod.rods);
    }

    @After
    public void restoreRods() {
        WandRod.rods.clear();
        WandRod.rods.putAll(this.oldRods);
    }

    @Test
    public void runedStaffAddsExactlyOneFocusPotency() {
        ItemWandCasting wand = new ItemWandCasting();
        ItemStack plainStack = new ItemStack(wand);
        StaffRod plain = new StaffRod("c1_plain", 25, ItemStack.EMPTY, 1);
        ItemWandCasting.setRod(plainStack, plain);

        ItemStack runedStack = new ItemStack(wand);
        StaffRod runed = new StaffRod("c1_runed", 25, ItemStack.EMPTY, 1);
        runed.setRunes(true);
        ItemWandCasting.setRod(runedStack, runed);

        assertEquals(0, wand.getFocusPotency(plainStack));
        assertEquals(wand.getFocusPotency(plainStack) + 1, wand.getFocusPotency(runedStack));
    }

    @Test
    public void wandCastsUseRunicPotencyAndArcaneBoreDoesNot() throws IOException {
        assertWandPotencyCalls("FocusFire.java", 2);
        assertWandPotencyCalls("FocusFrost.java", 1);
        assertWandPotencyCalls("FocusShock.java", 2);
        assertWandPotencyCalls("FocusHellbat.java", 1);
        assertWandPotencyCalls("FocusExcavation.java", 1);

        String bore = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");
        assertTrue(bore.contains("excavation.getUpgradeLevel(focus, FocusUpgradeType.potency)"));
        assertFalse(bore.contains("getFocusPotency("));
    }

    private static void assertWandPotencyCalls(String file, int expected) throws IOException {
        String source = read("src/main/java/thaumcraft/common/items/wands/foci/" + file);
        assertEquals(file, expected, occurrences(source, "wand.getFocusPotency(wandStack)"));
        assertFalse(file, source.contains("getUpgradeLevel(focusStack, FocusUpgradeType.potency)"));
    }

    private static int occurrences(String source, String needle) {
        int count = 0;
        for (int at = 0; (at = source.indexOf(needle, at)) >= 0; at += needle.length()) {
            count++;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
