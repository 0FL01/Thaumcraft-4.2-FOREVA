package thaumcraft.common.items.equipment;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
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
import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemVoidEquipmentParityTest {
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
    public void voidToolsUseReferenceMaterialAndAxeAttackIntent() {
        ItemVoidSword sword = new ItemVoidSword(ThaumcraftApi.toolMatVoid);
        ItemVoidPickaxe pick = new ItemVoidPickaxe(ThaumcraftApi.toolMatVoid);
        ItemVoidAxe axe = new ItemVoidAxe(ThaumcraftApi.toolMatVoid);
        ItemVoidShovel shovel = new ItemVoidShovel(ThaumcraftApi.toolMatVoid);
        ItemVoidHoe hoe = new ItemVoidHoe(ThaumcraftApi.toolMatVoid);

        assertEquals(4, ThaumcraftApi.toolMatVoid.getHarvestLevel());
        assertEquals(150, ThaumcraftApi.toolMatVoid.getMaxUses());
        assertEquals(8.0F, ThaumcraftApi.toolMatVoid.getEfficiency(), 0.0F);
        assertEquals(3.0F, ThaumcraftApi.toolMatVoid.getAttackDamage(), 0.0F);
        assertEquals(10, ThaumcraftApi.toolMatVoid.getEnchantability());
        assertEquals(150, sword.getMaxDamage());
        assertEquals(150, pick.getMaxDamage());
        assertEquals(150, axe.getMaxDamage());
        assertEquals(150, shovel.getMaxDamage());
        assertEquals(150, hoe.getMaxDamage());
        assertEquals(10, sword.getItemEnchantability());
        assertEquals(10, pick.getItemEnchantability());
        assertEquals(10, axe.getItemEnchantability());
        assertEquals(10, shovel.getItemEnchantability());
        assertEquals(5, hoe.getItemEnchantability());
        assertEquals(6.0D, attributeAmount(axe, SharedMonsterAttributes.ATTACK_DAMAGE.getName()), 0.0D);
        assertEquals(-3.0D, attributeAmount(axe, SharedMonsterAttributes.ATTACK_SPEED.getName()), 0.0D);
    }

    @Test
    public void voidToolsApplyWeaknessAndAcceptOnlyCharmRepair() {
        TestWorld world = new TestWorld();
        EntityZombie target = new EntityZombie(world);
        ItemVoidSword sword = new ItemVoidSword(ThaumcraftApi.toolMatVoid);
        ItemStack swordStack = new ItemStack(sword);
        ItemStack charm = new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_CHARM);
        ItemStack ingot = new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_VOID_INGOT);

        ItemVoidSword.tryApplyVoidWeakness(target, target, 80);

        assertTrue(target.isPotionActive(MobEffects.WEAKNESS));
        assertEquals(80, target.getActivePotionEffect(MobEffects.WEAKNESS).getDuration());
        assertFalse(target.isPotionActive(MobEffects.WITHER));
        assertTrue(ThaumcraftApi.toolMatVoid.getRepairItemStack().isEmpty());
        assertTrue(ItemVoidSword.isVoidToolRepair(charm));
        assertFalse(ItemVoidSword.isVoidToolRepair(ingot));
        assertTrue(sword.getIsRepairable(swordStack, charm));
        assertFalse(sword.getIsRepairable(swordStack, ingot));
    }

    @Test
    public void selfRepairKeepsLivingCadenceAndDoesNotRepairCreativeStacks() {
        TestWorld world = new TestWorld();
        ItemVoidSword sword = new ItemVoidSword(ThaumcraftApi.toolMatVoid);
        ItemStack survivalStack = new ItemStack(sword);
        survivalStack.setItemDamage(5);
        EntityZombie living = new EntityZombie(world);
        living.ticksExisted = 20;

        ItemVoidSword.repairVoid(survivalStack, world, living);
        assertEquals(4, survivalStack.getItemDamage());

        ItemStack creativeStack = new ItemStack(sword);
        creativeStack.setItemDamage(5);
        TestPlayer creative = new TestPlayer(world);
        creative.capabilities.isCreativeMode = true;
        creative.ticksExisted = 20;

        ItemVoidSword.repairVoid(creativeStack, world, creative);
        assertEquals(5, creativeStack.getItemDamage());
    }

    private static double attributeAmount(Item item, String attribute) {
        Multimap<String, AttributeModifier> modifiers = item.getItemAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        Collection<AttributeModifier> values = modifiers.get(attribute);
        assertEquals(1, values.size());
        return values.iterator().next().getAmount();
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes("void_equipment".getBytes(StandardCharsets.UTF_8)), "void_equipment"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return this.capabilities.isCreativeMode; }
    }

    static final class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "void_equipment"),
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
                @Override public String makeString() { return "void_equipment_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
