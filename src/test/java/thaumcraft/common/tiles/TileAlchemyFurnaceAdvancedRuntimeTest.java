package thaumcraft.common.tiles;

import java.util.Arrays;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockAlchemyFurnace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TileAlchemyFurnaceAdvancedRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void blockMetadataCreatesOnlyTheCenterAndLowerNozzleTilesAndNeverListsAnItem() {
        BlockAlchemyFurnace block = new BlockAlchemyFurnace();
        for (int meta = BlockAlchemyFurnace.CENTER; meta <= BlockAlchemyFurnace.LOWER_CORNER; ++meta) {
            assertEquals(meta, block.getMetaFromState(block.getStateFromMeta(meta)));
            assertEquals(EnumBlockRenderType.INVISIBLE, block.getRenderType(block.getStateFromMeta(meta)));
        }
        assertTrue(block.createNewTileEntity(null, BlockAlchemyFurnace.CENTER)
                instanceof TileAlchemyFurnaceAdvanced);
        assertTrue(block.createNewTileEntity(null, BlockAlchemyFurnace.LOWER_NOZZLE)
                instanceof TileAlchemyFurnaceAdvancedNozzle);
        assertNull(block.createNewTileEntity(null, BlockAlchemyFurnace.UPPER_CORNER));
        assertNull(block.createNewTileEntity(null, BlockAlchemyFurnace.UPPER_CARDINAL));
        assertNull(block.createNewTileEntity(null, BlockAlchemyFurnace.LOWER_CORNER));

        NonNullList<ItemStack> items = NonNullList.create();
        block.getSubBlocks(CreativeTabs.SEARCH, items);
        assertTrue(items.isEmpty());
    }

    @Test
    public void processingConsumesPowerAddsAspectsAndEnforcesCooldownAndCapacity() {
        Item ingredient = new Item();
        ItemStack stack = new ItemStack(ingredient);
        ThaumcraftApi.registerObjectTag(stack, new AspectList().add(Aspect.AIR, 4));
        try {
            TileAlchemyFurnaceAdvanced furnace = new TileAlchemyFurnaceAdvanced();
            furnace.heat = furnace.maxPower;
            furnace.power1 = furnace.maxPower;
            furnace.power2 = furnace.maxPower;

            assertTrue(furnace.process(stack));
            assertEquals(492, furnace.heat);
            assertEquals(496, furnace.power1);
            assertEquals(496, furnace.power2);
            assertEquals(4, furnace.aspects.getAmount(Aspect.AIR));
            assertEquals(4, furnace.vis);
            assertTrue(furnace.processed > 0);
            assertFalse(furnace.process(stack));

            furnace.processed = 0;
            furnace.aspects = new AspectList().add(Aspect.FIRE, 498);
            furnace.vis = furnace.aspects.visSize();
            assertFalse(furnace.process(stack));
        } finally {
            ThaumcraftApi.objectTags.remove(Arrays.asList(ingredient, 0));
        }
    }

    @Test
    public void emittedLightUsesTheSameBoundaryAsTheBlockRenderer() {
        assertEquals(0, BlockAlchemyFurnace.getHeatLight(100, 500));
        assertEquals(11, BlockAlchemyFurnace.getHeatLight(459, 500));
        assertEquals(10, BlockAlchemyFurnace.getHeatLight(458, 500));
        assertEquals(12, BlockAlchemyFurnace.getHeatLight(500, 500));
    }

    @Test
    public void updatePacketRoundTripCarriesAllBackendStateAndExactRenderBounds() {
        TileAlchemyFurnaceAdvanced source = new TileAlchemyFurnaceAdvanced();
        source.setPos(new BlockPos(10, 20, 30));
        source.aspects.add(Aspect.FIRE, 7).add(Aspect.WATER, 3);
        source.vis = 10;
        source.heat = 421;
        source.power1 = 312;
        source.power2 = 287;
        source.processed = 19;
        source.destroy = true;

        SPacketUpdateTileEntity packet = source.getUpdatePacket();
        NBTTagCompound packetNbt = packet.getNbtCompound();
        assertTrue(packetNbt.hasKey("Aspects"));
        assertTrue(packetNbt.hasKey("vis"));
        assertTrue(packetNbt.hasKey("heat"));
        assertTrue(packetNbt.hasKey("power1"));
        assertTrue(packetNbt.hasKey("power2"));
        assertTrue(packetNbt.hasKey("processed"));
        assertTrue(packetNbt.hasKey("destroy"));

        TileAlchemyFurnaceAdvanced restored = new TileAlchemyFurnaceAdvanced();
        restored.onDataPacket(null, packet);
        assertEquals(7, restored.aspects.getAmount(Aspect.FIRE));
        assertEquals(3, restored.aspects.getAmount(Aspect.WATER));
        assertEquals(10, restored.vis);
        assertEquals(421, restored.heat);
        assertEquals(312, restored.power1);
        assertEquals(287, restored.power2);
        assertEquals(19, restored.processed);
        assertTrue(restored.destroy);

        assertEquals(new AxisAlignedBB(9.0D, 20.0D, 29.0D, 12.0D, 22.0D, 32.0D),
                source.getRenderBoundingBox());
    }

    @Test
    public void nozzleOnlyOutputsThroughItsResolvedFaceAndHandlesAnEmptyTank() {
        TileAlchemyFurnaceAdvanced furnace = new TileAlchemyFurnaceAdvanced();
        TileAlchemyFurnaceAdvancedNozzle nozzle = new TileAlchemyFurnaceAdvancedNozzle();
        nozzle.furnace = furnace;
        nozzle.facing = EnumFacing.EAST;

        assertNull(nozzle.getEssentiaType(EnumFacing.EAST));
        assertEquals(0, nozzle.getEssentiaAmount(EnumFacing.EAST));
        assertFalse(nozzle.canOutputTo(EnumFacing.WEST));

        furnace.aspects.add(Aspect.FIRE, 3);
        furnace.vis = 3;
        assertTrue(nozzle.canOutputTo(EnumFacing.EAST));
        assertEquals(Aspect.FIRE, nozzle.getEssentiaType(EnumFacing.EAST));
        assertEquals(3, nozzle.getEssentiaAmount(EnumFacing.EAST));
        assertEquals(2, nozzle.takeEssentia(Aspect.FIRE, 2, EnumFacing.EAST));
        assertEquals(1, furnace.vis);
        assertEquals(1, furnace.aspects.getAmount(Aspect.FIRE));
    }
}
