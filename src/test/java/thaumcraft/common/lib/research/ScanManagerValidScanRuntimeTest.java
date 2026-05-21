package thaumcraft.common.lib.research;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
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
import net.minecraftforge.common.capabilities.Capability;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScanManagerValidScanRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void freshPlayerShouldAcceptStoneAndSandLikeAspectLists() {
        ScanWorld world = new ScanWorld();
        ScanPlayer player = new ScanPlayer(world, "valid_scan_fresh");

        assertTrue(ScanManager.validScan(new AspectList().add(Aspect.EARTH, 3), player));
        assertTrue(ScanManager.validScan(new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 1), player));
    }

    @Test
    public void freshPlayerShouldRejectGrassAndOtherCompoundsWhoseParentAspectRemainsUndiscovered() {
        ScanWorld world = new ScanWorld();
        ScanPlayer player = new ScanPlayer(world, "valid_scan_compound");

        assertFalse(ScanManager.validScan(new AspectList().add(Aspect.EARTH, 1).add(Aspect.PLANT, 1), player));
        assertFalse(ScanManager.validScan(new AspectList().add(Aspect.TREE, 1), player));
    }

    private static class ScanPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();

        ScanPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.knowledge.addDiscoveredPrimalAspects();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE || super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                @SuppressWarnings("unchecked")
                T value = (T) this.knowledge;
                return value;
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    private static class ScanWorld extends World {
        ScanWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "scan_valid"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
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
                    return "scan-valid";
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
