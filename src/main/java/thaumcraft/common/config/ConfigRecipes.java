package thaumcraft.common.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.recipes.ConfigRecipesArcaneSlice;
import thaumcraft.common.config.recipes.ConfigRecipesCrucibleSlice;
import thaumcraft.common.config.recipes.ConfigRecipesInfusionSlice;
import thaumcraft.common.config.recipes.ConfigRecipesSpecialSlice;
import thaumcraft.common.config.recipes.ConfigRecipesSmeltingSlice;
import thaumcraft.common.config.research.ConfigResearch;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;
import thaumcraft.common.lib.crafting.ShapelessNBTOreRecipe;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;

public class ConfigRecipes {
    private static boolean specialRecipesRegistered = false;
    private static boolean recipesInitialized = false;
    private static final String[] DYES = new String[]{
            "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown",
            "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray",
            "dyeGray", "dyePink", "dyeLime", "dyeYellow",
            "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"
    };
    private static final Map<String, IRecipe> specialResearchRecipeHandles = new LinkedHashMap<String, IRecipe>();
    private static final List<IRecipe> recipeJarLabelAspects = new ArrayList<IRecipe>();
    private static IRecipe recipeArcaneStone2;
    private static IRecipe recipeArcaneStone3;
    private static IRecipe recipeArcaneStone4;
    private static IRecipe recipeKnowFrag;
    private static IRecipe recipePlankGreatwood;
    private static IRecipe recipePlankSilverwood;
    private static IRecipe recipeGrate;
    private static IRecipe recipePhial;
    private static IRecipe recipeTable;
    private static IRecipe recipeScribe1;
    private static IRecipe recipeScribe2;
    private static IRecipe recipeScribe3;
    private static IRecipe recipeThaumometer;
    private static IRecipe recipeWandCapIron;
    private static IRecipe recipeWandBasic;
    private static IRecipe recipeMundaneAmulet;
    private static IRecipe recipeMundaneRing;
    private static IRecipe recipeMundaneBelt;
    private static IRecipe recipeBlockFlesh;
    private static IRecipe recipeBlockTallow;
    private static final IRecipe[] recipeClusters = new IRecipe[7];
    private static CrucibleRecipe recipeNitor;
    private static CrucibleRecipe recipeAlumentum;
    private static CrucibleRecipe recipeThaumium;
    private static CrucibleRecipe recipeVoidMetal;
    private static CrucibleRecipe recipeVoidSeed;

