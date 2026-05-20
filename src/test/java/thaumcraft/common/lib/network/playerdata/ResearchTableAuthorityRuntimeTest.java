package thaumcraft.common.lib.network.playerdata;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.tiles.TileResearchTable;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ResearchTableAuthorityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void resolveResearchTableRequiresActiveMatchingUsableContainer() {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_container");
        player.setPosition(0.5D, 0.5D, 0.5D);
        player.openContainer = new ContainerResearchTable(player.inventory, table);

        assertSame(table, PacketAspectPlaceToServer.resolveResearchTable(player, BlockPos.ORIGIN));

        player.openContainer = player.inventoryContainer;
        assertNull(PacketAspectPlaceToServer.resolveResearchTable(player, BlockPos.ORIGIN));

        player.openContainer = new ContainerResearchTable(player.inventory, table);
        assertNull(PacketAspectPlaceToServer.resolveResearchTable(player, BlockPos.ORIGIN.east()));

        player.setPosition(100.0D, 0.5D, 100.0D);
        assertNull(PacketAspectPlaceToServer.resolveResearchTable(player, BlockPos.ORIGIN));
    }

    @Test
    public void placeAspectRejectsInvalidHexAndUndiscoveredAspectWithoutMutatingNote() throws Exception {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_hex_guard");
        player.knowledge().addDiscoveredAspect(Aspect.AIR.getTag());
        seedTableInventory(table, createNoteStack(), new ItemStack(new TestScribeTools()));

        table.placeAspect(5, 5, Aspect.FIRE, player);
        ResearchNoteData invalidHex = ResearchManager.getData(table.getStackInSlot(1));
        assertFalse(invalidHex.hexes.containsKey("5:5"));
        assertEquals(0, table.getStackInSlot(0).getItemDamage());

        table.placeAspect(1, 0, Aspect.FIRE, player);
        ResearchNoteData undiscovered = ResearchManager.getData(table.getStackInSlot(1));
        ResearchManager.HexEntry entry = undiscovered.hexEntries.get("1:0");
        assertTrue(entry != null && entry.aspect == null && entry.type == 0);
        assertEquals(0, player.knowledge().getAspectPoolFor(Aspect.FIRE));
        assertEquals(0, table.getStackInSlot(0).getItemDamage());
    }

    @Test
    public void placeAspectConsumesKnownPoolAndUpdatesExistingHex() throws Exception {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_place_valid");
        player.knowledge().addDiscoveredAspect(Aspect.AIR.getTag());
        player.knowledge().addDiscoveredAspect(Aspect.FIRE.getTag());
        player.knowledge().setAspectPool(Aspect.FIRE, 1);
        seedTableInventory(table, createNoteStack(), new ItemStack(new TestScribeTools()));

        table.placeAspect(1, 0, Aspect.FIRE, player);

        ResearchNoteData updated = ResearchManager.getData(table.getStackInSlot(1));
        ResearchManager.HexEntry entry = updated.hexEntries.get("1:0");
        assertTrue(entry != null && entry.aspect == Aspect.FIRE && entry.type == 2);
        assertEquals(0, player.knowledge().getAspectPoolFor(Aspect.FIRE));
        assertEquals(1, table.getStackInSlot(0).getItemDamage());
    }

    @Test
    public void combinationInputValidationIsAtomicForPoolAndBonusSources() {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_combo");
        player.knowledge().addDiscoveredAspect(Aspect.AIR.getTag());
        player.knowledge().addDiscoveredAspect(Aspect.ORDER.getTag());
        player.knowledge().setAspectPool(Aspect.AIR, 1);
        player.knowledge().setAspectPool(Aspect.ORDER, 1);

        assertNull(PacketAspectCombinationToServer.consumeCombinationInputs(
                player, table, player.knowledge(), Aspect.AIR, Aspect.ORDER, true, false));
        assertEquals(1, player.knowledge().getAspectPoolFor(Aspect.AIR));
        assertEquals(1, player.knowledge().getAspectPoolFor(Aspect.ORDER));

        table.bonusAspects.add(Aspect.ORDER, 1);
        Aspect combo = PacketAspectCombinationToServer.consumeCombinationInputs(
                player, table, player.knowledge(), Aspect.AIR, Aspect.ORDER, false, true);
        assertSame(ResearchManager.getCombinationResult(Aspect.AIR, Aspect.ORDER), combo);
        assertEquals(0, player.knowledge().getAspectPoolFor(Aspect.AIR));
        assertEquals(1, player.knowledge().getAspectPoolFor(Aspect.ORDER));
        assertEquals(0, table.bonusAspects.getAmount(Aspect.ORDER));
    }

    @Test
    public void placePacketDispatchRequiresMatchingPlayerDimensionAndTableRoute() throws Exception {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_place_dispatch");
        player.setPosition(0.5D, 0.5D, 0.5D);
        player.openContainer = new ContainerResearchTable(player.inventory, table);
        player.knowledge().addDiscoveredAspect(Aspect.FIRE.getTag());
        player.knowledge().setAspectPool(Aspect.FIRE, 1);
        seedTableInventory(table, createNoteStack(), new ItemStack(new TestScribeTools()));

        assertTrue(PacketAspectPlaceToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension(), 0, 0, 0, Aspect.FIRE, (byte) 1, (byte) 0));
        ResearchNoteData placed = ResearchManager.getData(table.getStackInSlot(1));
        assertSame(Aspect.FIRE, placed.hexEntries.get("1:0").aspect);

        int beforePool = player.knowledge().getAspectPoolFor(Aspect.FIRE);
        assertFalse(PacketAspectPlaceToServer.dispatch(
                player, player.getEntityId() + 1, world.provider.getDimension(), 0, 0, 0, Aspect.FIRE, (byte) 1, (byte) 0));
        assertFalse(PacketAspectPlaceToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension() + 1, 0, 0, 0, Aspect.FIRE, (byte) 1, (byte) 0));
        assertFalse(PacketAspectPlaceToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension(), 1, 0, 0, Aspect.FIRE, (byte) 1, (byte) 0));
        assertEquals(beforePool, player.knowledge().getAspectPoolFor(Aspect.FIRE));
    }

    @Test
    public void combinationPacketDispatchRequiresMatchingPlayerDimensionAndTableRoute() {
        ResearchWorld world = new ResearchWorld();
        TileResearchTable table = new TestResearchTable();
        world.attachTable(table, BlockPos.ORIGIN);
        TestPlayer player = new TestPlayer(world, "stage9e_combo_dispatch");
        player.setPosition(0.5D, 0.5D, 0.5D);
        player.openContainer = new ContainerResearchTable(player.inventory, table);
        player.knowledge().addDiscoveredAspect(Aspect.AIR.getTag());
        player.knowledge().addDiscoveredAspect(Aspect.ORDER.getTag());
        Aspect combo = ResearchManager.getCombinationResult(Aspect.AIR, Aspect.ORDER);
        player.knowledge().setAspectPool(Aspect.AIR, 1);
        table.bonusAspects.add(Aspect.ORDER, 1);

        assertTrue(PacketAspectCombinationToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension(), 0, 0, 0, Aspect.AIR, Aspect.ORDER, false, true));
        assertTrue(player.knowledge().hasDiscoveredAspect(combo));
        assertEquals(0, table.bonusAspects.getAmount(Aspect.ORDER));

        int beforePool = player.knowledge().getAspectPoolFor(Aspect.AIR);
        assertFalse(PacketAspectCombinationToServer.dispatch(
                player, player.getEntityId() + 1, world.provider.getDimension(), 0, 0, 0, Aspect.AIR, Aspect.ORDER, false, true));
        assertFalse(PacketAspectCombinationToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension() + 1, 0, 0, 0, Aspect.AIR, Aspect.ORDER, false, true));
        assertFalse(PacketAspectCombinationToServer.dispatch(
                player, player.getEntityId(), world.provider.getDimension(), 1, 0, 0, Aspect.AIR, Aspect.ORDER, false, true));
        assertEquals(beforePool, player.knowledge().getAspectPoolFor(Aspect.AIR));
    }

    private static ItemStack createNoteStack() {
        ItemStack stack = new ItemStack(new ItemResearchNotes(), 1, 0);
        ResearchNoteData data = new ResearchNoteData();
        data.key = "TEST";
        data.color = 0xFFFFFF;
        data.hexes.put("0:0", new thaumcraft.common.lib.utils.HexUtils.Hex(0, 0));
        data.hexEntries.put("0:0", new ResearchManager.HexEntry(null, 1));
        data.hexes.put("-1:0", new thaumcraft.common.lib.utils.HexUtils.Hex(-1, 0));
        data.hexEntries.put("-1:0", new ResearchManager.HexEntry(null, 1));
        data.hexes.put("1:0", new thaumcraft.common.lib.utils.HexUtils.Hex(1, 0));
        data.hexEntries.put("1:0", new ResearchManager.HexEntry(null, 0));
        ResearchManager.updateData(stack, data);
        return stack;
    }

    private static void seedTableInventory(TileResearchTable table, ItemStack notes, ItemStack scribe) throws Exception {
        Field stackList = TileResearchTable.class.getDeclaredField("stackList");
        stackList.setAccessible(true);
        ItemStack[] stacks = (ItemStack[]) stackList.get(table);
        stacks[0] = scribe;
        stacks[1] = notes;
    }

    private static class TestScribeTools extends Item implements IScribeTools {
        TestScribeTools() {
            this.setMaxDamage(16);
            this.setMaxStackSize(1);
        }
    }

    private static class TestPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();

        TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.knowledge.addDiscoveredPrimalAspects();
        }

        PlayerKnowledgeCapability knowledge() {
            return this.knowledge;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE || super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                @SuppressWarnings("unchecked")
                T value = (T) this.knowledge;
                return value;
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return this.capabilities.isCreativeMode;
        }
    }

    private static class TestResearchTable extends TileResearchTable {
        @Override
        public void markDirty() {
        }
    }

    private static class ResearchWorld extends World {
        private TileResearchTable table;
        private IBlockState defaultState = Blocks.AIR.getDefaultState();

        ResearchWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "stage9e"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.getWorldInfo().setSpawn(new BlockPos(0, 64, 0));
            this.chunkProvider = this.createChunkProvider();
        }

        void attachTable(TileResearchTable table, BlockPos pos) {
            this.table = table;
            table.setWorld(this);
            table.setPos(pos);
        }

        @Override
        public net.minecraft.tileentity.TileEntity getTileEntity(BlockPos pos) {
            return this.table != null && pos.equals(this.table.getPos()) ? this.table : null;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return this.defaultState;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        }

        @Override
        public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) {
                    return null;
                }

                @Override
                public Chunk provideChunk(int x, int z) {
                    return null;
                }

                @Override
                public boolean tick() {
                    return false;
                }

                @Override
                public String makeString() {
                    return "stage9e_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return false;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
