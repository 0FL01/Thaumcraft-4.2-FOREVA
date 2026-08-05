package thaumcraft.common.items.wands;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ItemWandCastingNbtSubstrateRuntimeTest {
    private static final UUID STAFF_ATTACK_UUID = UUID.fromString("1d082610-4093-11e4-916c-0800200c9a66");
    private static final UUID STAFF_SPEED_UUID = UUID.fromString("1d082611-4093-11e4-916c-0800200c9a66");
    private static final UUID ADDON_UUID = UUID.fromString("69d8b4f5-ad4d-43cf-b286-23c10d8fb28d");

    private LinkedHashMap<String, WandRod> oldRods;
    private LinkedHashMap<String, WandCap> oldCaps;
    private WandRod wood;
    private StaffRod staff;
    private WandCap iron;
    private ItemWandCasting wand;
    private World serverWorld;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUp() {
        this.oldRods = new LinkedHashMap<>(WandRod.rods);
        this.oldCaps = new LinkedHashMap<>(WandCap.caps);
        WandRod.rods.clear();
        WandCap.caps.clear();
        this.wood = new WandRod("wood", 25, new ItemStack(new Item()), 1);
        this.staff = new StaffRod("greatwood", 125, new ItemStack(new Item()), 8);
        this.iron = new WandCap("iron", 1.0F, new ItemStack(new Item()), 1);
        this.wand = new ItemWandCasting();
        this.serverWorld = new TestWorld();
    }

    @After
    public void tearDown() {
        WandRod.rods.clear();
        WandRod.rods.putAll(this.oldRods);
        WandCap.caps.clear();
        WandCap.caps.putAll(this.oldCaps);
    }

    @Test
    public void rodGetterIsPureAndBareStacksKeepDefaultComponents() {
        ItemStack bare = new ItemStack(this.wand);
        assertEquals(this.wood, ItemWandCasting.getRod(bare));
        assertEquals(this.iron, ItemWandCasting.getCap(bare));
        assertFalse(bare.hasTagCompound());

        ItemStack legacy = legacyStaffStack(false);
        NBTTagCompound before = legacy.getTagCompound().copy();

        assertEquals(this.staff, ItemWandCasting.getRod(legacy));
        assertEquals(this.staff, ItemWandCasting.getRod(legacy));
        assertEquals(before, legacy.getTagCompound());
    }

    @Test
    public void serverUpdateDropsKnownOnlyLegacyListAndUsesDynamicStaffModifiers() {
        ItemStack stack = legacyStaffStack(false);

        this.wand.onUpdate(stack, this.serverWorld, null, 0, false);

        assertFalse(stack.getTagCompound().hasKey("AttributeModifiers"));
        assertStaffModifiers(stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
    }

    @Test
    public void serverUpdatePreservesAddonModifierAndCompletesStaffNbtModifiers() {
        ItemStack stack = legacyStaffStack(true);
        NBTTagCompound addonBefore = findModifier(stack, ADDON_UUID).copy();

        this.wand.onUpdate(stack, this.serverWorld, null, 0, false);

        assertEquals(addonBefore, findModifier(stack, ADDON_UUID));
        assertEquals(3, stack.getTagCompound().getTagList("AttributeModifiers", 10).tagCount());
        assertStaffModifiers(stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        NBTTagCompound migrated = stack.getTagCompound().copy();
        this.wand.onUpdate(stack, this.serverWorld, null, 0, false);
        assertEquals(migrated, stack.getTagCompound());
    }

    @Test
    public void nonstaffUpdateRemovesOnlyKnownStaleStaffEntries() {
        ItemStack stack = legacyStaffStack(true);
        stack.getTagCompound().setString(ItemWandCasting.TAG_ROD, this.wood.getTag());
        stack.getTagCompound().getTagList("AttributeModifiers", 10).appendTag(
                modifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), STAFF_SPEED_UUID, -3.2D, false));
        NBTTagCompound addonBefore = findModifier(stack, ADDON_UUID).copy();

        this.wand.onUpdate(stack, this.serverWorld, null, 0, false);

        NBTTagList modifiers = stack.getTagCompound().getTagList("AttributeModifiers", 10);
        assertEquals(1, modifiers.tagCount());
        assertEquals(addonBefore, modifiers.getCompoundTagAt(0));
        assertFalse(hasModifier(stack, STAFF_ATTACK_UUID));
        assertFalse(hasModifier(stack, STAFF_SPEED_UUID));
    }

    @Test
    public void serverUpdateLeavesAddonOnlyModifierListsUntouched() {
        ItemStack stack = new ItemStack(this.wand);
        NBTTagCompound tag = ItemWandCasting.ensureTag(stack);
        tag.setString(ItemWandCasting.TAG_ROD, this.staff.getTag());
        NBTTagList modifiers = new NBTTagList();
        modifiers.appendTag(modifier(SharedMonsterAttributes.LUCK.getName(), ADDON_UUID, 2.0D, true));
        tag.setTag("AttributeModifiers", modifiers);
        NBTTagCompound before = tag.copy();

        this.wand.onUpdate(stack, this.serverWorld, null, 0, false);

        assertEquals(before, stack.getTagCompound());
    }

    private ItemStack legacyStaffStack(boolean includeAddon) {
        ItemStack stack = new ItemStack(this.wand);
        NBTTagCompound tag = ItemWandCasting.ensureTag(stack);
        tag.setString(ItemWandCasting.TAG_ROD, this.staff.getTag());
        NBTTagList modifiers = new NBTTagList();
        if (includeAddon) {
            modifiers.appendTag(modifier(SharedMonsterAttributes.LUCK.getName(), ADDON_UUID, 2.0D, true));
        }
        modifiers.appendTag(modifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), STAFF_ATTACK_UUID, 5.0D, false));
        tag.setTag("AttributeModifiers", modifiers);
        return stack;
    }

    private static NBTTagCompound modifier(String attribute, UUID uuid, double amount, boolean mainhand) {
        NBTTagCompound modifier = SharedMonsterAttributes.writeAttributeModifierToNBT(
                new AttributeModifier(uuid, "test modifier", amount, 0));
        modifier.setString("AttributeName", attribute);
        if (mainhand) {
            modifier.setString("Slot", EntityEquipmentSlot.MAINHAND.getName());
        }
        return modifier;
    }

    private static void assertStaffModifiers(Multimap<String, AttributeModifier> modifiers) {
        assertModifier(modifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()), STAFF_ATTACK_UUID, 6.0D);
        assertModifier(modifiers.get(SharedMonsterAttributes.ATTACK_SPEED.getName()), STAFF_SPEED_UUID, -3.2D);
    }

    private static void assertModifier(Collection<AttributeModifier> modifiers, UUID uuid, double amount) {
        AttributeModifier found = modifiers.stream()
                .filter(modifier -> uuid.equals(modifier.getID()))
                .findFirst().orElse(null);
        assertNotNull(found);
        assertEquals(amount, found.getAmount(), 0.0D);
    }

    private static boolean hasModifier(ItemStack stack, UUID uuid) {
        return findModifier(stack, uuid) != null;
    }

    private static NBTTagCompound findModifier(ItemStack stack, UUID uuid) {
        NBTTagList modifiers = stack.getTagCompound().getTagList("AttributeModifiers", 10);
        for (int i = 0; i < modifiers.tagCount(); i++) {
            NBTTagCompound modifier = modifiers.getCompoundTagAt(i);
            if (modifier.getLong("UUIDMost") == uuid.getMostSignificantBits()
                    && modifier.getLong("UUIDLeast") == uuid.getLeastSignificantBits()) {
                return modifier;
            }
        }
        return null;
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "wand_nbt"),
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
                @Override public String makeString() { return "wand_nbt_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
