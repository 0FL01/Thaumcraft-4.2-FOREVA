package thaumcraft.common.config.research;

import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;

final class ConfigResearchArtifice {
    private ConfigResearchArtifice() {}

    static void initArtificeResearchBaseline() {
        new ResearchItem(
                "ARCANESTONE",
                "ARTIFICE",
                new AspectList(),
                5,
                -2,
                0,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANESTONE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcaneStone1")),
                        new ResearchPage(ConfigResearch.recipeI("ArcaneStone2")),
                        new ResearchPage(ConfigResearch.recipeI("ArcaneStone3")),
                        new ResearchPage(ConfigResearch.recipeI("ArcaneStone4")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "GRATE",
                "ARTIFICE",
                new AspectList(),
                2,
                -1,
                0,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.GRATE.1"),
                        new ResearchPage(ConfigResearch.recipeI("Grate")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "TABLE",
                "ARTIFICE",
                new AspectList(),
                0,
                -1,
                0,
                new ItemStack(ConfigBlocks.blockTable))
                .setPages(
                        new ResearchPage("tc.research_page.TABLE.1"),
                        new ResearchPage(ConfigResearch.recipeI("Table")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "ARCTABLE",
                "ARTIFICE",
                new AspectList(),
                -1,
                -3,
                0,
                new ItemStack(ConfigBlocks.blockTable, 1, 15))
                .setPages(
                        new ResearchPage("tc.research_page.ARCTABLE.1"),
                        new ResearchPage(ConfigResearch.recipeList("ArcTable")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .setParents("TABLE")
                .registerResearchItem();

        new ResearchItem(
                "RESTABLE",
                "ARTIFICE",
                new AspectList(),
                1,
                -3,
                0,
                new ItemStack(ConfigBlocks.blockTable, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RESTABLE.1"),
                        new ResearchPage(ConfigResearch.recipeList("ResTable")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .setParents("TABLE")
                .registerResearchItem();

        new ResearchItem(
                "THAUMOMETER",
                "ARTIFICE",
                new AspectList(),
                2,
                1,
                0,
                new ItemStack(ConfigItems.itemThaumometer))
                .setPages(
                        new ResearchPage("tc.research_page.THAUMOMETER.1"),
                        new ResearchPage(ConfigResearch.recipeI("Thaumometer")))
                .setStub()
                .setAutoUnlock()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "PAVETRAVEL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.FLIGHT, 3),
                4,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.PAVETRAVEL.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("PaveTravel")))
                .setParents("ARCANESTONE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "PAVEWARD",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.TRAP, 3)
                        .add(Aspect.BEAST, 3),
                6,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.PAVEWARD.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("PaveWard")),
                        new ResearchPage("tc.research_page.PAVEWARD.2"))
                .setParents("ARCANESTONE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "GOGGLES",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MAGIC, 3),
                4,
                1,
                1,
                new ItemStack(ConfigItems.itemGoggles))
                .setPages(
                        new ResearchPage("tc.research_page.GOGGLES.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("Goggles")))
                .setParents("THAUMOMETER")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ARCANEEAR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.AIR, 3),
                6,
                0,
                1,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANEEAR.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcaneEar")))
                .setParents("GOGGLES")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "SINSTONE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.DARKNESS, 3)
                        .add(Aspect.ELDRITCH, 3)
                        .add(Aspect.AURA, 3),
                6,
                2,
                1,
                new ItemStack(ConfigItems.itemCompassStone, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.SINSTONE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("SinStone")))
                .setParents("GOGGLES")
                .setConcealed()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("SINSTONE", 2);

        new ResearchItem(
                "LEVITATOR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.FLIGHT, 3)
                        .add(Aspect.AIR, 3),
                -3,
                -3,
                1,
                new ItemStack(ConfigBlocks.blockLifter))
                .setPages(
                        new ResearchPage("tc.research_page.LEVITATOR.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("Levitator")))
                .setConcealed()
                .setParents("NITOR")
                .registerResearchItem();

        new ResearchItem(
                "INFERNALFURNACE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.METAL, 3)
                        .add(Aspect.CRAFT, 3)
                        .add(Aspect.AURA, 3),
                -4,
                -1,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_infernalfurnace.png"))
                .setPages(
                        new ResearchPage("tc.research_page.INFERNALFURNACE.1"),
                        new ResearchPage(ConfigResearch.recipeList("InfernalFurnace")),
                        new ResearchPage("tc.research_page.INFERNALFURNACE.2"))
                .setParents("NITOR", "ALUMENTUM")
                .setConcealed()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("INFERNALFURNACE", 2);

        new ResearchItem(
                "BELLOWS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.AIR, 6)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.MOTION, 3),
                -6,
                -2,
                1,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.BELLOWS.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("Bellows")),
                        new ResearchPage("tc.research_page.BELLOWS.2"))
                .setParents("INFERNALFURNACE")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ARCANEBORE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MINE, 6)
                        .add(Aspect.MOTION, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.TOOL, 3),
                -3,
                8,
                2,
                new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANEBORE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ArcaneBore")),
                        new ResearchPage("tc.research_page.ARCANEBORE.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcaneBoreBase")),
                        new ResearchPage("tc.research_page.ARCANEBORE.3"))
                .setConcealed()
                .setParents("FOCUSEXCAVATION", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ARCANELAMP",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.LIGHT, 3)
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.DARKNESS, 3),
                -3,
                1,
                1,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ARCANELAMP.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcaneLamp")),
                        new ResearchPage("ARCANEBORE", "tc.research_page.ARCANELAMP.2"))
                .setSecondary()
                .setParents("NITOR")
                .registerResearchItem();

        new ResearchItem(
                "ENCHFABRIC",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.CLOTH, 3)
                        .add(Aspect.MAGIC, 3),
                0,
                3,
                1,
                new ItemStack(ConfigItems.itemResource, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ENCHFABRIC.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("EnchantedFabric")),
                        new ResearchPage("tc.research_page.ENCHFABRIC.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("RobeChest")),
                        new ResearchPage(ConfigResearch.recipeArcane("RobeLegs")),
                        new ResearchPage(ConfigResearch.recipeArcane("RobeBoots")))
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "RUNICARMOR",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.ARMOR, 6)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MIND, 3),
                3,
                4,
                3,
                new ItemStack(ConfigItems.itemRingRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICARMOR.1"),
                        new ResearchPage("tc.research_page.RUNICARMOR.2"),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicRing")),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicAmulet")),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicGirdle")))
                .setParentsHidden("INFUSION")
                .setParents("ENCHFABRIC")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICCHARGED",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.ENERGY, 6),
                2,
                3,
                2,
                new ItemStack(ConfigItems.itemRingRunic, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICCHARGED.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicRingCharged")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICHEALING",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.HEAL, 4)
                        .add(Aspect.WATER, 4),
                4,
                3,
                2,
                new ItemStack(ConfigItems.itemRingRunic, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICHEALING.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicRingHealing")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICKINETIC",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.AIR, 6),
                2,
                5,
                2,
                new ItemStack(ConfigItems.itemGirdleRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICKINETIC.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicGirdleKinetic")),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicGirdleKinetic_2")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "RUNICEMERGENCY",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.EARTH, 4)
                        .add(Aspect.VOID, 4),
                4,
                5,
                2,
                new ItemStack(ConfigItems.itemAmuletRunic, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICEMERGENCY.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("RunicAmuletEmergency")))
                .setParents("RUNICARMOR")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();

        ArrayList<InfusionRunicAugmentRecipe> runicAugmentRecipes = new ArrayList<>();
        for (int a = 0; a <= 4; ++a) {
            ItemStack runicChestplate = new ItemStack(ConfigItems.itemChestRobe);
            if (a > 0) {
                runicChestplate.setTagInfo("RS.HARDEN", new NBTTagByte((byte) a));
            }
            runicAugmentRecipes.add(new InfusionRunicAugmentRecipe(runicChestplate));
        }
        new ResearchItem(
                "RUNICAUGMENTATION",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.EXCHANGE, 4)
                        .add(Aspect.GREED, 4),
                6,
                4,
                1,
                new ResourceLocation("thaumcraft", "textures/misc/r_runicupg.png"))
                .setPages(
                        new ResearchPage("tc.research_page.RUNICAUGMENTATION.1"),
                        new ResearchPage(runicAugmentRecipes.toArray(new InfusionRecipe[0])),
                        new ResearchPage("tc.research_page.RUNICAUGMENTATION.2"))
                .setParents("RUNICARMOR")
                .setConcealed()
                .registerResearchItem();

        ArrayList<IArcaneRecipe> bannerRecipes = new ArrayList<>();
        for (int a = 0; a < 16; ++a) {
            bannerRecipes.add(ConfigResearch.recipeArcane("Banner_" + a));
        }
        ItemStack bannerAnchor = new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8);
        bannerAnchor.setTagCompound(new NBTTagCompound());
        bannerAnchor.getTagCompound().setByte("color", (byte) 10);
        new ResearchItem(
                "BANNERS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 3)
                        .add(Aspect.CLOTH, 3)
                        .add(Aspect.MAGIC, 1),
                4,
                8,
                1,
                bannerAnchor)
                .setPages(
                        new ResearchPage("tc.research_page.BANNERS.1"),
                        new ResearchPage(bannerRecipes.toArray(new IArcaneRecipe[0])))
                .setHidden()
                .setItemTriggers(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 8))
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALAXE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MOTION, 3),
                -7,
                4,
                2,
                new ItemStack(ConfigItems.itemAxeElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALAXE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ElementalAxe")),
                        new ResearchPage("tc.research_page.ELEMENTALAXE.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALPICK",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.SENSES, 3),
                -7,
                3,
                2,
                new ItemStack(ConfigItems.itemPickElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALPICK.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ElementalPick")),
                        new ResearchPage("tc.research_page.ELEMENTALPICK.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALSWORD",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.ENERGY, 3),
                -7,
                5,
                2,
                new ItemStack(ConfigItems.itemSwordElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALSWORD.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ElementalSword")))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALSHOVEL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.CRAFT, 3),
                -7,
                6,
                2,
                new ItemStack(ConfigItems.itemShovelElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALSHOVEL.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ElementalShovel")),
                        new ResearchPage("tc.research_page.ELEMENTALSHOVEL.2"))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "ELEMENTALHOE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.LIFE, 3)
                        .add(Aspect.CROP, 3),
                -7,
                7,
                2,
                new ItemStack(ConfigItems.itemHoeElemental))
                .setPages(
                        new ResearchPage("tc.research_page.ELEMENTALHOE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ElementalHoe")))
                .setParents("THAUMIUM", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        if (Config.wardedStone) {
            new ResearchItem(
                    "WARDEDARCANA",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TOOL, 6)
                            .add(Aspect.MIND, 3)
                            .add(Aspect.MECHANISM, 3)
                            .add(Aspect.ARMOR, 3),
                    -5,
                    -4,
                    2,
                    new ItemStack(ConfigItems.itemArcaneDoor))
                    .setPages(
                            new ResearchPage("tc.research_page.WARDEDARCANA.1"),
                            new ResearchPage(ConfigResearch.recipeArcane("ArcaneDoor")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.2"),
                            new ResearchPage(ConfigResearch.recipeArcane("IronKey")),
                            new ResearchPage(ConfigResearch.recipeArcane("GoldKey")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.3"),
                            new ResearchPage(ConfigResearch.recipeArcane("ArcanePressurePlate")),
                            new ResearchPage("tc.research_page.WARDEDARCANA.4"),
                            new ResearchPage(ConfigResearch.recipeArcane("WardedGlass")))
                    .setParents("THAUMIUM")
                    .registerResearchItem();
        }

        new ResearchItem(
                "BONEBOW",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MOTION, 3),
                -7,
                1,
                1,
                new ItemStack(ConfigItems.itemBowBone))
                .setPages(
                        new ResearchPage("tc.research_page.BONEBOW.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("BoneBow")))
                .setHidden()
                .setItemTriggers(
                        new ItemStack(Items.BOW, 1, Short.MAX_VALUE),
                        new ItemStack(Items.BONE))
                .registerResearchItem();

        ArrayList<IArcaneRecipe> primalArrowRecipes = new ArrayList<>();
        for (int a = 0; a < 6; ++a) {
            primalArrowRecipes.add(ConfigResearch.recipeArcane("PrimalArrow_" + a));
        }
        new ResearchItem(
                "PRIMALARROW",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.ORDER, 3)
                        .add(Aspect.ENTROPY, 3),
                -9,
                0,
                2,
                new ItemStack(ConfigItems.itemPrimalArrow, 1, Short.MAX_VALUE))
                .setPages(
                        new ResearchPage("tc.research_page.PRIMALARROW.1"),
                        new ResearchPage(primalArrowRecipes.toArray(new IArcaneRecipe[0])),
                        new ResearchPage("tc.research_page.PRIMALARROW.2"),
                        new ResearchPage("tc.research_page.PRIMALARROW.3"))
                .setConcealed()
                .setParents("BONEBOW")
                .registerResearchItem();

        new ResearchItem(
                "INFUSION",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.CRAFT, 6),
                -4,
                5,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.INFUSION.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("InfusionMatrix")),
                        new ResearchPage(ConfigResearch.recipeArcane("ArcanePedestal")),
                        new ResearchPage("tc.research_page.INFUSION.2"),
                        new ResearchPage(ConfigResearch.recipeList("InfusionAltar")),
                        new ResearchPage("tc.research_page.INFUSION.3"),
                        new ResearchPage("tc.research_page.INFUSION.4"),
                        new ResearchPage("tc.research_page.INFUSION.5"))
                .setParents("DISTILESSENTIA")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "LAMPGROWTH",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.LIGHT, 3)
                        .add(Aspect.PLANT, 6)
                        .add(Aspect.LIFE, 3)
                        .add(Aspect.CROP, 3),
                -4,
                3,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.LAMPGROWTH.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("LampGrowth")))
                .setHidden()
                .setAspectTriggers(Aspect.LIGHT, Aspect.CROP)
                .setParents("ARCANELAMP", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "LAMPFERTILITY",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.BEAST, 6)
                        .add(Aspect.LIFE, 6)
                        .add(Aspect.LIGHT, 3),
                -2,
                3,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 13))
                .setPages(
                        new ResearchPage("tc.research_page.LAMPFERTILITY.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("LampFertility")))
                .setHidden()
                .setAspectTriggers(Aspect.LIGHT, Aspect.LIFE)
                .setParents("ARCANELAMP", "INFUSION")
                .registerResearchItem();

        if (Config.allowMirrors) {
            new ResearchItem(
                    "MIRROR",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TRAVEL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.DARKNESS, 3)
                            .add(Aspect.CRYSTAL, 3),
                    -1,
                    8,
                    2,
                    new ItemStack(ConfigBlocks.blockMirror, 1, 0))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRROR.1"),
                            new ResearchPage("tc.research_page.MIRROR.2"),
                            new ResearchPage(ConfigResearch.recipeInfusion("Mirror")),
                            new ResearchPage("tc.research_page.MIRROR.3"))
                    .setHidden()
                    .setEntityTriggers("Enderman")
                    .setItemTriggers(
                            new ItemStack(Items.ENDER_PEARL),
                            new ItemStack(Blocks.STAINED_GLASS, 1, Short.MAX_VALUE),
                            new ItemStack(Blocks.STAINED_GLASS_PANE, 1, Short.MAX_VALUE),
                            new ItemStack(Blocks.OBSIDIAN, 1, Short.MAX_VALUE))
                    .setParents("INFUSION")
                    .registerResearchItem();

            new ResearchItem(
                    "MIRRORHAND",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TOOL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.CRYSTAL, 3)
                            .add(Aspect.TRAVEL, 3),
                    1,
                    9,
                    2,
                    new ItemStack(ConfigItems.itemHandMirror))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRRORHAND.1"),
                            new ResearchPage(ConfigResearch.recipeInfusion("MirrorHand")))
                    .setConcealed()
                    .setSecondary()
                    .setParents("MIRROR")
                    .registerResearchItem();

            new ResearchItem(
                    "MIRRORESSENTIA",
                    "ARTIFICE",
                    new AspectList()
                            .add(Aspect.TRAVEL, 6)
                            .add(Aspect.ELDRITCH, 3)
                            .add(Aspect.WATER, 3)
                            .add(Aspect.MAGIC, 3),
                    -1,
                    10,
                    2,
                    new ItemStack(ConfigBlocks.blockMirror, 1, 6))
                    .setPages(
                            new ResearchPage("tc.research_page.MIRRORESSENTIA.1"),
                            new ResearchPage(ConfigResearch.recipeInfusion("MirrorEssentia")),
                            new ResearchPage("tc.research_page.MIRRORESSENTIA.2"))
                    .setSecondary()
                    .setConcealed()
                    .setParents("MIRROR")
                    .registerResearchItem();
        }

        new ResearchItem(
                "JARBRAIN",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.HUNGER, 3)
                        .add(Aspect.MIND, 3)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.GREED, 3),
                -5,
                9,
                2,
                new ItemStack(ConfigBlocks.blockJar, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.JARBRAIN.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("JarBrain")))
                .setParents("INFUSION")
                .setHidden()
                .setItemTriggers(new ItemStack(ConfigItems.itemResource, 1, 3))
                .setEntityTriggers("Thaumcraft.BrainyZombie", "Thaumcraft.GiantBrainyZombie")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("JARBRAIN", 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigBlocks.blockJar, 1, 1), 1);

        new ResearchItem(
                "INFUSIONENCHANTMENT",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.MIND, 3)
                        .add(Aspect.WEAPON, 3)
                        .add(Aspect.ARMOR, 3)
                        .add(Aspect.TOOL, 3),
                -6,
                11,
                3,
                new ResourceLocation("thaumcraft", "textures/misc/r_enchant.png"))
                .setPages(
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.1"),
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.2"),
                        new ResearchPage("tc.research_page.INFUSIONENCHANTMENT.3"),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnchRepair")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnchHaste")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch0")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch1")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch2")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch3")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch4")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch5")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch6")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch7")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch8")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch9")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch10")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch11")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch12")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch13")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch14")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch15")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch16")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch17")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch18")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch19")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch20")),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnch21")))
                .setConcealed()
                .setParents("JARBRAIN")
                .registerResearchItem();

        new ResearchItem(
                "ARMORFORTRESS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.ARMOR, 5)
                        .add(Aspect.CRAFT, 5),
                -8,
                9,
                2,
                new ItemStack(ConfigItems.itemHelmFortress))
                .setPages(
                        new ResearchPage("tc.research_page.ARMORFORTRESS.1"),
                        new ResearchPage("tc.research_page.ARMORFORTRESS.2"),
                        new ResearchPage(ConfigResearch.recipeInfusion("ThaumiumFortressHelm")),
                        new ResearchPage(ConfigResearch.recipeInfusion("ThaumiumFortressChest")),
                        new ResearchPage(ConfigResearch.recipeInfusion("ThaumiumFortressLegs")))
                .setParents("THAUMIUM", "INFUSIONENCHANTMENT")
                .setHidden()
                .setAspectTriggers(Aspect.ARMOR)
                .registerResearchItem();

        new ResearchItem(
                "HELMGOGGLES",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.SENSES, 5)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.ARMOR, 3),
                -9,
                7,
                2,
                new ItemStack(ConfigItems.itemGoggles))
                .setPages(
                        new ResearchPage("tc.research_page.HELMGOGGLES.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("HelmGoggles")))
                .setParentsHidden("GOGGLES")
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKGRINNINGDEVIL",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.HEAL, 5)
                        .add(Aspect.MIND, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                8,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask0.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKGRINNINGDEVIL.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("MaskGrinningDevil")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKANGRYGHOST",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.ENTROPY, 5)
                        .add(Aspect.DEATH, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                9,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask1.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKANGRYGHOST.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("MaskAngryGhost")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "MASKSIPPINGFIEND",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.UNDEAD, 5)
                        .add(Aspect.LIFE, 5)
                        .add(Aspect.ARMOR, 3),
                -10,
                10,
                2,
                new ResourceLocation("thaumcraft", "textures/misc/r_mask2.png"))
                .setPages(
                        new ResearchPage("tc.research_page.MASKSIPPINGFIEND.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("MaskSippingFiend")))
                .setParents("ARMORFORTRESS")
                .setConcealed()
                .setSecondary()
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("MASKANGRYGHOST", 1);
        ThaumcraftApi.addWarpToResearch("MASKSIPPINGFIEND", 1);

        new ResearchItem(
                "BOOTSTRAVELLER",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.FLIGHT, 3)
                        .add(Aspect.WATER, 3),
                -1,
                5,
                2,
                new ItemStack(ConfigItems.itemBootsTraveller))
                .setPages(
                        new ResearchPage("tc.research_page.BOOTSTRAVELLER.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("BootsTraveller")))
                .setParents("ENCHFABRIC", "INFUSION")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "HOVERHARNESS",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 6)
                        .add(Aspect.TRAVEL, 6)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.MECHANISM, 3),
                1,
                7,
                3,
                new ItemStack(ConfigItems.itemHoverHarness))
                .setPages(
                        new ResearchPage("tc.research_page.HOVERHARNESS.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("HoverHarness")),
                        new ResearchPage("tc.research_page.HOVERHARNESS.2"))
                .setParents("BOOTSTRAVELLER")
                .setConcealed()
                .registerResearchItem();

        new ResearchItem(
                "HOVERGIRDLE",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 6)
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.MOTION, 6),
                2,
                7,
                3,
                new ItemStack(ConfigItems.itemGirdleHover))
                .setPages(
                        new ResearchPage("tc.research_page.HOVERGIRDLE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("HoverGirdle")))
                .setHidden()
                .setAspectTriggers(Aspect.FLIGHT)
                .setParents("HOVERHARNESS")
                .setSecondary()
                .registerResearchItem();
    }

    static void initArtificeResearchTextOnlyBaseline() {
        new ResearchItem(
                "BASICARTIFACE",
                "ARTIFICE",
                new AspectList(),
                0,
                1,
                0,
                new ItemStack(ConfigItems.itemResource, 1, 15))
                .setPages(
                        new ResearchPage("tc.research_page.BASICARTIFACE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("PrimalCharm")),
                        new ResearchPage(ConfigResearch.recipeI("MundaneAmulet")),
                        new ResearchPage(ConfigResearch.recipeI("MundaneRing")),
                        new ResearchPage(ConfigResearch.recipeI("MundaneBelt")),
                        new ResearchPage(ConfigResearch.recipeArcane("MirrorGlass")))
                .setStub()
                .setRound()
                .setAutoUnlock()
                .registerResearchItem();

        new ResearchItem(
                "FLUXSCRUB",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.TRAP, 3)
                        .add(Aspect.AIR, 3)
                        .add(Aspect.WATER, 3),
                -8,
                -3,
                1,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 14))
                .setPages(
                        new ResearchPage("tc.research_page.FLUXSCRUB.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FluxScrubber")))
                .setParentsHidden("INFUSION")
                .setParents("VISPOWER", "BELLOWS", "TUBES")
                .setSecondary()
                .registerResearchItem();
    }
}
