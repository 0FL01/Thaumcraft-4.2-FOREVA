package thaumcraft.common.items.wands.foci;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
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
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.wands.FocusUpgradeType;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FocusHellbatUpgradeGateRuntimeTest {
    private IInternalMethodHandler oldInternalMethods;
    private ResearchHandler research;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installResearchHandler() {
        this.oldInternalMethods = ThaumcraftApi.internalMethods;
        this.research = new ResearchHandler();
        ThaumcraftApi.internalMethods = this.research;
    }

    @After
    public void restoreResearchHandler() {
        ThaumcraftApi.internalMethods = this.oldInternalMethods;
    }

    @Test
    public void rankFiveOffersVampireBatsAndOnlyThatUpgradeNeedsExactResearch() {
        FocusHellbat focus = new FocusHellbat();
        ItemStack stack = new ItemStack(focus);
        TestPlayer player = new TestPlayer(new TestWorld());
        FocusUpgradeType[] rankFive = focus.getPossibleUpgradesByRank(stack, 5);

        assertTrue(Arrays.asList(rankFive).contains(FocusUpgradeType.frugal));
        assertTrue(Arrays.asList(rankFive).contains(FocusUpgradeType.potency));
        assertTrue(Arrays.asList(rankFive).contains(FocusHellbat.vampirebats));
        assertFalse(Arrays.asList(focus.getPossibleUpgradesByRank(stack, 3)).contains(FocusHellbat.vampirebats));

        this.research.completedKey = "VAMPBAT_CHILD";
        assertFalse(focus.canApplyUpgrade(stack, player, FocusHellbat.vampirebats, 5));
        assertEquals("VAMPBAT", this.research.lastRequestedKey);

        this.research.completedKey = "VAMPBAT";
        assertTrue(focus.canApplyUpgrade(stack, player, FocusHellbat.vampirebats, 5));
        assertEquals("VAMPBAT", this.research.lastRequestedKey);

        this.research.completedKey = null;
        assertTrue(focus.canApplyUpgrade(stack, player, FocusUpgradeType.frugal, 5));
        assertTrue(focus.canApplyUpgrade(stack, player, FocusUpgradeType.potency, 5));
        assertTrue(focus.canApplyUpgrade(stack, player, FocusHellbat.batbombs, 3));
        assertTrue(focus.canApplyUpgrade(stack, player, FocusHellbat.devilbats, 3));
    }

    private static final class ResearchHandler extends DummyInternalMethodHandler {
        private String completedKey;
        private String lastRequestedKey;

        @Override
        public boolean isResearchComplete(String username, String researchkey) {
            this.lastRequestedKey = researchkey;
            return researchkey.equals(this.completedKey);
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "hellbat_upgrade_gate"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "hellbat_upgrade_gate"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "hellbat_upgrade_gate_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
    }
}
