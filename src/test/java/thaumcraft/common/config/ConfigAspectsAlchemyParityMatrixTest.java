package thaumcraft.common.config;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigAspectsAlchemyParityMatrixTest {
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
    public void vanillaAlchemyInputsMatchTc4235() throws Exception {
        invokeRegistration("registerVanillaBlocks");
        invokeRegistration("registerVanillaItems");

        assertAspects(new ItemStack(Blocks.STONE), Aspect.EARTH, 2);
        assertAspects(new ItemStack(Items.GOLD_INGOT), Aspect.METAL, 3, Aspect.GREED, 2);
        assertAspects(new ItemStack(Items.GOLD_NUGGET), Aspect.METAL, 1);
        assertAspects(new ItemStack(Items.BUCKET), Aspect.METAL, 8, Aspect.VOID, 1);
        assertAspects(new ItemStack(Items.WATER_BUCKET),
                Aspect.METAL, 8, Aspect.VOID, 1, Aspect.WATER, 4);
        assertAspects(new ItemStack(Items.LAVA_BUCKET),
                Aspect.METAL, 8, Aspect.VOID, 1, Aspect.FIRE, 4, Aspect.EARTH, 1);
    }

    @Test
    public void compatMetalMatrixKeepsTc4ValuesAndIngotGate() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/config/ConfigAspects.java")), StandardCharsets.UTF_8);
        int start = source.indexOf("private static void registerOreDictionary()");
        int end = source.indexOf("private static void registerThaumcraftAlchemyBaseline()", start);
        String matrix = source.substring(start, end);

        assertFalse(matrix.contains("foundCopperOre"));
        assertFalse(matrix.contains("foundTinOre"));
        assertFalse(matrix.contains("foundSilverOre"));
        assertFalse(matrix.contains("foundLeadOre"));
        assertTrue(matrix.contains("if (thaumcraft.common.config.Config.foundCopperIngot)"));
        assertTrue(matrix.contains("\"ingotCopper\", new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 1)"));
        assertTrue(matrix.contains("\"dustCopper\", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.EXCHANGE, 1)"));
        assertTrue(matrix.contains("\"oreCopper\", new AspectList().add(Aspect.METAL, 2).add(Aspect.EARTH, 1).add(Aspect.EXCHANGE, 1)"));

        assertTrue(matrix.contains("if (thaumcraft.common.config.Config.foundTinIngot)"));
        assertTrue(matrix.contains("\"ingotTin\", new AspectList().add(Aspect.METAL, 3).add(Aspect.CRYSTAL, 1)"));
        assertTrue(matrix.contains("\"dustTin\", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.CRYSTAL, 1)"));
        assertTrue(matrix.contains("\"oreTin\", new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1).add(Aspect.CRYSTAL, 1)"));

        assertTrue(matrix.contains("if (thaumcraft.common.config.Config.foundSilverIngot)"));
        assertTrue(matrix.contains("\"ingotSilver\", new AspectList().add(Aspect.METAL, 3).add(Aspect.GREED, 1)"));
        assertTrue(matrix.contains("\"dustSilver\", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.GREED, 1)"));
        assertTrue(matrix.contains("\"oreSilver\", new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1).add(Aspect.GREED, 1)"));

        assertTrue(matrix.contains("if (thaumcraft.common.config.Config.foundLeadIngot)"));
        assertTrue(matrix.contains("\"ingotLead\", new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 1)"));
        assertTrue(matrix.contains("\"dustLead\", new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.ORDER, 1)"));
        assertTrue(matrix.contains("\"oreLead\", new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1).add(Aspect.ORDER, 1)"));
    }

    private static void invokeRegistration(String name) throws Exception {
        Method method = ConfigAspects.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(null);
    }

    private static void assertAspects(ItemStack stack, Object... expected) {
        AspectList actual = ThaumcraftCraftingManager.getObjectTags(stack);
        assertEquals(expected.length / 2, actual.size());
        for (int i = 0; i < expected.length; i += 2) {
            assertEquals(expected[i + 1], actual.getAmount((Aspect) expected[i]));
        }
    }
}
