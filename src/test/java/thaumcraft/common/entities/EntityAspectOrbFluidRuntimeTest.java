package thaumcraft.common.entities;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.SoundEvents;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityAspectOrbFluidRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void waterUsesFluidAccelerationWithoutLavaFizzOrBounce() {
        TestWorld world = new TestWorld(Blocks.WATER.getDefaultState());
        EntityAspectOrb orb = createOrb(world);

        orb.onUpdate();

        assertTrue(world.accelerationChecked);
        assertSame(Material.WATER, world.accelerationMaterial);
        assertSame(orb, world.acceleratedEntity);
        assertFalse(world.sounds.contains(SoundEvents.BLOCK_FIRE_EXTINGUISH));
        assertTrue(orb.motionY < 0.0D);
    }

    @Test
    public void lavaTriggersOriginalFizzAndUpwardBounce() {
        TestWorld world = new TestWorld(Blocks.LAVA.getDefaultState());
        EntityAspectOrb orb = createOrb(world);

        orb.onUpdate();

        assertEquals(1, world.sounds.size());
        assertSame(SoundEvents.BLOCK_FIRE_EXTINGUISH, world.sounds.get(0));
        assertTrue(orb.motionY > 0.0D);
    }

    private static EntityAspectOrb createOrb(World world) {
        EntityAspectOrb orb = new EntityAspectOrb(world);
        orb.setPosition(0.5D, 64.0D, 0.5D);
        orb.motionX = 0.0D;
        orb.motionY = 0.0D;
        orb.motionZ = 0.0D;
        return orb;
    }

    private static final class TestWorld extends World {
        private final IBlockState state;
        private final List<SoundEvent> sounds = new ArrayList<SoundEvent>();
        private boolean accelerationChecked;
        private Material accelerationMaterial;
        private Entity acceleratedEntity;

        private TestWorld(IBlockState state) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL,
                            false, false, WorldType.DEFAULT), "aspect_orb_fluid"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.state = state;
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return this.state;
        }

        @Override
        public boolean handleMaterialAcceleration(AxisAlignedBB bounds, Material material, Entity entity) {
            this.accelerationChecked = true;
            this.accelerationMaterial = material;
            this.acceleratedEntity = entity;
            return this.state.getMaterial() == material;
        }

        @Override
        public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent sound,
                              SoundCategory category, float volume, float pitch) {
            this.sounds.add(sound);
        }

        @Override
        public List<AxisAlignedBB> getCollisionBoxes(Entity entity, AxisAlignedBB bounds) {
            return Collections.emptyList();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "aspect_orb_fluid_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
