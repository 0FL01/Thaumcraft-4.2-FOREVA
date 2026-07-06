package thaumcraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.lib.utils.CropUtils;

public class ThaumcraftApi {
    public static Item.ToolMaterial toolMatThaumium = EnumHelper.addToolMaterial((String)"THAUMIUM", (int)3, (int)400, (float)7.0f, (float)2.0f, (int)22);
    public static Item.ToolMaterial toolMatVoid = EnumHelper.addToolMaterial((String)"VOID", (int)4, (int)150, (float)8.0f, (float)3.0f, (int)10);
    public static Item.ToolMaterial toolMatElemental = EnumHelper.addToolMaterial((String)"THAUMIUM_ELEMENTAL", (int)3, (int)1500, (float)10.0f, (float)3.0f, (int)18);
    public static ItemArmor.ArmorMaterial armorMatThaumium = EnumHelper.addArmorMaterial("THAUMIUM", "thaumcraft:thaumium", 25, new int[]{2, 6, 5, 2}, 25, (SoundEvent)null, 0.0f);
    public static ItemArmor.ArmorMaterial armorMatSpecial = EnumHelper.addArmorMaterial("SPECIAL", "thaumcraft:special", 25, new int[]{1, 3, 2, 1}, 25, (SoundEvent)null, 0.0f);
    public static ItemArmor.ArmorMaterial armorMatThaumiumFortress = EnumHelper.addArmorMaterial("FORTRESS", "thaumcraft:fortress", 40, new int[]{3, 7, 6, 3}, 25, (SoundEvent)null, 0.0f);
    public static ItemArmor.ArmorMaterial armorMatVoid = EnumHelper.addArmorMaterial("VOID", "thaumcraft:void", 10, new int[]{3, 7, 6, 3}, 10, (SoundEvent)null, 0.0f);
    public static ItemArmor.ArmorMaterial armorMatVoidFortress = EnumHelper.addArmorMaterial("VOIDFORTRESS", "thaumcraft:voidfortress", 18, new int[]{4, 8, 7, 4}, 10, (SoundEvent)null, 0.0f);
    public static int enchantFrugal;
    public static int enchantPotency;
    public static int enchantWandFortune;
    public static int enchantHaste;
    public static int enchantRepair;
    public static ArrayList<Block> portableHoleBlackList;
    public static IInternalMethodHandler internalMethods;
    public static ArrayList<IScanEventHandler> scanEventhandlers;
    public static ArrayList<EntityTags> scanEntities;
    private static ArrayList craftingRecipes;
    private static HashMap<Object, ItemStack> smeltingBonus;
    private static HashMap<int[], Object[]> keyCache;
    public static ConcurrentHashMap<List, AspectList> objectTags;
    public static ConcurrentHashMap<List, int[]> groupedObjectTags;
    private static HashMap<Object, Integer> warpMap;
    private static HashMap<String, ItemStack> seedList;
    public static ArrayList<ResourceLocation> researchLocations;

    public static void registerScanEventhandler(IScanEventHandler scanEventHandler) {
        scanEventhandlers.add(scanEventHandler);
    }

    public static void registerEntityTag(String entityName, AspectList aspects, EntityTagsNBT ... nbt) {
        scanEntities.add(new EntityTags(entityName, aspects, nbt));
    }

    public static void addSmeltingBonus(ItemStack in, ItemStack out) {
        smeltingBonus.put(Arrays.asList(in.getItem(), in.getMetadata()), new ItemStack(out.getItem(), 0, out.getMetadata()));
    }

    public static void addSmeltingBonus(String in, ItemStack out) {
        smeltingBonus.put(in, new ItemStack(out.getItem(), 0, out.getMetadata()));
    }

    public static ItemStack getSmeltingBonus(ItemStack in) {
        ItemStack out = smeltingBonus.get(Arrays.asList(in.getItem(), in.getMetadata()));
        if (out == null) {
            out = smeltingBonus.get(Arrays.asList(in.getItem(), (int)Short.MAX_VALUE));
        }
        if (out == null) {
            int[] oreIDs = OreDictionary.getOreIDs(in);
            if (oreIDs.length > 0) {
                String od = OreDictionary.getOreName(oreIDs[0]);
                out = smeltingBonus.get(od);
            }
        }
        return out;
    }

    public static List getCraftingRecipes() {
        return craftingRecipes;
    }

    public static ShapedArcaneRecipe addArcaneCraftingRecipe(String research, ItemStack result, AspectList aspects, Object ... recipe) {
        ShapedArcaneRecipe r = new ShapedArcaneRecipe(research, result, aspects, recipe);
        craftingRecipes.add(r);
        return r;
    }

    public static ShapelessArcaneRecipe addShapelessArcaneCraftingRecipe(String research, ItemStack result, AspectList aspects, Object ... recipe) {
        ShapelessArcaneRecipe r = new ShapelessArcaneRecipe(research, result, aspects, recipe);
        craftingRecipes.add(r);
        return r;
    }

