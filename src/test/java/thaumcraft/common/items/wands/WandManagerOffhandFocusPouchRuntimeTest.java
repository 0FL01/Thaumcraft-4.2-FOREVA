package thaumcraft.common.items.wands;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import thaumcraft.api.wands.ItemFocusBasic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class WandManagerOffhandFocusPouchRuntimeTest {
    private static final KeyedFocus FOCUS = new KeyedFocus();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void offhandPouchParticipatesInTransactionalFocusSwap() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        MemoryWand wandItem = new MemoryWand();
        ItemStack wand = new ItemStack(wandItem);
        ItemStack current = focus("current", "current");
        ItemStack selected = focus("selected", "selected");
        wandItem.setFocus(wand, current);
        player.setHeldItem(EnumHand.MAIN_HAND, wand);

        MemoryPouch pouchItem = new MemoryPouch();
        ItemStack pouch = new ItemStack(pouchItem);
        ItemStack[] contents = emptyPouchContents();
        contents[0] = selected;
        pouchItem.setInventory(pouch, contents);
        player.setHeldItem(EnumHand.OFF_HAND, pouch);

        WandManager.changeFocus(wand, world, player, "selected");

        assertEquals("selected", wandItem.focus.getTagCompound().getString("marker"));
        ItemStack returned = pouchItem.getInventory(player.getHeldItemOffhand())[0];
        assertSame(FOCUS, returned.getItem());
        assertEquals("current", returned.getTagCompound().getString("marker"));
    }

    @Test
    public void offhandScanAfterMainInventoryWinsDuplicateSortingKeys() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        MemoryWand wandItem = new MemoryWand();
        ItemStack wand = new ItemStack(wandItem);
        player.setHeldItem(EnumHand.MAIN_HAND, wand);

        MemoryPouch pouchItem = new MemoryPouch();
        ItemStack mainPouch = pouchWith(pouchItem, focus("duplicate", "main"));
        ItemStack offhandPouch = pouchWith(pouchItem, focus("duplicate", "offhand"));
        player.inventory.setInventorySlotContents(1, mainPouch);
        player.setHeldItem(EnumHand.OFF_HAND, offhandPouch);

        WandManager.changeFocus(wand, world, player, "duplicate");

        assertEquals("offhand", wandItem.focus.getTagCompound().getString("marker"));
        assertEquals("main", pouchItem.getInventory(mainPouch)[0].getTagCompound().getString("marker"));
        assertTrue(pouchItem.getInventory(player.getHeldItemOffhand())[0].isEmpty());
    }

    private static ItemStack focus(String key, String marker) {
        ItemStack stack = new ItemStack(FOCUS);
        stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        stack.getTagCompound().setString("sort", key);
        stack.getTagCompound().setString("marker", marker);
        return stack;
    }

    private static ItemStack pouchWith(ItemFocusPouch pouchItem, ItemStack focus) {
        ItemStack pouch = new ItemStack(pouchItem);
        ItemStack[] contents = emptyPouchContents();
        contents[0] = focus;
        pouchItem.setInventory(pouch, contents);
        return pouch;
    }

    private static ItemStack[] emptyPouchContents() {
        ItemStack[] contents = new ItemStack[18];
        for (int i = 0; i < contents.length; i++) contents[i] = ItemStack.EMPTY;
        return contents;
    }

    private static final class KeyedFocus extends ItemFocusBasic {
        @Override
        public String getSortingHelper(ItemStack focusstack) {
            return focusstack.hasTagCompound() ? focusstack.getTagCompound().getString("sort") : "";
        }
    }

    private static final class MemoryWand extends ItemWandCasting {
        private ItemStack focus = ItemStack.EMPTY;

        @Override
        public ItemStack getFocusItem(ItemStack stack) {
            return this.focus.copy();
        }

        @Override
        public ItemFocusBasic getFocus(ItemStack stack) {
            return this.focus.isEmpty() ? null : (ItemFocusBasic) this.focus.getItem();
        }

        @Override
        public void setFocus(ItemStack stack, ItemStack focus) {
            this.focus = focus == null ? ItemStack.EMPTY : focus.copy();
        }
    }

    private static final class MemoryPouch extends ItemFocusPouch {
        private final Map<ItemStack, ItemStack[]> inventories = new IdentityHashMap<>();

        @Override
        public ItemStack[] getInventory(ItemStack item) {
            ItemStack[] stored = this.inventories.get(item);
            return stored == null ? emptyPouchContents() : copy(stored);
        }

        @Override
        public void setInventory(ItemStack item, ItemStack[] inventory) {
            this.inventories.put(item, copy(inventory));
        }

        private static ItemStack[] copy(ItemStack[] inventory) {
            ItemStack[] copy = new ItemStack[18];
            for (int i = 0; i < copy.length; i++) {
                ItemStack stack = i < inventory.length ? inventory[i] : ItemStack.EMPTY;
                copy[i] = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            }
            return copy;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private final IBaublesItemHandler baubles = (IBaublesItemHandler) Proxy.newProxyInstance(
                IBaublesItemHandler.class.getClassLoader(), new Class[]{IBaublesItemHandler.class},
                (proxy, method, args) -> {
                    if (method.getReturnType() == int.class) return 0;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == ItemStack.class) return ItemStack.EMPTY;
                    return null;
                });

        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "offhand_focus_pouch"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == BaublesCapabilities.CAPABILITY_BAUBLES
                    ? (T) this.baubles : super.getCapability(capability, facing);
        }
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "offhand_focus_pouch"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                              float volume, float pitch) {
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "offhand_focus_pouch_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
