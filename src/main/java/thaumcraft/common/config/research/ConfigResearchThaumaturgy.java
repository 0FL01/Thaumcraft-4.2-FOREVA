package thaumcraft.common.config.research;

import java.util.ArrayList;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

final class ConfigResearchThaumaturgy {
    private ConfigResearchThaumaturgy() {}

    static void initThaumaturgyResearchBaseline() {
        new ResearchItem(
                "BASICTHAUMATURGY",
                "THAUMATURGY",
                new AspectList(),
                0,
                0,
                0,
                new ItemStack(ConfigItems.itemWandCasting, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.BASICTHAUMATURGY.1"),
                        new ResearchPage("tc.research_page.BASICTHAUMATURGY.2"),
                        new ResearchPage(ConfigResearch.recipeI("WandCapIron")),
                        new ResearchPage(ConfigResearch.recipeI("WandBasic")))
                .setAutoUnlock()
                .setStub()
                .setRound()
                .registerResearchItem();

        new ResearchItem(
                "FOCUSFIRE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                2,
                -2,
                1,
                new ItemStack(ConfigItems.focusFire))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSFIRE.1"),
                        new ResearchPage("tc.research_page.FOCUSFIRE.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusFire")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSFROST",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.COLD, 6),
                1,
                -5,
                1,
                new ItemStack(ConfigItems.focusFrost))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSFROST.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusFrost")))
                .setConcealed()
                .setSecondary()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSHELLBAT",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.BEAST, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                3,
                -7,
                2,
                new ItemStack(ConfigItems.focusHellbat))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSHELLBAT.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("FocusHellbat")))
                .setHidden()
                .setEntityTriggers("Thaumcraft.Firebat")
                .setAspectTriggers(Aspect.FIRE)
                .setParentsHidden("FOCUSFIRE", "INFUSION")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("FOCUSHELLBAT", 2);
        ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.focusHellbat), 1);

        new ResearchItem(
                "FOCUSEXCAVATION",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.ENTROPY, 3)
                        .add(Aspect.MAGIC, 3),
                0,
                -3,
                2,
                new ItemStack(ConfigItems.focusExcavation))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSEXCAVATION.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusExcavation")))
                .setConcealed()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        if (Config.wardedStone) {
            new ResearchItem(
                    "FOCUSWARDING",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.EARTH, 6)
                            .add(Aspect.ARMOR, 3)
                            .add(Aspect.ORDER, 3)
                            .add(Aspect.MIND, 3),
                    -2,
                    -4,
                    3,
                    new ItemStack(ConfigItems.focusWarding))
                    .setPages(
                            new ResearchPage("tc.research_page.FOCUSWARDING.1"),
                            new ResearchPage(ConfigResearch.recipeInfusion("FocusWarding")))
                    .setConcealed()
                    .setParents("FOCUSEXCAVATION", "INFUSION")
                    .registerResearchItem();
        }

        new ResearchItem(
                "FOCUSSHOCK",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AIR, 3)
                        .add(Aspect.ENERGY, 6)
                        .add(Aspect.MAGIC, 3),
                3,
                -5,
                1,
                new ItemStack(ConfigItems.focusShock))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSSHOCK.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusShock")))
                .setConcealed()
                .setSecondary()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSTRADE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.EARTH, 3)
                        .add(Aspect.EXCHANGE, 6)
                        .add(Aspect.MAGIC, 3),
                4,
                -3,
                2,
                new ItemStack(ConfigItems.focusTrade))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSTRADE.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusTrade")))
                .setConcealed()
                .setParents("FOCUSFIRE")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSPORTABLEHOLE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TRAVEL, 3)
                        .add(Aspect.ENTROPY, 3)
                        .add(Aspect.ELDRITCH, 6)
                        .add(Aspect.AIR, 3),
                7,
                -2,
                2,
                new ItemStack(ConfigItems.focusPortableHole))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSPORTABLEHOLE.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("FocusPortableHole")))
                .setConcealed()
                .setParents("FOCUSTRADE", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "FOCUSPOUCH",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.VOID, 6)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.TOOL, 3),
                4,
                -1,
                1,
                new ItemStack(ConfigItems.itemFocusPouch))
                .setPages(
                        new ResearchPage("tc.research_page.FOCUSPOUCH.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocusPouch")))
                .setParents("FOCUSFIRE")
                .setSecondary()
                .registerResearchItem();

        new ResearchItem(
                "CAP_gold",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.METAL, 3)
                        .add(Aspect.GREED, 3)
                        .add(Aspect.TOOL, 3),
                3,
                2,
                1,
                new ItemStack(ConfigItems.itemWandCap, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.CAP_gold.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandCapGold")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        if (Config.foundCopperIngot) {
            new ResearchItem(
                    "CAP_copper",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.METAL, 3)
                            .add(Aspect.EXCHANGE, 3)
                            .add(Aspect.TOOL, 3),
                    2,
                    0,
                    1,
                    new ItemStack(ConfigItems.itemWandCap, 1, 3))
                    .setPages(
                            new ResearchPage("tc.research_page.CAP_copper.1"),
                            new ResearchPage(ConfigResearch.recipeArcane("WandCapCopper")))
                    .setParents("BASICTHAUMATURGY")
                    .registerResearchItem();
        }

        new ResearchItem(
                "CAP_thaumium",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.METAL, 6)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AURA, 3),
                5,
                4,
                2,
                new ItemStack(ConfigItems.itemWandCap, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.CAP_thaumium.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandCapThaumiumInert")),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandCapThaumium")))
                .setParents("CAP_gold", "THAUMIUM", "INFUSION")
                .registerResearchItem();

        if (Config.foundSilverIngot) {
            new ResearchItem(
                    "CAP_silver",
                    "THAUMATURGY",
                    new AspectList()
                            .add(Aspect.METAL, 3)
                            .add(Aspect.GREED, 3)
                            .add(Aspect.TOOL, 3)
                            .add(Aspect.AURA, 3),
                    5,
                    1,
                    1,
                    new ItemStack(ConfigItems.itemWandCap, 1, 4))
                    .setPages(
                            new ResearchPage("tc.research_page.CAP_silver.1"),
                            new ResearchPage(ConfigResearch.recipeArcane("WandCapSilverInert")),
                            new ResearchPage(ConfigResearch.recipeInfusion("WandCapSilver")))
                    .setConcealed()
                    .setParents("CAP_gold", "INFUSION")
                    .registerResearchItem();
        }

        new ResearchItem(
                "ROD_greatwood",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 3),
                -5,
                2,
                1,
                new ItemStack(ConfigItems.itemWandRod, 1, 0))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_greatwood.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodGreatwood")))
                .setParents("BASICTHAUMATURGY")
                .registerResearchItem();

        new ResearchItem(
                "ROD_reed",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.PLANT, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_reed.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodReed")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_blaze",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MAGIC, 3),
                -7,
                0,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 6))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_blaze.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodBlaze")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_obsidian",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_obsidian.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodObsidian")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_ice",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.COLD, 6)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3),
                -7,
                4,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 3))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_ice.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodIce")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_quartz",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.CRYSTAL, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                5,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 4))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_quartz.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodQuartz")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        new ResearchItem(
                "ROD_bone",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.MAGIC, 3),
                -3,
                0,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 7))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_bone.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodBone")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("ROD_bone", 1);

        new ResearchItem(
                "ROD_silverwood",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                -2,
                5,
                3,
                new ItemStack(ConfigItems.itemWandRod, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_silverwood.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandRodSilverwood")))
                .setParents("ROD_greatwood", "INFUSION")
                .registerResearchItem();

        ArrayList<ShapedArcaneRecipe> sceptreRecipes = new ArrayList<>();

        WandCap wandCapIron = WandCap.caps.get("iron");
        WandCap wandCapGold = WandCap.caps.get("gold");
        WandCap wandCapThaumium = WandCap.caps.get("thaumium");
        WandRod wandRodWood = WandRod.rods.get("wood");
        WandRod wandRodGreatwood = WandRod.rods.get("greatwood");
        WandRod wandRodSilverwood = WandRod.rods.get("silverwood");

        int sceptreCost1 = (int) ((float) (wandCapIron.getCraftCost() * wandRodWood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis1 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis1.add(primal, sceptreCost1);
        }
        ItemStack sceptre1 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost1);
        ItemWandCasting.setCap(sceptre1, wandCapIron);
        ItemWandCasting.setRod(sceptre1, wandRodWood);
        sceptre1.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre1,
                sceptreCostVis1,
                " TF",
                " RT",
                "T  ",
                'T', wandCapIron.getItem(),
                'R', wandRodWood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        int sceptreCost2 = (int) ((float) (wandCapGold.getCraftCost() * wandRodGreatwood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis2 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis2.add(primal, sceptreCost2);
        }
        ItemStack sceptre2 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost2);
        ItemWandCasting.setCap(sceptre2, wandCapGold);
        ItemWandCasting.setRod(sceptre2, wandRodGreatwood);
        sceptre2.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre2,
                sceptreCostVis2,
                " TF",
                " RT",
                "T  ",
                'T', wandCapGold.getItem(),
                'R', wandRodGreatwood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        int sceptreCost3 = (int) ((float) (wandCapThaumium.getCraftCost() * wandRodSilverwood.getCraftCost()) * 1.5f);
        AspectList sceptreCostVis3 = new AspectList();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            sceptreCostVis3.add(primal, sceptreCost3);
        }
        ItemStack sceptre3 = new ItemStack(ConfigItems.itemWandCasting, 1, sceptreCost3);
        ItemWandCasting.setCap(sceptre3, wandCapThaumium);
        ItemWandCasting.setRod(sceptre3, wandRodSilverwood);
        sceptre3.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        sceptreRecipes.add(new ShapedArcaneRecipe(
                "SCEPTRE",
                sceptre3,
                sceptreCostVis3,
                " TF",
                " RT",
                "T  ",
                'T', wandCapThaumium.getItem(),
                'R', wandRodSilverwood.getItem(),
                'F', new ItemStack(ConfigItems.itemResource, 1, 15)));

        new ResearchItem(
                "SCEPTRE",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.CRAFT, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                0,
                4,
                3,
                sceptre3)
                .setPages(
                        new ResearchPage("tc.research_page.SCEPTRE.1"),
                        new ResearchPage(sceptreRecipes.toArray(new IArcaneRecipe[0])))
                .setConcealed()
                .setParents("ROD_silverwood")
                .registerResearchItem();

        new ResearchItem(
                "ROD_greatwood_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 3),
                -1,
                7,
                1,
                new ItemStack(ConfigItems.itemWandRod, 1, 50))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_greatwood_staff.1"),
                        new ResearchPage("tc.research_page.ROD_greatwood_staff.2"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodGreatwoodStaff")))
                .setParents("ROD_silverwood")
                .registerResearchItem();

        new ResearchItem(
                "ROD_reed_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.AIR, 6)
                        .add(Aspect.PLANT, 3)
                        .add(Aspect.MAGIC, 3),
                -5,
                -2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 55))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_reed_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodReedStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_reed")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_blaze_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.FIRE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 56))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_blaze_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodBlazeStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_blaze")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_obsidian_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.EARTH, 6)
                        .add(Aspect.FIRE, 3)
                        .add(Aspect.MAGIC, 3),
                -9,
                2,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 51))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_obsidian_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodObsidianStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_obsidian")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_ice_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.COLD, 6)
                        .add(Aspect.WATER, 3)
                        .add(Aspect.MAGIC, 3),
                -8,
                5,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 53))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_ice_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodIceStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_ice")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_quartz_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.CRYSTAL, 3)
                        .add(Aspect.MAGIC, 3),
                -4,
                6,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 54))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_quartz_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodQuartzStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_quartz")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "ROD_bone_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 3)
                        .add(Aspect.ENTROPY, 6)
                        .add(Aspect.UNDEAD, 3)
                        .add(Aspect.MAGIC, 3),
                -2,
                -1,
                2,
                new ItemStack(ConfigItems.itemWandRod, 1, 57))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_bone_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodBoneStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_bone")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();
        ThaumcraftApi.addWarpToResearch("ROD_bone_staff", 1);

        new ResearchItem(
                "ROD_silverwood_staff",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.TOOL, 6)
                        .add(Aspect.TREE, 6)
                        .add(Aspect.MAGIC, 9),
                -1,
                5,
                3,
                new ItemStack(ConfigItems.itemWandRod, 1, 52))
                .setPages(
                        new ResearchPage("tc.research_page.ROD_silverwood_staff.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("WandRodSilverwoodStaff")))
                .setSecondary()
                .setConcealed()
                .setParents("ROD_silverwood")
                .setParentsHidden("ROD_greatwood_staff")
                .registerResearchItem();

        new ResearchItem(
                "NODESTABILIZER",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 4)
                        .add(Aspect.ORDER, 4)
                        .add(Aspect.ENERGY, 4),
                -7,
                -4,
                1,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.NODESTABILIZER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("NodeStabilizer")),
                        new ResearchPage("tc.research_page.NODESTABILIZER.2"))
                .setParents("NODEPRESERVE")
                .registerResearchItem();

        new ResearchItem(
                "NODESTABILIZERADV",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 9)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.ORDER, 6)
                        .add(Aspect.ENERGY, 6),
                -8,
                -3,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 9))
                .setPages(
                        new ResearchPage("tc.research_page.NODESTABILIZERADV.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("NodeStabilizerAdv")))
                .setSecondary()
                .setConcealed()
                .setParents("NODESTABILIZER")
                .registerResearchItem();

        new ResearchItem(
                "VISPOWER",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.ENERGY, 6),
                -5,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 11))
                .setPages(
                        new ResearchPage("tc.research_page.VISPOWER.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("NodeTransducer")),
                        new ResearchPage("tc.research_page.VISPOWER.2"),
                        new ResearchPage("tc.research_page.VISPOWER.3"),
                        new ResearchPage(ConfigResearch.recipeArcane("NodeRelay")),
                        new ResearchPage("tc.research_page.VISPOWER.4"),
                        new ResearchPage("tc.research_page.VISPOWER.5"))
                .setParents("NODESTABILIZER")
                .setSpecial()
                .registerResearchItem();

        new ResearchItem(
                "FOCALMANIPULATION",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.MAGIC, 8)
                        .add(Aspect.TOOL, 8)
                        .add(Aspect.CRAFT, 5)
                        .add(Aspect.CRYSTAL, 5)
                        .add(Aspect.ENERGY, 5),
                -3,
                -8,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 13))
                .setPages(
                        new ResearchPage("tc.research_page.FOCALMANIPULATION.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("FocalManipulator")),
                        new ResearchPage("tc.research_page.FOCALMANIPULATION.2"))
                .setParentsHidden("INFUSION", "FOCUSFIRE")
                .setParents("VISPOWER")
                .registerResearchItem();

        new ResearchItem(
                "VAMPBAT",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.HUNGER, 5)
                        .add(Aspect.LIFE, 5)
                        .add(Aspect.MAGIC, 5),
                4,
                -8,
                1,
                new ResourceLocation("thaumcraft", "textures/foci/vampirebats.png"))
                .setPages(new ResearchPage("focus.upgrade.vampirebats.text"))
                .setSecondary()
                .setParents("FOCUSHELLBAT")
                .setParentsHidden("FOCALMANIPULATION")
                .registerResearchItem();

        new ResearchItem(
                "WANDPED",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.EXCHANGE, 3)
                        .add(Aspect.ENERGY, 3),
                -9,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 5))
                .setPages(
                        new ResearchPage("tc.research_page.WANDPED.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandPed")))
                .setConcealed()
                .setParents("INFUSION", "NODEPRESERVE", "NODESTABILIZER")
                .registerResearchItem();

        new ResearchItem(
                "VISAMULET",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.VOID, 3),
                -9,
                -8,
                2,
                new ItemStack(ConfigItems.itemAmuletVis, 1, 1))
                .setPages(
                        new ResearchPage("tc.research_page.VISAMULET.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("VisAmulet")),
                        new ResearchPage("tc.research_page.VISAMULET.2"))
                .setConcealed()
                .setParents("WANDPED")
                .registerResearchItem();

        new ResearchItem(
                "WANDPEDFOC",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.AURA, 6)
                        .add(Aspect.MAGIC, 6)
                        .add(Aspect.EXCHANGE, 6)
                        .add(Aspect.ENERGY, 3)
                        .add(Aspect.TOOL, 3),
                -10,
                -7,
                3,
                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 8))
                .setPages(
                        new ResearchPage("tc.research_page.WANDPEDFOC.1"),
                        new ResearchPage(ConfigResearch.recipeInfusion("WandPedFocus")))
                .setSecondary()
                .setConcealed()
                .setParents("WANDPED")
                .registerResearchItem();

        new ResearchItem(
                "VISCHARGERELAY",
                "THAUMATURGY",
                new AspectList()
                        .add(Aspect.MAGIC, 3)
                        .add(Aspect.AURA, 3)
                        .add(Aspect.MECHANISM, 3)
                        .add(Aspect.ENERGY, 6),
                -7,
                -6,
                2,
                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 2))
                .setPages(
                        new ResearchPage("tc.research_page.VISCHARGERELAY.1"),
                        new ResearchPage(ConfigResearch.recipeArcane("NodeChargeRelay")))
                .setParents("VISPOWER", "WANDPED")
                .setParentsHidden("ROD_greatwood")
                .setSecondary()
                .setConcealed()
                .registerResearchItem();
    }


    static void initThaumaturgyResearchTextOnlyBaseline() {
        new ResearchItem("CAP_iron", "THAUMATURGY")
                .setAutoUnlock()
                .registerResearchItem();
        new ResearchItem("ROD_wood", "THAUMATURGY")
                .setAutoUnlock()
                .registerResearchItem();
    }

}
