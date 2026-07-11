package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TileWardedSyncRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void storedBlockDataSurvivesNbtRoundTrip() {
        TileWarded source = new TileWarded();
        IBlockState stored = Blocks.STONE.getStateFromMeta(3);
        source.setStoredBlock(stored, 11, "ward-owner");

        NBTTagCompound nbt = new NBTTagCompound();
        source.writeCustomNBT(nbt);
        TileWarded restored = new TileWarded();
        restored.readCustomNBT(nbt);

        assertSame(Blocks.STONE, restored.block);
        assertEquals(3, restored.blockMd & 255);
        assertEquals(11, restored.light & 255);
        assertEquals("ward-owner".hashCode(), restored.owner);
        assertEquals(stored, restored.getStoredState());
    }

    @Test
    public void updatePacketCarriesStoredFacadeAndOwner() {
        TileWarded source = new TileWarded();
        source.setStoredBlock(Blocks.PLANKS.getStateFromMeta(2), 7, "packet-owner");
        SPacketUpdateTileEntity packet = source.getUpdatePacket();

        TileWarded target = new TileWarded();
        target.onDataPacket(null, packet);

        assertSame(Blocks.PLANKS, target.block);
        assertEquals(2, target.blockMd & 255);
        assertEquals(7, target.light & 255);
        assertEquals("packet-owner".hashCode(), target.owner);
    }

    @Test
    public void legacyOwnerAndInvalidBlockUseCompatibleFallbacks() {
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setString("blockName", "missing:not_registered");
        legacy.setString("owner", "legacy-owner");

        TileWarded tile = new TileWarded();
        tile.readCustomNBT(legacy);

        assertSame(Blocks.STONE, tile.block);
        assertEquals("legacy-owner".hashCode(), tile.owner);
    }
}
