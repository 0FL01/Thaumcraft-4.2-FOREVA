package thaumcraft.common.lib.world.dim;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OuterDungeonGenerationStaticGuardTest {

    @Test
    public void outerDungeonKeepsSpawnerRoomAndPublicationContracts() throws IOException {
        String common = read("src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java");
        String passage = read("src/main/java/thaumcraft/common/lib/world/dim/GenPassage.java");
        String entities = read("src/main/java/thaumcraft/common/config/ConfigEntities.java");
        String boss = read("src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java");
        String mazeGenerator = read("src/main/java/thaumcraft/common/lib/world/dim/MazeGenerator.java");
        String mazeThread = read("src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java");
        String worldGenerator = read("src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java");
        String altar = read("src/main/java/thaumcraft/common/tiles/TileEldritchAltar.java");
        String chunkProvider = read("src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java");
        String blockUtils = read("src/main/java/thaumcraft/common/lib/utils/BlockUtils.java");
        String loot = read("src/main/java/thaumcraft/common/blocks/BlockLoot.java");
        String permanentItem = read("src/main/java/thaumcraft/common/entities/EntityPermanentItem.java");
        String crabSpawner = read("src/main/java/thaumcraft/common/tiles/TileEldritchCrabSpawner.java");
        String crystal = read("src/main/java/thaumcraft/common/blocks/BlockCrystal.java");

        assertTrue(common.contains("b == STONE && cell.feature == 7")
                && common.contains("b = CRUST;")
                && common.contains("boolean crab = cell.feature == 7 || world.rand.nextInt(50) == 0;")
                && common.contains("block = Blocks.BEDROCK;")
                && common.contains("oppositeBlock == Blocks.BEDROCK"));
        assertFalse(common.contains("boolean bl"));

        assertTrue(entities.contains("public static final ResourceLocation MIND_SPIDER_ID")
                && entities.contains("ConfigBlocks.legacyPath(\"MindSpider\")"));
        assertTrue(passage.contains("!p.equals(spawnerPos)")
                && passage.contains("setEntityId(ConfigEntities.MIND_SPIDER_ID)"));
        assertFalse(passage.contains("mind_spider"));

        assertTrue(boss.contains("if (dir == null) return;"));
        assertTrue(mazeGenerator.contains("if (!connected) {")
                && mazeGenerator.contains("success = false;"));
        assertFalse(mazeThread.contains("(short) 0"));
        assertFalse(worldGenerator.contains("new Thread(new MazeThread"));
        assertFalse(altar.contains("new Thread(new MazeThread"));
        assertTrue(chunkProvider.contains("try {")
                && chunkProvider.contains("finally {")
                && chunkProvider.contains("BlockFalling.fallInstantly = false;"));

        assertTrue(blockUtils.contains("for (int xx = -1; xx <= 1; xx++)")
                && blockUtils.contains("for (int yy = -1; yy <= 1; yy++)")
                && blockUtils.contains("for (int zz = -1; zz <= 1; zz++)")
                && blockUtils.contains("maxMeta == Short.MAX_VALUE || block.getMetaFromState(state) == maxMeta"));
        assertTrue(loot.contains("int rolls = 1 + meta + random.nextInt(3);"));
        assertTrue(permanentItem.contains("class EntityPermanentItem extends EntitySpecialItem"));

        assertTrue(crabSpawner.contains("for (int i = 0; i < 3; i++)")
                && crabSpawner.contains("this.world.rand.nextInt(20) == 0")
                && crabSpawner.contains("Thaumcraft.proxy.drawVentParticles("));

        assertTrue(common.contains("world.setBlockState(crystalPos, ConfigBlocks.blockCrystal.getStateFromMeta(7), 3);")
                && common.contains("((TileCrystal) te).orientation = (short) facing.ordinal();"));
        assertFalse(crystal.contains("void onBlockAdded("));
        assertTrue(crystal.contains("EnumFacing attachment = EnumFacing.byIndex(crystal.orientation);")
                && crystal.contains("BlockPos support = pos.offset(attachment.getOpposite());")
                && crystal.contains("return !worldIn.isAirBlock(support);")
                && crystal.contains("return worldIn.isSideSolid(support, attachment);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
