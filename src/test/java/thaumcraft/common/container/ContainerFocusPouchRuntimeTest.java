package thaumcraft.common.container;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumHand;
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
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.items.wands.ItemFocusPouch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ContainerFocusPouchRuntimeTest {
    private static final ItemFocusBasic FOCUS = new ItemFocusBasic();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void itemPayloadAndServerRouteBindTheOpeningHand() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        ItemFocusPouch pouchItem = new ItemFocusPouch();
        ItemStack main = new ItemStack(pouchItem);
        ItemStack offhand = new ItemStack(pouchItem);
        player.setHeldItem(EnumHand.MAIN_HAND, main);
        player.setHeldItem(EnumHand.OFF_HAND, offhand);

        pouchItem.onItemRightClick(world, player, EnumHand.MAIN_HAND);
        assertEquals(EnumHand.MAIN_HAND.ordinal(), player.openPayload);
        pouchItem.onItemRightClick(world, player, EnumHand.OFF_HAND);
        assertEquals(EnumHand.OFF_HAND.ordinal(), player.openPayload);

        CommonProxy proxy = new CommonProxy();
        ContainerFocusPouch mainContainer = (ContainerFocusPouch) proxy.getServerGuiElement(
                CommonProxy.GUI_FOCUS_POUCH, player, world, EnumHand.MAIN_HAND.ordinal(), 0, 0);
        ContainerFocusPouch offhandContainer = (ContainerFocusPouch) proxy.getServerGuiElement(
                CommonProxy.GUI_FOCUS_POUCH, player, world, EnumHand.OFF_HAND.ordinal(), 0, 0);

        assertTrue(mainContainer.canInteractWith(player));
        assertTrue(offhandContainer.canInteractWith(player));
        player.setHeldItem(EnumHand.OFF_HAND, offhand.copy());
        assertTrue(mainContainer.canInteractWith(player));
        assertFalse(offhandContainer.canInteractWith(player));
        assertNull(proxy.getServerGuiElement(CommonProxy.GUI_FOCUS_POUCH, player, world, -1, 0, 0));
        assertNull(proxy.getServerGuiElement(CommonProxy.GUI_FOCUS_POUCH, player, world,
                EnumHand.values().length, 0, 0));
    }

    @Test
    public void validInsertAndRemovePersistImmediatelyAndValidClosePersistsAgain() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        CountingPouch pouchItem = new CountingPouch();
        ItemStack pouch = new ItemStack(pouchItem);
        player.setHeldItem(EnumHand.MAIN_HAND, pouch);
        ContainerFocusPouch container = new ContainerFocusPouch(player.inventory, world, EnumHand.MAIN_HAND);

        player.inventory.setItemStack(new ItemStack(FOCUS));
        container.slotClick(0, 0, ClickType.PICKUP, player);
        int writesAfterInsert = pouchItem.writes;
        assertTrue(writesAfterInsert > 0);
        assertEquals(1, pouch.getTagCompound().getTagList("Inventory", 10).tagCount());

        player.inventory.setItemStack(ItemStack.EMPTY);
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertTrue(pouchItem.writes > writesAfterInsert);
        assertEquals(0, pouch.getTagCompound().getTagList("Inventory", 10).tagCount());

        int writesBeforeClose = pouchItem.writes;
        player.inventory.setItemStack(ItemStack.EMPTY);
        container.onContainerClosed(player);
        assertEquals(writesBeforeClose + 1, pouchItem.writes);
    }

    @Test
    public void exactReferenceAndHeldSlotChangesInvalidateWithoutStaleCloseWrites() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        CountingPouch pouchItem = new CountingPouch();
        ItemStack pouch = new ItemStack(pouchItem);
        player.inventory.currentItem = 2;
        player.setHeldItem(EnumHand.MAIN_HAND, pouch);
        ContainerFocusPouch container = new ContainerFocusPouch(player.inventory, world, EnumHand.MAIN_HAND);
        container.inventorySlots.get(0).putStack(new ItemStack(FOCUS));
        pouchItem.writes = 0;
        NBTTagCompound original = pouch.getTagCompound().copy();

        player.setHeldItem(EnumHand.MAIN_HAND, pouch.copy());
        assertFalse(container.canInteractWith(player));
        container.inventorySlots.get(0).putStack(ItemStack.EMPTY);
        container.onContainerClosed(player);
        assertEquals(0, pouchItem.writes);
        assertEquals(original, pouch.getTagCompound());

        player.setHeldItem(EnumHand.MAIN_HAND, pouch);
        ContainerFocusPouch selectedSlotContainer = new ContainerFocusPouch(
                player.inventory, world, EnumHand.MAIN_HAND);
        player.inventory.currentItem = 3;
        assertFalse(selectedSlotContainer.canInteractWith(player));
    }

    @Test
    public void swapDirectAndQuickMoveCannotRelocateTheBoundMainhandPouch() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        ItemStack pouch = new ItemStack(new ItemFocusPouch());
        ItemStack neighbor = new ItemStack(Items.STICK);
        player.inventory.currentItem = 2;
        player.setHeldItem(EnumHand.MAIN_HAND, pouch);
        player.inventory.setInventorySlotContents(1, neighbor);
        ContainerFocusPouch container = new ContainerFocusPouch(player.inventory, world, EnumHand.MAIN_HAND);
        int boundContainerSlot = 47;

        assertTrue(container.slotClick(boundContainerSlot, 0, ClickType.PICKUP, player).isEmpty());
        assertTrue(container.slotClick(boundContainerSlot, 0, ClickType.QUICK_MOVE, player).isEmpty());
        assertTrue(container.transferStackInSlot(player, boundContainerSlot).isEmpty());
        assertTrue(container.slotClick(46, 2, ClickType.SWAP, player).isEmpty());

        assertSame(pouch, player.inventory.getStackInSlot(2));
        assertSame(neighbor, player.inventory.getStackInSlot(1));
        assertTrue(container.canInteractWith(player));
    }

    private static final class CountingPouch extends ItemFocusPouch {
        private int writes;

        @Override
        public void setInventory(ItemStack item, ItemStack[] inventory) {
            this.writes++;
            super.setInventory(item, inventory);
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private int openPayload = -1;

        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "focus_pouch_container"));
        }

        @Override
        public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
            this.openPayload = x;
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "focus_pouch_container"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "focus_pouch_container_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
