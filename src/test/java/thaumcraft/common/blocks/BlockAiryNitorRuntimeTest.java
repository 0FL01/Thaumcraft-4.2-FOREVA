package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNitor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BlockAiryNitorRuntimeTest {
    private static final BlockPos POS = new BlockPos(3, 64, 5);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void airyStatesNeverBakeOrdinaryBlockGeometry() {
        for (int meta = 0; meta <= 12; meta++) {
            assertEquals(EnumBlockRenderType.INVISIBLE,
                    ConfigBlocks.blockAiry.getRenderType(ConfigBlocks.blockAiry.getStateFromMeta(meta)));
        }
    }

    @Test
    public void nitorKeepsLightParticlesShapeWithoutPhysicalCollision() {
        IBlockState nitor = ConfigBlocks.blockAiry.getStateFromMeta(1);
        AxisAlignedBB localBounds = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

        assertEquals(localBounds, ConfigBlocks.blockAiry.getBoundingBox(nitor, null, POS));
        assertEquals(localBounds.offset(POS), ConfigBlocks.blockAiry.getSelectedBoundingBox(nitor, null, POS));
        assertNull(ConfigBlocks.blockAiry.getCollisionBoundingBox(nitor, null, POS));
        List<AxisAlignedBB> collisions = new ArrayList<>();
        ConfigBlocks.blockAiry.addCollisionBoxToList(nitor, null, POS, localBounds.offset(POS),
                collisions, null, false);
        assertTrue(collisions.isEmpty());
        assertEquals(15, ConfigBlocks.blockAiry.getLightValue(nitor));
        assertTrue(ConfigBlocks.blockAiry.createTileEntity(null, nitor) instanceof TileNitor);
    }

    @Test
    public void creativeInventoryExposesOnlyTheAuraNodeBlockItem() {
        NonNullList<ItemStack> items = NonNullList.create();

        ConfigBlocks.blockAiry.getSubBlocks(CreativeTabs.SEARCH, items);

        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getMetadata());
    }
}
