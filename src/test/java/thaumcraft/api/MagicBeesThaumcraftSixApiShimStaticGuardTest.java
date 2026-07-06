package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MagicBeesThaumcraftSixApiShimStaticGuardTest {

    @Test
    public void blocksTcMustExposeMagicBeesThaumcraftSixBlockFields() throws IOException {
        String blocksTc = readFile("src/main/java/thaumcraft/api/blocks/BlocksTC.java");
        String configBlocks = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String compat = readFile("src/main/java/thaumcraft/common/compat/ThaumcraftSixCompatibility.java");

        assertTrue("Magic Bees links TC6 BlocksTC fields during thaumcraft integration init",
                blocksTc.contains("public static Block shimmerleaf;")
                        && blocksTc.contains("public static Block cinderpearl;")
                        && blocksTc.contains("public static Block vishroom;")
                        && blocksTc.contains("public static Block crystalAir;")
                        && blocksTc.contains("public static Block crystalFire;")
                        && blocksTc.contains("public static Block crystalWater;")
                        && blocksTc.contains("public static Block crystalEarth;")
                        && blocksTc.contains("public static Block crystalOrder;")
                        && blocksTc.contains("public static Block crystalEntropy;")
                        && blocksTc.contains("public static Block crystalTaint;"));
        assertTrue("BlocksTC fields must be populated after ConfigBlocks creates the registered blocks",
                configBlocks.contains("ThaumcraftSixCompatibility.initBlockAliases();")
                        && compat.contains("BlocksTC.init();")
                        && blocksTc.contains("shimmerleaf = ConfigBlocks.blockCustomPlant;")
                        && blocksTc.contains("crystalAir = ConfigBlocks.blockCrystal;"));
    }

    @Test
    public void itemsTcMustExposeMagicBeesThaumcraftSixItemFields() throws IOException {
        String itemsTc = readFile("src/main/java/thaumcraft/api/items/ItemsTC.java");
        String configItems = readFile("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String compat = readFile("src/main/java/thaumcraft/common/compat/ThaumcraftSixCompatibility.java");
        String resourceAlias = readFile("src/main/java/thaumcraft/common/items/resources/ItemCrystalEssence.java");

        assertTrue("Magic Bees links TC6 ItemsTC fields during thaumcraft recipes/init",
                itemsTc.contains("public static Item amber;")
                        && itemsTc.contains("public static Item brain;")
                        && itemsTc.contains("public static Item celestialNotes;")
                        && itemsTc.contains("public static Item chunks;")
                        && itemsTc.contains("public static Item crystalEssence;")
                        && itemsTc.contains("public static Item curio;")
                        && itemsTc.contains("public static Item nuggets;"));
        assertTrue("ItemsTC fields must be populated after ConfigItems creates registered items",
                configItems.contains("ThaumcraftSixCompatibility.initItemAliases();")
                        && compat.contains("ItemsTC.init();")
                        && itemsTc.contains("brain = ConfigItems.itemZombieBrain;")
                        && itemsTc.contains("crystalEssence = ConfigItems.itemCrystalEssence;")
                        && itemsTc.contains("nuggets = ConfigItems.itemNugget;"));
        assertTrue("Magic Bees casts ItemsTC.crystalEssence to the TC6 resources package class",
                resourceAlias.contains("package thaumcraft.common.items.resources;")
                        && resourceAlias.contains("extends thaumcraft.common.items.ItemCrystalEssence"));
    }

    @Test
    public void auraSoundsResearchAndEntityAliasesMustCoverMagicBeesRuntimeSurface() throws IOException {
        String apiAura = readFile("src/main/java/thaumcraft/api/aura/AuraHelper.java");
        String auraChunk = readFile("src/main/java/thaumcraft/common/world/aura/AuraChunk.java");
        String auraHandler = readFile("src/main/java/thaumcraft/common/world/aura/AuraHandler.java");
        String sounds = readFile("src/main/java/thaumcraft/common/lib/SoundsTC.java");
        String api = readFile("src/main/java/thaumcraft/api/ThaumcraftApi.java");
        String taintHelper = readFile("src/main/java/thaumcraft/common/blocks/world/taint/TaintHelper.java");
        String cultist = readFile("src/main/java/thaumcraft/common/entities/monster/cult/EntityCultist.java");
        String taintSeed = readFile("src/main/java/thaumcraft/common/entities/monster/tainted/EntityTaintSeed.java");

        assertTrue("Magic Bees mutation conditions call TC6 AuraHelper.getVis(World, BlockPos)",
                apiAura.contains("public static float getVis(World world, BlockPos pos)"));
        assertTrue("Magic Bees bee effects read/write TC6 AuraChunk values",
                auraChunk.contains("public short getBase()")
                        && auraChunk.contains("public void setBase(short base)")
                        && auraChunk.contains("public float getVis()")
                        && auraChunk.contains("public void setVis(float vis)")
                        && auraChunk.contains("public float getFlux()")
                        && auraChunk.contains("public void setFlux(float flux)"));
        assertTrue("Magic Bees aura effects call TC6 AuraHandler methods",
                auraHandler.contains("public static AuraChunk getAuraChunk(int dim, int chunkX, int chunkZ)")
                        && auraHandler.contains("public static void addFlux(World world, BlockPos pos, float amount)"));
        assertTrue("Magic Bees dark hunger effect links SoundsTC.wandfail",
                sounds.contains("public static final SoundEvent wandfail = TCSounds.WANDFAIL;"));
        assertTrue("Magic Bees registers a TC6 research JSON location",
                api.contains("public static void registerResearchLocation(ResourceLocation location)")
                        && api.contains("researchLocations.add(location);"));
        assertTrue("Magic Bees taint effects link TC6 taint helper and entity packages",
                taintHelper.contains("public static void addTaintSeed(World world, BlockPos pos)")
                        && taintHelper.contains("public static void spreadFibres(World world, BlockPos pos, boolean notify)")
                        && cultist.contains("package thaumcraft.common.entities.monster.cult;")
                        && taintSeed.contains("package thaumcraft.common.entities.monster.tainted;"));
    }

    @Test
    public void magicBeesSmokeModsetMustStayAvailable() throws IOException {
        String modset = readFile("scripts/smoke-modsets/magicbees.txt");

        assertTrue("Magic Bees smoke must pin the repro jars and checksums",
                modset.contains("forestry_1.12.2-5.8.2.424.jar")
                        && modset.contains("MagicBees-1.12.2-3.2.25.jar")
                        && modset.contains("fce6af7931dc054afc33fc79db7e3e4d81477591fe7bbd563cd1a5b2699342d4")
                        && modset.contains("2c780e8e7b22a374c069e3a20674fac9382fe0ef1100555197ff23eb9ab12d9e"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
