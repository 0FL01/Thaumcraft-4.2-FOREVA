package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileInfusionMatrixArtificeParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void firstWandUseActivatesAndSecondUseStartsCrafting() {
        TestMatrix matrix = new TestMatrix();
        TestWorld world = new TestWorld();

        matrix.onWandRightClick(world, ItemStack.EMPTY, null, 0, 0, 0, 1, 2);
        assertTrue(matrix.active);
        assertFalse(matrix.crafting);
        assertEquals(0, matrix.starts);

        matrix.onWandRightClick(world, ItemStack.EMPTY, null, 0, 0, 0, 1, 2);
        assertEquals(1, matrix.starts);
    }

    @Test
    public void wandPedestalNeverQualifiesAsInfusionPedestal() {
        assertTrue(TileInfusionMatrix.isInfusionPedestal(new TilePedestal()));
        assertFalse(TileInfusionMatrix.isInfusionPedestal(new TileWandPedestal()));
    }

    @Test
    public void publicAspectContainerCannotMutateOutstandingRecipeCost() {
        TileInfusionMatrix matrix = new TileInfusionMatrix();
        matrix.getAspects().add(Aspect.FIRE, 8);

        matrix.setAspects(new AspectList().add(Aspect.WATER, 4));
        assertEquals(0, matrix.addToContainer(Aspect.WATER, 2));
        assertFalse(matrix.takeFromContainer(Aspect.FIRE, 1));
        assertFalse(matrix.takeFromContainer(new AspectList().add(Aspect.FIRE, 1)));
        assertFalse(matrix.doesContainerContainAmount(Aspect.FIRE, 1));
        assertFalse(matrix.doesContainerContain(new AspectList().add(Aspect.FIRE, 1)));
        assertEquals(0, matrix.containerContains(Aspect.FIRE));
        assertTrue(matrix.doesContainerAccept(Aspect.FIRE));
        assertEquals(8, matrix.getAspects().getAmount(Aspect.FIRE));
        assertEquals(0, matrix.getAspects().getAmount(Aspect.WATER));
    }

    @Test
    public void activeCraftStateSurvivesFreshPortNbtRoundTrip() {
        NBTTagCompound saved = new NBTTagCompound();
        saved.setBoolean("active", true);
        saved.setBoolean("crafting", true);
        saved.setShort("instability", (short) 7);
        new AspectList().add(Aspect.FIRE, 8).writeToNBT(saved);
        NBTTagList ingredients = new NBTTagList();
        ingredients.appendTag(new ItemStack(Items.REDSTONE).writeToNBT(new NBTTagCompound()));
        saved.setTag("recipein", ingredients);
        saved.setString("rotype", "@");
        saved.setTag("recipeout", new ItemStack(Items.DIAMOND).writeToNBT(new NBTTagCompound()));
        saved.setTag("recipeinput", new ItemStack(Items.IRON_INGOT).writeToNBT(new NBTTagCompound()));
        saved.setInteger("recipeinst", 4);
        saved.setInteger("recipetype", 0);
        saved.setInteger("recipexp", 3);
        saved.setString("recipeplayer", "artificer");
        saved.setInteger("countdelay", 20);
        saved.setInteger("itemcount", 2);

        TileInfusionMatrix matrix = new TileInfusionMatrix();
        matrix.readCustomNBT(saved);
        NBTTagCompound roundTrip = new NBTTagCompound();
        matrix.writeCustomNBT(roundTrip);

        assertTrue(roundTrip.getBoolean("active"));
        assertTrue(roundTrip.getBoolean("crafting"));
        assertEquals(7, roundTrip.getShort("instability"));
        assertEquals(8, matrix.getAspects().getAmount(Aspect.FIRE));
        assertEquals(1, roundTrip.getTagList("recipein", 10).tagCount());
        assertTrue(new ItemStack(roundTrip.getCompoundTag("recipeout")).isItemEqual(new ItemStack(Items.DIAMOND)));
        assertTrue(new ItemStack(roundTrip.getCompoundTag("recipeinput")).isItemEqual(new ItemStack(Items.IRON_INGOT)));
        assertEquals(20, roundTrip.getInteger("countdelay"));
        assertEquals(2, roundTrip.getInteger("itemcount"));
    }

    @Test
    public void interactionAndFxRoutesStaySingleAndReferenceShaped() throws Exception {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java");
        String wandManager = read("src/main/java/thaumcraft/common/items/wands/WandManager.java");
        String pedestal = read("src/main/java/thaumcraft/common/tiles/TilePedestal.java");
        String sourceFx = read("src/main/java/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.java");

        int activationStart = block.indexOf("public boolean onBlockActivated");
        int activationEnd = block.indexOf("private boolean handleWandPedestalActivation", activationStart);
        assertFalse(block.substring(activationStart, activationEnd).contains("TileInfusionMatrix) te).onWandRightClick"));
        assertTrue(wandManager.contains("world.addBlockEvent(target, ConfigBlocks.blockStoneDevice, 1, 0);"));
        assertTrue(block.contains("Thaumcraft.proxy.blockSparkle(worldIn, pos.getX(), pos.getY(), pos.getZ(), 11960575, 2);"));
        assertTrue(block.contains("Blocks.QUARTZ_BLOCK.getDefaultState()"));
        assertTrue(pedestal.contains("0xC000C0, 2"));
        assertTrue(pedestal.contains("-9999, 2"));
        assertTrue(sourceFx.contains("TileInfusionMatrix.isInfusionPedestal(sourceTile)"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static final class TestMatrix extends TileInfusionMatrix {
        int starts;

        @Override
        public boolean validLocation() {
            return true;
        }

        @Override
        public void craftingStart(EntityPlayer player) {
            ++this.starts;
        }
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "infusion"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "InfusionTest"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return false; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
