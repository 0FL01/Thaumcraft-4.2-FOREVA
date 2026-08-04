package thaumcraft.common.lib.events;

import com.mojang.authlib.GameProfile;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRepairableExtended;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.armor.ItemHoverHarness;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.nio.charset.StandardCharsets;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;

public class ArtificeRepairRuntimeTest {
    private Enchantment previousRepair;
    private Map<List, AspectList> previousObjectTags;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void useRegisteredEnchantmentForFixture() {
        this.previousRepair = Config.enchRepair;
        this.previousObjectTags = new HashMap<>(ThaumcraftApi.objectTags);
        Config.enchRepair = Enchantments.UNBREAKING;
    }

    @After
    public void restoreConfig() {
        Config.enchRepair = this.previousRepair;
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.objectTags.putAll(this.previousObjectTags);
    }

    @Test
    public void repairsHotbarAndArmorEveryFortyTicksAndCapsAtLevelTwo() {
        TestPlayer player = new TestPlayer(new TestWorld());
        TestWand wand = new TestWand();
        player.inventory.setInventorySlotContents(35, new ItemStack(wand));

        RepairableItem repairable = new RepairableItem();
        registerAirTags(repairable, 8);
        ItemStack hotbar = damagedAndEnchanted(repairable, 10, 3);
        player.inventory.setInventorySlotContents(0, hotbar);

        player.ticksExisted = 39;
        tick(player);
        assertEquals(10, hotbar.getItemDamage());
        assertEquals(0, wand.consumeCalls);

        player.ticksExisted = 40;
        tick(player);
        assertEquals(8, hotbar.getItemDamage());
        assertEquals(1, wand.consumeCalls);
        assertEquals(8, wand.lastCost.getAmount(Aspect.AIR));

        ItemStack inventory = damagedAndEnchanted(repairable, 10, 3);
        player.inventory.setInventorySlotContents(10, inventory);
        player.inventory.setInventorySlotContents(0, ItemStack.EMPTY);
        player.ticksExisted = 80;
        tick(player);
        assertEquals(10, inventory.getItemDamage());
        assertEquals(1, wand.consumeCalls);

        ItemHoverHarness harnessItem = new ItemHoverHarness(ItemArmor.ArmorMaterial.IRON, 0,
                EntityEquipmentSlot.CHEST);
        registerAirTags(harnessItem, 8);
        ItemStack carriedHarness = damagedAndEnchanted(harnessItem, 10, 2);
        ItemStack wornHarness = damagedAndEnchanted(harnessItem, 10, 2);
        player.inventory.setInventorySlotContents(0, carriedHarness);
        player.setItemStackToSlot(EntityEquipmentSlot.CHEST, wornHarness);
        player.ticksExisted = 120;
        tick(player);
        assertEquals(10, carriedHarness.getItemDamage());
        assertEquals(8, wornHarness.getItemDamage());
        assertEquals(2, wand.consumeCalls);

    }

    @Test
    public void extendedRepairHookCanVetoVisConsumption() {
        TestPlayer player = new TestPlayer(new TestWorld());
        TestWand wand = new TestWand();
        player.inventory.setInventorySlotContents(35, new ItemStack(wand));
        ExtendedRepairableItem item = new ExtendedRepairableItem();
        registerAirTags(item, 8);
        ItemStack stack = damagedAndEnchanted(item, 10, 3);

        EventHandlerEntity.doRepair(stack, player);

        assertEquals(2, item.lastLevel);
        assertEquals(10, stack.getItemDamage());
        assertEquals(0, wand.consumeCalls);
    }

    private static void tick(EntityPlayer player) {
        new EventHandlerEntity().onLivingUpdate(new LivingEvent.LivingUpdateEvent(player));
    }

    private static ItemStack damagedAndEnchanted(Item item, int damage, int level) {
        ItemStack stack = new ItemStack(item);
        stack.setItemDamage(damage);
        stack.addEnchantment(Enchantments.UNBREAKING, level);
        return stack;
    }

    private static void registerAirTags(Item item, int amount) {
        ThaumcraftApi.registerObjectTag(new ItemStack(item), new AspectList().add(Aspect.AIR, amount));
    }

    private static class RepairableItem extends Item implements IRepairable {
        private RepairableItem() {
            this.setMaxDamage(100);
            this.setMaxStackSize(1);
        }
    }

    private static final class ExtendedRepairableItem extends RepairableItem implements IRepairableExtended {
        private int lastLevel;

        @Override
        public boolean doRepair(ItemStack stack, EntityPlayer player, int level) {
            this.lastLevel = level;
            return false;
        }
    }

    private static final class TestWand extends ItemWandCasting {
        private int consumeCalls;
        private AspectList lastCost;

        @Override
        public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost,
                                     boolean doit, boolean crafting) {
            if (doit) {
                this.consumeCalls++;
                this.lastCost = cost.copy();
            }
            return true;
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

        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes("artifice_repair".getBytes(StandardCharsets.UTF_8)),
                    "artifice_repair"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return this.capabilities.isCreativeMode; }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == BaublesCapabilities.CAPABILITY_BAUBLES
                    ? (T) this.baubles : super.getCapability(capability, facing);
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "artifice_repair"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override public boolean spawnEntity(Entity entity) { return true; }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "artifice_repair_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
