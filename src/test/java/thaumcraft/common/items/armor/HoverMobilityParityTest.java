package thaumcraft.common.items.armor;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.config.ConfigBlocks;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HoverMobilityParityTest {
    private BlockJar previousJar;
    private BlockJar blockJar;
    private BlockJarItem jarItem;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void createJarMapping() {
        this.previousJar = ConfigBlocks.blockJar;
        this.blockJar = new BlockJar();
        this.jarItem = new BlockJarItem(this.blockJar);
        ConfigBlocks.blockJar = this.blockJar;
    }

    @After
    public void restoreJarMapping() {
        ConfigBlocks.blockJar = this.previousJar;
    }

    @Test
    public void fuelAcceptsOnlyNormalEnergyJarAndRewritesItAsEnergyOnly() {
        ItemStack normal = jar(0, new AspectList().add(Aspect.ENERGY, 2).add(Aspect.AIR, 3));
        ItemStack harness = harnessWith(normal, 360);

        assertTrue(Hover.isNormalEnergyJar(normal));
        assertTrue(Hover.consumeEnergyUnit(harness, normal));

        AspectList remainingAspects = this.jarItem.getAspects(normal);
        assertEquals(1, remainingAspects.getAmount(Aspect.ENERGY));
        assertEquals(0, remainingAspects.getAmount(Aspect.AIR));
        assertEquals(0, harness.getTagCompound().getShort("charge"));

        assertFalse(Hover.isNormalEnergyJar(jar(1, new AspectList().add(Aspect.ENERGY, 2))));
        BlockJar otherBlock = new BlockJar();
        BlockJarItem otherItem = new BlockJarItem(otherBlock);
        ItemStack otherJar = new ItemStack(otherItem, 1, 0);
        otherItem.setAspects(otherJar, new AspectList().add(Aspect.ENERGY, 2));
        assertFalse(Hover.isNormalEnergyJar(otherJar));
    }

    @Test
    public void stepAssistRestoresTheSavedHeightWithoutClobberingOtherMods() {
        TestPlayer player = new TestPlayer(new TestWorld(true));
        player.stepHeight = 0.75F;
        player.moveForward = 1.0F;
        player.onGround = false;

        Hover.doHover(ItemStack.EMPTY, player, player.world, 0);
        assertEquals(1.0F, player.stepHeight, 0.0F);
        Hover.resetHover(player);
        assertEquals(0.75F, player.stepHeight, 0.0F);

        player.stepHeight = 1.25F;
        Hover.resetHover(player);
        assertEquals(1.25F, player.stepHeight, 0.0F);
    }

    @Test
    public void containerValidationDelegatesToTheSameFuelPredicate() throws Exception {
        String source = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(
                "src/main/java/thaumcraft/common/container/ContainerHoverHarness.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("return Hover.isNormalEnergyJar(stack);"));
    }

    private ItemStack jar(int metadata, AspectList aspects) {
        ItemStack jar = new ItemStack(this.jarItem, 1, metadata);
        this.jarItem.setAspects(jar, aspects);
        return jar;
    }

    private static ItemStack harnessWith(ItemStack jar, int charge) {
        ItemStack harness = new ItemStack(net.minecraft.init.Items.LEATHER_CHESTPLATE);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound jarTag = new NBTTagCompound();
        jar.writeToNBT(jarTag);
        tag.setTag("jar", jarTag);
        tag.setShort("charge", (short) charge);
        harness.setTagCompound(tag);
        return harness;
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes("hover_mobility".getBytes(StandardCharsets.UTF_8)),
                    "hover_mobility"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return this.capabilities.isCreativeMode; }
    }

    private static final class TestWorld extends World {
        private TestWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "hover_mobility"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "hover_mobility_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
