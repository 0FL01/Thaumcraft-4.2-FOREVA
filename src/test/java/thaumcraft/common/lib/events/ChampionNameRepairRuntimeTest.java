package thaumcraft.common.lib.events;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.lib.utils.EntityUtils;

import static org.junit.Assert.assertEquals;

public class ChampionNameRepairRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void joinWorldShouldRepairPersistedTechnicalChampionNames() {
        EventHandlerEntity handler = new EventHandlerEntity();
        TestWorld world = new TestWorld();
        EntityZombie mob = new EntityZombie(world);

        handler.onEntityConstructing(new EntityEvent.EntityConstructing(mob));

        IAttributeInstance instance = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);
        instance.applyModifier(ChampionModifier.mods[6].attributeMod);
        mob.setCustomNameTag("champion.mod.warp Zombie");

        handler.onEntityJoinWorld(new EntityJoinWorldEvent(mob, world));

        assertEquals("Warped Zombie", mob.getCustomNameTag());
    }

    private static class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "champion_name_repair"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.getWorldInfo().setSpawn(new BlockPos(0, 64, 0));
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) {
                    return null;
                }

                @Override
                public Chunk provideChunk(int x, int z) {
                    return null;
                }

                @Override
                public boolean tick() {
                    return false;
                }

                @Override
                public String makeString() {
                    return "champion_name_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return true;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
