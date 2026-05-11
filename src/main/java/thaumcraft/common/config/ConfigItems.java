package thaumcraft.common.config;

import net.minecraft.item.Item;
import thaumcraft.common.items.ItemBathSalts;
import thaumcraft.common.items.ItemBottleTaint;
import thaumcraft.common.items.ItemBucketDeath;
import thaumcraft.common.items.ItemBucketPure;
import thaumcraft.common.items.ItemCompassStone;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.items.ItemEldritchObject;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.items.ItemInkwell;
import thaumcraft.common.items.ItemKey;
import thaumcraft.common.items.ItemLootBag;
import thaumcraft.common.items.ItemManaBean;
import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemSanitySoap;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.items.ItemTripleMeatTreat;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.items.ItemZombieBrain;
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

    // Basic items
    public static ItemShard itemShard;
    public static ItemWispEssence itemWispEssence;

    // All registered items
    private static final List<Item> allItems = new ArrayList<>();

    // Phase 5 items
    public static ItemResource itemResource;
    public static ItemEssence itemEssence;
    public static ItemCrystalEssence itemCrystalEssence;
    public static ItemNugget itemNugget;
    public static ItemEldritchObject itemEldritchObject;
    public static ItemLootBag itemLootBag;

    // Phase 5.2 utility items
    public static ItemBottleTaint itemBottleTaint;
    public static ItemBucketDeath itemBucketDeath;
    public static ItemBucketPure itemBucketPure;
    public static ItemBathSalts itemBathSalts;
    public static ItemCompassStone itemCompassStone;
    public static ItemInkwell itemInkwell;
    public static ItemKey itemKey;
    public static ItemManaBean itemManaBean;
    public static ItemResearchNotes itemResearchNotes;
    public static ItemSanitySoap itemSanitySoap;
    public static ItemTripleMeatTreat itemTripleMeatTreat;
    public static ItemZombieBrain itemZombieBrain;

    // Future items (stubs for compilation compatibility)
    public static Object itemAmuletVis;
    public static Object itemRingRunic;
    public static Object itemBaubleBlanks;
    public static Object itemThaumonomicon;
    public static Object itemSwordThaumium;
    public static Object itemPickThaumium;
    public static Object itemAxeThaumium;
    public static Object itemHoeThaumium;

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

        itemShard = (ItemShard) new ItemShard()
                .setRegistryName("thaumcraft", "shard")
                .setTranslationKey("thaumcraft.shard");
        allItems.add(itemShard);

        itemWispEssence = (ItemWispEssence) new ItemWispEssence()
                .setRegistryName("thaumcraft", "wisp_essence")
                .setTranslationKey("thaumcraft.wisp_essence");
        allItems.add(itemWispEssence);

        itemResource = (ItemResource) new ItemResource()
                .setRegistryName("thaumcraft", "resource")
                .setTranslationKey("thaumcraft.resource")
                .setCreativeTab(tab);
        allItems.add(itemResource);

        itemEssence = (ItemEssence) new ItemEssence()
                .setRegistryName("thaumcraft", "essence")
                .setTranslationKey("thaumcraft.essence")
                .setCreativeTab(tab);
        allItems.add(itemEssence);

        itemCrystalEssence = (ItemCrystalEssence) new ItemCrystalEssence()
                .setRegistryName("thaumcraft", "crystal_essence")
                .setTranslationKey("thaumcraft.crystal_essence")
                .setCreativeTab(tab);
        allItems.add(itemCrystalEssence);

        itemNugget = (ItemNugget) new ItemNugget()
                .setRegistryName("thaumcraft", "nugget")
                .setTranslationKey("thaumcraft.nugget")
                .setCreativeTab(tab);
        allItems.add(itemNugget);

        itemEldritchObject = (ItemEldritchObject) new ItemEldritchObject()
                .setRegistryName("thaumcraft", "eldritch_object")
                .setTranslationKey("thaumcraft.eldritch_object")
                .setCreativeTab(tab);
        allItems.add(itemEldritchObject);

        itemLootBag = (ItemLootBag) new ItemLootBag()
                .setRegistryName("thaumcraft", "loot_bag")
                .setTranslationKey("thaumcraft.loot_bag")
                .setCreativeTab(tab);
        allItems.add(itemLootBag);

        itemBottleTaint = (ItemBottleTaint) new ItemBottleTaint()
                .setRegistryName("thaumcraft", "bottle_taint")
                .setTranslationKey("thaumcraft.bottle_taint")
                .setCreativeTab(tab);
        allItems.add(itemBottleTaint);

        itemBucketDeath = (ItemBucketDeath) new ItemBucketDeath()
                .setRegistryName("thaumcraft", "bucket_death")
                .setTranslationKey("thaumcraft.bucket_death")
                .setCreativeTab(tab);
        allItems.add(itemBucketDeath);

        itemBucketPure = (ItemBucketPure) new ItemBucketPure()
                .setRegistryName("thaumcraft", "bucket_pure")
                .setTranslationKey("thaumcraft.bucket_pure")
                .setCreativeTab(tab);
        allItems.add(itemBucketPure);

        itemBathSalts = (ItemBathSalts) new ItemBathSalts()
                .setRegistryName("thaumcraft", "bath_salts")
                .setTranslationKey("thaumcraft.bath_salts")
                .setCreativeTab(tab);
        allItems.add(itemBathSalts);

        itemCompassStone = (ItemCompassStone) new ItemCompassStone()
                .setRegistryName("thaumcraft", "compass_stone")
                .setTranslationKey("thaumcraft.compass_stone")
                .setCreativeTab(tab);
        allItems.add(itemCompassStone);

        itemInkwell = (ItemInkwell) new ItemInkwell()
                .setRegistryName("thaumcraft", "inkwell")
                .setTranslationKey("thaumcraft.inkwell")
                .setCreativeTab(tab);
        allItems.add(itemInkwell);

        itemKey = (ItemKey) new ItemKey()
                .setRegistryName("thaumcraft", "key")
                .setTranslationKey("thaumcraft.key")
                .setCreativeTab(tab);
        allItems.add(itemKey);

        itemManaBean = (ItemManaBean) new ItemManaBean()
                .setRegistryName("thaumcraft", "mana_bean")
                .setTranslationKey("thaumcraft.mana_bean")
                .setCreativeTab(tab);
        allItems.add(itemManaBean);

        itemResearchNotes = (ItemResearchNotes) new ItemResearchNotes()
                .setRegistryName("thaumcraft", "research_notes")
                .setTranslationKey("thaumcraft.research_notes")
                .setCreativeTab(tab);
        allItems.add(itemResearchNotes);

        itemSanitySoap = (ItemSanitySoap) new ItemSanitySoap()
                .setRegistryName("thaumcraft", "sanity_soap")
                .setTranslationKey("thaumcraft.sanity_soap")
                .setCreativeTab(tab);
        allItems.add(itemSanitySoap);

        itemTripleMeatTreat = (ItemTripleMeatTreat) new ItemTripleMeatTreat()
                .setRegistryName("thaumcraft", "triple_meat_treat")
                .setTranslationKey("thaumcraft.triple_meat_treat")
                .setCreativeTab(tab);
        allItems.add(itemTripleMeatTreat);

        itemZombieBrain = (ItemZombieBrain) new ItemZombieBrain()
                .setRegistryName("thaumcraft", "zombie_brain")
                .setTranslationKey("thaumcraft.zombie_brain")
                .setCreativeTab(tab);
        allItems.add(itemZombieBrain);
    }

    public static Item[] getAllItems() {
        return allItems.toArray(new Item[0]);
    }
}
