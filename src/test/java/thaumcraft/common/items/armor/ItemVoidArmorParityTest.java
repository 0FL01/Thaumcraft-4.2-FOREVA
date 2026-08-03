package thaumcraft.common.items.armor;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
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
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemVoidArmorParityTest {
    private ItemResource oldResource;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installResourceFixture() {
        this.oldResource = ConfigItems.itemResource;
        ConfigItems.itemResource = new ItemResource();
    }

    @After
    public void restoreResourceFixture() {
        ConfigItems.itemResource = this.oldResource;
    }

    @Test
    public void voidAndFortressMaterialsMapReferenceSlotsToForgeOrdering() {
        assertArmorMaterial(ThaumcraftApi.armorMatVoid, 10, 3, 7, 6, 3);
        assertArmorMaterial(ThaumcraftApi.armorMatThaumiumFortress, 40, 3, 7, 6, 3);
    }

    @Test
    public void voidRobesUseVoidMaterialAndKeepVisWarpContracts() {
        ItemVoidRobeArmor helm = new ItemVoidRobeArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.HEAD);
        ItemVoidRobeArmor chest = new ItemVoidRobeArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.CHEST);
        ItemVoidRobeArmor legs = new ItemVoidRobeArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.LEGS);

        assertEquals(110, helm.getMaxDamage());
        assertEquals(160, chest.getMaxDamage());
        assertEquals(150, legs.getMaxDamage());
        assertEquals(3, helm.damageReduceAmount);
        assertEquals(7, chest.damageReduceAmount);
        assertEquals(6, legs.damageReduceAmount);
        assertEquals(5, helm.getVisDiscount(new ItemStack(helm), null, null));
        assertEquals(2, helm.getWarp(new ItemStack(helm), null));
        assertTrue(helm.showNodes(new ItemStack(helm), null));
        assertFalse(chest.showNodes(new ItemStack(chest), null));
    }

    @Test
    public void voidArmorRepairUsesIngotAndOriginalLivingCadence() {
        assertTrue(ItemVoidArmor.isVoidArmorRepair(
                new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_VOID_INGOT)));
        assertFalse(ItemVoidArmor.isVoidArmorRepair(
                new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_CHARM)));

        TestWorld world = new TestWorld();
        ItemVoidArmor armor = new ItemVoidArmor(ThaumcraftApi.armorMatVoid, 0, EntityEquipmentSlot.CHEST);
        ItemStack survivalStack = new ItemStack(armor);
        survivalStack.setItemDamage(5);
        EntityZombie living = new EntityZombie(world);
        living.ticksExisted = 20;

        ItemVoidArmor.repairVoidArmor(survivalStack, world, living);
        assertEquals(4, survivalStack.getItemDamage());

        ItemStack creativeStack = new ItemStack(armor);
        creativeStack.setItemDamage(5);
        TestPlayer creative = new TestPlayer(world);
        creative.capabilities.isCreativeMode = true;
        creative.ticksExisted = 20;

        ItemVoidArmor.repairVoidArmor(creativeStack, world, creative);
        assertEquals(5, creativeStack.getItemDamage());
    }

    private static void assertArmorMaterial(ItemArmor.ArmorMaterial material, int durabilityFactor,
                                            int head, int chest, int legs, int feet) {
        assertEquals(head, material.getDamageReductionAmount(EntityEquipmentSlot.HEAD));
        assertEquals(chest, material.getDamageReductionAmount(EntityEquipmentSlot.CHEST));
        assertEquals(legs, material.getDamageReductionAmount(EntityEquipmentSlot.LEGS));
        assertEquals(feet, material.getDamageReductionAmount(EntityEquipmentSlot.FEET));
        assertEquals(11 * durabilityFactor, material.getDurability(EntityEquipmentSlot.HEAD));
        assertEquals(16 * durabilityFactor, material.getDurability(EntityEquipmentSlot.CHEST));
        assertEquals(15 * durabilityFactor, material.getDurability(EntityEquipmentSlot.LEGS));
        assertEquals(13 * durabilityFactor, material.getDurability(EntityEquipmentSlot.FEET));
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes("void_armor".getBytes(StandardCharsets.UTF_8)), "void_armor"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return this.capabilities.isCreativeMode; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "void_armor"),
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
                @Override public String makeString() { return "void_armor_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
