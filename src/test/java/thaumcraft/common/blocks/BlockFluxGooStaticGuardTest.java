package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockFluxGooStaticGuardTest {

    @Test
    public void fluxBlocksShouldUseFiniteFluidRenderingContract() throws IOException {
        String goo = read("src/main/java/thaumcraft/common/blocks/BlockFluxGoo.java");
        String gas = read("src/main/java/thaumcraft/common/blocks/BlockFluxGas.java");
        String configBlocks = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String thaumicSlime = read("src/main/java/thaumcraft/common/entities/monster/EntityThaumicSlime.java");

        assertTrue("Flux goo must be a Forge finite fluid using the TC4 flux material and registered fluxGoo Fluid",
                goo.contains("extends BlockFluidFinite")
                        && goo.contains("super(ConfigBlocks.FLUXGOO, Config.fluxGoomaterial);")
                        && goo.contains("this.setCreativeTab(Thaumcraft.tabTC);")
                        && goo.contains("state.getValue(BlockFluidBase.LEVEL)")
                        && goo.contains("getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)")
                        && goo.contains("return originalColor;")
                        && goo.contains("onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)")
                        && goo.contains("entity instanceof EntityThaumicSlime")
                        && goo.contains("THAUMIC_SLIME_GROWTH_INTERVAL_TICKS = 20")
                        && goo.contains("slime.ticksExisted % THAUMIC_SLIME_GROWTH_INTERVAL_TICKS == 0")
                        && goo.contains("slime.setSlimeSize(slime.getSlimeSize() + 1)")
                        && goo.contains("entity.motionX *= 1.0F - quanta;")
                        && goo.contains("entity.motionZ *= 1.0F - quanta;")
                        && goo.contains("new PotionEffect(Config.potionVisExhaust, 600, meta / 3, true, true)")
                        && goo.contains("pe.getCurativeItems().clear();")
                        && goo.contains("updateTick(World world, BlockPos pos, IBlockState state, Random rand)")
                        && goo.contains("super.updateTick(world, pos, state, rand);")
                        && goo.contains("meta >= 2 && meta < 6 && world.isAirBlock(above) && rand.nextInt(25) == 0")
                        && goo.contains("spawnThaumicSlime(world, pos, 1)")
                        && goo.contains("meta >= 6 && world.isAirBlock(above)")
                        && goo.contains("spawnThaumicSlime(world, pos, 2)")
                        && goo.contains("Config.taintFromFlux && rand.nextInt(50) == 0")
                        && goo.contains("Utils.setBiomeAt(world, pos.getX(), pos.getZ(), ThaumcraftWorldGenerator.biomeTaint)")
                        && goo.contains("ConfigBlocks.blockTaintFibres.getStateFromMeta(0)")
                        && goo.contains("rand.nextInt(30) == 0")
                        && goo.contains("ConfigBlocks.blockFluxGas.getStateFromMeta(0)")
                        && goo.contains("TCSounds.GORE")
                        && goo.contains("getQuanta()")
                        && goo.contains("MathHelper.clamp(meta, 0, this.getMaxRenderHeightMeta())")
                        && !goo.contains("EnumBlockRenderType.INVISIBLE")
                        && !goo.contains("Material.WATER")
                        && !goo.contains("BlockLiquid.LEVEL"));

        assertTrue("Flux gas must be a Forge finite fluid using the TC4 flux material and registered fluxGas Fluid",
                gas.contains("extends BlockFluidFinite")
                        && gas.contains("super(ConfigBlocks.FLUXGAS, Config.fluxGoomaterial);")
                        && gas.contains("this.setCreativeTab(Thaumcraft.tabTC);")
                        && gas.contains("state.getValue(BlockFluidBase.LEVEL)")
                        && gas.contains("getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)")
                        && gas.contains("return originalColor;")
                        && gas.contains("onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)")
                        && gas.contains("world.rand.nextInt(10) != 0")
                        && gas.contains("entity instanceof ITaintedMob")
                        && gas.contains("living.isEntityUndead()")
                        && gas.contains("living.isPotionActive(Config.potionVisExhaust)")
                        && gas.contains("living.isPotionActive(MobEffects.NAUSEA)")
                        && gas.contains("new PotionEffect(Config.potionVisExhaust, 1200, meta / 3, true, true)")
                        && gas.contains("new PotionEffect(MobEffects.NAUSEA, 80 + meta * 20, 0, false, true)")
                        && gas.contains("world.setBlockState(pos, this.getStateFromMeta(meta - 1), 3)")
                        && gas.contains("getQuanta()")
                        && gas.contains("MathHelper.clamp(meta, 0, this.getMaxRenderHeightMeta())")
                        && !gas.contains("EnumBlockRenderType.INVISIBLE")
                        && !gas.contains("Material.CIRCUITS"));

        assertTrue("Flux fluids must be registered before flux blocks are instantiated",
                configBlocks.contains("public static Fluid FLUXGOO;")
                        && configBlocks.contains("public static Fluid FLUXGAS;")
                        && configBlocks.contains("new Fluid(\"fluxgoo\"")
                        && configBlocks.contains("new Fluid(\"fluxgas\"")
                        && configBlocks.contains("new ResourceLocation(\"thaumcraft\", \"blocks/fluxgoo\")")
                        && configBlocks.contains("new ResourceLocation(\"thaumcraft\", \"blocks/fluxgas\")")
                        && !configBlocks.contains("new Fluid(\"fluxGoo\"")
                        && !configBlocks.contains("new Fluid(\"fluxGas\"")
                        && configBlocks.contains(".setDensity(8)")
                        && configBlocks.contains(".setDensity(-4)")
                        && configBlocks.contains(".setViscosity(6000)")
                        && configBlocks.contains(".setViscosity(2500)"));

        assertTrue("Thaumic slime must drive 1.12 jumping through EntityJumpHelper so slime jump sounds produce movement",
                thaumicSlime.contains("this.getJumpHelper().setJumping();")
                        && thaumicSlime.contains("MIN_ATTACK_JUMP_DELAY_TICKS = 8")
                        && thaumicSlime.contains("Math.max(MIN_ATTACK_JUMP_DELAY_TICKS, this.slimeJumpDelay / 2)")
                        && !thaumicSlime.contains("this.slimeJumpDelay /= 3;")
                        && !thaumicSlime.contains("this.isJumping = true;"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
