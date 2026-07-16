package thaumcraft.common.items.wands;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemWandCastingTooltipRuntimeTest {
    private CommonProxy oldProxy;
    private LinkedHashMap<String, WandCap> oldCaps;
    private LinkedHashMap<String, WandRod> oldRods;
    private TooltipProxy proxy;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUp() {
        this.oldProxy = Thaumcraft.proxy;
        this.oldCaps = new LinkedHashMap<>(WandCap.caps);
        this.oldRods = new LinkedHashMap<>(WandRod.rods);
        WandCap.caps.clear();
        WandRod.rods.clear();
        this.proxy = new TooltipProxy();
        Thaumcraft.proxy = this.proxy;
    }

    @After
    public void tearDown() {
        Thaumcraft.proxy = this.oldProxy;
        WandCap.caps.clear();
        WandCap.caps.putAll(this.oldCaps);
        WandRod.rods.clear();
        WandRod.rods.putAll(this.oldRods);
    }

    @Test
    public void tooltipShowsCompactAndShiftDetailedPrimalVis() {
        WandRod rod = new WandRod("tooltip_rod", 25, new ItemStack(new Item()), 1);
        WandCap cap = new WandCap("tooltip_cap", 0.9F, new ItemStack(new Item()), 1);
        ItemWandCasting wand = new ItemWandCasting();
        ItemStack stack = new ItemStack(wand);
        ItemWandCasting.setRod(stack, rod);
        ItemWandCasting.setCap(stack, cap);

        int vis = 125;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            ItemWandCasting.setVis(stack, aspect, vis);
            vis += 125;
        }

        List<String> compactTooltip = new ArrayList<>();
        wand.addInformation(stack, null, compactTooltip, ITooltipFlag.TooltipFlags.NORMAL);
        String compactVis = findLine(compactTooltip, " | ");
        assertEquals(Aspect.getPrimalAspects().size() - 1, occurrences(compactVis, " | "));
        assertTrue(findLine(compactTooltip, "90%").contains("25"));

        DecimalFormat formatter = new DecimalFormat("#####.##");
        vis = 125;
        for (Aspect ignored : Aspect.getPrimalAspects()) {
            assertTrue(compactVis.contains(formatter.format((float) vis / 100.0F)));
            vis += 125;
        }

        this.proxy.shift = true;
        List<String> detailedTooltip = new ArrayList<>();
        wand.addInformation(stack, null, detailedTooltip, ITooltipFlag.TooltipFlags.NORMAL);
        assertFalse(detailedTooltip.stream().anyMatch(line -> line.contains(" | ")));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            assertTrue(detailedTooltip.stream().anyMatch(line -> line.contains(aspect.getName()) && line.contains("90%")));
        }
    }

    private static String findLine(List<String> lines, String text) {
        return lines.stream().filter(line -> line.contains(text)).findFirst().orElse("");
    }

    private static int occurrences(String text, String needle) {
        return (text.length() - text.replace(needle, "").length()) / needle.length();
    }

    private static class TooltipProxy extends CommonProxy {
        private boolean shift;

        @Override
        public boolean isShiftKeyDown() {
            return this.shift;
        }
    }
}
