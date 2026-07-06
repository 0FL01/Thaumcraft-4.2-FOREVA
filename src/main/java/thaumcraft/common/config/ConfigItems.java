package thaumcraft.common.config;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.entities.golems.ItemGolemCore;
import thaumcraft.common.entities.golems.ItemGolemDecoration;
import thaumcraft.common.entities.golems.ItemGolemPlacer;
import thaumcraft.common.entities.golems.ItemGolemUpgrade;
import thaumcraft.common.entities.golems.ItemTrunkSpawner;
import thaumcraft.common.blocks.ItemArcaneDoor;
import thaumcraft.common.compat.ThaumcraftSixCompatibility;
import thaumcraft.common.items.ItemBathSalts;
import thaumcraft.common.items.ItemBottleTaint;
import thaumcraft.common.items.ItemBucketDeath;
import thaumcraft.common.items.ItemBucketPure;
import thaumcraft.common.items.ItemCompassStone;
import thaumcraft.common.items.ItemEldritchObject;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.items.ItemInkwell;
import thaumcraft.common.items.ItemKey;
import thaumcraft.common.items.ItemLootBag;
import thaumcraft.common.items.ItemManaBean;
import thaumcraft.common.items.ItemNuggetEdible;
import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemSanitySoap;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.items.ItemTripleMeatTreat;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.items.ItemZombieBrain;
import thaumcraft.common.items.armor.*;
import thaumcraft.common.items.baubles.*;
import thaumcraft.common.items.equipment.*;
import thaumcraft.common.items.relics.*;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.items.wands.ItemFocusPouch;
import thaumcraft.common.items.wands.ItemFocusPouchBauble;
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

    public static ItemFocusPouch itemFocusPouch;
    public static ItemFocusPouchBauble itemFocusPouchBauble;

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

    // Tool materials
    public static ToolMaterial TOOLMAT_THAUMIUM;
    public static ToolMaterial TOOLMAT_VOID;
    public static ToolMaterial TOOLMAT_ELEMENTAL;
    public static ToolMaterial TOOLMAT_PRIMALVOID;

    // Armor materials
    public static ArmorMaterial ARMOR_THAUMIUM;
    public static ArmorMaterial ARMOR_VOID;
    public static ArmorMaterial ARMOR_FORTRESS;
    public static ArmorMaterial ARMOR_ROBE;
    public static ArmorMaterial ARMOR_VOID_ROBE;
    public static ArmorMaterial ARMOR_CULTIST;
    public static ArmorMaterial ARMOR_CULTIST_PLATE;
    public static ArmorMaterial ARMOR_CULTIST_LEADER;
    public static ArmorMaterial ARMOR_CULTIST_BOOTS;
    public static ArmorMaterial ARMOR_GOGGLES;
    public static ArmorMaterial ARMOR_TRAVELLER;
    public static ArmorMaterial ARMOR_HOVER;

    // Armor items
    public static ItemThaumiumArmor itemHelmThaumium;
    public static ItemThaumiumArmor itemChestThaumium;
    public static ItemThaumiumArmor itemLegsThaumium;
    public static ItemThaumiumArmor itemBootsThaumium;
    public static ItemVoidArmor itemHelmVoid;
    public static ItemVoidArmor itemChestVoid;
    public static ItemVoidArmor itemLegsVoid;
    public static ItemVoidArmor itemBootsVoid;
    public static ItemFortressArmor itemHelmFortress;
    public static ItemFortressArmor itemChestFortress;
    public static ItemFortressArmor itemLegsFortress;
    public static ItemFortressArmor itemBootsFortress;
    public static ItemRobeArmor itemHelmRobe;
    public static ItemRobeArmor itemChestRobe;
    public static ItemRobeArmor itemLegsRobe;
    public static ItemRobeArmor itemBootsRobe;
    public static ItemVoidRobeArmor itemHelmVoidRobe;
    public static ItemVoidRobeArmor itemChestVoidRobe;
    public static ItemVoidRobeArmor itemLegsVoidRobe;
    public static ItemVoidRobeArmor itemBootsVoidRobe;
    public static ItemCultistRobeArmor itemHelmetCultistRobe;
    public static ItemCultistRobeArmor itemChestCultistRobe;
    public static ItemCultistRobeArmor itemLegsCultistRobe;
    public static ItemCultistRobeArmor itemCultistRobe;
    public static ItemCultistPlateArmor itemHelmetCultistPlate;
    public static ItemCultistPlateArmor itemChestCultistPlate;
    public static ItemCultistPlateArmor itemLegsCultistPlate;
    public static ItemCultistPlateArmor itemCultistPlate;
    public static ItemCultistLeaderArmor itemHelmetCultistLeader;
    public static ItemCultistLeaderArmor itemChestCultistLeader;
    public static ItemCultistLeaderArmor itemLegsCultistLeader;
    public static ItemCultistLeaderArmor itemCultistLeader;
    public static ItemCultistBoots itemCultistBoots;
    public static ItemGoggles itemGoggles;
    public static ItemBootsTraveller itemBootsTraveller;
    public static ItemHoverHarness itemHoverHarness;

    // Equipment items
    public static ItemThaumiumSword itemSwordThaumium;
    public static ItemThaumiumPickaxe itemPickThaumium;
    public static ItemThaumiumAxe itemAxeThaumium;
    public static ItemThaumiumShovel itemShovelThaumium;
    public static ItemThaumiumHoe itemHoeThaumium;
    public static ItemVoidSword itemSwordVoid;
    public static ItemVoidPickaxe itemPickVoid;
    public static ItemVoidAxe itemAxeVoid;
    public static ItemVoidShovel itemShovelVoid;
    public static ItemVoidHoe itemHoeVoid;
    public static ItemElementalSword itemSwordElemental;
    public static ItemElementalPickaxe itemPickElemental;
    public static ItemElementalAxe itemAxeElemental;
    public static ItemElementalShovel itemShovelElemental;
    public static ItemElementalHoe itemHoeElemental;
    public static ItemBowBone itemBowBone;
    public static ItemCrimsonSword itemCrimsonSword;
    public static ItemPrimalArrow itemPrimalArrow;
    public static ItemPrimalCrusher itemPrimalCrusher;

    // Phase 5 items
    public static ItemResource itemResource;
    public static ItemEssence itemEssence;
    public static ItemCrystalEssence itemCrystalEssence;
    public static ItemNugget itemNugget;
    public static ItemNuggetEdible itemNuggetEdible;
    public static ItemEldritchObject itemEldritchObject;
    public static ItemLootBag itemLootBag;

    // Phase 5.2 utility items
    public static ItemBottleTaint itemBottleTaint;
    public static ItemBucketDeath itemBucketDeath;
    public static ItemBucketPure itemBucketPure;
    public static ItemBathSalts itemBathSalts;
    public static ItemCompassStone itemCompassStone;
    public static ItemInkwell itemInkwell;
    public static ItemArcaneDoor itemArcaneDoor;
    public static ItemKey itemKey;
    public static ItemManaBean itemManaBean;
    public static ItemResearchNotes itemResearchNotes;
    public static ItemSanitySoap itemSanitySoap;
    public static ItemTripleMeatTreat itemTripleMeatTreat;
    public static ItemZombieBrain itemZombieBrain;

    // Baubles
    public static ItemRingRunic itemRingRunic;
    public static ItemAmuletRunic itemAmuletRunic;
    public static ItemAmuletVis itemAmuletVis;
    public static ItemGirdleRunic itemGirdleRunic;
    public static ItemGirdleHover itemGirdleHover;
    public static ItemBaubleBlanks itemBaubleBlanks;

    // Relics
    public static ItemThaumometer itemThaumometer;
    public static ItemThaumonomicon itemThaumonomicon;
    public static ItemHandMirror itemHandMirror;
    public static ItemResonator itemResonator;
    public static ItemSanityChecker itemSanityChecker;

    // Golems and trunks
    public static Item itemGolemPlacer;
    public static Item itemGolemBell;
    public static Item itemGolemDecoration;
    public static Item itemGolemCore;
    public static Item itemGolemUpgrade;
    public static Item itemTrunkSpawner;

    public static void init() {
        CreativeTabThaumcraft tab = CreativeTabThaumcraft.tabThaumcraft;

        // Initialize tool materials
        TOOLMAT_THAUMIUM = EnumHelper.addToolMaterial("THAUMIUM", 3, 500, 7.0f, 2.5f, 18);
        TOOLMAT_VOID = EnumHelper.addToolMaterial("VOID", 4, 600, 8.0f, 3.0f, 20);
        TOOLMAT_ELEMENTAL = EnumHelper.addToolMaterial("ELEMENTAL", 4, 1561, 10.0f, 4.0f, 22);
        TOOLMAT_PRIMALVOID = EnumHelper.addToolMaterial("PRIMALVOID", 5, 500, 8.0f, 4.0f, 20);

        // Use armor materials from ThaumcraftApi (defined there with EnumHelper)
        ARMOR_THAUMIUM = thaumcraft.api.ThaumcraftApi.armorMatThaumium;
        ARMOR_VOID = thaumcraft.api.ThaumcraftApi.armorMatVoid;
        ARMOR_FORTRESS = thaumcraft.api.ThaumcraftApi.armorMatThaumiumFortress;
        ARMOR_ROBE = thaumcraft.api.ThaumcraftApi.armorMatSpecial;
        ARMOR_VOID_ROBE = thaumcraft.api.ThaumcraftApi.armorMatVoidFortress;
        ARMOR_CULTIST = thaumcraft.api.ThaumcraftApi.armorMatSpecial;
        ARMOR_CULTIST_PLATE = thaumcraft.api.ThaumcraftApi.armorMatThaumium;
        ARMOR_CULTIST_LEADER = thaumcraft.api.ThaumcraftApi.armorMatVoid;
        ARMOR_CULTIST_BOOTS = thaumcraft.api.ThaumcraftApi.armorMatSpecial;
        ARMOR_GOGGLES = thaumcraft.api.ThaumcraftApi.armorMatSpecial;
        ARMOR_TRAVELLER = thaumcraft.api.ThaumcraftApi.armorMatSpecial;
        ARMOR_HOVER = thaumcraft.api.ThaumcraftApi.armorMatSpecial;

        itemWandCasting = (ItemWandCasting) new ItemWandCasting()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("WandCasting"))
                .setTranslationKey("thaumcraft.wand_casting")
                .setCreativeTab(tab);
        allItems.add(itemWandCasting);

        itemWandRod = (ItemWandRod) new ItemWandRod()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("WandRod"))
                .setTranslationKey("thaumcraft.wand_rod")
                .setCreativeTab(tab);
        allItems.add(itemWandRod);

        itemWandCap = (ItemWandCap) new ItemWandCap()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("WandCap"))
                .setTranslationKey("thaumcraft.wand_cap")
                .setCreativeTab(tab);
        allItems.add(itemWandCap);

        itemFocusPouch = (ItemFocusPouch) new ItemFocusPouch()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusPouch"))
                .setTranslationKey("thaumcraft.focus_pouch")
                .setCreativeTab(tab);
        allItems.add(itemFocusPouch);

        itemFocusPouchBauble = (ItemFocusPouchBauble) new ItemFocusPouchBauble()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusPouchBauble"))
                .setTranslationKey("thaumcraft.focus_pouch_bauble")
                .setCreativeTab(tab);
        allItems.add(itemFocusPouchBauble);

        focusShock = (FocusShock) new FocusShock()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusShock"))
                .setTranslationKey("thaumcraft.focus_shock")
                .setCreativeTab(tab);
        allItems.add(focusShock);

        focusFire = (FocusFire) new FocusFire()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusFire"))
                .setTranslationKey("thaumcraft.focus_fire")
                .setCreativeTab(tab);
        allItems.add(focusFire);

        focusFrost = (FocusFrost) new FocusFrost()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusFrost"))
                .setTranslationKey("thaumcraft.focus_frost")
                .setCreativeTab(tab);
        allItems.add(focusFrost);

        focusExcavation = (FocusExcavation) new FocusExcavation()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusExcavation"))
                .setTranslationKey("thaumcraft.focus_excavation")
                .setCreativeTab(tab);
        allItems.add(focusExcavation);

        focusPrimal = (FocusPrimal) new FocusPrimal()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusPrimal"))
                .setTranslationKey("thaumcraft.focus_primal")
                .setCreativeTab(tab);
        allItems.add(focusPrimal);

        focusWarding = (FocusWarding) new FocusWarding()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusWarding"))
                .setTranslationKey("thaumcraft.focus_warding")
                .setCreativeTab(tab);
        allItems.add(focusWarding);

        focusHellbat = (FocusHellbat) new FocusHellbat()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusHellbat"))
                .setTranslationKey("thaumcraft.focus_hellbat")
                .setCreativeTab(tab);
        allItems.add(focusHellbat);

        focusPech = (FocusPech) new FocusPech()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusPech"))
                .setTranslationKey("thaumcraft.focus_pech")
                .setCreativeTab(tab);
        allItems.add(focusPech);

        focusTrade = (FocusTrade) new FocusTrade()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusTrade"))
                .setTranslationKey("thaumcraft.focus_trade")
                .setCreativeTab(tab);
        allItems.add(focusTrade);

        focusPortableHole = (FocusPortableHole) new FocusPortableHole()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("FocusPortableHole"))
                .setTranslationKey("thaumcraft.focus_portable_hole")
                .setCreativeTab(tab);
        allItems.add(focusPortableHole);

        itemShard = (ItemShard) new ItemShard()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemShard"))
                .setTranslationKey("thaumcraft.shard");
        allItems.add(itemShard);

        itemWispEssence = (ItemWispEssence) new ItemWispEssence()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemWispEssence"))
                .setTranslationKey("thaumcraft.wisp_essence");
        allItems.add(itemWispEssence);

        itemResource = (ItemResource) new ItemResource()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemResource"))
                .setTranslationKey("thaumcraft.resource")
                .setCreativeTab(tab);
        allItems.add(itemResource);
        configureRepairMaterials();

        itemEssence = (ItemEssence) new ItemEssence()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemEssence"))
                .setTranslationKey("thaumcraft.essence")
                .setCreativeTab(tab);
        allItems.add(itemEssence);

        itemCrystalEssence = (ItemCrystalEssence) new ItemCrystalEssence()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemCrystalEssence"))
                .setTranslationKey("thaumcraft.crystal_essence")
                .setCreativeTab(tab);
        allItems.add(itemCrystalEssence);

        itemNugget = (ItemNugget) new ItemNugget()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemNugget"))
                .setTranslationKey("thaumcraft.nugget")
                .setCreativeTab(tab);
        allItems.add(itemNugget);

        itemNuggetEdible = (ItemNuggetEdible) new ItemNuggetEdible()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemNuggetEdible"))
                .setTranslationKey("thaumcraft.nugget_edible")
                .setCreativeTab(tab);
        allItems.add(itemNuggetEdible);

        itemEldritchObject = (ItemEldritchObject) new ItemEldritchObject()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemEldritchObject"))
                .setTranslationKey("thaumcraft.eldritch_object")
                .setCreativeTab(tab);
        allItems.add(itemEldritchObject);

        itemLootBag = (ItemLootBag) new ItemLootBag()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLootBag"))
                .setTranslationKey("thaumcraft.loot_bag")
                .setCreativeTab(tab);
        allItems.add(itemLootBag);

        itemBottleTaint = (ItemBottleTaint) new ItemBottleTaint()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBottleTaint"))
                .setTranslationKey("thaumcraft.bottle_taint")
                .setCreativeTab(tab);
        allItems.add(itemBottleTaint);

        itemBucketDeath = (ItemBucketDeath) new ItemBucketDeath()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBucketDeath"))
                .setTranslationKey("thaumcraft.bucket_death")
                .setCreativeTab(tab);
        allItems.add(itemBucketDeath);

        itemBucketPure = (ItemBucketPure) new ItemBucketPure()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBucketPure"))
                .setTranslationKey("thaumcraft.bucket_pure")
                .setCreativeTab(tab);
        allItems.add(itemBucketPure);

        itemBathSalts = (ItemBathSalts) new ItemBathSalts()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBathSalts"))
                .setTranslationKey("thaumcraft.bath_salts")
                .setCreativeTab(tab);
        allItems.add(itemBathSalts);

        itemCompassStone = (ItemCompassStone) new ItemCompassStone()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemCompassStone"))
                .setTranslationKey("thaumcraft.compass_stone")
                .setCreativeTab(tab);
        allItems.add(itemCompassStone);

        itemInkwell = (ItemInkwell) new ItemInkwell()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemInkwell"))
                .setTranslationKey("thaumcraft.inkwell")
                .setCreativeTab(tab);
        allItems.add(itemInkwell);

        itemArcaneDoor = (ItemArcaneDoor) new ItemArcaneDoor()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemArcaneDoor"))
                .setTranslationKey("thaumcraft.arcane_door")
                .setCreativeTab(tab);
        allItems.add(itemArcaneDoor);

        itemKey = (ItemKey) new ItemKey()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ArcaneDoorKey"))
                .setTranslationKey("thaumcraft.key")
                .setCreativeTab(tab);
        allItems.add(itemKey);

        itemManaBean = (ItemManaBean) new ItemManaBean()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemManaBean"))
                .setTranslationKey("thaumcraft.mana_bean")
                .setCreativeTab(tab);
        allItems.add(itemManaBean);

        itemResearchNotes = (ItemResearchNotes) new ItemResearchNotes()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemResearchNotes"))
                .setTranslationKey("thaumcraft.research_notes")
                .setCreativeTab(tab);
        allItems.add(itemResearchNotes);

        itemSanitySoap = (ItemSanitySoap) new ItemSanitySoap()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSanitySoap"))
                .setTranslationKey("thaumcraft.sanity_soap")
                .setCreativeTab(tab);
        allItems.add(itemSanitySoap);

        itemTripleMeatTreat = (ItemTripleMeatTreat) new ItemTripleMeatTreat()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("TripleMeatTreat"))
                .setTranslationKey("thaumcraft.triple_meat_treat")
                .setCreativeTab(tab);
        allItems.add(itemTripleMeatTreat);

        itemZombieBrain = (ItemZombieBrain) new ItemZombieBrain()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemZombieBrain"))
                .setTranslationKey("thaumcraft.zombie_brain")
                .setCreativeTab(tab);
        allItems.add(itemZombieBrain);

        // Equipment
        itemSwordThaumium = (ItemThaumiumSword) new ItemThaumiumSword(TOOLMAT_THAUMIUM)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSwordThaumium"))
                .setTranslationKey("thaumcraft.sword_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemSwordThaumium);

        itemPickThaumium = (ItemThaumiumPickaxe) new ItemThaumiumPickaxe(TOOLMAT_THAUMIUM)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemPickThaumium"))
                .setTranslationKey("thaumcraft.pick_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemPickThaumium);

        itemAxeThaumium = (ItemThaumiumAxe) new ItemThaumiumAxe(TOOLMAT_THAUMIUM)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemAxeThaumium"))
                .setTranslationKey("thaumcraft.axe_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemAxeThaumium);

        itemShovelThaumium = (ItemThaumiumShovel) new ItemThaumiumShovel(TOOLMAT_THAUMIUM)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemShovelThaumium"))
                .setTranslationKey("thaumcraft.shovel_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemShovelThaumium);

        itemHoeThaumium = (ItemThaumiumHoe) new ItemThaumiumHoe(TOOLMAT_THAUMIUM)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHoeThaumium"))
                .setTranslationKey("thaumcraft.hoe_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemHoeThaumium);

        itemSwordVoid = (ItemVoidSword) new ItemVoidSword(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSwordVoid"))
                .setTranslationKey("thaumcraft.sword_void")
                .setCreativeTab(tab);
        allItems.add(itemSwordVoid);

        itemPickVoid = (ItemVoidPickaxe) new ItemVoidPickaxe(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemPickVoid"))
                .setTranslationKey("thaumcraft.pick_void")
                .setCreativeTab(tab);
        allItems.add(itemPickVoid);

        itemAxeVoid = (ItemVoidAxe) new ItemVoidAxe(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemAxeVoid"))
                .setTranslationKey("thaumcraft.axe_void")
                .setCreativeTab(tab);
        allItems.add(itemAxeVoid);

        itemShovelVoid = (ItemVoidShovel) new ItemVoidShovel(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemShovelVoid"))
                .setTranslationKey("thaumcraft.shovel_void")
                .setCreativeTab(tab);
        allItems.add(itemShovelVoid);

        itemHoeVoid = (ItemVoidHoe) new ItemVoidHoe(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHoeVoid"))
                .setTranslationKey("thaumcraft.hoe_void")
                .setCreativeTab(tab);
        allItems.add(itemHoeVoid);

        itemSwordElemental = (ItemElementalSword) new ItemElementalSword(TOOLMAT_ELEMENTAL)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSwordElemental"))
                .setTranslationKey("thaumcraft.sword_elemental")
                .setCreativeTab(tab);
        allItems.add(itemSwordElemental);

        itemPickElemental = (ItemElementalPickaxe) new ItemElementalPickaxe(TOOLMAT_ELEMENTAL)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemPickaxeElemental"))
                .setTranslationKey("thaumcraft.pick_elemental")
                .setCreativeTab(tab);
        allItems.add(itemPickElemental);

        itemAxeElemental = (ItemElementalAxe) new ItemElementalAxe(TOOLMAT_ELEMENTAL)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemAxeElemental"))
                .setTranslationKey("thaumcraft.axe_elemental")
                .setCreativeTab(tab);
        allItems.add(itemAxeElemental);

        itemShovelElemental = (ItemElementalShovel) new ItemElementalShovel(TOOLMAT_ELEMENTAL)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemShovelElemental"))
                .setTranslationKey("thaumcraft.shovel_elemental")
                .setCreativeTab(tab);
        allItems.add(itemShovelElemental);

        itemHoeElemental = (ItemElementalHoe) new ItemElementalHoe(TOOLMAT_ELEMENTAL)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHoeElemental"))
                .setTranslationKey("thaumcraft.hoe_elemental")
                .setCreativeTab(tab);
        allItems.add(itemHoeElemental);

        itemBowBone = (ItemBowBone) new ItemBowBone()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBowBone"))
                .setTranslationKey("thaumcraft.bow_bone")
                .setCreativeTab(tab);
        allItems.add(itemBowBone);

        itemCrimsonSword = (ItemCrimsonSword) new ItemCrimsonSword(TOOLMAT_VOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSwordCrimson"))
                .setTranslationKey("thaumcraft.crimson_sword")
                .setCreativeTab(tab);
        allItems.add(itemCrimsonSword);

        itemPrimalArrow = (ItemPrimalArrow) new ItemPrimalArrow()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("PrimalArrow"))
                .setTranslationKey("thaumcraft.primal_arrow")
                .setCreativeTab(tab);
        allItems.add(itemPrimalArrow);

        itemPrimalCrusher = (ItemPrimalCrusher) new ItemPrimalCrusher(TOOLMAT_PRIMALVOID)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemPrimalCrusher"))
                .setTranslationKey("thaumcraft.primal_crusher")
                .setCreativeTab(tab);
        allItems.add(itemPrimalCrusher);

        // Armor
        itemHelmThaumium = (ItemThaumiumArmor) new ItemThaumiumArmor(ARMOR_THAUMIUM, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetThaumium"))
                .setTranslationKey("thaumcraft.helm_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemHelmThaumium);

        itemChestThaumium = (ItemThaumiumArmor) new ItemThaumiumArmor(ARMOR_THAUMIUM, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateThaumium"))
                .setTranslationKey("thaumcraft.chest_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemChestThaumium);

        itemLegsThaumium = (ItemThaumiumArmor) new ItemThaumiumArmor(ARMOR_THAUMIUM, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsThaumium"))
                .setTranslationKey("thaumcraft.legs_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemLegsThaumium);

        itemBootsThaumium = (ItemThaumiumArmor) new ItemThaumiumArmor(ARMOR_THAUMIUM, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsThaumium"))
                .setTranslationKey("thaumcraft.boots_thaumium")
                .setCreativeTab(tab);
        allItems.add(itemBootsThaumium);

        itemHelmVoid = (ItemVoidArmor) new ItemVoidArmor(ARMOR_VOID, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetVoid"))
                .setTranslationKey("thaumcraft.helm_void")
                .setCreativeTab(tab);
        allItems.add(itemHelmVoid);

        itemChestVoid = (ItemVoidArmor) new ItemVoidArmor(ARMOR_VOID, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateVoid"))
                .setTranslationKey("thaumcraft.chest_void")
                .setCreativeTab(tab);
        allItems.add(itemChestVoid);

        itemLegsVoid = (ItemVoidArmor) new ItemVoidArmor(ARMOR_VOID, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsVoid"))
                .setTranslationKey("thaumcraft.legs_void")
                .setCreativeTab(tab);
        allItems.add(itemLegsVoid);

        itemBootsVoid = (ItemVoidArmor) new ItemVoidArmor(ARMOR_VOID, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsVoid"))
                .setTranslationKey("thaumcraft.boots_void")
                .setCreativeTab(tab);
        allItems.add(itemBootsVoid);

        itemHelmFortress = (ItemFortressArmor) new ItemFortressArmor(ARMOR_FORTRESS, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetFortress"))
                .setTranslationKey("thaumcraft.helm_fortress")
                .setCreativeTab(tab);
        allItems.add(itemHelmFortress);

        itemChestFortress = (ItemFortressArmor) new ItemFortressArmor(ARMOR_FORTRESS, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateFortress"))
                .setTranslationKey("thaumcraft.chest_fortress")
                .setCreativeTab(tab);
        allItems.add(itemChestFortress);

        itemLegsFortress = (ItemFortressArmor) new ItemFortressArmor(ARMOR_FORTRESS, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsFortress"))
                .setTranslationKey("thaumcraft.legs_fortress")
                .setCreativeTab(tab);
        allItems.add(itemLegsFortress);

        itemBootsFortress = (ItemFortressArmor) new ItemFortressArmor(ARMOR_FORTRESS, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsFortress"))
                .setTranslationKey("thaumcraft.boots_fortress")
                .setCreativeTab(tab);
        allItems.add(itemBootsFortress);

        itemHelmRobe = (ItemRobeArmor) new ItemRobeArmor(ARMOR_ROBE, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetRobe"))
                .setTranslationKey("thaumcraft.helm_robe")
                .setCreativeTab(tab);
        allItems.add(itemHelmRobe);

        itemChestRobe = (ItemRobeArmor) new ItemRobeArmor(ARMOR_ROBE, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateRobe"))
                .setTranslationKey("thaumcraft.chest_robe")
                .setCreativeTab(tab);
        allItems.add(itemChestRobe);

        itemLegsRobe = (ItemRobeArmor) new ItemRobeArmor(ARMOR_ROBE, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsRobe"))
                .setTranslationKey("thaumcraft.legs_robe")
                .setCreativeTab(tab);
        allItems.add(itemLegsRobe);

        itemBootsRobe = (ItemRobeArmor) new ItemRobeArmor(ARMOR_ROBE, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsRobe"))
                .setTranslationKey("thaumcraft.boots_robe")
                .setCreativeTab(tab);
        allItems.add(itemBootsRobe);

        itemHelmVoidRobe = (ItemVoidRobeArmor) new ItemVoidRobeArmor(ARMOR_VOID_ROBE, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetVoidFortress"))
                .setTranslationKey("thaumcraft.helm_void_robe")
                .setCreativeTab(tab);
        allItems.add(itemHelmVoidRobe);

        itemChestVoidRobe = (ItemVoidRobeArmor) new ItemVoidRobeArmor(ARMOR_VOID_ROBE, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateVoidFortress"))
                .setTranslationKey("thaumcraft.chest_void_robe")
                .setCreativeTab(tab);
        allItems.add(itemChestVoidRobe);

        itemLegsVoidRobe = (ItemVoidRobeArmor) new ItemVoidRobeArmor(ARMOR_VOID_ROBE, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsVoidFortress"))
                .setTranslationKey("thaumcraft.legs_void_robe")
                .setCreativeTab(tab);
        allItems.add(itemLegsVoidRobe);

        itemBootsVoidRobe = (ItemVoidRobeArmor) new ItemVoidRobeArmor(ARMOR_VOID_ROBE, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsVoidRobe"))
                .setTranslationKey("thaumcraft.boots_void_robe")
                .setCreativeTab(tab);
        allItems.add(itemBootsVoidRobe);

        itemHelmetCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetCultistRobe"))
                .setTranslationKey("thaumcraft.cultist_robe_hood")
                .setCreativeTab(tab);
        allItems.add(itemHelmetCultistRobe);

        itemChestCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateCultistRobe"))
                .setTranslationKey("thaumcraft.cultist_robe")
                .setCreativeTab(tab);
        allItems.add(itemChestCultistRobe);
        itemCultistRobe = itemChestCultistRobe;

        itemLegsCultistRobe = (ItemCultistRobeArmor) new ItemCultistRobeArmor(ARMOR_CULTIST, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsCultistRobe"))
                .setTranslationKey("thaumcraft.cultist_robe_legs")
                .setCreativeTab(tab);
        allItems.add(itemLegsCultistRobe);

        itemHelmetCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetCultistPlate"))
                .setTranslationKey("thaumcraft.cultist_plate_helm")
                .setCreativeTab(tab);
        allItems.add(itemHelmetCultistPlate);

        itemChestCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateCultistPlate"))
                .setTranslationKey("thaumcraft.cultist_plate")
                .setCreativeTab(tab);
        allItems.add(itemChestCultistPlate);
        itemCultistPlate = itemChestCultistPlate;

        itemLegsCultistPlate = (ItemCultistPlateArmor) new ItemCultistPlateArmor(ARMOR_CULTIST_PLATE, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsCultistPlate"))
                .setTranslationKey("thaumcraft.cultist_plate_legs")
                .setCreativeTab(tab);
        allItems.add(itemLegsCultistPlate);

        itemHelmetCultistLeader = (ItemCultistLeaderArmor) new ItemCultistLeaderArmor(ARMOR_CULTIST_LEADER, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemHelmetCultistLeaderPlate"))
                .setTranslationKey("thaumcraft.cultist_leader_helm")
                .setCreativeTab(tab);
        allItems.add(itemHelmetCultistLeader);

        itemChestCultistLeader = (ItemCultistLeaderArmor) new ItemCultistLeaderArmor(ARMOR_CULTIST_LEADER, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemChestplateCultistLeaderPlate"))
                .setTranslationKey("thaumcraft.cultist_leader")
                .setCreativeTab(tab);
        allItems.add(itemChestCultistLeader);
        itemCultistLeader = itemChestCultistLeader;

        itemLegsCultistLeader = (ItemCultistLeaderArmor) new ItemCultistLeaderArmor(ARMOR_CULTIST_LEADER, 0, EntityEquipmentSlot.LEGS)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemLeggingsCultistLeaderPlate"))
                .setTranslationKey("thaumcraft.cultist_leader_legs")
                .setCreativeTab(tab);
        allItems.add(itemLegsCultistLeader);

        itemCultistBoots = (ItemCultistBoots) new ItemCultistBoots(ARMOR_CULTIST_BOOTS, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBootsCultist"))
                .setTranslationKey("thaumcraft.cultist_boots")
                .setCreativeTab(tab);
        allItems.add(itemCultistBoots);

        itemGoggles = (ItemGoggles) new ItemGoggles(ARMOR_GOGGLES, 0, EntityEquipmentSlot.HEAD)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGoggles"))
                .setTranslationKey("thaumcraft.goggles")
                .setCreativeTab(tab);
        allItems.add(itemGoggles);

        itemBootsTraveller = (ItemBootsTraveller) new ItemBootsTraveller(ARMOR_TRAVELLER, 0, EntityEquipmentSlot.FEET)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("BootsTraveller"))
                .setTranslationKey("thaumcraft.boots_traveller")
                .setCreativeTab(tab);
        allItems.add(itemBootsTraveller);

        itemHoverHarness = (ItemHoverHarness) new ItemHoverHarness(ARMOR_HOVER, 0, EntityEquipmentSlot.CHEST)
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("HoverHarness"))
                .setTranslationKey("thaumcraft.hover_harness")
                .setCreativeTab(tab);
        allItems.add(itemHoverHarness);

        // Baubles
        itemRingRunic = (ItemRingRunic) new ItemRingRunic()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemRingRunic"))
                .setTranslationKey("thaumcraft.ring_runic")
                .setCreativeTab(tab);
        allItems.add(itemRingRunic);

        itemAmuletRunic = (ItemAmuletRunic) new ItemAmuletRunic()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemAmuletRunic"))
                .setTranslationKey("thaumcraft.amulet_runic")
                .setCreativeTab(tab);
        allItems.add(itemAmuletRunic);

        itemAmuletVis = (ItemAmuletVis) new ItemAmuletVis()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemAmuletVis"))
                .setTranslationKey("thaumcraft.amulet_vis")
                .setCreativeTab(tab);
        allItems.add(itemAmuletVis);

        itemGirdleRunic = (ItemGirdleRunic) new ItemGirdleRunic()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGirdleRunic"))
                .setTranslationKey("thaumcraft.girdle_runic")
                .setCreativeTab(tab);
        allItems.add(itemGirdleRunic);

        itemGirdleHover = (ItemGirdleHover) new ItemGirdleHover()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGirdleHover"))
                .setTranslationKey("thaumcraft.girdle_hover")
                .setCreativeTab(tab);
        allItems.add(itemGirdleHover);

        itemBaubleBlanks = (ItemBaubleBlanks) new ItemBaubleBlanks()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemBaubleBlanks"))
                .setTranslationKey("thaumcraft.bauble_blanks")
                .setCreativeTab(tab);
        allItems.add(itemBaubleBlanks);

        // Relics
        itemThaumometer = (ItemThaumometer) new ItemThaumometer()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemThaumometer"))
                .setTranslationKey("thaumcraft.thaumometer")
                .setCreativeTab(tab);
        allItems.add(itemThaumometer);

        itemThaumonomicon = (ItemThaumonomicon) new ItemThaumonomicon()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemThaumonomicon"))
                .setTranslationKey("thaumcraft.thaumonomicon")
                .setCreativeTab(tab);
        allItems.add(itemThaumonomicon);

        itemHandMirror = (ItemHandMirror) new ItemHandMirror()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("HandMirror"))
                .setTranslationKey("thaumcraft.hand_mirror")
                .setCreativeTab(tab);
        allItems.add(itemHandMirror);

        itemResonator = (ItemResonator) new ItemResonator()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemResonator"))
                .setTranslationKey("thaumcraft.resonator")
                .setCreativeTab(tab);
        allItems.add(itemResonator);

        itemSanityChecker = (ItemSanityChecker) new ItemSanityChecker()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemSanityChecker"))
                .setTranslationKey("thaumcraft.sanity_checker")
                .setCreativeTab(tab);
        allItems.add(itemSanityChecker);

        itemTrunkSpawner = new ItemTrunkSpawner()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("TrunkSpawner"))
                .setTranslationKey("TrunkSpawner")
                .setCreativeTab(tab);
        allItems.add(itemTrunkSpawner);

        itemGolemPlacer = new ItemGolemPlacer()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGolemPlacer"))
                .setTranslationKey("ItemGolemPlacer")
                .setCreativeTab(tab);
        allItems.add(itemGolemPlacer);

        itemGolemCore = new ItemGolemCore()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGolemCore"))
                .setTranslationKey("ItemGolemCore")
                .setCreativeTab(tab);
        allItems.add(itemGolemCore);

        itemGolemUpgrade = new ItemGolemUpgrade()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGolemUpgrade"))
                .setTranslationKey("ItemGolemUpgrade")
                .setCreativeTab(tab);
        allItems.add(itemGolemUpgrade);

        itemGolemBell = new ItemGolemBell()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("GolemBell"))
                .setTranslationKey("GolemBell")
                .setCreativeTab(tab);
        allItems.add(itemGolemBell);

        itemGolemDecoration = new ItemGolemDecoration()
                .setRegistryName("thaumcraft", ConfigBlocks.legacyPath("ItemGolemDecoration"))
                .setTranslationKey("ItemGolemDecoration")
                .setCreativeTab(tab);
        allItems.add(itemGolemDecoration);

        ThaumcraftSixCompatibility.initItemAliases();
        registerOreDictionary();
    }

    public static Item[] getAllItems() {
        return allItems.toArray(new Item[0]);
    }

    private static void configureRepairMaterials() {
        ItemStack thaumium = new ItemStack(itemResource, 1, ItemResource.META_THAUMIUM_INGOT);
        ItemStack voidIngot = new ItemStack(itemResource, 1, ItemResource.META_VOID_INGOT);
        ItemStack cloth = new ItemStack(itemResource, 1, ItemResource.META_CLOTH);

        setRepairItem(TOOLMAT_THAUMIUM, thaumium);
        setRepairItem(TOOLMAT_ELEMENTAL, thaumium);
        setRepairItem(TOOLMAT_VOID, voidIngot);
        setRepairItem(TOOLMAT_PRIMALVOID, new ItemStack(itemResource, 1, ItemResource.META_CHARM));

        setRepairItem(ARMOR_THAUMIUM, thaumium);
        setRepairItem(ARMOR_FORTRESS, thaumium);
        setRepairItem(ARMOR_VOID, voidIngot);
        setRepairItem(ARMOR_VOID_ROBE, voidIngot);
        setRepairItem(ARMOR_ROBE, cloth);
        setRepairItem(ARMOR_CULTIST, cloth);
        setRepairItem(ARMOR_CULTIST_PLATE, thaumium);
        setRepairItem(ARMOR_CULTIST_LEADER, voidIngot);
        setRepairItem(ARMOR_CULTIST_BOOTS, cloth);
        setRepairItem(ARMOR_GOGGLES, cloth);
        setRepairItem(ARMOR_TRAVELLER, cloth);
        setRepairItem(ARMOR_HOVER, cloth);
    }

    private static void setRepairItem(ToolMaterial material, ItemStack repairItem) {
        ItemStack current = material.getRepairItemStack();
        if (current.isEmpty()) {
            material.setRepairItem(repairItem);
            return;
        }
        if (!ItemStack.areItemsEqual(current, repairItem)) {
            throw new IllegalStateException("Conflicting repair item for tool material " + material + ": " + current + " vs " + repairItem);
        }
    }

    private static void setRepairItem(ArmorMaterial material, ItemStack repairItem) {
        ItemStack current = material.getRepairItemStack();
        if (current.isEmpty()) {
            material.setRepairItem(repairItem);
            return;
        }
        if (!ItemStack.areItemsEqual(current, repairItem)) {
            throw new IllegalStateException("Conflicting repair item for armor material " + material + ": " + current + " vs " + repairItem);
        }
    }

    private static void registerOreDictionary() {
        OreDictionary.registerOre("oreCinnabar", new ItemStack(ConfigBlocks.blockCustomOre, 1, 0));
        OreDictionary.registerOre("oreInfusedAir", new ItemStack(ConfigBlocks.blockCustomOre, 1, 1));
        OreDictionary.registerOre("oreInfusedFire", new ItemStack(ConfigBlocks.blockCustomOre, 1, 2));
        OreDictionary.registerOre("oreInfusedWater", new ItemStack(ConfigBlocks.blockCustomOre, 1, 3));
        OreDictionary.registerOre("oreInfusedEarth", new ItemStack(ConfigBlocks.blockCustomOre, 1, 4));
        OreDictionary.registerOre("oreInfusedOrder", new ItemStack(ConfigBlocks.blockCustomOre, 1, 5));
        OreDictionary.registerOre("oreInfusedEntropy", new ItemStack(ConfigBlocks.blockCustomOre, 1, 6));
        OreDictionary.registerOre("oreAmber", new ItemStack(ConfigBlocks.blockCustomOre, 1, 7));

        OreDictionary.registerOre("shardAir", new ItemStack(itemShard, 1, 0));
        OreDictionary.registerOre("shardFire", new ItemStack(itemShard, 1, 1));
        OreDictionary.registerOre("shardWater", new ItemStack(itemShard, 1, 2));
        OreDictionary.registerOre("shardEarth", new ItemStack(itemShard, 1, 3));
        OreDictionary.registerOre("shardOrder", new ItemStack(itemShard, 1, 4));
        OreDictionary.registerOre("shardEntropy", new ItemStack(itemShard, 1, 5));

        OreDictionary.registerOre("quicksilver", new ItemStack(itemResource, 1, 3));
        OreDictionary.registerOre("gemAmber", new ItemStack(itemResource, 1, 6));
        OreDictionary.registerOre("nuggetQuicksilver", new ItemStack(itemResource, 1, 3));
        OreDictionary.registerOre("nuggetGold", new ItemStack(itemResource, 1, 18));
        OreDictionary.registerOre("ingotThaumium", new ItemStack(itemResource, 1, 2));
        OreDictionary.registerOre("ingotVoid", new ItemStack(itemResource, 1, 16));

        OreDictionary.registerOre("nuggetIron", new ItemStack(itemNugget, 1, 0));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(itemNugget, 1, 1));
        OreDictionary.registerOre("nuggetTin", new ItemStack(itemNugget, 1, 2));
        OreDictionary.registerOre("nuggetSilver", new ItemStack(itemNugget, 1, 3));
        OreDictionary.registerOre("nuggetLead", new ItemStack(itemNugget, 1, 4));
        OreDictionary.registerOre("nuggetThaumium", new ItemStack(itemNugget, 1, 6));
        OreDictionary.registerOre("nuggetVoid", new ItemStack(itemNugget, 1, 7));

        OreDictionary.registerOre("clusterIron", new ItemStack(itemNugget, 1, 16));
        OreDictionary.registerOre("clusterCopper", new ItemStack(itemNugget, 1, 17));
        OreDictionary.registerOre("clusterTin", new ItemStack(itemNugget, 1, 18));
        OreDictionary.registerOre("clusterSilver", new ItemStack(itemNugget, 1, 19));
        OreDictionary.registerOre("clusterLead", new ItemStack(itemNugget, 1, 20));
        OreDictionary.registerOre("clusterCinnabar", new ItemStack(itemNugget, 1, 21));
        OreDictionary.registerOre("clusterGold", new ItemStack(itemNugget, 1, 31));

        OreDictionary.registerOre("logWood", new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));
        OreDictionary.registerOre("logWood", new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1));
        OreDictionary.registerOre("plankWood", new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6));
        OreDictionary.registerOre("plankWood", new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7));
        OreDictionary.registerOre("slabWood", new ItemStack(ConfigBlocks.blockSlabWood, 1, 0));
        OreDictionary.registerOre("slabWood", new ItemStack(ConfigBlocks.blockSlabWood, 1, 1));
        OreDictionary.registerOre("treeSapling", new ItemStack(ConfigBlocks.blockCustomPlant, 1, 0));
        OreDictionary.registerOre("treeSapling", new ItemStack(ConfigBlocks.blockCustomPlant, 1, 1));
    }
}
