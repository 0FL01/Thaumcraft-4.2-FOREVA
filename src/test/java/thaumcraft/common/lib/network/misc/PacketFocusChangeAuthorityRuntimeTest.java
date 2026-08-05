package thaumcraft.common.lib.network.misc;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumHand;
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
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.container.ContainerFocusPouch;
import thaumcraft.common.items.wands.ItemWandCasting;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PacketFocusChangeAuthorityRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void sceptreRejectsFocusChangeBeforeInventoryMutation() {
        TestPlayer player = playerWithWandAndSpareFocus();
        ItemStack sceptre = player.getHeldItemMainhand();
        ItemStack spareFocus = player.inventory.getStackInSlot(1);
        ItemWandCasting.ensureTag(sceptre).setBoolean("sceptre", true);

        PacketFocusChangeToServer.handleFocusChange(player, "");

        assertTrue(ItemWandCasting.isSceptre(sceptre));
        assertSame(spareFocus, player.inventory.getStackInSlot(1));
    }

    @Test
    public void openFocusPouchRejectsFocusChangeBeforeInventoryMutation() {
        TestPlayer player = playerWithWandAndSpareFocus();
        ItemStack spareFocus = player.inventory.getStackInSlot(1);
        player.openContainer = new ContainerFocusPouch();

        PacketFocusChangeToServer.handleFocusChange(player, "");

        assertSame(spareFocus, player.inventory.getStackInSlot(1));
    }

    private static TestPlayer playerWithWandAndSpareFocus() {
        TestPlayer player = new TestPlayer(new TestWorld());
        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(new ItemWandCasting()));
        player.inventory.setInventorySlotContents(1, new ItemStack(new ItemFocusBasic()));
        return player;
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "focus_packet_authority"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "focus_packet_authority"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "focus_packet_authority_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
