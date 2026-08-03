package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.armor.ItemVoidArmor;
import thaumcraft.common.items.equipment.ItemVoidAxe;
import thaumcraft.common.items.equipment.ItemVoidHoe;
import thaumcraft.common.items.equipment.ItemVoidPickaxe;
import thaumcraft.common.items.equipment.ItemVoidShovel;
import thaumcraft.common.items.equipment.ItemVoidSword;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigAspectsEldritchParityTest {
    private Map<List, AspectList> oldObjectTags;
    private Map<List, int[]> oldGroupedObjectTags;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void saveTagRegistry() {
        this.oldObjectTags = new ConcurrentHashMap<>(ThaumcraftApi.objectTags);
        this.oldGroupedObjectTags = new ConcurrentHashMap<>(ThaumcraftApi.groupedObjectTags);
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.groupedObjectTags.clear();
    }

    @After
    public void restoreTagRegistry() {
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.objectTags.putAll(this.oldObjectTags);
        ThaumcraftApi.groupedObjectTags.clear();
        ThaumcraftApi.groupedObjectTags.putAll(this.oldGroupedObjectTags);
    }

    @Test
    public void vanillaEldritchScanTagsMatchTc4235() throws Exception {
        invokeRegistration("registerVanillaBlocks");
        invokeRegistration("registerVanillaItems");

        assertAspects(ThaumcraftCraftingManager.getObjectTags(new ItemStack(Items.ENDER_PEARL)),
                Aspect.ELDRITCH, 4, Aspect.MAGIC, 2, Aspect.TRAVEL, 4);
        assertAspects(ThaumcraftCraftingManager.getObjectTags(new ItemStack(Items.RECORD_FAR)),
                Aspect.SENSES, 4, Aspect.AIR, 4, Aspect.ELDRITCH, 4, Aspect.GREED, 4);
        assertAspects(ThaumcraftCraftingManager.getObjectTags(new ItemStack(Items.NETHER_STAR)),
                Aspect.ELDRITCH, 8, Aspect.MAGIC, 8, Aspect.ORDER, 8, Aspect.LIGHT, 8);
        assertAspects(ThaumcraftCraftingManager.getObjectTags(new ItemStack(Blocks.DRAGON_EGG)),
                Aspect.ELDRITCH, 8, Aspect.BEAST, 8, Aspect.MAGIC, 8);
        assertAspects(ThaumcraftCraftingManager.getObjectTags(new ItemStack(Blocks.END_PORTAL_FRAME)),
                Aspect.ELDRITCH, 4, Aspect.MECHANISM, 4, Aspect.TRAVEL, 4);
    }

    @Test
    public void voidEquipmentFinalTagsMatchTc4RecipeDerivationAndBonuses() throws Exception {
        assertFinalTags(new ItemStack(new ItemVoidArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.HEAD)), 5, 0, Aspect.ARMOR, 3);
        assertFinalTags(new ItemStack(new ItemVoidArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.CHEST)), 8, 0, Aspect.ARMOR, 7);
        assertFinalTags(new ItemStack(new ItemVoidArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.LEGS)), 7, 0, Aspect.ARMOR, 6);
        assertFinalTags(new ItemStack(new ItemVoidArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.FEET)), 4, 0, Aspect.ARMOR, 3);
        assertFinalTags(new ItemStack(new ItemVoidSword(ThaumcraftApi.toolMatVoid)), 2, 1, Aspect.WEAPON, 8);
        assertFinalTags(new ItemStack(new ItemVoidPickaxe(ThaumcraftApi.toolMatVoid)), 3, 2, Aspect.MINE, 5);
        assertFinalTags(new ItemStack(new ItemVoidAxe(ThaumcraftApi.toolMatVoid)), 3, 2, Aspect.TOOL, 5);
        assertFinalTags(new ItemStack(new ItemVoidShovel(ThaumcraftApi.toolMatVoid)), 1, 2, Aspect.TOOL, 5);
        assertFinalTags(new ItemStack(new ItemVoidHoe(ThaumcraftApi.toolMatVoid)), 2, 2, Aspect.HARVEST, 3);

        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/config/ConfigAspects.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("new ItemStack(ConfigItems.itemHelmVoid), getVoidEquipmentRecipeTags(5, 0)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemChestVoid), getVoidEquipmentRecipeTags(8, 0)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemLegsVoid), getVoidEquipmentRecipeTags(7, 0)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemBootsVoid), getVoidEquipmentRecipeTags(4, 0)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemSwordVoid), getVoidEquipmentRecipeTags(2, 1)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemPickVoid), getVoidEquipmentRecipeTags(3, 2)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemAxeVoid), getVoidEquipmentRecipeTags(3, 2)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemShovelVoid), getVoidEquipmentRecipeTags(1, 2)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemHoeVoid), getVoidEquipmentRecipeTags(2, 2)"));
    }

    private static void invokeRegistration(String name) throws Exception {
        Method method = ConfigAspects.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(null);
    }

    private static void assertFinalTags(ItemStack stack, int ingots, int sticks, Aspect bonus, int amount) {
        AspectList expected = tc4RecipeTags(ingots, sticks);
        expected.merge(bonus, amount);
        expected = ThaumcraftApiHelper.cullTags(expected);
        AspectList actual = ThaumcraftCraftingManager.getBonusTags(
                stack, ConfigAspects.getVoidEquipmentRecipeTags(ingots, sticks));
        assertSameAspects(expected, actual);
    }

    private static AspectList tc4RecipeTags(int ingots, int sticks) {
        AspectList ingredients = new AspectList();
        for (int i = 0; i < ingots; ++i) {
            ingredients.add(Aspect.ELDRITCH, 5).add(Aspect.MAGIC, 2).add(Aspect.TRAVEL, 4)
                    .add(Aspect.DARKNESS, 2).add(Aspect.VOID, 2).add(Aspect.METAL, 2);
        }
        ingredients.add(Aspect.TREE, sticks);
        AspectList result = new AspectList();
        for (Aspect aspect : ingredients.getAspects()) {
            int scaled = (int)(ingredients.getAmount(aspect) * 0.75F);
            if (scaled > 0) result.add(aspect, scaled);
        }
        return result;
    }

    private static void assertAspects(AspectList actual, Object... expected) {
        assertEquals(expected.length / 2, actual.size());
        for (int i = 0; i < expected.length; i += 2) {
            assertEquals(((Integer)expected[i + 1]).intValue(), actual.getAmount((Aspect)expected[i]));
        }
    }

    private static void assertSameAspects(AspectList expected, AspectList actual) {
        assertEquals(expected.size(), actual.size());
        for (Aspect aspect : expected.getAspects()) {
            assertEquals("Mismatch for " + aspect.getTag(), expected.getAmount(aspect), actual.getAmount(aspect));
        }
    }
}
