package thaumcraft.common.config.recipes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.research.ConfigResearch;

public class ConfigRecipesInfusionSlice {

    public static void initializeInfusionWandRecipeBaseline() {
        if (Config.foundSilverIngot) {
            registerInfusionRecipe("WandCapSilver", "CAP_silver",
                    new ItemStack(ConfigItems.itemWandCap, 1, 4),
                    4,
                    new AspectList().add(Aspect.ENERGY, getWandCapCost("silver") * 2)
                            .add(Aspect.AURA, getWandCapCost("silver")),
                    new ItemStack(ConfigItems.itemWandCap, 1, 5),
                    new ItemStack(ConfigItems.itemResource, 1, 14),
                    new ItemStack(ConfigItems.itemResource, 1, 14));
        }

        registerInfusionRecipe("WandCapThaumium", "CAP_thaumium",
                new ItemStack(ConfigItems.itemWandCap, 1, 2),
                5,
                new AspectList().add(Aspect.ENERGY, getWandCapCost("thaumium") * 2)
                        .add(Aspect.AURA, getWandCapCost("thaumium")),
                new ItemStack(ConfigItems.itemWandCap, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionRecipe("WandCapVoid", "CAP_void",
                new ItemStack(ConfigItems.itemWandCap, 1, 7),
                8,
                new AspectList().add(Aspect.ENERGY, getWandCapCost("void") * 2)
                        .add(Aspect.VOID, getWandCapCost("void") * 2)
                        .add(Aspect.ELDRITCH, getWandCapCost("void") * 2)
                        .add(Aspect.AURA, getWandCapCost("void") * 2),
                new ItemStack(ConfigItems.itemWandCap, 1, 8),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionRecipe("WandRodObsidian", "ROD_obsidian",
                new ItemStack(ConfigItems.itemWandRod, 1, 1),
                3,
                new AspectList().add(Aspect.EARTH, getWandRodCost("obsidian") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("obsidian"))
                        .add(Aspect.DARKNESS, getWandRodCost("blaze")),
                new ItemStack(Blocks.OBSIDIAN),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 3));

        registerInfusionRecipe("WandRodIce", "ROD_ice",
                new ItemStack(ConfigItems.itemWandRod, 1, 3),
                3,
                new AspectList().add(Aspect.WATER, getWandRodCost("ice") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("ice"))
                        .add(Aspect.COLD, getWandRodCost("blaze")),
                new ItemStack(Blocks.PACKED_ICE),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 2));

        registerInfusionRecipe("WandRodQuartz", "ROD_quartz",
                new ItemStack(ConfigItems.itemWandRod, 1, 4),
                3,
                new AspectList().add(Aspect.ORDER, getWandRodCost("quartz") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("quartz"))
                        .add(Aspect.CRYSTAL, getWandRodCost("blaze")),
                new ItemStack(Blocks.QUARTZ_BLOCK),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 4));

        registerInfusionRecipe("WandRodReed", "ROD_reed",
                new ItemStack(ConfigItems.itemWandRod, 1, 5),
                3,
                new AspectList().add(Aspect.AIR, getWandRodCost("reed") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("reed"))
                        .add(Aspect.MOTION, getWandRodCost("blaze")),
                new ItemStack(Items.REEDS),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 0));

