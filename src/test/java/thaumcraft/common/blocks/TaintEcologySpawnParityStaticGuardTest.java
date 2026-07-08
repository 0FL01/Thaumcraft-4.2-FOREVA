package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TaintEcologySpawnParityStaticGuardTest {

    @Test
    public void taintBiomeAndBlockSpawnsStayOnTc4EcologyRoutes() throws IOException {
        String taint = read("src/main/java/thaumcraft/common/blocks/BlockTaint.java");
        String fibres = read("src/main/java/thaumcraft/common/blocks/BlockTaintFibres.java");
        String biomeTaint = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java");
        String magicalForest = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java");
        String taintacle = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java");
        String smallTaintacle = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintacleSmall.java");
        String giantTaintacle = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java");

        assertTrue("Tainted Land should keep TC4 natural-spawn parity: only the base taintacle is a biome monster entry",
                biomeTaint.contains("this.spawnableMonsterList.clear();")
                        && biomeTaint.contains("if (Config.spawnTaintacle)")
                        && biomeTaint.contains("new Biome.SpawnListEntry(EntityTaintacle.class, 1, 1, 1)")
                        && !biomeTaint.contains("EntityTaintacleSmall")
                        && !biomeTaint.contains("EntityTaintacleGiant")
                        && !biomeTaint.contains("EntityTaintSpore"));
        assertTrue("Small and giant taintacles should remain non-natural spawns like TC4",
                smallTaintacle.contains("public boolean getCanSpawnHere()")
                        && smallTaintacle.contains("return false;")
                        && giantTaintacle.contains("public boolean getCanSpawnHere() { return false; }"));
        assertTrue("Base taintacles should keep TC4 ground rules while accepting 1.12 natural-spawn air cells above taint ground",
                taintacle.contains("private boolean isValidTaintacleSpawnGround(BlockPos pos)")
                        && taintacle.contains("this.isValidTaintacleSpawnGround(pos) || this.isValidTaintacleSpawnGround(pos.down())")
                        && taintacle.contains("state.getBlock() == ConfigBlocks.blockTaintFibres && state.getValue(BlockTaintFibres.TYPE) == 0")
                        && taintacle.contains("state.getBlock() == ConfigBlocks.blockTaint && state.getValue(BlockTaint.TYPE) == 1")
                        && taintacle.contains("private boolean isTaintBiome(BlockPos pos)")
                        && taintacle.contains("Biome.getIdForBiome(biome) == Config.biomeTaintID")
                        && taintacle.contains("Biome.getIdForBiome(biome) == Biome.getIdForBiome(ThaumcraftWorldGenerator.biomeTaint)"));
        assertTrue("Magical Forest should directly carry Pech and Wisp monster entries, independent of global post-init injection",
                magicalForest.contains("if (Config.spawnPech)")
                        && magicalForest.contains("new Biome.SpawnListEntry(EntityPech.class, 10, 1, 2)")
                        && magicalForest.contains("if (Config.spawnWisp)")
                        && magicalForest.contains("new Biome.SpawnListEntry(EntityWisp.class, 10, 1, 2)"));

        assertTrue("Fibrous taint blocks should restore the TC4 spore-swarmer block tick route",
                taint.contains("private boolean handleTaintSporeSwarmerSpawn(World world, BlockPos pos, Random rand)")
                        && taint.contains("Config.spawnTaintSpore")
                        && taint.contains("world.isAirBlock(pos.up())")
                        && taint.contains("rand.nextInt(200) != 0")
                        && taint.contains("getEntitiesWithinAABB(EntityTaintSporeSwarmer.class, new AxisAlignedBB(pos).grow(16.0D)).isEmpty()")
                        && taint.contains("world.setBlockToAir(pos)")
                        && taint.contains("new EntityTaintSporeSwarmer(world)")
                        && taint.contains("swarmer.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D")
                        && taint.contains("world.spawnEntity(swarmer)")
                        && taint.contains("TCSounds.ROOTS"));
        assertTrue("Taint fibres should mature stalks into EntityTaintSpore and reset abandoned mature stalks",
                fibres.contains("private void updateSporeStalk(World world, BlockPos pos, int meta, Random rand)")
                        && fibres.contains("meta == 3 && Config.spawnTaintSpore && rand.nextInt(10) == 0 && world.isAirBlock(pos.up())")
                        && fibres.contains("ConfigBlocks.blockTaintFibres.getStateFromMeta(4)")
                        && fibres.contains("new EntityTaintSpore(world)")
                        && fibres.contains("spore.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D")
                        && fibres.contains("world.spawnEntity(spore)")
                        && fibres.contains("meta == 4 && world.getEntitiesWithinAABB(EntityTaintSpore.class, new AxisAlignedBB(pos.up())).isEmpty()")
                        && fibres.contains("ConfigBlocks.blockTaintFibres.getStateFromMeta(3)"));
        assertTrue("Taint fibre ticks should keep adjacent taint conversion instead of only placing loose fibres",
                fibres.contains("convertAdjacentBlockToTaint(world, target)")
                        && fibres.contains("adjacent >= 2 && (Utils.isWoodLog(world, target) || targetBlock.isLeaves(targetState, world, target))")
                        && fibres.contains("adjacent >= 3 && isTaintSoilTarget(material)")
                        && fibres.contains("world.addBlockEvent(target, ConfigBlocks.blockTaint, 1, 0)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
