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

        assertTrue("Flux goo must be a Forge finite fluid using the TC4 flux material and registered fluxGoo Fluid",
                goo.contains("extends BlockFluidFinite")
                        && goo.contains("super(ConfigBlocks.FLUXGOO, Config.fluxGoomaterial);")
                        && goo.contains("this.setCreativeTab(Thaumcraft.tabTC);")
                        && goo.contains("state.getValue(BlockFluidBase.LEVEL)")
                        && goo.contains("getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)")
                        && goo.contains("return originalColor;")
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
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
