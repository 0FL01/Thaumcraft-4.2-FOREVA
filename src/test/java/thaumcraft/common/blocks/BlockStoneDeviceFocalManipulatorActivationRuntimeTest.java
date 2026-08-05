package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileFocalManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockStoneDeviceFocalManipulatorActivationRuntimeTest {
    private IInternalMethodHandler oldInternalMethods;
    private ResearchHandler research;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockStoneDevice == null) ConfigBlocks.init();
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
    public void activationRequiresExactResearchAndLetsSneakingFallThroughSilently() {
        BlockStoneDevice block = ConfigBlocks.blockStoneDevice;
        BlockPos pos = new BlockPos(0, 64, 0);

        for (boolean researched : new boolean[]{false, true}) {
            for (boolean sneaking : new boolean[]{false, true}) {
                TestWorld world = new TestWorld();
                world.attach(pos, block.getStateFromMeta(13), new TileFocalManipulator());
                TestPlayer player = new TestPlayer(world);
                player.setSneaking(sneaking);
                this.research.researched = researched;
                this.research.lastKey = null;

                boolean activated = block.onBlockActivated(world, pos, block.getStateFromMeta(13), player,
                        EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F);

                if (sneaking) {
                    assertFalse(activated);
                    assertEquals(0, player.guiCount);
                    assertTrue(player.messages.isEmpty());
                    assertEquals(null, this.research.lastKey);
                } else if (researched) {
                    assertTrue(activated);
                    assertEquals(1, player.guiCount);
                    assertEquals(CommonProxy.GUI_FOCAL_MANIPULATOR, player.lastGui);
                    assertTrue(player.messages.isEmpty());
                    assertEquals("FOCALMANIPULATION", this.research.lastKey);
                } else {
                    assertTrue(activated);
                    assertEquals(0, player.guiCount);
                    assertEquals(1, player.messages.size());
                    ITextComponent message = player.messages.get(0);
                    assertTrue(message instanceof TextComponentTranslation);
                    assertEquals("tc.researchmissing", ((TextComponentTranslation) message).getKey());
                    assertSame(TextFormatting.RED, message.getStyle().getColor());
                    assertEquals("FOCALMANIPULATION", this.research.lastKey);
                }
            }
        }
    }

    private static final class ResearchHandler extends DummyInternalMethodHandler {
        private boolean researched;
        private String lastKey;

        @Override
        public boolean isResearchComplete(String username, String researchkey) {
            this.lastKey = researchkey;
            return this.researched && "FOCALMANIPULATION".equals(researchkey);
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private final List<ITextComponent> messages = new ArrayList<ITextComponent>();
        private int guiCount;
        private int lastGui = -1;

        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "focal_activation"));
        }

        @Override
        public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
            ++this.guiCount;
            this.lastGui = modGuiId;
        }

        @Override
        public void sendMessage(ITextComponent component) {
            this.messages.add(component);
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private IBlockState state;
        private TileEntity tile;

        private TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "focal_activation"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.state = state;
            this.tile = tile;
            tile.setWorld(this);
            tile.setPos(pos);
        }

        @Override public IBlockState getBlockState(BlockPos pos) { return this.state == null ? Blocks.AIR.getDefaultState() : this.state; }
        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tile; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "focal_activation_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
    }
}
