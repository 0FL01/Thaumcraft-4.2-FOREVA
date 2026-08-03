package thaumcraft.common.items.relics;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
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
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemSanityCheckerRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void rightClickEmitsOneActionbarMessageInsteadOfOverwritingIt() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        ItemSanityChecker checker = new ItemSanityChecker();
        player.inventory.currentItem = 0;
        player.inventory.mainInventory.set(0, new ItemStack(checker));
        player.knowledge.setWarpPerm(7);
        player.knowledge.setWarpSticky(5);
        player.knowledge.setWarpTemp(3);

        ActionResult<ItemStack> result = checker.onItemRightClick(world, player, EnumHand.MAIN_HAND);

        assertEquals(EnumActionResult.SUCCESS, result.getType());
        assertEquals(1, player.messages.size());
        assertTrue(player.messages.get(0) instanceof TextComponentTranslation);
        TextComponentTranslation message = (TextComponentTranslation)player.messages.get(0);
        assertEquals("tc.sanity", message.getKey());
        assertEquals(15, ((Number)message.getFormatArgs()[0]).intValue());
    }

    private static final class TestPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        private final List<ITextComponent> messages = new ArrayList<>();

        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    "sanity_checker".getBytes(StandardCharsets.UTF_8)), "sanity_checker"));
        }

        @Override
        public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
            assertTrue(actionBar);
            this.messages.add(chatComponent);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                return (T)this.knowledge;
            }
            return super.getCapability(capability, facing);
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "sanity_checker"),
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
                @Override public String makeString() { return "sanity_checker_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
