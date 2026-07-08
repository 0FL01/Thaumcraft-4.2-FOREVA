package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PavingStoneParityStaticGuardTest {

    @Test
    public void travelStoneShouldKeepTc4WalkEffectsAndSparkle() throws IOException {
        String solid = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java");

        assertTrue("Paving Stone of Travel must keep TC4 type id and walk-effect surface",
                solid.contains("public static final int TYPE_TRAVEL = 2")
                        && solid.contains("public void onEntityWalk(World world, BlockPos pos, Entity entity)")
                        && solid.contains("MobEffects.SPEED, 40, 1, false, false")
                        && solid.contains("MobEffects.JUMP_BOOST, 40, 0, false, false")
                        && solid.contains("Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 32768, 5)"));

        assertTrue("Paving Stone of Travel and Warding should keep TC4 no-mob-spawn contract",
                solid.contains("public boolean canCreatureSpawn")
                        && solid.contains("meta == TYPE_TRAVEL || meta == TYPE_WARDING"));
    }

    @Test
    public void wardingStoneShouldKeepTc4RedstoneGapAndBlockingRunes() throws IOException {
        String solid = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java");
        String runes = read("src/main/java/thaumcraft/client/fx/particles/FXBlockRunes.java");

        assertTrue("Paving Stone of Warding must keep TC4 type id and random display tick FX surface",
                solid.contains("public static final int TYPE_WARDING = 3")
                        && solid.contains("public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)")
                        && solid.contains("world.isBlockPowered(pos)")
                        && solid.contains("Thaumcraft.proxy.particleCount(2)")
                        && solid.contains("0.8F + rand.nextFloat() * 0.2F, 20, -0.02F")
                        && solid.contains("Thaumcraft.proxy.particleCount(3)")
                        && solid.contains("rand.nextFloat() * 0.3F, 24, -0.02F")
                        && solid.contains("0.3F + rand.nextFloat() * 0.7F, 20, 0.0F"));

        assertTrue("FXBlockRunes must use the TC particle sheet through ParticleEngine, not vanilla particles.png",
                runes.contains("extends Particle implements ITCParticle")
                        && runes.contains("this.runeIndex = 224 + this.rand.nextInt(16);")
                        && runes.contains("float v0 = 0.375F;")
                        && runes.contains("public int getTCParticleLayer()")
                        && runes.contains("return 1;")
                        && !runes.contains("textures/particle/particles.png"));
    }

    @Test
    public void airyWardingFenceShouldBeInvisibleAndRedstoneGated() throws IOException {
        String airy = read("src/main/java/thaumcraft/common/blocks/BlockAiry.java");

        assertTrue("Warding aura block must be invisible and only collide when supported by unpowered warding stone",
                airy.contains("meta == 0 || meta == 4 || meta == 5 ? EnumBlockRenderType.INVISIBLE")
                        && airy.contains("private static boolean isActiveWardingStoneSupport(World world, BlockPos pos)")
                        && airy.contains("BlockCosmeticSolid.TYPE_WARDING")
                        && airy.contains("return !world.isBlockPowered(basePos);"));
    }

    @Test
    public void legacyOpaquePavingMetasShouldNotExposeDeadDuplicateItems() throws IOException {
        String opaque = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticOpaque.java");

        assertTrue("Legacy opaque paving metas should be hidden and redirected to functional solid paving stones",
                opaque.contains("for (int i = 0; i < 3; i++)")
                        && opaque.contains("meta == 3 || meta == 4")
                        && opaque.contains("BlockCosmeticSolid.TYPE_WARDING : BlockCosmeticSolid.TYPE_TRAVEL"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