    public static void init() {
        if (recipesInitialized) {
            return;
        }
        ConfigResearch.recipes.clear();
        initializeArcaneRecipeBaseline();
        insertDynamicArcaneRecipes(ThaumcraftApi.getCraftingRecipes(), ConfigResearch.recipes.get("JarVoid"));
        initializeInfusionWandRecipeBaseline();
        initializeInfusionEnchantmentRecipeBaseline();
        initializeInfusionFocusDeviceRecipeBaseline();
        initializeInfusionGolemDeviceRecipeBaseline();
        initializeInfusionEquipmentArmorRecipeBaseline();
        initializeCrucibleRecipeBaseline();
        ConfigRecipesSmeltingSlice.initializeSmeltingBaseline();
        ConfigRecipesSmeltingSlice.initializeSmeltingBonusBaseline();
        if (recipeArcaneStone2 != null) {
            ConfigResearch.recipes.put("ArcaneStone2", recipeArcaneStone2);
        }
        if (recipeArcaneStone3 != null) {
            ConfigResearch.recipes.put("ArcaneStone3", recipeArcaneStone3);
        }
        if (recipeArcaneStone4 != null) {
            ConfigResearch.recipes.put("ArcaneStone4", recipeArcaneStone4);
        }
        if (recipeKnowFrag != null) {
            ConfigResearch.recipes.put("KnowFrag", recipeKnowFrag);
        }
        if (recipePlankGreatwood != null) {
            ConfigResearch.recipes.put("PlankGreatwood", recipePlankGreatwood);
        }
        if (recipePlankSilverwood != null) {
            ConfigResearch.recipes.put("PlankSilverwood", recipePlankSilverwood);
        }
        if (recipeGrate != null) {
            ConfigResearch.recipes.put("Grate", recipeGrate);
        }
        if (recipePhial != null) {
            ConfigResearch.recipes.put("Phial", recipePhial);
        }
        if (recipeTable != null) {
            ConfigResearch.recipes.put("Table", recipeTable);
        }
        if (recipeScribe1 != null) {
            ConfigResearch.recipes.put("Scribe1", recipeScribe1);
        }
        if (recipeScribe2 != null) {
            ConfigResearch.recipes.put("Scribe2", recipeScribe2);
        }
        if (recipeScribe3 != null) {
            ConfigResearch.recipes.put("Scribe3", recipeScribe3);
        }
        if (recipeThaumometer != null) {
            ConfigResearch.recipes.put("Thaumometer", recipeThaumometer);
        }
        if (recipeWandCapIron != null) {
            ConfigResearch.recipes.put("WandCapIron", recipeWandCapIron);
        }
        if (recipeWandBasic != null) {
            ConfigResearch.recipes.put("WandBasic", recipeWandBasic);
        }
        if (recipeNitor != null) {
            ConfigResearch.recipes.put("Nitor", recipeNitor);
        }
        if (recipeAlumentum != null) {
            ConfigResearch.recipes.put("Alumentum", recipeAlumentum);
        }
        if (recipeThaumium != null) {
            ConfigResearch.recipes.put("Thaumium", recipeThaumium);
        }
        if (recipeVoidMetal != null) {
            ConfigResearch.recipes.put("VoidMetal", recipeVoidMetal);
        }
        if (recipeVoidSeed != null) {
            ConfigResearch.recipes.put("VoidSeed", recipeVoidSeed);
        }
        ItemStack basicWand = new ItemStack(ConfigItems.itemWandCasting, 1, 0);
        ConfigResearch.recipes.put("Thaumonomicon",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(Blocks.BOOKSHELF))));
        ConfigResearch.recipes.put("ArcTable",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(ConfigBlocks.blockTable))));
        ConfigResearch.recipes.put("ResTable",
                Arrays.asList(new AspectList(), 1, 2, 2,
                        Arrays.asList(null, new ItemStack(ConfigItems.itemInkwell),
                                new ItemStack(ConfigBlocks.blockTable), new ItemStack(ConfigBlocks.blockTable))));
        ConfigResearch.recipes.put("Crucible",
                Arrays.asList(new AspectList(), 1, 2, 1,
                        Arrays.asList(basicWand, new ItemStack(Items.CAULDRON))));
        ConfigResearch.recipes.put("InfernalFurnace",
                Arrays.asList(
                        new AspectList().add(Aspect.FIRE, 50).add(Aspect.EARTH, 50),
                        3,
                        3,
                        3,
                        Arrays.asList(
                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK),
                                new ItemStack(Blocks.OBSIDIAN), ItemStack.EMPTY, new ItemStack(Blocks.OBSIDIAN),
                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK),

                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK),
                                new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.LAVA), new ItemStack(Blocks.IRON_BARS),
                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK),

                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK),
                                new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.OBSIDIAN),
                                new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.NETHER_BRICK))));
        ConfigResearch.recipes.put("InfusionAltar",
                Arrays.asList(
                        new AspectList().add(Aspect.FIRE, 25).add(Aspect.EARTH, 25).add(Aspect.ORDER, 25)
                                .add(Aspect.AIR, 25).add(Aspect.ENTROPY, 25).add(Aspect.WATER, 25),
                        3,
                        3,
                        3,
                        Arrays.asList(
                                ItemStack.EMPTY, null, ItemStack.EMPTY,
                                null, new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), null,
                                ItemStack.EMPTY, null, ItemStack.EMPTY,

                                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                                null, null, null,
                                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),

                                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7),
                                null, new ItemStack(ConfigBlocks.blockStoneDevice, 1, 1), null,
                                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7))));
        ConfigResearch.recipes.put("NodeJar",
                Arrays.asList(
                        new AspectList().add(Aspect.FIRE, 70).add(Aspect.EARTH, 70).add(Aspect.AIR, 70)
                                .add(Aspect.WATER, 70).add(Aspect.ORDER, 70).add(Aspect.ENTROPY, 70),
                        3,
                        4,
                        3,
                        Arrays.asList(
                                "slabWood", "slabWood", "slabWood",
                                "slabWood", "slabWood", "slabWood",
                                "slabWood", "slabWood", "slabWood",

                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),

                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(ConfigBlocks.blockAiry, 1, 5), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),

                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS),
                                new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.GLASS))));
        ConfigResearch.recipes.put("Thaumatorium",
                Arrays.asList(
                        new AspectList().add(Aspect.FIRE, 15).add(Aspect.ORDER, 30).add(Aspect.WATER, 30),
                        1,
                        3,
                        1,
                        Arrays.asList(
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 0))));
        ConfigResearch.recipes.put("AdvAlchemyFurnace",
                Arrays.asList(
                        new AspectList().add(Aspect.FIRE, 50).add(Aspect.WATER, 50).add(Aspect.ORDER, 50),
                        3,
                        2,
                        3,
                        Arrays.asList(
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9), ItemStack.EMPTY, new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 9), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 1),

                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3), new ItemStack(ConfigBlocks.blockStoneDevice, 1, 0), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3), new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3))));
        if (Thaumcraft.proxy != null && Thaumcraft.proxy.wandManager != null) {
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 0, Blocks.BOOKSHELF, 0, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 1, Blocks.CAULDRON, -1, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.OBSIDIAN, -1, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.NETHER_BRICK, -1, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.IRON_BARS, -1, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 3, ConfigBlocks.blockStoneDevice, 2, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 4, Blocks.GLASS, -1, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 5, ConfigBlocks.blockMetalDevice, 9, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 6, ConfigBlocks.blockEldritch, 0, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 7, ConfigBlocks.blockMetalDevice, 3, "Thaumcraft");
            WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 7, ConfigBlocks.blockMetalDevice, 9, "Thaumcraft_2");
        }
        for (int a = 0; a < 6; a++) {
            if (recipeClusters[a] != null) {
                ConfigResearch.recipes.put("Clusters" + a, recipeClusters[a]);
            } else {
                ConfigResearch.recipes.put("Clusters" + a,
                        new ShapelessOreRecipe(
                                null,
                                new ItemStack(ConfigBlocks.blockCrystal, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a)));
            }
        }
        if (recipeClusters[6] != null) {
            ConfigResearch.recipes.put("Clusters6", recipeClusters[6]);
        } else {
            ConfigResearch.recipes.put("Clusters6",
                    new ShapelessOreRecipe(
                            null,
                            new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                            new ItemStack(ConfigItems.itemShard, 1, 0),
                            new ItemStack(ConfigItems.itemShard, 1, 1),
                            new ItemStack(ConfigItems.itemShard, 1, 2),
                            new ItemStack(ConfigItems.itemShard, 1, 3),
                            new ItemStack(ConfigItems.itemShard, 1, 4),
                            new ItemStack(ConfigItems.itemShard, 1, 5)));
        }
        if (recipeMundaneAmulet != null) {
            ConfigResearch.recipes.put("MundaneAmulet", recipeMundaneAmulet);
        } else {
            ConfigResearch.recipes.put("MundaneAmulet",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                            new Object[]{
                                    " S ",
                                    "S S",
                                    " I ",
                                    'S', new ItemStack(Items.STRING),
                                    'I', new ItemStack(Items.IRON_INGOT)
                            }));
        }
        if (recipeMundaneRing != null) {
            ConfigResearch.recipes.put("MundaneRing", recipeMundaneRing);
        } else {
            ConfigResearch.recipes.put("MundaneRing",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
                            new Object[]{
                                    " N ",
                                    "N N",
                                    " N ",
                                    'N', new ItemStack(Items.GOLD_NUGGET)
                            }));
        }
        if (recipeMundaneBelt != null) {
            ConfigResearch.recipes.put("MundaneBelt", recipeMundaneBelt);
        } else {
            ConfigResearch.recipes.put("MundaneBelt",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                            new Object[]{
                                    " L ",
                                    "L L",
                                    " I ",
                                    'L', new ItemStack(Items.LEATHER),
                                    'I', new ItemStack(Items.IRON_INGOT)
                            }));
        }
        if (recipeBlockFlesh != null) {
            ConfigResearch.recipes.put("BlockFlesh", recipeBlockFlesh);
        } else {
            ConfigResearch.recipes.put("BlockFlesh",
                    oreDictRecipe(
                            new ItemStack(ConfigBlocks.blockTaint, 1, 2),
                            new Object[]{
                                    "KKK",
                                    "KKK",
                                    "KKK",
                                    'K', new ItemStack(Items.ROTTEN_FLESH)
                            }));
        }
        if (recipeBlockTallow != null) {
            ConfigResearch.recipes.put("BlockTallow", recipeBlockTallow);
        } else {
            ConfigResearch.recipes.put("BlockTallow",
                    oreDictRecipe(
                            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5),
                            new Object[]{
                                    "KKK",
                                    "KKK",
                                    "KKK",
                                    'K', new ItemStack(ConfigItems.itemResource, 1, 4)
                            }));
        }
        for (Map.Entry<String, IRecipe> entry : specialResearchRecipeHandles.entrySet()) {
            if (entry.getValue() != null) {
                ConfigResearch.recipes.put(entry.getKey(), entry.getValue());
            }
        }
        for (int i = 0; i < recipeJarLabelAspects.size(); i++) {
            IRecipe recipe = recipeJarLabelAspects.get(i);
            if (recipe != null) {
                ConfigResearch.recipes.put("JarLabel" + i, recipe);
            }
        }
        if (!containsExactRecipeClass(ThaumcraftApi.getCraftingRecipes(), InfusionRunicAugmentRecipe.class)) {
            ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());
        }
        recipesInitialized = true;
    }

    static void insertDynamicArcaneRecipes(List recipes, Object jarVoid) {
        int insertionIndex = recipes.indexOf(jarVoid);
        insertionIndex = insertionIndex < 0 ? recipes.size() : insertionIndex + 1;
        insertionIndex = insertDynamicRecipeIfMissing(
                recipes, insertionIndex, ArcaneWandRecipe.class, new ArcaneWandRecipe());
        insertDynamicRecipeIfMissing(
                recipes, insertionIndex, ArcaneSceptreRecipe.class, new ArcaneSceptreRecipe());
    }

    private static int insertDynamicRecipeIfMissing(List recipes, int insertionIndex,
                                                     Class<?> recipeClass, Object recipeToInsert) {
        for (int i = 0; i < recipes.size(); ++i) {
            Object recipe = recipes.get(i);
            if (recipe != null && recipe.getClass() == recipeClass) {
                return i == insertionIndex ? insertionIndex + 1 : insertionIndex;
            }
        }
        recipes.add(insertionIndex, recipeToInsert);
        return insertionIndex + 1;
    }

    private static boolean containsExactRecipeClass(List recipes, Class<?> recipeClass) {
        for (Object recipe : recipes) {
            if (recipe != null && recipe.getClass() == recipeClass) {
                return true;
            }
        }
        return false;
    }

    private static void initializeCrucibleRecipeBaseline() {
        ConfigRecipesCrucibleSlice.CrucibleRecipeHandles handles = ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline();
        recipeNitor = handles.recipeNitor;
        recipeAlumentum = handles.recipeAlumentum;
        recipeThaumium = handles.recipeThaumium;
        recipeVoidMetal = handles.recipeVoidMetal;
        recipeVoidSeed = handles.recipeVoidSeed;
    }

    private static void initializeArcaneRecipeBaseline() {
        ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline();
    }

    private static void initializeInfusionWandRecipeBaseline() {
        ConfigRecipesInfusionSlice.initializeInfusionWandRecipeBaseline();
    }

    private static void initializeInfusionEnchantmentRecipeBaseline() {
        ConfigRecipesInfusionSlice.initializeInfusionEnchantmentRecipeBaseline();
    }

    private static void initializeInfusionFocusDeviceRecipeBaseline() {
        ConfigRecipesInfusionSlice.initializeInfusionFocusDeviceRecipeBaseline();
    }

    private static void initializeInfusionGolemDeviceRecipeBaseline() {
        ConfigRecipesInfusionSlice.initializeInfusionGolemDeviceRecipeBaseline();
    }

    private static void initializeInfusionEquipmentArmorRecipeBaseline() {
        ConfigRecipesInfusionSlice.initializeInfusionEquipmentArmorRecipeBaseline();
    }

    private static void registerInfusionRecipe(String key, String research, Object output, int instability, AspectList aspects,
                                               ItemStack centralInput, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionCraftingRecipe(
                research,
                output,
                instability,
                aspects,
                centralInput,
                components));
    }

    private static void registerInfusionEnchantmentRecipe(String key, String research, Enchantment enchantment, int instability,
                                                          AspectList aspects, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionEnchantmentRecipe(
                research,
                enchantment,
                instability,
                aspects,
                components));
    }

    public static IRecipe oreDictRecipe(ItemStack output, Object[] recipe) {
        return new ShapedOreRecipe(null, output, recipe);
    }

    public static IRecipe shapelessOreDictRecipe(ItemStack output, Object[] recipe) {
        return new ShapelessOreRecipe(null, output, recipe);
    }

    public static IRecipe shapelessNBTOreRecipe(ItemStack output, Object[] recipe) {
        return new ShapelessNBTOreRecipe(output, recipe);
    }

    public static void registerSpecialRecipes(IForgeRegistry<IRecipe> registry) {
        ConfigRecipesSpecialSlice.registerSpecialRecipes(registry, new SpecialRecipesBridge());
        refreshLateBoundResearchRecipeHandles();
    }

    private static void refreshLateBoundResearchRecipeHandles() {
        if (recipeArcaneStone2 != null) {
            ConfigResearch.recipes.put("ArcaneStone2", recipeArcaneStone2);
        }
        if (recipeArcaneStone3 != null) {
            ConfigResearch.recipes.put("ArcaneStone3", recipeArcaneStone3);
        }
        if (recipeArcaneStone4 != null) {
            ConfigResearch.recipes.put("ArcaneStone4", recipeArcaneStone4);
        }
        if (recipeKnowFrag != null) {
            ConfigResearch.recipes.put("KnowFrag", recipeKnowFrag);
        }
        if (recipePlankGreatwood != null) {
            ConfigResearch.recipes.put("PlankGreatwood", recipePlankGreatwood);
        }
        if (recipePlankSilverwood != null) {
            ConfigResearch.recipes.put("PlankSilverwood", recipePlankSilverwood);
        }
        if (recipeGrate != null) {
            ConfigResearch.recipes.put("Grate", recipeGrate);
        }
        if (recipePhial != null) {
            ConfigResearch.recipes.put("Phial", recipePhial);
        }
        if (recipeTable != null) {
            ConfigResearch.recipes.put("Table", recipeTable);
        }
        if (recipeScribe1 != null) {
            ConfigResearch.recipes.put("Scribe1", recipeScribe1);
        }
        if (recipeScribe2 != null) {
            ConfigResearch.recipes.put("Scribe2", recipeScribe2);
        }
        if (recipeScribe3 != null) {
            ConfigResearch.recipes.put("Scribe3", recipeScribe3);
        }
        if (recipeThaumometer != null) {
            ConfigResearch.recipes.put("Thaumometer", recipeThaumometer);
        }
        if (recipeWandCapIron != null) {
            ConfigResearch.recipes.put("WandCapIron", recipeWandCapIron);
        }
        if (recipeWandBasic != null) {
            ConfigResearch.recipes.put("WandBasic", recipeWandBasic);
        }
        if (recipeNitor != null) {
            ConfigResearch.recipes.put("Nitor", recipeNitor);
        }
        if (recipeAlumentum != null) {
            ConfigResearch.recipes.put("Alumentum", recipeAlumentum);
        }
        if (recipeThaumium != null) {
            ConfigResearch.recipes.put("Thaumium", recipeThaumium);
        }
        if (recipeVoidMetal != null) {
            ConfigResearch.recipes.put("VoidMetal", recipeVoidMetal);
        }
        if (recipeVoidSeed != null) {
            ConfigResearch.recipes.put("VoidSeed", recipeVoidSeed);
        }
        for (int a = 0; a < 6; a++) {
            if (recipeClusters[a] != null) {
                ConfigResearch.recipes.put("Clusters" + a, recipeClusters[a]);
            } else {
                ConfigResearch.recipes.put("Clusters" + a,
                        new ShapelessOreRecipe(
                                null,
                                new ItemStack(ConfigBlocks.blockCrystal, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a),
                                new ItemStack(ConfigItems.itemShard, 1, a)));
            }
        }
        if (recipeClusters[6] != null) {
            ConfigResearch.recipes.put("Clusters6", recipeClusters[6]);
        } else {
            ConfigResearch.recipes.put("Clusters6",
                    new ShapelessOreRecipe(
                            null,
                            new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                            new ItemStack(ConfigItems.itemShard, 1, 0),
                            new ItemStack(ConfigItems.itemShard, 1, 1),
                            new ItemStack(ConfigItems.itemShard, 1, 2),
                            new ItemStack(ConfigItems.itemShard, 1, 3),
                            new ItemStack(ConfigItems.itemShard, 1, 4),
                            new ItemStack(ConfigItems.itemShard, 1, 5)));
        }
        if (recipeMundaneAmulet != null) {
            ConfigResearch.recipes.put("MundaneAmulet", recipeMundaneAmulet);
        } else {
            ConfigResearch.recipes.put("MundaneAmulet",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                            new Object[]{
                                    " S ",
                                    "S S",
                                    " I ",
                                    'S', new ItemStack(Items.STRING),
                                    'I', new ItemStack(Items.IRON_INGOT)
                            }));
        }
        if (recipeMundaneRing != null) {
            ConfigResearch.recipes.put("MundaneRing", recipeMundaneRing);
        } else {
            ConfigResearch.recipes.put("MundaneRing",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
                            new Object[]{
                                    " N ",
                                    "N N",
                                    " N ",
                                    'N', new ItemStack(Items.GOLD_NUGGET)
                            }));
        }
        if (recipeMundaneBelt != null) {
            ConfigResearch.recipes.put("MundaneBelt", recipeMundaneBelt);
        } else {
            ConfigResearch.recipes.put("MundaneBelt",
                    oreDictRecipe(
                            new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                            new Object[]{
                                    " L ",
                                    "L L",
                                    " I ",
                                    'L', new ItemStack(Items.LEATHER),
                                    'I', new ItemStack(Items.IRON_INGOT)
                            }));
        }
        if (recipeBlockFlesh != null) {
            ConfigResearch.recipes.put("BlockFlesh", recipeBlockFlesh);
        } else {
            ConfigResearch.recipes.put("BlockFlesh",
                    oreDictRecipe(
                            new ItemStack(ConfigBlocks.blockTaint, 1, 2),
                            new Object[]{
                                    "KKK",
                                    "KKK",
                                    "KKK",
                                    'K', new ItemStack(Items.ROTTEN_FLESH)
                            }));
        }
        if (recipeBlockTallow != null) {
            ConfigResearch.recipes.put("BlockTallow", recipeBlockTallow);
        } else {
            ConfigResearch.recipes.put("BlockTallow",
                    oreDictRecipe(
                            new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5),
                            new Object[]{
                                    "KKK",
                                    "KKK",
                                    "KKK",
                                    'K', new ItemStack(ConfigItems.itemResource, 1, 4)
                            }));
        }
        for (Map.Entry<String, IRecipe> entry : specialResearchRecipeHandles.entrySet()) {
            if (entry.getValue() != null) {
                ConfigResearch.recipes.put(entry.getKey(), entry.getValue());
            }
        }
        for (int i = 0; i < recipeJarLabelAspects.size(); i++) {
            IRecipe recipe = recipeJarLabelAspects.get(i);
            if (recipe != null) {
                ConfigResearch.recipes.put("JarLabel" + i, recipe);
            }
        }
    }

    public static final class SpecialRecipesBridge {
        public boolean areSpecialRecipesRegistered() { return specialRecipesRegistered; }
        public void clearSpecialResearchRecipeHandles() { specialResearchRecipeHandles.clear(); }
        public void clearRecipeJarLabelAspects() { recipeJarLabelAspects.clear(); }
        public void setRecipeArcaneStone2(IRecipe recipe) { recipeArcaneStone2 = recipe; }
        public void setRecipeArcaneStone3(IRecipe recipe) { recipeArcaneStone3 = recipe; }
        public void setRecipeArcaneStone4(IRecipe recipe) { recipeArcaneStone4 = recipe; }
        public void setRecipeKnowFrag(IRecipe recipe) { recipeKnowFrag = recipe; }
        public void setRecipePlankGreatwood(IRecipe recipe) { recipePlankGreatwood = recipe; }
        public void setRecipePlankSilverwood(IRecipe recipe) { recipePlankSilverwood = recipe; }
        public void setRecipeGrate(IRecipe recipe) { recipeGrate = recipe; }
        public void setRecipePhial(IRecipe recipe) { recipePhial = recipe; }
        public void setRecipeTable(IRecipe recipe) { recipeTable = recipe; }
        public void setRecipeScribe1(IRecipe recipe) { recipeScribe1 = recipe; }
        public void setRecipeScribe2(IRecipe recipe) { recipeScribe2 = recipe; }
        public void setRecipeScribe3(IRecipe recipe) { recipeScribe3 = recipe; }
        public void setRecipeThaumometer(IRecipe recipe) { recipeThaumometer = recipe; }
        public void setRecipeWandCapIron(IRecipe recipe) { recipeWandCapIron = recipe; }
        public void setRecipeWandBasic(IRecipe recipe) { recipeWandBasic = recipe; }
        public void setRecipeMundaneAmulet(IRecipe recipe) { recipeMundaneAmulet = recipe; }
        public void setRecipeMundaneRing(IRecipe recipe) { recipeMundaneRing = recipe; }
        public void setRecipeMundaneBelt(IRecipe recipe) { recipeMundaneBelt = recipe; }
        public void setRecipeBlockFlesh(IRecipe recipe) { recipeBlockFlesh = recipe; }
        public void setRecipeBlockTallow(IRecipe recipe) { recipeBlockTallow = recipe; }
        public void addSpecialResearchRecipeHandle(String key, IRecipe recipe) { specialResearchRecipeHandles.put(key, recipe); }
        public void addRecipeJarLabelAspect(IRecipe recipe) { recipeJarLabelAspects.add(recipe); }
        public void setRecipeCluster(int index, IRecipe recipe) { recipeClusters[index] = recipe; }
        public void registerCompatNuggetRecipes(IForgeRegistry<IRecipe> registry, String metalName, String ingotOreDictName,
                                                int compatNuggetMeta, int nativeNuggetMeta) {
            ConfigRecipes.registerCompatNuggetRecipes(registry, metalName, ingotOreDictName, compatNuggetMeta, nativeNuggetMeta);
        }
        public void markSpecialRecipesRegistered() { specialRecipesRegistered = true; }
    }

    private static void registerCompatNuggetRecipes(IForgeRegistry<IRecipe> registry, String metalName, String ingotOreDictName,
                                                    int compatNuggetMeta, int nativeNuggetMeta) {
        List<ItemStack> entries = OreDictionary.getOres(ingotOreDictName);
        if (entries == null || entries.isEmpty()) {
            return;
        }

        ItemStack firstIngot = ItemStack.EMPTY;
        for (int index = 0; index < entries.size(); index++) {
            ItemStack ingot = entries.get(index);
            if (ingot.isEmpty()) {
                continue;
            }
            ItemStack ingotSingle = ingot.copy();
            ingotSingle.setCount(1);
            if (firstIngot.isEmpty()) {
                firstIngot = ingotSingle.copy();
            }
            IRecipe recipeIngotToCompatNuggets = new ShapedOreRecipe(
                    null,
                    new ItemStack(ConfigItems.itemNugget, 9, compatNuggetMeta),
                    "#",
                    '#', ingotSingle)
                    .setRegistryName("thaumcraft", "compat_" + metalName + "_nuggets_" + index);
            registry.register(recipeIngotToCompatNuggets);
        }

        if (!firstIngot.isEmpty()) {
            IRecipe recipeCompatNuggetsToIngot = new ShapedOreRecipe(
                    null,
                    firstIngot.copy(),
                    "###",
                    "###",
                    "###",
                    '#', new ItemStack(ConfigItems.itemNugget, 1, compatNuggetMeta))
                    .setRegistryName("thaumcraft", "compat_" + metalName + "_ingot");
            registry.register(recipeCompatNuggetsToIngot);

            ItemStack smeltingOutput = firstIngot.copy();
            smeltingOutput.setCount(2);
            FurnaceRecipes.instance().addSmeltingRecipe(
                    new ItemStack(ConfigItems.itemNugget, 1, nativeNuggetMeta),
                    smeltingOutput,
                    1.0F);
        }
    }
}