        registerInfusionRecipe("WandRodBlaze", "ROD_blaze",
                new ItemStack(ConfigItems.itemWandRod, 1, 6),
                3,
                new AspectList().add(Aspect.FIRE, getWandRodCost("blaze") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("blaze"))
                        .add(Aspect.BEAST, getWandRodCost("blaze")),
                new ItemStack(Items.BLAZE_ROD),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 1));

        registerInfusionRecipe("WandRodBone", "ROD_bone",
                new ItemStack(ConfigItems.itemWandRod, 1, 7),
                3,
                new AspectList().add(Aspect.ENTROPY, getWandRodCost("bone") * 2)
                        .add(Aspect.MAGIC, getWandRodCost("bone"))
                        .add(Aspect.UNDEAD, getWandRodCost("blaze")),
                new ItemStack(Items.BONE),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("WandRodSilverwood", "ROD_silverwood",
                new ItemStack(ConfigItems.itemWandRod, 1, 2),
                5,
                new AspectList().add(Aspect.AIR, getWandRodCost("silverwood"))
                        .add(Aspect.FIRE, getWandRodCost("silverwood"))
                        .add(Aspect.WATER, getWandRodCost("silverwood"))
                        .add(Aspect.EARTH, getWandRodCost("silverwood"))
                        .add(Aspect.ORDER, getWandRodCost("silverwood"))
                        .add(Aspect.ENTROPY, getWandRodCost("silverwood"))
                        .add(Aspect.MAGIC, getWandRodCost("silverwood")),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemShard, 1, 0),
                new ItemStack(ConfigItems.itemShard, 1, 1),
                new ItemStack(ConfigItems.itemShard, 1, 2),
                new ItemStack(ConfigItems.itemShard, 1, 3),
                new ItemStack(ConfigItems.itemShard, 1, 4),
                new ItemStack(ConfigItems.itemShard, 1, 5));

        registerInfusionRecipe("WandRodPrimalStaff", "ROD_primal_staff",
                new ItemStack(ConfigItems.itemWandRod, 1, 100),
                8,
                new AspectList().add(Aspect.AIR, getWandRodCost("primal_staff"))
                        .add(Aspect.FIRE, getWandRodCost("primal_staff"))
                        .add(Aspect.WATER, getWandRodCost("primal_staff"))
                        .add(Aspect.EARTH, getWandRodCost("primal_staff"))
                        .add(Aspect.ORDER, getWandRodCost("primal_staff"))
                        .add(Aspect.ENTROPY, getWandRodCost("primal_staff"))
                        .add(Aspect.MAGIC, getWandRodCost("primal_staff") * 2),
                new ItemStack(ConfigItems.itemWandRod, 1, 2),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemWandRod, 1, 1),
                new ItemStack(ConfigItems.itemWandRod, 1, 3),
                new ItemStack(ConfigItems.itemWandRod, 1, 4),
                new ItemStack(ConfigItems.itemResource, 1, 15),
                new ItemStack(ConfigItems.itemWandRod, 1, 5),
                new ItemStack(ConfigItems.itemWandRod, 1, 6),
                new ItemStack(ConfigItems.itemWandRod, 1, 7));
    }

    public static void initializeInfusionEnchantmentRecipeBaseline() {
        registerInfusionEnchantmentRecipe("InfEnchRepair", "INFUSIONENCHANTMENT",
                Config.enchRepair,
                4,
                new AspectList().add(Aspect.MAGIC, 8).add(Aspect.CRAFT, 10).add(Aspect.ORDER, 10),
                new ItemStack(Blocks.ANVIL),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnchHaste", "INFUSIONENCHANTMENT",
                Config.enchHaste,
                3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TRAVEL, 8).add(Aspect.FLIGHT, 8),
                new ItemStack(ConfigItems.itemResource, 1, 1),
                new ItemStack(ConfigItems.itemResource, 1, 14));

        registerInfusionEnchantmentRecipe("InfEnch0", "INFUSIONENCHANTMENT", Enchantments.PROTECTION, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 8),
                new ItemStack(Items.IRON_INGOT), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch1", "INFUSIONENCHANTMENT", Enchantments.FIRE_PROTECTION, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.FIRE, 4),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.MAGMA_CREAM), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch2", "INFUSIONENCHANTMENT", Enchantments.FEATHER_FALLING, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.ENTROPY, 4),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GUNPOWDER), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch3", "INFUSIONENCHANTMENT", Enchantments.BLAST_PROTECTION, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.ARMOR, 4).add(Aspect.FLIGHT, 4),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.ARROW), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch4", "INFUSIONENCHANTMENT", Enchantments.PROJECTILE_PROTECTION, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.AIR, 4).add(Aspect.FLIGHT, 4),
                new ItemStack(Items.FEATHER), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch5", "INFUSIONENCHANTMENT", Enchantments.RESPIRATION, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.AIR, 8).add(Aspect.WATER, 8),
                new ItemStack(Items.REEDS), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch6", "INFUSIONENCHANTMENT", Enchantments.AQUA_AFFINITY, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.MOTION, 8).add(Aspect.WATER, 8),
                new ItemStack(Items.REEDS), new ItemStack(Items.SLIME_BALL), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch7", "INFUSIONENCHANTMENT", Enchantments.THORNS, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8).add(Aspect.PLANT, 8),
                new ItemStack(Blocks.DEADBUSH), new ItemStack(Items.QUARTZ), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch8", "INFUSIONENCHANTMENT", Enchantments.SHARPNESS, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8),
                new ItemStack(Items.IRON_SWORD), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch9", "INFUSIONENCHANTMENT", Enchantments.SMITE, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.UNDEAD, 4),
                new ItemStack(Items.IRON_SWORD), new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch10", "INFUSIONENCHANTMENT", Enchantments.BANE_OF_ARTHROPODS, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4),
                new ItemStack(Items.IRON_SWORD), new ItemStack(ConfigItems.itemResource, 1, 6), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch11", "INFUSIONENCHANTMENT", Enchantments.KNOCKBACK, 1,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 3).add(Aspect.MOTION, 3),
                new ItemStack(Blocks.PISTON), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch12", "INFUSIONENCHANTMENT", Enchantments.FIRE_ASPECT, 3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.FIRE, 8),
                new ItemStack(Items.IRON_SWORD), new ItemStack(Items.BLAZE_POWDER), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch13", "INFUSIONENCHANTMENT", Enchantments.LOOTING, 3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.GREED, 8),
                new ItemStack(Items.IRON_SWORD), new ItemStack(Items.DIAMOND), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch14", "INFUSIONENCHANTMENT", Enchantments.EFFICIENCY, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.ORDER, 4),
                new ItemStack(Items.IRON_PICKAXE), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch15", "INFUSIONENCHANTMENT", Enchantments.SILK_TOUCH, 5,
                new AspectList().add(Aspect.MAGIC, 16).add(Aspect.TOOL, 16).add(Aspect.ORDER, 16).add(Aspect.HARVEST, 16).add(Aspect.MINE, 16),
                new ItemStack(Items.IRON_PICKAXE), new ItemStack(Blocks.WEB), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch16", "INFUSIONENCHANTMENT", Enchantments.UNBREAKING, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.ORDER, 8),
                new ItemStack(Items.IRON_PICKAXE), new ItemStack(Blocks.OBSIDIAN), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch17", "INFUSIONENCHANTMENT", Enchantments.FORTUNE, 3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.TOOL, 4).add(Aspect.GREED, 8),
                new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.DIAMOND), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch18", "INFUSIONENCHANTMENT", Enchantments.POWER, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 8),
                new ItemStack(Items.BOW), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch19", "INFUSIONENCHANTMENT", Enchantments.PUNCH, 2,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 3).add(Aspect.MOTION, 3),
                new ItemStack(Blocks.PISTON), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch20", "INFUSIONENCHANTMENT", Enchantments.FLAME, 3,
                new AspectList().add(Aspect.MAGIC, 4).add(Aspect.WEAPON, 4).add(Aspect.FIRE, 8),
                new ItemStack(Items.BOW), new ItemStack(Items.BLAZE_POWDER), new ItemStack(ConfigItems.itemResource, 1, 14));
        registerInfusionEnchantmentRecipe("InfEnch21", "INFUSIONENCHANTMENT", Enchantments.INFINITY, 5,
                new AspectList().add(Aspect.MAGIC, 8).add(Aspect.WEAPON, 16).add(Aspect.VOID, 16).add(Aspect.EXCHANGE, 16),
                new ItemStack(Items.BOW), new ItemStack(Items.ARROW), new ItemStack(ConfigItems.itemResource, 1, 14));
    }

    public static void initializeInfusionFocusDeviceRecipeBaseline() {
        ConfigRecipesInfusionDeviceSlice.initializeInfusionFocusDeviceRecipeBaseline();
    }

    public static void initializeInfusionGolemDeviceRecipeBaseline() {
        ConfigRecipesInfusionDeviceSlice.initializeInfusionGolemDeviceRecipeBaseline();
    }

    public static void initializeInfusionEquipmentArmorRecipeBaseline() {
        ConfigRecipesInfusionEquipmentSlice.initializeInfusionEquipmentArmorRecipeBaseline();
    }

    static void registerInfusionRecipe(String key, String research, Object output, int instability, AspectList aspects,
                                       ItemStack centralInput, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionCraftingRecipe(research, output, instability, aspects, centralInput, components));
    }

    private static void registerInfusionEnchantmentRecipe(String key, String research, Enchantment enchantment, int instability,
                                                          AspectList aspects, ItemStack... components) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionEnchantmentRecipe(research, enchantment, instability, aspects, components));
    }

    private static int getWandCapCost(String tag) {
        WandCap cap = WandCap.caps.get(tag);
        return cap != null ? cap.getCraftCost() : 0;
    }

    private static int getWandRodCost(String tag) {
        WandRod rod = WandRod.rods.get(tag);
        return rod != null ? rod.getCraftCost() : 0;
    }
}
