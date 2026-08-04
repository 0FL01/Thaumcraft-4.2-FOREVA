package thaumcraft.common.lib.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
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
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventHandlerDropParityRuntimeTest {
    private Map<String, Integer> oldWhitelist;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void saveWhitelist() {
        this.oldWhitelist = new LinkedHashMap<>(ConfigEntities.CHAMPION_WHITELIST);
        ConfigEntities.CHAMPION_WHITELIST.clear();
    }

    @After
    public void restoreWhitelist() {
        ConfigEntities.CHAMPION_WHITELIST.clear();
        ConfigEntities.CHAMPION_WHITELIST.putAll(this.oldWhitelist);
    }

    @Test
    public void aspectOrbEligibilityUsesRecentlyHitForRegularAndTaintedMobs() {
        TestWorld world = new TestWorld();
        EntityZombie regular = new EntityZombie(world);
        EntityTaintPig tainted = new EntityTaintPig(world);

        EntityUtils.setRecentlyHit(regular, 20);
        EntityUtils.setRecentlyHit(tainted, 15);

        assertEquals(20, EntityUtils.getRecentlyHit(regular));
        assertEquals(15, EntityUtils.getRecentlyHit(tainted));
        assertTrue(EventHandlerEntity.isAspectOrbEligible(regular));
        assertTrue(EventHandlerEntity.isAspectOrbEligible(tainted));

        EntityUtils.setRecentlyHit(regular, 0);

        assertFalse(EventHandlerEntity.isAspectOrbEligible(regular));
    }

    @Test
    public void championWhitelistUsesInheritedRegistryAndClassEntriesWithMaximumTier() {
        EventHandlerEntity handler = new EventHandlerEntity();
        TestWorld world = new TestWorld();
        ConfigEntities.registerChampionWhitelist("minecraft:zombie", 1);
        ConfigEntities.registerChampionWhitelist(EntityBrainyZombie.class.getName(), 3);
        ConfigEntities.registerChampionWhitelist(EntityCultist.class.getName(), 2);

        assertEquals(3, handler.getChampionWhitelistTier(new EntityGiantBrainyZombie(world)));
        assertEquals(2, handler.getChampionWhitelistTier(new EntityCultistKnight(world)));
    }

    @Test
    public void championWhitelistKeepsBareLegacyNamesAndAssignableDirection() {
        EventHandlerEntity handler = new EventHandlerEntity();
        TestWorld world = new TestWorld();
        ConfigEntities.registerChampionWhitelist("Zombie", 4);

        assertEquals(4, handler.getChampionWhitelistTier(new EntityBrainyZombie(world)));

        ConfigEntities.CHAMPION_WHITELIST.clear();
        ConfigEntities.registerChampionWhitelist(EntityBrainyZombie.class.getName(), 5);

        assertEquals(-1, handler.getChampionWhitelistTier(new EntityZombie(world)));
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "event_drop_parity"),
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
                @Override public String makeString() { return "event_drop_parity_dummy"; }
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
