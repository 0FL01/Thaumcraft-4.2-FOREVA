package thaumcraft.common.lib.events;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChampionAttributePersistenceRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void championAttributeShouldExistBeforeAttributeNbtIsApplied() {
        EventHandlerEntity handler = new EventHandlerEntity();
        TestWorld world = new TestWorld();
        EntityZombie mob = new EntityZombie(world);

        handler.onEntityConstructing(new EntityEvent.EntityConstructing(mob));

        IAttributeInstance instance = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);
        assertNotNull(instance);
        assertEquals(-2.0D, instance.getBaseValue(), 0.0D);

        UUID modifierId = UUID.fromString("a9b9f8e5-473c-44b2-91c1-0d8e8f302701");
        NBTTagCompound modifier = new NBTTagCompound();
        modifier.setString("Name", "Champion test");
        modifier.setDouble("Amount", 5.0D);
        modifier.setInteger("Operation", 0);
        modifier.setUniqueId("UUID", modifierId);

        NBTTagList modifiers = new NBTTagList();
        modifiers.appendTag(modifier);

        NBTTagCompound championAttribute = new NBTTagCompound();
        championAttribute.setString("Name", "tc.mobmod");
        championAttribute.setDouble("Base", -2.0D);
        championAttribute.setTag("Modifiers", modifiers);

        NBTTagList attributes = new NBTTagList();
        attributes.appendTag(championAttribute);

        SharedMonsterAttributes.setAttributeModifiers(mob.getAttributeMap(), attributes);

        AttributeModifier applied = instance.getModifier(modifierId);
        assertNotNull(applied);
        assertEquals(3.0D, instance.getAttributeValue(), 0.0D);
    }

    private static class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "champion_attr"),
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
                    return "champion_attr_dummy";
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
