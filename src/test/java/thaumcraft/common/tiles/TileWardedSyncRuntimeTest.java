package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileWardedSyncRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void storedBlockDataSurvivesNbtRoundTrip() {
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        TestPlayer owner = player(ownerId, "ward-owner");
        TileWarded source = new TileWarded();
        IBlockState stored = Blocks.STONE.getStateFromMeta(3);
        source.setStoredBlock(stored, 11, owner);

        NBTTagCompound nbt = new NBTTagCompound();
        source.writeCustomNBT(nbt);
        TileWarded restored = new TileWarded();
        restored.readCustomNBT(nbt);

        assertSame(Blocks.STONE, restored.block);
        assertEquals(3, restored.blockMd & 255);
        assertEquals(11, restored.light & 255);
        assertEquals("ward-owner".hashCode(), restored.owner);
        assertEquals("ward-owner".hashCode(), nbt.getInteger("oi"));
        assertTrue(nbt.hasUniqueId("ownerUUID"));
        assertEquals(ownerId, nbt.getUniqueId("ownerUUID"));
        assertTrue(restored.isOwner(player(ownerId, "renamed-owner")));
        assertEquals(stored, restored.getStoredState());
    }

    @Test
    public void updatePacketCarriesStoredFacadeAndOwner() {
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000102");
        TileWarded source = new TileWarded();
        source.setStoredBlock(Blocks.PLANKS.getStateFromMeta(2), 7, player(ownerId, "packet-owner"));
        SPacketUpdateTileEntity packet = source.getUpdatePacket();

        TileWarded target = new TileWarded();
        target.onDataPacket(null, packet);

        assertSame(Blocks.PLANKS, target.block);
        assertEquals(2, target.blockMd & 255);
        assertEquals(7, target.light & 255);
        assertEquals("packet-owner".hashCode(), target.owner);
        assertEquals("packet-owner".hashCode(), packet.getNbtCompound().getInteger("oi"));
        assertEquals(ownerId, packet.getNbtCompound().getUniqueId("ownerUUID"));
        assertTrue(target.isOwner(player(ownerId, "packet-owner-renamed")));
    }

    @Test
    public void uuidOwnerRejectsAPlayerWithACollidingNameHash() {
        assertEquals("Aa".hashCode(), "BB".hashCode());
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000103");
        UUID collisionId = UUID.fromString("00000000-0000-0000-0000-000000000104");
        TileWarded tile = new TileWarded();
        tile.setStoredBlock(Blocks.STONE.getDefaultState(), 0, player(ownerId, "Aa"));

        assertTrue(tile.isOwner(player(ownerId, "owner-after-name-change")));
        assertFalse(tile.isOwner(player(collisionId, "BB")));
    }

    @Test
    public void oiOnlySaveUsesNameHashFallbackAndMissingOwnershipDenies() {
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setInteger("oi", "legacy-owner".hashCode());
        TileWarded tile = new TileWarded();
        tile.readCustomNBT(legacy);

        assertTrue(tile.isOwner(player(UUID.randomUUID(), "legacy-owner")));
        assertFalse(tile.isOwner(player(UUID.randomUUID(), "nonowner")));

        tile.readCustomNBT(new NBTTagCompound());
        assertFalse(tile.isOwner(player(UUID.randomUUID(), "")));
    }

    @Test
    public void readingNbtWithoutUuidClearsStaleUuidAuthority() {
        UUID originalId = UUID.fromString("00000000-0000-0000-0000-000000000105");
        TileWarded tile = new TileWarded();
        tile.setStoredBlock(Blocks.STONE.getDefaultState(), 0, player(originalId, "original-owner"));

        NBTTagCompound oiOnly = new NBTTagCompound();
        oiOnly.setInteger("oi", "fallback-owner".hashCode());
        tile.readCustomNBT(oiOnly);

        assertFalse(tile.isOwner(player(originalId, "renamed-original-owner")));
        assertTrue(tile.isOwner(player(UUID.randomUUID(), "fallback-owner")));
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

    private static TestPlayer player(UUID id, String name) {
        return new TestPlayer(new TestWorld(), id, name);
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, UUID id, String name) {
            super(world, new GameProfile(id, name));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "warded_sync_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "warded_sync_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
