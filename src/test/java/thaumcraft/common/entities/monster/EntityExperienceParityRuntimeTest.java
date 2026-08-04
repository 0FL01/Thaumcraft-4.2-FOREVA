package thaumcraft.common.entities.monster;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
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
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;

import static org.junit.Assert.assertEquals;

public class EntityExperienceParityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void cultistPortalUsesThirtyExperienceAndFiveFortyTalkInterval() {
        TestCultistPortal portal = new TestCultistPortal(new TestWorld());

        assertEquals(30, portal.getExperiencePointsForTest());
        assertEquals(540, portal.getTalkInterval());
    }

    @Test
    public void taintSporeExperienceTracksSizeAndUsesTwoHundredTalkInterval() {
        TestTaintSpore spore = new TestTaintSpore(new TestWorld());

        assertEquals(2, spore.getExperiencePointsForTest());
        assertEquals(200, spore.getTalkInterval());

        spore.setSporeSize(6);

        assertEquals(6, spore.getExperiencePointsForTest());
    }

    @Test
    public void taintSporeSwarmerExperienceTracksConstructionAndNbtSize() {
        TestTaintSporeSwarmer swarmer = new TestTaintSporeSwarmer(new TestWorld());

        assertEquals(10, swarmer.getExperiencePointsForTest());
        assertEquals(200, swarmer.getTalkInterval());

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Size", 6);
        swarmer.readEntityFromNBT(nbt);

        assertEquals(7, swarmer.getSporeSize());
        assertEquals(7, swarmer.getExperiencePointsForTest());
    }

    private static final class TestCultistPortal extends EntityCultistPortal {
        private TestCultistPortal(World world) {
            super(world);
        }

        private int getExperiencePointsForTest() {
            return this.getExperiencePoints(null);
        }
    }

    private static final class TestTaintSpore extends EntityTaintSpore {
        private TestTaintSpore(World world) {
            super(world);
        }

        private int getExperiencePointsForTest() {
            return this.getExperiencePoints(null);
        }
    }

    private static final class TestTaintSporeSwarmer extends EntityTaintSporeSwarmer {
        private TestTaintSporeSwarmer(World world) {
            super(world);
        }

        private int getExperiencePointsForTest() {
            return this.getExperiencePoints(null);
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "entity_experience_parity"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "entity_experience_parity_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }

        @Override
        public IBlockState getBlockState(net.minecraft.util.math.BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }
    }
}