    public static InfusionRecipe addInfusionCraftingRecipe(String research, Object result, int instability, AspectList aspects, ItemStack input, ItemStack[] recipe) {
        if (!(result instanceof ItemStack) && !(result instanceof Object[])) {
            return null;
        }
        InfusionRecipe r = new InfusionRecipe(research, result, instability, aspects, input, recipe);
        craftingRecipes.add(r);
        return r;
    }

    public static InfusionEnchantmentRecipe addInfusionEnchantmentRecipe(String research, Enchantment enchantment, int instability, AspectList aspects, ItemStack[] recipe) {
        InfusionEnchantmentRecipe r = new InfusionEnchantmentRecipe(research, enchantment, instability, aspects, recipe);
        craftingRecipes.add(r);
        return r;
    }

    public static InfusionRecipe getInfusionRecipe(ItemStack res) {
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof InfusionRecipe) || !(((InfusionRecipe)r).getRecipeOutput() instanceof ItemStack) || !((ItemStack)((InfusionRecipe)r).getRecipeOutput()).isItemEqual(res)) continue;
            return (InfusionRecipe)r;
        }
        return null;
    }

    public static CrucibleRecipe addCrucibleRecipe(String key, ItemStack result, Object catalyst, AspectList tags) {
        CrucibleRecipe rc = new CrucibleRecipe(key, result, catalyst, tags);
        ThaumcraftApi.getCraftingRecipes().add(rc);
        return rc;
    }

    public static CrucibleRecipe getCrucibleRecipe(ItemStack stack) {
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof CrucibleRecipe) || !((CrucibleRecipe)r).getRecipeOutput().isItemEqual(stack)) continue;
            return (CrucibleRecipe)r;
        }
        return null;
    }

    public static CrucibleRecipe getCrucibleRecipeFromHash(int hash) {
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof CrucibleRecipe) || ((CrucibleRecipe)r).hash != hash) continue;
            return (CrucibleRecipe)r;
        }
        return null;
    }

    public static Object[] getCraftingRecipeKey(EntityPlayer player, ItemStack stack) {
        int[] key = new int[]{Item.getIdFromItem(stack.getItem()), stack.getMetadata()};
        if (keyCache.containsKey(key)) {
            if (keyCache.get(key) == null) {
                return null;
            }
            if (ThaumcraftApiHelper.isResearchComplete(player.getName(), (String)keyCache.get(key)[0])) {
                return keyCache.get(key);
            }
            return null;
        }
        for (ResearchCategoryList rcl : ResearchCategories.researchCategories.values()) {
            for (ResearchItem ri : rcl.research.values()) {
                if (ri.getPages() == null) continue;
                for (int a = 0; a < ri.getPages().length; ++a) {
                    ResearchPage page = ri.getPages()[a];
                    if (page.recipe != null && page.recipe instanceof CrucibleRecipe[]) {
                        CrucibleRecipe[] crs;
                        for (CrucibleRecipe cr : crs = (CrucibleRecipe[])page.recipe) {
                            if (!cr.getRecipeOutput().isItemEqual(stack)) continue;
                            keyCache.put(key, new Object[]{ri.key, a});
                            if (!ThaumcraftApiHelper.isResearchComplete(player.getName(), ri.key)) continue;
                            return new Object[]{ri.key, a};
                        }
                        continue;
                    }
                    if (page.recipeOutput == null || stack == null || !page.recipeOutput.isItemEqual(stack)) continue;
                    keyCache.put(key, new Object[]{ri.key, a});
                    if (ThaumcraftApiHelper.isResearchComplete(player.getName(), ri.key)) {
                        return new Object[]{ri.key, a};
                    }
                    return null;
                }
            }
        }
        keyCache.put(key, null);
        return null;
    }

    public static boolean exists(Item item, int meta) {
        AspectList tmp = objectTags.get(Arrays.asList(item, meta));
        if (tmp == null) {
            int[] group = groupedObjectTags.get(Arrays.asList(item, meta));
            if (group != null && group.length > 0) {
                tmp = objectTags.get(Arrays.asList(item, group[0]));
            }
        }
        if (tmp == null) {
            tmp = objectTags.get(Arrays.asList(item, (int)Short.MAX_VALUE));
            if (meta == Short.MAX_VALUE && tmp == null) {
                int index = 0;
                do {
                    tmp = objectTags.get(Arrays.asList(item, index));
                    if (tmp == null) {
                        int[] group = groupedObjectTags.get(Arrays.asList(item, index));
                        if (group != null && group.length > 0) {
                            tmp = objectTags.get(Arrays.asList(item, group[0]));
                        }
                    }
                } while (++index < 16 && tmp == null);
            }
            if (tmp == null) {
                return false;
            }
        }
        return true;
    }

    public static void registerObjectTag(ItemStack item, AspectList aspects) {
        if (aspects == null) {
            aspects = new AspectList();
        }
        try {
            objectTags.put(Arrays.asList(item.getItem(), item.getMetadata()), aspects);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void registerObjectTag(ItemStack item, int[] meta, AspectList aspects) {
        if (aspects == null) {
            aspects = new AspectList();
        }
        try {
            objectTags.put(Arrays.asList(item.getItem(), meta[0]), aspects);
            for (int m : meta) {
                groupedObjectTags.put(Arrays.asList(item.getItem(), m), meta);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void registerObjectTag(String oreDict, AspectList aspects) {
        if (aspects == null) {
            aspects = new AspectList();
        }
        for (ItemStack ore : OreDictionary.getOres(oreDict)) {
            try {
                objectTags.put(Arrays.asList(ore.getItem(), ore.getMetadata()), aspects);
            } catch (Exception e) {}
        }
    }

    public static void registerComplexObjectTag(String oreDict, AspectList aspects) {
        if (aspects == null) {
            aspects = new AspectList();
        }
        for (ItemStack ore : OreDictionary.getOres(oreDict)) {
            try {
                ThaumcraftApi.registerComplexObjectTag(ore, aspects.copy());
            } catch (Exception e) {}
        }
    }

    public static void registerComplexObjectTag(ItemStack item, AspectList aspects) {
        if (!ThaumcraftApi.exists(item.getItem(), item.getMetadata())) {
            AspectList tmp = ThaumcraftApiHelper.generateTags(item.getItem(), item.getMetadata());
            if (tmp != null && tmp.size() > 0) {
                for (Aspect tag : tmp.getAspects()) {
                    aspects.add(tag, tmp.getAmount(tag));
                }
            }
            ThaumcraftApi.registerObjectTag(item, aspects);
        } else {
            AspectList tmp = ThaumcraftApiHelper.getObjectAspects(item);
            for (Aspect tag : aspects.getAspects()) {
                tmp.merge(tag, tmp.getAmount(tag));
            }
            ThaumcraftApi.registerObjectTag(item, tmp);
        }
    }

    public static void addWarpToItem(ItemStack craftresult, int amount) {
        warpMap.put(Arrays.asList(craftresult.getItem(), craftresult.getMetadata()), amount);
    }

    public static void addWarpToResearch(String research, int amount) {
        warpMap.put(research, amount);
    }

    public static int getWarp(Object in) {
        if (in == null) {
            return 0;
        }
        if (in instanceof ItemStack && warpMap.containsKey(Arrays.asList(((ItemStack)in).getItem(), ((ItemStack)in).getMetadata()))) {
            return warpMap.get(Arrays.asList(((ItemStack)in).getItem(), ((ItemStack)in).getMetadata()));
        }
        if (in instanceof String && warpMap.containsKey((String)in)) {
            return warpMap.get((String)in);
        }
        return 0;
    }

    public static void addLootBagItem(ItemStack item, int weight, int ... bagTypes) {
        if (bagTypes == null || bagTypes.length == 0) {
            WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
        } else {
            block5: for (int rarity : bagTypes) {
                switch (rarity) {
                    case 0: {
                        WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
                        continue block5;
                    }
                    case 1: {
                        WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item, weight));
                        continue block5;
                    }
                    case 2: {
                        WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item, weight));
                    }
                }
            }
        }
    }

    public static void registerSeed(Block crop, ItemStack seed) {
        if (crop == null || seed == null) {
            return;
        }

        // TC6 addons call registerSeed(cropBlock, seedStack). Store the TC6
        // seed lookup and also register the crop block with this
        // port's growth-lamp crop registry, which originally only accepted a
        // seed ItemStack and inferred the block from it.
        seedList.put(crop.getTranslationKey(), seed);
        CropUtils.addStandardCrop(crop, Short.MAX_VALUE);
    }

    public static ItemStack getSeed(Block crop) {
        if (crop == null) {
            return null;
        }
        return seedList.get(crop.getTranslationKey());
    }

    public static void registerResearchLocation(ResourceLocation location) {
        if (location != null && !researchLocations.contains(location)) {
            researchLocations.add(location);
        }
    }

    static {
        portableHoleBlackList = new ArrayList();
        internalMethods = new DummyInternalMethodHandler();
        scanEventhandlers = new ArrayList();
        scanEntities = new ArrayList();
        craftingRecipes = new ArrayList();
        smeltingBonus = new HashMap();
        keyCache = new HashMap();
        objectTags = new ConcurrentHashMap();
        groupedObjectTags = new ConcurrentHashMap();
        warpMap = new HashMap();
        seedList = new HashMap();
        researchLocations = new ArrayList();
    }

    public static class EntityTags {
        public String entityName;
        public EntityTagsNBT[] nbts;
        public AspectList aspects;

        public EntityTags(String entityName, AspectList aspects, EntityTagsNBT ... nbts) {
            this.entityName = entityName;
            this.nbts = nbts;
            this.aspects = aspects;
        }
    }

    public static class EntityTagsNBT {
        public String name;
        public Object value;

        public EntityTagsNBT(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
