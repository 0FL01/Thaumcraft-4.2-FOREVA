package thaumcraft.common.container;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.NonNullList;
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
import thaumcraft.common.tiles.TileAlchemyFurnace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContainerAlchemyFurnaceRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void listenerAndClientReceiveAllFiveTc4Properties() {
        TileAlchemyFurnace furnace = new TileAlchemyFurnace();
        furnace.furnaceCookTime = 11;
        furnace.furnaceBurnTime = 22;
        furnace.currentItemBurnTime = 33;
        furnace.vis = 44;
        furnace.smeltTime = 55;
        ContainerAlchemyFurnace container = new ContainerAlchemyFurnace(null, furnace);
        PropertyListener listener = new PropertyListener();

        container.addListener(listener);

        assertEquals(Integer.valueOf(11), listener.values.get(0));
        assertEquals(Integer.valueOf(22), listener.values.get(1));
        assertEquals(Integer.valueOf(33), listener.values.get(2));
        assertEquals(Integer.valueOf(44), listener.values.get(3));
        assertEquals(Integer.valueOf(55), listener.values.get(4));

        listener.events.clear();
        container.detectAndSendChanges();
        listener.events.clear();
        furnace.furnaceCookTime = 12;
        furnace.furnaceBurnTime = 23;
        furnace.currentItemBurnTime = 34;
        furnace.vis = 45;
        furnace.smeltTime = 56;
        container.detectAndSendChanges();
        assertEquals(5, listener.events.size());

        container.updateProgressBar(0, 101);
        container.updateProgressBar(1, 102);
        container.updateProgressBar(2, 103);
        container.updateProgressBar(3, 104);
        container.updateProgressBar(4, 105);
        assertEquals(101, furnace.furnaceCookTime);
        assertEquals(102, furnace.furnaceBurnTime);
        assertEquals(103, furnace.currentItemBurnTime);
        assertEquals(104, furnace.vis);
        assertEquals(105, furnace.smeltTime);
    }

    @Test
    public void dualPurposeFuelQuickMovesToFuelSlotFirst() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        TestFurnace furnace = new TestFurnace();
        ContainerAlchemyFurnace container = new ContainerAlchemyFurnace(player.inventory, furnace);
        player.inventory.setInventorySlotContents(9, new ItemStack(Items.COAL, 3));

        ItemStack moved = container.transferStackInSlot(player, 2);

        assertEquals(3, moved.getCount());
        assertEquals(3, furnace.getStackInSlot(1).getCount());
        assertTrue(furnace.getStackInSlot(0).isEmpty());
        assertTrue(player.inventory.getStackInSlot(9).isEmpty());
    }

    private static final class TestFurnace extends TileAlchemyFurnace {
        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return index == 0 && stack.getItem() == Items.COAL || super.isItemValidForSlot(index, stack);
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "alchemy_furnace_runtime"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class PropertyListener implements IContainerListener {
        private final Map<Integer, Integer> values = new HashMap<>();
        private final List<Integer> events = new ArrayList<>();

        @Override public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) { }
        @Override public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) { }
        @Override public void sendWindowProperty(Container containerIn, int id, int data) {
            this.values.put(id, data);
            this.events.add(id);
        }
        @Override public void sendAllWindowProperties(Container containerIn, IInventory inventory) { }
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "alchemy_furnace_runtime"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "alchemy_furnace_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
