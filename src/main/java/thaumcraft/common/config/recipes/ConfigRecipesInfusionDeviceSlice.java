package thaumcraft.common.config.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

class ConfigRecipesInfusionDeviceSlice {

    static void initializeInfusionFocusDeviceRecipeBaseline() {
        ConfigRecipesInfusionSlice.registerInfusionRecipe("FocusHellbat", "FOCUSHELLBAT",
                new ItemStack(ConfigItems.focusHellbat),
                3,
                new AspectList().add(Aspect.FIRE, 25).add(Aspect.AIR, 15).add(Aspect.BEAST, 15).add(Aspect.ENTROPY, 25),
                new ItemStack(Items.MAGMA_CREAM),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 5));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("FocusPortableHole", "FOCUSPORTABLEHOLE",
                new ItemStack(ConfigItems.focusPortableHole),
                3,
                new AspectList().add(Aspect.TRAVEL, 25).add(Aspect.ELDRITCH, 10).add(Aspect.EXCHANGE, 10).add(Aspect.ENTROPY, 25),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 5));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("FocusWarding", "FOCUSWARDING",
                new ItemStack(ConfigItems.focusWarding),
                4,
                new AspectList().add(Aspect.EARTH, 25).add(Aspect.ARMOR, 25).add(Aspect.ORDER, 25).add(Aspect.MIND, 10),
                new ItemStack(Items.NETHER_STAR),
                new ItemStack(ConfigItems.itemResource, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemShard, 1, 4));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("WandPed", "WANDPED",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5),
                3,
                new AspectList().add(Aspect.AURA, 10).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 15),
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 1),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(Items.DIAMOND));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("WandPedFocus", "WANDPEDFOC",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 8),
                4,
                new AspectList().add(Aspect.ORDER, 10).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 10),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8),
                new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemResource, 1, 8));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("NodeStabilizerAdv", "NODESTABILIZERADV",
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 10),
                10,
                new AspectList().add(Aspect.AURA, 32).add(Aspect.MAGIC, 16).add(Aspect.ORDER, 16).add(Aspect.ENERGY, 16),
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 0), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 1), new ItemStack(Blocks.REDSTONE_BLOCK),
                new ItemStack(ConfigItems.itemResource, 1, 0), new ItemStack(Blocks.REDSTONE_BLOCK));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("JarBrain", "JARBRAIN",
                new ItemStack(ConfigBlocks.blockJar, 1, 1),
                4,
                new AspectList().add(Aspect.MIND, 10).add(Aspect.SENSES, 10).add(Aspect.UNDEAD, 20),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Items.WATER_BUCKET),
                new ItemStack(Items.SPIDER_EYE));

        if (Config.allowMirrors) {
            ConfigRecipesInfusionSlice.registerInfusionRecipe("Mirror", "MIRROR",
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0),
                    1,
                    new AspectList().add(Aspect.TRAVEL, 8).add(Aspect.DARKNESS, 8).add(Aspect.EXCHANGE, 8),
                    new ItemStack(ConfigItems.itemResource, 1, 10),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.GOLD_INGOT),
                    new ItemStack(Items.ENDER_PEARL));

            ConfigRecipesInfusionSlice.registerInfusionRecipe("MirrorHand", "MIRRORHAND",
                    new ItemStack(ConfigItems.itemHandMirror),
                    5,
                    new AspectList().add(Aspect.TOOL, 16).add(Aspect.TRAVEL, 16),
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0),
                    new ItemStack(Items.STICK),
                    new ItemStack(Items.COMPASS),
                    new ItemStack(Items.MAP));

            ConfigRecipesInfusionSlice.registerInfusionRecipe("MirrorEssentia", "MIRRORESSENTIA",
                    new ItemStack(ConfigBlocks.blockMirror, 1, 6),
                    2,
                    new AspectList().add(Aspect.TRAVEL, 8).add(Aspect.WATER, 8).add(Aspect.EXCHANGE, 8),
                    new ItemStack(ConfigItems.itemResource, 1, 10),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.IRON_INGOT),
                    new ItemStack(Items.ENDER_PEARL));
        }
    }

    static void initializeInfusionGolemDeviceRecipeBaseline() {
        ConfigRecipesInfusionSlice.registerInfusionRecipe("AdvancedGolem", "ADVANCEDGOLEM",
                new Object[]{"advanced", new NBTTagByte((byte) 1)},
                3,
                new AspectList().add(Aspect.MIND, 8).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8),
                new ItemStack(ConfigItems.itemGolemPlacer, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(Items.REDSTONE),
                new ItemStack(Items.GLOWSTONE_DUST),
                new ItemStack(Items.GUNPOWDER),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemZombieBrain));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("CoreAlchemy", "COREALCHEMY",
                new ItemStack(ConfigItems.itemGolemCore, 1, 6),
                2,
                new AspectList().add(Aspect.MAGIC, 15).add(Aspect.WATER, 15).add(Aspect.MOTION, 15),
                new ItemStack(ConfigItems.itemGolemCore, 1, 5),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.POTIONITEM));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("CoreSorting", "CORESORTING",
                new ItemStack(ConfigItems.itemGolemCore, 1, 10),
                3,
                new AspectList().add(Aspect.VOID, 16).add(Aspect.EXCHANGE, 16).add(Aspect.HUNGER, 16).add(Aspect.GREED, 16),
                new ItemStack(ConfigItems.itemZombieBrain),
                new ItemStack(ConfigItems.itemGolemCore, 1, 0),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(ConfigItems.itemGolemCore, 1, 1),
                new ItemStack(Items.PAPER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("CoreLumber", "CORELUMBER",
                new ItemStack(ConfigItems.itemGolemCore, 1, 7),
                2,
                new AspectList().add(Aspect.TOOL, 16).add(Aspect.TREE, 16).add(Aspect.HARVEST, 16),
                new ItemStack(ConfigItems.itemGolemCore, 1, 3),
                new ItemStack(ConfigItems.itemAxeElemental),
                new ItemStack(Items.IRON_AXE),
                new ItemStack(Items.IRON_AXE),
                new ItemStack(Items.IRON_AXE));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("CoreFishing", "COREFISHING",
                new ItemStack(ConfigItems.itemGolemCore, 1, 11),
                3,
                new AspectList().add(Aspect.WATER, 16).add(Aspect.HARVEST, 16).add(Aspect.BEAST, 16),
                new ItemStack(ConfigItems.itemGolemCore, 1, 3),
                new ItemStack(Items.FISHING_ROD),
                new ItemStack(Items.FISH, 1, 0),
                new ItemStack(Items.FISH, 1, 3),
                new ItemStack(Items.FISH, 1, 1));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("CoreUse", "COREUSE",
                new ItemStack(ConfigItems.itemGolemCore, 1, 8),
                3,
                new AspectList().add(Aspect.TOOL, 20).add(Aspect.MECHANISM, 20).add(Aspect.MAN, 20),
                new ItemStack(ConfigItems.itemGolemCore, 1, 1),
                new ItemStack(Items.COMPARATOR),
                new ItemStack(Items.FLINT_AND_STEEL),
                new ItemStack(Items.SHEARS),
                new ItemStack(Blocks.LEVER));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("ArcaneBore", "ARCANEBORE",
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 5),
                4,
                new AspectList().add(Aspect.ENERGY, 16).add(Aspect.MINE, 32).add(Aspect.MECHANISM, 32)
                        .add(Aspect.VOID, 16).add(Aspect.MOTION, 16),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DIAMOND_PICKAXE),
                new ItemStack(Items.DIAMOND_SHOVEL),
                new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("LampGrowth", "LAMPGROWTH",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8),
                4,
                new AspectList().add(Aspect.PLANT, 16).add(Aspect.LIGHT, 8).add(Aspect.LIFE, 16),
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DYE, 1, 15),
                new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.DYE, 1, 15),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("LampFertility", "LAMPFERTILITY",
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 13),
                4,
                new AspectList().add(Aspect.BEAST, 16).add(Aspect.LIFE, 16).add(Aspect.LIGHT, 8),
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.WHEAT),
                new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.CARROT),
                new ItemStack(ConfigItems.itemShard, 1, 1));

        ConfigRecipesInfusionSlice.registerInfusionRecipe("EssentiaReservoir", "ESSENTIARESERVOIR",
                new ItemStack(ConfigBlocks.blockEssentiaReservoir),
                6,
                new AspectList().add(Aspect.WATER, 8).add(Aspect.VOID, 8).add(Aspect.MAGIC, 8).add(Aspect.EXCHANGE, 8),
                new ItemStack(ConfigBlocks.blockTube, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 16),
                new ItemStack(ConfigBlocks.blockJar, 1, 0),
                new ItemStack(ConfigBlocks.blockJar, 1, 0));
    }
}
