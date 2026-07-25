package thaumcraft.common.blocks;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.BlockRenderLayer;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTaintFibresRenderingRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTaintFibres == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void tc4FibresUseTranslucentRenderingForTheirPartialAlphaTexture() {
        assertEquals(BlockRenderLayer.TRANSLUCENT, ConfigBlocks.blockTaintFibres.getRenderLayer());
        assertTrue(ConfigBlocks.blockTaintFibres.canRenderInLayer(
                ConfigBlocks.blockTaintFibres.getDefaultState(), BlockRenderLayer.TRANSLUCENT));
        assertFalse(ConfigBlocks.blockTaintFibres.canRenderInLayer(
                ConfigBlocks.blockTaintFibres.getDefaultState(), BlockRenderLayer.CUTOUT_MIPPED));
    }
}
