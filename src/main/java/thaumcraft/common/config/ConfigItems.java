package thaumcraft.common.config;

import net.minecraft.item.Item;
import thaumcraft.common.items.wands.ItemWandCap;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.ItemWandRod;
import thaumcraft.common.items.wands.foci.*;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.ArrayList;
import java.util.List;

public class ConfigItems {

    // Wand items
    public static ItemWandCasting itemWandCasting;
    public static ItemWandRod itemWandRod;
    public static ItemWandCap itemWandCap;

    // Wand foci
    public static FocusShock focusShock;
    public static FocusFire focusFire;
    public static FocusFrost focusFrost;
    public static FocusExcavation focusExcavation;
    public static FocusPrimal focusPrimal;
    public static FocusWarding focusWarding;
    public static FocusHellbat focusHellbat;
    public static FocusPech focusPech;
    public static FocusTrade focusTrade;
    public static FocusPortableHole focusPortableHole;

    // All registered items
    private static final List<Item> allItems = new ArrayList<>();

    // Future items (stubs for compilation compatibility)
    public static Object itemAmuletVis;
    public static Object itemRingRunic;
    public static Object itemBaubleBlanks;
    public static Object itemResource;
    public static Object itemLootbag;
    public static Object itemThaumonomicon;
    public static Object itemSwordThaumium;
    public static Object itemPickThaumium;
    public static Object itemAxeThaumium;
    public static Object itemHoeThaumium;
    public static Object itemEldritchObject;
    public static Object itemNugget;
    public static Object itemEssence;

    public static void init() {
        CreativeTabThaumcraft tab = CreativeTabThaumcraft.tabThaumcraft;

        itemWandCasting = (ItemWandCasting) new ItemWandCasting()
                .setRegistryName("thaumcraft", "wand_casting")
                .setTranslationKey("thaumcraft.wand_casting")
                .setCreativeTab(tab);
        allItems.add(itemWandCasting);

        itemWandRod = (ItemWandRod) new ItemWandRod()
                .setRegistryName("thaumcraft", "wand_rod")
                .setTranslationKey("thaumcraft.wand_rod")
                .setCreativeTab(tab);
        allItems.add(itemWandRod);

        itemWandCap = (ItemWandCap) new ItemWandCap()
                .setRegistryName("thaumcraft", "wand_cap")
                .setTranslationKey("thaumcraft.wand_cap")
                .setCreativeTab(tab);
        allItems.add(itemWandCap);

        focusShock = (FocusShock) new FocusShock()
                .setRegistryName("thaumcraft", "focus_shock")
                .setTranslationKey("thaumcraft.focus_shock")
                .setCreativeTab(tab);
        allItems.add(focusShock);

        focusFire = (FocusFire) new FocusFire()
                .setRegistryName("thaumcraft", "focus_fire")
                .setTranslationKey("thaumcraft.focus_fire")
                .setCreativeTab(tab);
        allItems.add(focusFire);

        focusFrost = (FocusFrost) new FocusFrost()
                .setRegistryName("thaumcraft", "focus_frost")
                .setTranslationKey("thaumcraft.focus_frost")
                .setCreativeTab(tab);
        allItems.add(focusFrost);

        focusExcavation = (FocusExcavation) new FocusExcavation()
                .setRegistryName("thaumcraft", "focus_excavation")
                .setTranslationKey("thaumcraft.focus_excavation")
                .setCreativeTab(tab);
        allItems.add(focusExcavation);

        focusPrimal = (FocusPrimal) new FocusPrimal()
                .setRegistryName("thaumcraft", "focus_primal")
                .setTranslationKey("thaumcraft.focus_primal")
                .setCreativeTab(tab);
        allItems.add(focusPrimal);

        focusWarding = (FocusWarding) new FocusWarding()
                .setRegistryName("thaumcraft", "focus_warding")
                .setTranslationKey("thaumcraft.focus_warding")
                .setCreativeTab(tab);
        allItems.add(focusWarding);

        focusHellbat = (FocusHellbat) new FocusHellbat()
                .setRegistryName("thaumcraft", "focus_hellbat")
                .setTranslationKey("thaumcraft.focus_hellbat")
                .setCreativeTab(tab);
        allItems.add(focusHellbat);

        focusPech = (FocusPech) new FocusPech()
                .setRegistryName("thaumcraft", "focus_pech")
                .setTranslationKey("thaumcraft.focus_pech")
                .setCreativeTab(tab);
        allItems.add(focusPech);

        focusTrade = (FocusTrade) new FocusTrade()
                .setRegistryName("thaumcraft", "focus_trade")
                .setTranslationKey("thaumcraft.focus_trade")
                .setCreativeTab(tab);
        allItems.add(focusTrade);

        focusPortableHole = (FocusPortableHole) new FocusPortableHole()
                .setRegistryName("thaumcraft", "focus_portable_hole")
                .setTranslationKey("thaumcraft.focus_portable_hole")
                .setCreativeTab(tab);
        allItems.add(focusPortableHole);
    }

    public static Item[] getAllItems() {
        return allItems.toArray(new Item[0]);
    }
}
