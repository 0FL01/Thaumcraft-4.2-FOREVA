package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;

public class ConfigRecipes {
    private static boolean specialRecipesRegistered = false;

    public static void init() {
        ConfigResearch.recipes.clear();
        initializeArcaneRecipeBaseline();

        boolean hasArcaneWand = false;
        boolean hasArcaneSceptre = false;
        boolean hasRunicAugment = false;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof ArcaneWandRecipe) {
                hasArcaneWand = true;
            } else if (recipe instanceof ArcaneSceptreRecipe) {
                hasArcaneSceptre = true;
            }
            if (recipe instanceof InfusionRunicAugmentRecipe) {
                hasRunicAugment = true;
            }
        }
        if (!hasArcaneWand) {
            ThaumcraftApi.getCraftingRecipes().add(new ArcaneWandRecipe());
        }
        if (!hasArcaneSceptre) {
            ThaumcraftApi.getCraftingRecipes().add(new ArcaneSceptreRecipe());
        }
        if (!hasRunicAugment) {
            ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());
        }
    }

    private static void initializeArcaneRecipeBaseline() {
        registerArcaneRecipe("PrimalCharm", "BASICARTIFACE",
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new AspectList().add(Aspect.EARTH, 25).add(Aspect.FIRE, 25).add(Aspect.AIR, 25)
                        .add(Aspect.WATER, 25).add(Aspect.ORDER, 25).add(Aspect.ENTROPY, 25),
                "123", "ISI", "456",
                'S', new ItemStack(ConfigItems.itemShard, 1, 6),
                'I', Items.GOLD_INGOT,
                '1', new ItemStack(ConfigItems.itemShard, 1, 0),
                '2', new ItemStack(ConfigItems.itemShard, 1, 1),
                '3', new ItemStack(ConfigItems.itemShard, 1, 2),
                '4', new ItemStack(ConfigItems.itemShard, 1, 3),
                '5', new ItemStack(ConfigItems.itemShard, 1, 4),
                '6', new ItemStack(ConfigItems.itemShard, 1, 5));

        registerArcaneRecipe("IronKey", "WARDEDARCANA",
                new ItemStack(ConfigItems.itemKey, 2, 0),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                "NNI", "N  ",
                'I', Items.IRON_INGOT,
                'N', "nuggetIron");

        if (Config.wardedStone) {
            registerArcaneRecipe("GoldKey", "WARDEDARCANA",
                    new ItemStack(ConfigItems.itemKey, 2, 1),
                    new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 5),
                    "NNI", "N  ",
                    'I', Items.GOLD_INGOT,
                    'N', Items.GOLD_NUGGET);
        }

        registerArcaneRecipe("ArcaneStone1", "ARCANESTONE",
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6),
                new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1),
                "SSS", "SCS", "SSS",
                'S', "stone",
                'C', new ItemStack(ConfigItems.itemShard, 1, OreDictionary.WILDCARD_VALUE));

        registerArcaneRecipe("WardedJar", "DISTILESSENTIA",
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new AspectList().add(Aspect.WATER, 1),
                "GWG", "G G", "GGG",
                'W', "slabWood",
                'G', Blocks.GLASS);

        registerArcaneRecipe("JarVoid", "JARVOID",
                new ItemStack(ConfigBlocks.blockJar, 1, 3),
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ENTROPY, 15),
                "O", "J", "P",
                'O', Blocks.OBSIDIAN,
                'P', Items.BLAZE_POWDER,
                'J', new ItemStack(ConfigBlocks.blockJar, 1, 0));

        registerArcaneRecipe("WandCapGold", "CAP_gold",
                new ItemStack(ConfigItems.itemWandCap, 1, 1),
                new AspectList().add(Aspect.ORDER, getWandCapCost("gold"))
                        .add(Aspect.FIRE, getWandCapCost("gold"))
                        .add(Aspect.AIR, getWandCapCost("gold")),
                "NNN", "N N",
                'N', Items.GOLD_NUGGET);

        if (Config.foundCopperIngot) {
            registerArcaneRecipe("WandCapCopper", "CAP_copper",
                    new ItemStack(ConfigItems.itemWandCap, 1, 3),
                    new AspectList().add(Aspect.ORDER, getWandCapCost("copper"))
                            .add(Aspect.FIRE, getWandCapCost("copper"))
                            .add(Aspect.AIR, getWandCapCost("copper")),
                    "NNN", "N N",
                    'N', "nuggetCopper");
        }

        if (Config.foundSilverIngot) {
            registerArcaneRecipe("WandCapSilverInert", "CAP_silver",
                    new ItemStack(ConfigItems.itemWandCap, 1, 5),
                    new AspectList().add(Aspect.ORDER, getWandCapCost("silver"))
                            .add(Aspect.FIRE, getWandCapCost("silver"))
                            .add(Aspect.AIR, getWandCapCost("silver")),
                    "NNN", "N N",
                    'N', "nuggetSilver");
        }

        registerArcaneRecipe("WandCapThaumiumInert", "CAP_thaumium",
                new ItemStack(ConfigItems.itemWandCap, 1, 6),
                new AspectList().add(Aspect.ORDER, getWandCapCost("thaumium"))
                        .add(Aspect.FIRE, getWandCapCost("thaumium"))
                        .add(Aspect.AIR, getWandCapCost("thaumium")),
                "NNN", "N N",
                'N', "nuggetThaumium");

        registerArcaneRecipe("WandCapVoidInert", "CAP_void",
                new ItemStack(ConfigItems.itemWandCap, 1, 8),
                new AspectList().add(Aspect.ENTROPY, getWandCapCost("void") * 3)
                        .add(Aspect.ORDER, getWandCapCost("void") * 3)
                        .add(Aspect.FIRE, getWandCapCost("void") * 2)
                        .add(Aspect.AIR, getWandCapCost("void") * 2),
                "NNN", "N N",
                'N', "nuggetVoid");

        registerArcaneRecipe("WandRodGreatwood", "ROD_greatwood",
                new ItemStack(ConfigItems.itemWandRod, 1, 0),
                new AspectList().add(Aspect.ENTROPY, getWandRodCost("greatwood")),
                " G", "G ",
                'G', new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));

        registerArcaneRecipe("WandRodGreatwoodStaff", "ROD_greatwood_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 50),
                new AspectList().add(Aspect.ORDER, getWandRodCost("greatwood_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 0));

        registerArcaneRecipe("WandRodObsidianStaff", "ROD_obsidian_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 51),
                new AspectList().add(Aspect.ORDER, getWandRodCost("obsidian_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 1));

        registerArcaneRecipe("WandRodSilverwoodStaff", "ROD_silverwood_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 52),
                new AspectList().add(Aspect.ORDER, getWandRodCost("silverwood_staff")),
                "  S", " G ", "G  ",
                'S', new ItemStack(ConfigItems.itemResource, 1, 15),
                'G', new ItemStack(ConfigItems.itemWandRod, 1, 2));

        registerArcaneRecipe("FocusFire", "FOCUSFIRE",
                new ItemStack(ConfigItems.focusFire),
                new AspectList().add(Aspect.FIRE, 20).add(Aspect.ENTROPY, 10),
                "CQC", "Q#Q", "CQC",
                '#', Items.FIRE_CHARGE,
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 1));

        registerArcaneRecipe("FocusFrost", "FOCUSFROST",
                new ItemStack(ConfigItems.focusFrost),
                new AspectList().add(Aspect.WATER, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
                "CQC", "Q#Q", "CQC",
                '#', Items.DIAMOND,
                'Q', Items.QUARTZ,
                'C', new ItemStack(ConfigItems.itemShard, 1, 2));

        registerArcaneRecipe("RobeChest", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemChestRobe, 1),
                new AspectList().add(Aspect.AIR, 5),
                "I I", "III", "III",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("RobeLegs", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemLegsRobe, 1),
                new AspectList().add(Aspect.WATER, 5),
                "III", "I I", "I I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("RobeBoots", "ENCHFABRIC",
                new ItemStack(ConfigItems.itemBootsRobe, 1),
                new AspectList().add(Aspect.EARTH, 3),
                "I I", "I I",
                'I', new ItemStack(ConfigItems.itemResource, 1, 7));

        registerArcaneRecipe("Goggles", "GOGGLES",
                new ItemStack(ConfigItems.itemGoggles),
                new AspectList().add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.WATER, 5)
                        .add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 3).add(Aspect.ORDER, 3),
                "LGL", "L L", "TGT",
                'T', ConfigItems.itemThaumometer,
                'G', Items.GOLD_INGOT,
                'L', Items.LEATHER);
    }

    private static void registerArcaneRecipe(String key, String research, ItemStack output, AspectList aspects, Object... recipe) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addArcaneCraftingRecipe(research, output, aspects, recipe));
    }

    private static int getWandCapCost(String tag) {
        WandCap cap = WandCap.caps.get(tag);
        return cap != null ? cap.getCraftCost() : 0;
    }

    private static int getWandRodCost(String tag) {
        WandRod rod = WandRod.rods.get(tag);
        return rod != null ? rod.getCraftCost() : 0;
    }

    public static void oreDictRecipe(Object input, Object[] output) {
        // Stage 9: ore dictionary recipe registration.
    }

    public static void registerSpecialRecipes(IForgeRegistry<IRecipe> registry) {
        if (specialRecipesRegistered) {
            return;
        }
        registry.register(new RecipesRobeArmorDyes().setRegistryName("forge", "robearmordye"));
        registry.register(new RecipesVoidRobeArmorDyes().setRegistryName("forge", "voidrobearmordye"));
        specialRecipesRegistered = true;
    }
}
