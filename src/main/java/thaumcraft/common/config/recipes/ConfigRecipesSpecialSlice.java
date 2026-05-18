package thaumcraft.common.config.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;
import thaumcraft.common.lib.crafting.ShapelessNBTOreRecipe;

public class ConfigRecipesSpecialSlice {
    private static final String[] DYES = new String[]{
            "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown",
            "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray",
            "dyeGray", "dyePink", "dyeLime", "dyeYellow",
            "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"
    };

    public static void registerSpecialRecipes(IForgeRegistry<IRecipe> registry, ConfigRecipes.SpecialRecipesBridge bridge) {
        if (bridge.areSpecialRecipesRegistered()) {
            return;
        }
        bridge.clearSpecialResearchRecipeHandles();
        bridge.clearRecipeJarLabelAspects();
        registry.register(new RecipesRobeArmorDyes().setRegistryName("thaumcraft", "robearmordye"));
        registry.register(new RecipesVoidRobeArmorDyes().setRegistryName("thaumcraft", "voidrobearmordye"));
        IRecipe recipeArcaneStone2 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7),
                "SS",
                "SS",
                'S', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6))
                .setRegistryName("thaumcraft", "arcanestone2");
        registry.register(recipeArcaneStone2);
        bridge.setRecipeArcaneStone2(recipeArcaneStone2);
        IRecipe recipeArcaneStone3 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsArcaneStone, 4, 0),
                "K  ",
                "KK ",
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7))
                .setRegistryName("thaumcraft", "arcanestone3");
        registry.register(recipeArcaneStone3);
        bridge.setRecipeArcaneStone3(recipeArcaneStone3);
        IRecipe recipeArcaneStone4 = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockSlabStone, 6, 0),
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7))
                .setRegistryName("thaumcraft", "arcanestone4");
        registry.register(recipeArcaneStone4);
        bridge.setRecipeArcaneStone4(recipeArcaneStone4);

        IRecipe recipeStairsGreatwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsGreatwood, 4, 0),
                "K  ",
                "KK ",
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6))
                .setRegistryName("thaumcraft", "blockstairsgreatwood");
        registry.register(recipeStairsGreatwood);

        IRecipe recipeStairsSilverwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsSilverwood, 4, 0),
                "K  ",
                "KK ",
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7))
                .setRegistryName("thaumcraft", "blockstairssilverwood");
        registry.register(recipeStairsSilverwood);

        IRecipe recipeSlabGreatwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockSlabWood, 6, 0),
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6))
                .setRegistryName("thaumcraft", "blockslabgreatwood");
        registry.register(recipeSlabGreatwood);

        IRecipe recipeSlabSilverwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockSlabWood, 6, 1),
                "KKK",
                'K', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7))
                .setRegistryName("thaumcraft", "blockslabsilverwood");
        registry.register(recipeSlabSilverwood);

        IRecipe recipeKnowFrag = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemResearchNotes, 1, 42),
                "KKK",
                "KKK",
                "KKK",
                'K', new ItemStack(ConfigItems.itemResource, 1, 9))
                .setRegistryName("thaumcraft", "knowfrag");
        registry.register(recipeKnowFrag);
        bridge.setRecipeKnowFrag(recipeKnowFrag);

        IRecipe recipePlankGreatwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 4, 6),
                "W",
                'W', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0))
                .setRegistryName("thaumcraft", "plankgreatwood");
        registry.register(recipePlankGreatwood);
        bridge.setRecipePlankGreatwood(recipePlankGreatwood);

        IRecipe recipePlankSilverwood = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 4, 7),
                "W",
                'W', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1))
                .setRegistryName("thaumcraft", "planksilverwood");
        registry.register(recipePlankSilverwood);
        bridge.setRecipePlankSilverwood(recipePlankSilverwood);

        IRecipe recipeGrate = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 5),
                "#",
                "T",
                '#', new ItemStack(Blocks.IRON_BARS),
                'T', new ItemStack(Blocks.TRAPDOOR))
                .setRegistryName("thaumcraft", "grate");
        registry.register(recipeGrate);
        bridge.setRecipeGrate(recipeGrate);

        IRecipe recipePhial = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemEssence, 8, 0),
                " C ",
                "G G",
                " G ",
                'G', Blocks.GLASS,
                'C', Items.CLAY_BALL)
                .setRegistryName("thaumcraft", "phial");
        registry.register(recipePhial);
        bridge.setRecipePhial(recipePhial);

        IRecipe recipeTable = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockTable, 1, 0),
                "SSS",
                "W W",
                'S', "slabWood",
                'W', "plankWood")
                .setRegistryName("thaumcraft", "table");
        registry.register(recipeTable);
        bridge.setRecipeTable(recipeTable);

        IRecipe recipeScribe1 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                new ItemStack(ConfigItems.itemEssence, 1, 0),
                Items.FEATHER,
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe1");
        registry.register(recipeScribe1);
        bridge.setRecipeScribe1(recipeScribe1);

        IRecipe recipeScribe2 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                Items.FEATHER,
                Items.DYE,
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe2");
        registry.register(recipeScribe2);
        bridge.setRecipeScribe2(recipeScribe2);

        IRecipe recipeScribe3 = new ShapelessOreRecipe(null,
                new ItemStack(ConfigItems.itemInkwell),
                new ItemStack(ConfigItems.itemInkwell, 1, OreDictionary.WILDCARD_VALUE),
                "dyeBlack")
                .setRegistryName("thaumcraft", "scribe3");
        registry.register(recipeScribe3);
        bridge.setRecipeScribe3(recipeScribe3);

        IRecipe recipeThaumometer = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemThaumometer),
                " 1 ",
                "IGI",
                " 1 ",
                'I', Items.IRON_INGOT,
                'G', Blocks.GLASS,
                '1', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE))
                .setRegistryName("thaumcraft", "thaumometer");
        registry.register(recipeThaumometer);
        bridge.setRecipeThaumometer(recipeThaumometer);

        IRecipe recipeWandCapIron = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemWandCap, 1, 0),
                "NNN",
                "N N",
                'N', "nuggetIron")
                .setRegistryName("thaumcraft", "wandcapiron");
        registry.register(recipeWandCapIron);
        bridge.setRecipeWandCapIron(recipeWandCapIron);

        IRecipe recipeWandBasic = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemWandCasting, 1, 0),
                "  I",
                " S ",
                "I  ",
                'I', new ItemStack(ConfigItems.itemWandCap, 1, 0),
                'S', "stickWood")
                .setRegistryName("thaumcraft", "wandbasic");
        registry.register(recipeWandBasic);
        bridge.setRecipeWandBasic(recipeWandBasic);

        IRecipe recipeMundaneAmulet = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0),
                " S ",
                "S S",
                " I ",
                'S', new ItemStack(Items.STRING),
                'I', new ItemStack(Items.IRON_INGOT))
                .setRegistryName("thaumcraft", "mundaneamulet");
        registry.register(recipeMundaneAmulet);
        bridge.setRecipeMundaneAmulet(recipeMundaneAmulet);

        IRecipe recipeMundaneRing = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 1),
                " N ",
                "N N",
                " N ",
                'N', new ItemStack(Items.GOLD_NUGGET))
                .setRegistryName("thaumcraft", "mundanering");
        registry.register(recipeMundaneRing);
        bridge.setRecipeMundaneRing(recipeMundaneRing);

        IRecipe recipeMundaneBelt = new ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemBaubleBlanks, 1, 2),
                " L ",
                "L L",
                " I ",
                'L', new ItemStack(Items.LEATHER),
                'I', new ItemStack(Items.IRON_INGOT))
                .setRegistryName("thaumcraft", "mundanebelt");
        registry.register(recipeMundaneBelt);
        bridge.setRecipeMundaneBelt(recipeMundaneBelt);

        IRecipe recipeBlockFlesh = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockTaint, 1, 2),
                "KKK",
                "KKK",
                "KKK",
                'K', new ItemStack(Items.ROTTEN_FLESH))
                .setRegistryName("thaumcraft", "blockflesh");
        registry.register(recipeBlockFlesh);
        bridge.setRecipeBlockFlesh(recipeBlockFlesh);

        IRecipe recipeBlockTallow = new ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5),
                "KKK",
                "KKK",
                "KKK",
                'K', new ItemStack(ConfigItems.itemResource, 1, 4))
                .setRegistryName("thaumcraft", "blocktallow");
        registry.register(recipeBlockTallow);
        bridge.setRecipeBlockTallow(recipeBlockTallow);

        IRecipe recipeBlockTallowDecompose = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 9, 4),
                "K",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5))
                .setRegistryName("thaumcraft", "blocktallow_decompose");
        registry.register(recipeBlockTallowDecompose);

        IRecipe recipeNuggetsIron = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemNugget, 9, 0),
                "#",
                '#', Items.IRON_INGOT)
                .setRegistryName("thaumcraft", "nuggets_iron");
        registry.register(recipeNuggetsIron);

        IRecipe recipeNuggetsThaumium = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemNugget, 9, 6),
                "#",
                '#', new ItemStack(ConfigItems.itemResource, 1, 2))
                .setRegistryName("thaumcraft", "nuggets_thaumium");
        registry.register(recipeNuggetsThaumium);

        IRecipe recipeNuggetsVoid = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemNugget, 9, 7),
                "#",
                '#', new ItemStack(ConfigItems.itemResource, 1, 16))
                .setRegistryName("thaumcraft", "nuggets_void");
        registry.register(recipeNuggetsVoid);

        IRecipe recipeIronFromNuggets = new ShapedOreRecipe(
                null,
                new ItemStack(Items.IRON_INGOT),
                "###",
                "###",
                "###",
                '#', new ItemStack(ConfigItems.itemNugget, 1, 0))
                .setRegistryName("thaumcraft", "iron_from_nuggets");
        registry.register(recipeIronFromNuggets);

        IRecipe recipeThaumiumFromNuggets = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 1, 2),
                "###",
                "###",
                "###",
                '#', new ItemStack(ConfigItems.itemNugget, 1, 6))
                .setRegistryName("thaumcraft", "thaumium_from_nuggets");
        registry.register(recipeThaumiumFromNuggets);

        IRecipe recipeNuggetsQuicksilver = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemNugget, 9, 5),
                "#",
                '#', new ItemStack(ConfigItems.itemResource, 1, 3))
                .setRegistryName("thaumcraft", "nuggets_quicksilver");
        registry.register(recipeNuggetsQuicksilver);

        IRecipe recipeQuicksilverFromNuggets = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 1, 3),
                "###",
                "###",
                "###",
                '#', new ItemStack(ConfigItems.itemNugget, 1, 5))
                .setRegistryName("thaumcraft", "quicksilver_from_nuggets");
        registry.register(recipeQuicksilverFromNuggets);

        IRecipe recipeVoidFromNuggets = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 1, 16),
                "###",
                "###",
                "###",
                '#', new ItemStack(ConfigItems.itemNugget, 1, 7))
                .setRegistryName("thaumcraft", "void_from_nuggets");
        registry.register(recipeVoidFromNuggets);

        IRecipe recipeTallowCandle = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCandle, 3, 0),
                " S ",
                " T ",
                " T ",
                'S', new ItemStack(Items.STRING),
                'T', new ItemStack(ConfigItems.itemResource, 1, 4))
                .setRegistryName("thaumcraft", "tallowcandle");
        registry.register(recipeTallowCandle);
        bridge.addSpecialResearchRecipeHandle("TallowCandle", recipeTallowCandle);
        for (int a = 1; a < 16; a++) {
            IRecipe recipeTallowCandleDye = new ShapelessOreRecipe(
                    null,
                    new ItemStack(ConfigBlocks.blockCandle, 1, a),
                    DYES[15 - a],
                    new ItemStack(ConfigBlocks.blockCandle, 1, 0))
                    .setRegistryName("thaumcraft", "tallowcandle_dye_" + a);
            registry.register(recipeTallowCandleDye);
        }
        IRecipe recipeTallowCandleReset = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCandle, 1, 0),
                new ItemStack(Items.DYE, 1, 15),
                new ItemStack(ConfigBlocks.blockCandle, 1, OreDictionary.WILDCARD_VALUE))
                .setRegistryName("thaumcraft", "tallowcandle_reset");
        registry.register(recipeTallowCandleReset);

        IRecipe recipeJarLabel = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 4, 13),
                "dyeBlack",
                Items.SLIME_BALL,
                Items.PAPER,
                Items.PAPER,
                Items.PAPER,
                Items.PAPER)
                .setRegistryName("thaumcraft", "jarlabel");
        registry.register(recipeJarLabel);
        bridge.addSpecialResearchRecipeHandle("JarLabel", recipeJarLabel);

        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect == null) {
                continue;
            }
            ItemStack essence = new ItemStack(ConfigItems.itemEssence, 1, 1);
            if (essence.getItem() instanceof IEssentiaContainerItem) {
                ((IEssentiaContainerItem) essence.getItem()).setAspects(essence, new AspectList().add(aspect, 8));
            }
            ItemStack output = new ItemStack(ConfigItems.itemResource, 1, 13);
            if (output.getItem() instanceof IEssentiaContainerItem) {
                ((IEssentiaContainerItem) output.getItem()).setAspects(output, new AspectList().add(aspect, 0));
            }
            IRecipe recipeJarLabelAspect = new ShapelessNBTOreRecipe(
                    output,
                    new ItemStack(ConfigItems.itemResource, 1, 13),
                    essence)
                    .setRegistryName("thaumcraft", "jarlabel_" + aspect.getTag().toLowerCase());
            registry.register(recipeJarLabelAspect);
            bridge.addRecipeJarLabelAspect(recipeJarLabelAspect);
        }

        ItemStack recipeJarLabelNullInput = new ItemStack(ConfigItems.itemResource, 1, 13);
        if (recipeJarLabelNullInput.getItem() instanceof IEssentiaContainerItem) {
            ((IEssentiaContainerItem) recipeJarLabelNullInput.getItem()).setAspects(recipeJarLabelNullInput, new AspectList().add(Aspect.WATER, 1));
        }
        IRecipe recipeJarLabelNull = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 1, 13),
                recipeJarLabelNullInput)
                .setRegistryName("thaumcraft", "jarlabelnull");
        registry.register(recipeJarLabelNull);
        bridge.addSpecialResearchRecipeHandle("JarLabelNull", recipeJarLabelNull);

        IRecipe recipeBlockThaumium = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4),
                "KKK",
                "KKK",
                "KKK",
                'K', "ingotThaumium")
                .setRegistryName("thaumcraft", "blockthaumium");
        registry.register(recipeBlockThaumium);
        bridge.addSpecialResearchRecipeHandle("BlockThaumium", recipeBlockThaumium);

        IRecipe recipeBlockThaumiumDecompose = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 9, 2),
                "K",
                'K', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4))
                .setRegistryName("thaumcraft", "blockthaumium_decompose");
        registry.register(recipeBlockThaumiumDecompose);

        IRecipe recipeThaumiumHelm = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemHelmThaumium, 1),
                "III",
                "I I",
                'I', "ingotThaumium")
                .setRegistryName("thaumcraft", "thaumiumhelm");
        registry.register(recipeThaumiumHelm);
        bridge.addSpecialResearchRecipeHandle("ThaumiumHelm", recipeThaumiumHelm);

        IRecipe recipeThaumiumChest = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemChestThaumium, 1),
                "I I",
                "III",
                "III",
                'I', "ingotThaumium")
                .setRegistryName("thaumcraft", "thaumiumchest");
        registry.register(recipeThaumiumChest);
        bridge.addSpecialResearchRecipeHandle("ThaumiumChest", recipeThaumiumChest);

        IRecipe recipeThaumiumLegs = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemLegsThaumium, 1),
                "III",
                "I I",
                "I I",
                'I', "ingotThaumium")
                .setRegistryName("thaumcraft", "thaumiumlegs");
        registry.register(recipeThaumiumLegs);
        bridge.addSpecialResearchRecipeHandle("ThaumiumLegs", recipeThaumiumLegs);

        IRecipe recipeThaumiumBoots = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemBootsThaumium, 1),
                "I I",
                "I I",
                'I', "ingotThaumium")
                .setRegistryName("thaumcraft", "thaumiumboots");
        registry.register(recipeThaumiumBoots);
        bridge.addSpecialResearchRecipeHandle("ThaumiumBoots", recipeThaumiumBoots);

        IRecipe recipeThaumiumShovel = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemShovelThaumium, 1),
                "I",
                "S",
                "S",
                'I', "ingotThaumium",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "thaumiumshovel");
        registry.register(recipeThaumiumShovel);
        bridge.addSpecialResearchRecipeHandle("ThaumiumShovel", recipeThaumiumShovel);

        IRecipe recipeThaumiumPick = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemPickThaumium, 1),
                "III",
                " S ",
                " S ",
                'I', "ingotThaumium",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "thaumiumpick");
        registry.register(recipeThaumiumPick);
        bridge.addSpecialResearchRecipeHandle("ThaumiumPick", recipeThaumiumPick);

        IRecipe recipeThaumiumAxe = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemAxeThaumium, 1),
                "II",
                "SI",
                "S ",
                'I', "ingotThaumium",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "thaumiumaxe");
        registry.register(recipeThaumiumAxe);
        bridge.addSpecialResearchRecipeHandle("ThaumiumAxe", recipeThaumiumAxe);

        IRecipe recipeThaumiumHoe = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemHoeThaumium, 1),
                "II",
                "S ",
                "S ",
                'I', "ingotThaumium",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "thaumiumhoe");
        registry.register(recipeThaumiumHoe);
        bridge.addSpecialResearchRecipeHandle("ThaumiumHoe", recipeThaumiumHoe);

        IRecipe recipeThaumiumSword = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemSwordThaumium, 1),
                "I",
                "I",
                "S",
                'I', "ingotThaumium",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "thaumiumsword");
        registry.register(recipeThaumiumSword);
        bridge.addSpecialResearchRecipeHandle("ThaumiumSword", recipeThaumiumSword);

        IRecipe recipeVoidHelm = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemHelmVoid, 1),
                "III",
                "I I",
                'I', "ingotVoid")
                .setRegistryName("thaumcraft", "voidhelm");
        registry.register(recipeVoidHelm);
        bridge.addSpecialResearchRecipeHandle("VoidHelm", recipeVoidHelm);

        IRecipe recipeVoidChest = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemChestVoid, 1),
                "I I",
                "III",
                "III",
                'I', "ingotVoid")
                .setRegistryName("thaumcraft", "voidchest");
        registry.register(recipeVoidChest);
        bridge.addSpecialResearchRecipeHandle("VoidChest", recipeVoidChest);

        IRecipe recipeVoidLegs = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemLegsVoid, 1),
                "III",
                "I I",
                "I I",
                'I', "ingotVoid")
                .setRegistryName("thaumcraft", "voidlegs");
        registry.register(recipeVoidLegs);
        bridge.addSpecialResearchRecipeHandle("VoidLegs", recipeVoidLegs);

        IRecipe recipeVoidBoots = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemBootsVoid, 1),
                "I I",
                "I I",
                'I', "ingotVoid")
                .setRegistryName("thaumcraft", "voidboots");
        registry.register(recipeVoidBoots);
        bridge.addSpecialResearchRecipeHandle("VoidBoots", recipeVoidBoots);

        IRecipe recipeVoidShovel = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemShovelVoid, 1),
                "I",
                "S",
                "S",
                'I', "ingotVoid",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "voidshovel");
        registry.register(recipeVoidShovel);
        bridge.addSpecialResearchRecipeHandle("VoidShovel", recipeVoidShovel);

        IRecipe recipeVoidPick = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemPickVoid, 1),
                "III",
                " S ",
                " S ",
                'I', "ingotVoid",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "voidpick");
        registry.register(recipeVoidPick);
        bridge.addSpecialResearchRecipeHandle("VoidPick", recipeVoidPick);

        IRecipe recipeVoidAxe = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemAxeVoid, 1),
                "II",
                "SI",
                "S ",
                'I', "ingotVoid",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "voidaxe");
        registry.register(recipeVoidAxe);
        bridge.addSpecialResearchRecipeHandle("VoidAxe", recipeVoidAxe);

        IRecipe recipeVoidHoe = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemHoeVoid, 1),
                "II",
                "S ",
                "S ",
                'I', "ingotVoid",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "voidhoe");
        registry.register(recipeVoidHoe);
        bridge.addSpecialResearchRecipeHandle("VoidHoe", recipeVoidHoe);

        IRecipe recipeVoidSword = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemSwordVoid, 1),
                "I",
                "I",
                "S",
                'I', "ingotVoid",
                'S', "stickWood")
                .setRegistryName("thaumcraft", "voidsword");
        registry.register(recipeVoidSword);
        bridge.addSpecialResearchRecipeHandle("VoidSword", recipeVoidSword);

        IRecipe recipeQuicksilverFromPlant = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 1, 3),
                "#",
                '#', new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2))
                .setRegistryName("thaumcraft", "quicksilverplant");
        registry.register(recipeQuicksilverFromPlant);

        IRecipe recipeSugarFromPlant = new ShapedOreRecipe(
                null,
                new ItemStack(Items.SUGAR),
                "#",
                '#', new ItemStack(ConfigBlocks.blockCustomPlant, 1, 3))
                .setRegistryName("thaumcraft", "sugarplant");
        registry.register(recipeSugarFromPlant);

        IRecipe recipeTripleMeatTreat0 = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemTripleMeatTreat),
                new ItemStack(Items.SUGAR),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 0),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 1),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 2))
                .setRegistryName("thaumcraft", "triplemeattreat_chicken_beef_pork");
        registry.register(recipeTripleMeatTreat0);

        IRecipe recipeTripleMeatTreat1 = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemTripleMeatTreat),
                new ItemStack(Items.SUGAR),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 0),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 1),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 3))
                .setRegistryName("thaumcraft", "triplemeattreat_chicken_beef_fish");
        registry.register(recipeTripleMeatTreat1);

        IRecipe recipeTripleMeatTreat2 = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemTripleMeatTreat),
                new ItemStack(Items.SUGAR),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 0),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 2),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 3))
                .setRegistryName("thaumcraft", "triplemeattreat_chicken_pork_fish");
        registry.register(recipeTripleMeatTreat2);

        IRecipe recipeTripleMeatTreat3 = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigItems.itemTripleMeatTreat),
                new ItemStack(Items.SUGAR),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 1),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 2),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 3))
                .setRegistryName("thaumcraft", "triplemeattreat_beef_pork_fish");
        registry.register(recipeTripleMeatTreat3);

        IRecipe recipeCosmeticOpaque0 = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 0),
                "##",
                "##",
                '#', new ItemStack(ConfigItems.itemResource, 1, 6))
                .setRegistryName("thaumcraft", "cosmeticopaque0");
        registry.register(recipeCosmeticOpaque0);

        IRecipe recipeCosmeticOpaque1 = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCosmeticOpaque, 4, 1),
                "##",
                "##",
                '#', new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 0))
                .setRegistryName("thaumcraft", "cosmeticopaque1");
        registry.register(recipeCosmeticOpaque1);

        IRecipe recipeCosmeticSolid1 = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 1),
                "##",
                "##",
                '#', new ItemStack(Blocks.MOSSY_COBBLESTONE))
                .setRegistryName("thaumcraft", "cosmeticsolid1");
        registry.register(recipeCosmeticSolid1);

        IRecipe recipeResource6FromOpaque0 = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 4, 6),
                "#",
                '#', new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 0))
                .setRegistryName("thaumcraft", "resource6fromopaque0");
        registry.register(recipeResource6FromOpaque0);

        IRecipe recipeResource6FromOpaque1 = new ShapedOreRecipe(
                null,
                new ItemStack(ConfigItems.itemResource, 4, 6),
                "#",
                '#', new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 1))
                .setRegistryName("thaumcraft", "resource6fromopaque1");
        registry.register(recipeResource6FromOpaque1);

        bridge.registerCompatNuggetRecipes(registry, "copper", "ingotCopper", 1, 17);
        bridge.registerCompatNuggetRecipes(registry, "tin", "ingotTin", 2, 18);
        bridge.registerCompatNuggetRecipes(registry, "silver", "ingotSilver", 3, 19);
        bridge.registerCompatNuggetRecipes(registry, "lead", "ingotLead", 4, 20);

        for (int a = 0; a < 6; a++) {
            IRecipe recipeCluster = new ShapelessOreRecipe(
                    null,
                    new ItemStack(ConfigBlocks.blockCrystal, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a),
                    new ItemStack(ConfigItems.itemShard, 1, a))
                    .setRegistryName("thaumcraft", "clusters" + a);
            bridge.setRecipeCluster(a, recipeCluster);
            registry.register(recipeCluster);
        }
        IRecipe recipeCluster6 = new ShapelessOreRecipe(
                null,
                new ItemStack(ConfigBlocks.blockCrystal, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemShard, 1, 5))
                .setRegistryName("thaumcraft", "clusters6");
        bridge.setRecipeCluster(6, recipeCluster6);
        registry.register(recipeCluster6);

        bridge.markSpecialRecipesRegistered();
    }
}
