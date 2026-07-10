package thaumcraft.common.container;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileMagicWorkbench;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ArcaneWorkbenchRuntimeIntegrationTest {

    private IInternalMethodHandler oldInternalMethods;
    private List<Object> oldRecipes;
    private LinkedHashMap<String, WandCap> oldCaps;
    private LinkedHashMap<String, WandRod> oldRods;
    private thaumcraft.common.items.wands.ItemWandCasting oldWandCasting;
    private thaumcraft.common.items.ItemResource oldItemResource;

    private TestInternalMethodHandler handler;
    private DummyWorld world;
    private DummyPlayer player;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUp() {
        this.oldInternalMethods = ThaumcraftApi.internalMethods;
        this.oldRecipes = new ArrayList<>(ThaumcraftApi.getCraftingRecipes());
        this.oldCaps = new LinkedHashMap<>(WandCap.caps);
        this.oldRods = new LinkedHashMap<>(WandRod.rods);
        this.oldWandCasting = ConfigItems.itemWandCasting;
        this.oldItemResource = ConfigItems.itemResource;

        ThaumcraftApi.getCraftingRecipes().clear();
        WandCap.caps.clear();
        WandRod.rods.clear();

        this.handler = new TestInternalMethodHandler();
        ThaumcraftApi.internalMethods = this.handler;

        ConfigItems.itemWandCasting = new ItemWandCasting();
        ConfigItems.itemResource = new ItemResource();

        this.world = new DummyWorld();
        this.player = new DummyPlayer(this.world, "stage9b_tester");
    }

    @After
    public void tearDown() {
        ThaumcraftApi.internalMethods = this.oldInternalMethods;
        ThaumcraftApi.getCraftingRecipes().clear();
        ThaumcraftApi.getCraftingRecipes().addAll(this.oldRecipes);

        WandCap.caps.clear();
        WandCap.caps.putAll(this.oldCaps);
        WandRod.rods.clear();
        WandRod.rods.putAll(this.oldRods);

        ConfigItems.itemWandCasting = this.oldWandCasting;
        ConfigItems.itemResource = this.oldItemResource;
    }

    @Test
    public void workbenchMatcherShouldResolveShapedAndShapelessArcaneRecipesThroughTileRuntime() {
        Item inputA = new Item();
        Item inputB = new Item();
        Item shapedOutputItem = new Item();
        Item shapelessOutputItem = new Item();

        AspectList shapedCost = new AspectList().add(Aspect.AIR, 4).add(Aspect.FIRE, 2);
        AspectList shapelessCost = new AspectList().add(Aspect.WATER, 3);

        ThaumcraftApi.getCraftingRecipes().add(new ShapedArcaneRecipe("",
                new ItemStack(shapedOutputItem, 1, 7), shapedCost,
                "AB", "BA",
                'A', new ItemStack(inputA, 1, 0),
                'B', new ItemStack(inputB, 1, 0)));
        ThaumcraftApi.getCraftingRecipes().add(new ShapelessArcaneRecipe("",
                new ItemStack(shapelessOutputItem, 1, 5), shapelessCost,
                new ItemStack(inputA, 1, 0), new ItemStack(inputB, 1, 0)));

        TileArcaneWorkbench shapedTile = newWorkbench();
        shapedTile.setInventorySlotContentsSoftly(0, new ItemStack(inputA));
        shapedTile.setInventorySlotContentsSoftly(1, new ItemStack(inputB));
        shapedTile.setInventorySlotContentsSoftly(3, new ItemStack(inputB));
        shapedTile.setInventorySlotContentsSoftly(4, new ItemStack(inputA));

        ItemStack shapedOutput = ThaumcraftCraftingManager.findMatchingArcaneRecipe(shapedTile, this.player);
        AspectList shapedAspects = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(shapedTile, this.player);
        assertSame(shapedOutputItem, shapedOutput.getItem());
        assertEquals(7, shapedOutput.getMetadata());
        assertEquals(4, shapedAspects.getAmount(Aspect.AIR));
        assertEquals(2, shapedAspects.getAmount(Aspect.FIRE));

        TileArcaneWorkbench shapelessTile = newWorkbench();
        shapelessTile.setInventorySlotContentsSoftly(4, new ItemStack(inputB));
        shapelessTile.setInventorySlotContentsSoftly(8, new ItemStack(inputA));

        ItemStack shapelessOutput = ThaumcraftCraftingManager.findMatchingArcaneRecipe(shapelessTile, this.player);
        AspectList shapelessAspects = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(shapelessTile, this.player);
        assertSame(shapelessOutputItem, shapelessOutput.getItem());
        assertEquals(5, shapelessOutput.getMetadata());
        assertEquals(3, shapelessAspects.getAmount(Aspect.WATER));
    }

    @Test
    public void dynamicWandRecipeShouldRespectResearchGateAndProduceTaggedOutput() {
        Item capItem = new Item();
        Item rodItem = new Item();
        ItemStack capStack = new ItemStack(capItem, 1, 0);
        ItemStack rodStack = new ItemStack(rodItem, 1, 0);

        new WandCap("testcap", 0.9F, capStack, 2);
        new WandRod("testrod", 25, rodStack, 3);
        ThaumcraftApi.getCraftingRecipes().add(new ArcaneWandRecipe());

        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(slot(0, 2), capStack.copy());
        tile.setInventorySlotContentsSoftly(slot(2, 0), capStack.copy());
        tile.setInventorySlotContentsSoftly(slot(1, 1), rodStack.copy());

        assertTrue(ThaumcraftCraftingManager.findMatchingArcaneRecipe(tile, this.player).isEmpty());
        assertEquals(0, ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(tile, this.player).size());

        this.handler.allowResearch("CAP_testcap");
        this.handler.allowResearch("ROD_testrod");

        ItemStack output = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tile, this.player);
        AspectList aspects = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(tile, this.player);

        assertSame(ConfigItems.itemWandCasting, output.getItem());
        assertEquals(6, output.getMetadata());
        assertEquals("testcap", ItemWandCasting.getCap(output).getTag());
        assertEquals("testrod", ItemWandCasting.getRod(output).getTag());
        for (Aspect primal : Aspect.getPrimalAspects()) {
            assertEquals(6, aspects.getAmount(primal));
        }
    }

    @Test
    public void dynamicSceptreRecipeShouldRequireSceptreResearchAndProduceTaggedOutput() {
        Item capItem = new Item();
        Item rodItem = new Item();
        ItemStack capStack = new ItemStack(capItem, 1, 0);
        ItemStack rodStack = new ItemStack(rodItem, 1, 0);

        new WandCap("sceptrecap", 0.9F, capStack, 2);
        new WandRod("sceptrod", 25, rodStack, 3);
        ThaumcraftApi.getCraftingRecipes().add(new ArcaneSceptreRecipe());

        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(slot(1, 0), capStack.copy());
        tile.setInventorySlotContentsSoftly(slot(2, 1), capStack.copy());
        tile.setInventorySlotContentsSoftly(slot(0, 2), capStack.copy());
        tile.setInventorySlotContentsSoftly(slot(1, 1), rodStack.copy());
        tile.setInventorySlotContentsSoftly(slot(2, 0), new ItemStack(ConfigItems.itemResource, 1, 15));

        this.handler.allowResearch("CAP_sceptrecap");
        this.handler.allowResearch("ROD_sceptrod");
        assertTrue(ThaumcraftCraftingManager.findMatchingArcaneRecipe(tile, this.player).isEmpty());

        this.handler.allowResearch("SCEPTRE");
        ItemStack output = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tile, this.player);
        AspectList aspects = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(tile, this.player);

        assertSame(ConfigItems.itemWandCasting, output.getItem());
        assertEquals(9, output.getMetadata());
        assertEquals(1, output.getTagCompound().getByte("sceptre"));
        assertEquals("sceptrecap", ItemWandCasting.getCap(output).getTag());
        assertEquals("sceptrod", ItemWandCasting.getRod(output).getTag());
        for (Aspect primal : Aspect.getPrimalAspects()) {
            assertEquals(9, aspects.getAmount(primal));
        }
    }

    @Test
    public void craftingSlotShouldConsumeVisOnceAndKeepContainerRemainderInGrid() {
        Item outputItem = Items.DIAMOND;
        TestContainerItem inputItem = new TestContainerItem();
        TestWandItem wandItem = new TestWandItem();

        ThaumcraftApi.getCraftingRecipes().add(new ShapedArcaneRecipe("",
                new ItemStack(outputItem, 1, 0), new AspectList().add(Aspect.AIR, 5),
                "A",
                'A', new ItemStack(inputItem, 1, 0)));

        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(slot(0, 0), new ItemStack(inputItem, 1, 0));
        tile.setInventorySlotContentsSoftly(10, new ItemStack(wandItem, 1, 0));

        ContainerArcaneWorkbench container = new ContainerArcaneWorkbench(this.player.inventory, tile);
        SlotCraftingArcaneWorkbench slot = (SlotCraftingArcaneWorkbench) container.inventorySlots.get(0);
        ItemStack crafted = slot.decrStackSize(1);
        slot.onTake(this.player, crafted);

        assertSame(outputItem, crafted.getItem());
        assertEquals(1, wandItem.actualConsumeCalls);
        assertTrue(ItemStack.areItemStacksEqual(inputItem.containerStack(), tile.getStackInSlot(slot(0, 0))));
    }

    @Test
    public void containerPreviewRebuildShouldUseDetachedCraftMatrixSnapshot() {
        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(slot(0, 0), new ItemStack(Items.STICK));

        ContainerArcaneWorkbench container = new ContainerArcaneWorkbench(this.player.inventory, tile);
        container.onCraftMatrixChanged(tile);

        assertEquals(47, container.inventorySlots.size());
    }

    @Test
    public void resultShouldBePlayerLocalAndRevalidatedBeforeTake() {
        Item input = Items.STICK;
        TestWandItem wand = new TestWandItem();
        ThaumcraftApi.getCraftingRecipes().add(new PlayerLockedRecipe("allowed", input, Items.DIAMOND));

        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(0, new ItemStack(input));
        tile.setInventorySlotContentsSoftly(10, new ItemStack(wand));

        DummyPlayer allowed = new DummyPlayer(this.world, "allowed");
        DummyPlayer allowedSecondViewer = new DummyPlayer(this.world, "allowed");
        ContainerArcaneWorkbench deniedContainer = new ContainerArcaneWorkbench(this.player.inventory, tile);
        ContainerArcaneWorkbench allowedContainer = new ContainerArcaneWorkbench(allowed.inventory, tile);
        ContainerArcaneWorkbench allowedSecondContainer = new ContainerArcaneWorkbench(allowedSecondViewer.inventory, tile);

        assertFalse(deniedContainer.inventorySlots.get(0).getHasStack());
        assertTrue(allowedContainer.inventorySlots.get(0).getHasStack());
        assertTrue(allowedSecondContainer.inventorySlots.get(0).getHasStack());
        assertTrue(tile.getStackInSlot(9).isEmpty());

        tile.setInventorySlotContents(0, ItemStack.EMPTY);
        assertFalse(allowedContainer.inventorySlots.get(0).getHasStack());
        assertFalse(allowedSecondContainer.inventorySlots.get(0).getHasStack());
        allowedSecondContainer.onContainerClosed(allowedSecondViewer);
        tile.setInventorySlotContents(0, new ItemStack(input));
        assertTrue(allowedContainer.inventorySlots.get(0).getHasStack());

        SlotCraftingArcaneWorkbench result = (SlotCraftingArcaneWorkbench) allowedContainer.inventorySlots.get(0);
        ItemStack staleOutput = result.getStack().copy();
        tile.setInventorySlotContentsSoftly(10, ItemStack.EMPTY);

        assertFalse(result.canTakeStack(allowed));
        assertTrue(result.onTake(allowed, staleOutput).isEmpty());
        assertFalse(tile.getStackInSlot(0).isEmpty());
        assertEquals(0, wand.actualConsumeCalls);
    }

    @Test
    public void shiftClickShouldNotPartiallyMoveAndLoseMultiItemResult() {
        TestWandItem wand = new TestWandItem();
        ThaumcraftApi.getCraftingRecipes().add(new ShapedArcaneRecipe("",
                new ItemStack(Items.DIAMOND, 4), new AspectList().add(Aspect.AIR, 1),
                "A", 'A', new ItemStack(Items.STICK)));

        TileArcaneWorkbench tile = newWorkbench();
        tile.setInventorySlotContentsSoftly(0, new ItemStack(Items.STICK));
        tile.setInventorySlotContentsSoftly(10, new ItemStack(wand));
        for (int i = 0; i < this.player.inventory.getSizeInventory(); i++) {
            this.player.inventory.setInventorySlotContents(i, new ItemStack(Blocks.COBBLESTONE, 64));
        }
        this.player.inventory.setInventorySlotContents(9, new ItemStack(Items.DIAMOND, 62));

        ContainerArcaneWorkbench container = new ContainerArcaneWorkbench(this.player.inventory, tile);
        assertFalse(container.canMergeSlot(new ItemStack(Items.DIAMOND), container.inventorySlots.get(0)));
        assertTrue(container.canMergeSlot(new ItemStack(Items.DIAMOND), container.inventorySlots.get(11)));
        assertTrue(container.transferStackInSlot(this.player, 0).isEmpty());
        assertEquals(4, container.inventorySlots.get(0).getStack().getCount());
        assertFalse(tile.getStackInSlot(0).isEmpty());
        assertEquals(0, wand.actualConsumeCalls);

        this.player.inventory.setInventorySlotContents(9, new ItemStack(Items.DIAMOND, 60));
        assertEquals(4, container.transferStackInSlot(this.player, 0).getCount());
        assertEquals(64, this.player.inventory.getStackInSlot(9).getCount());
        assertTrue(tile.getStackInSlot(0).isEmpty());
        assertEquals(1, wand.actualConsumeCalls);
    }

    @Test
    public void legacyResultSlotShouldNotPersistAndStaffShouldBeRejected() {
        TileArcaneWorkbench source = newWorkbench();
        source.setInventorySlotContentsSoftly(0, new ItemStack(Items.STICK));
        source.setInventorySlotContentsSoftly(9, new ItemStack(Items.DIAMOND));

        NBTTagCompound tag = new NBTTagCompound();
        source.writeCustomNBT(tag);
        TileArcaneWorkbench loaded = newWorkbench();
        loaded.readCustomNBT(tag);

        assertFalse(loaded.getStackInSlot(0).isEmpty());
        assertTrue(loaded.getStackInSlot(9).isEmpty());

        StaffRod staffRod = new StaffRod("test_staff", 100, new ItemStack(Items.STICK), 1);
        ItemStack staff = new ItemStack(ConfigItems.itemWandCasting);
        ItemWandCasting.setRod(staff, staffRod);
        assertFalse(loaded.isItemValidForSlot(10, staff));
        assertFalse(loaded.canInsertItem(10, staff, net.minecraft.util.EnumFacing.UP));
    }

    @Test
    public void craftingVisShouldRequireStoredVisAndSpecialCapModifierShouldReplaceBase() {
        WandCap cap = new WandCap("test_special", 0.5F,
                java.util.Collections.singletonList(Aspect.AIR), 0.9F, new ItemStack(Items.IRON_NUGGET), 1);
        WandRod rod = new WandRod("test_rod", 25, new ItemStack(Items.STICK), 1);
        ItemWandCasting wand = new ItemWandCasting();
        ItemStack stack = new ItemStack(wand);
        ItemWandCasting.setCap(stack, cap);
        ItemWandCasting.setRod(stack, rod);

        assertEquals(0.9F, ItemWandCasting.getConsumptionModifier(stack, null, Aspect.AIR, true), 0.0001F);
        assertEquals(0.5F, ItemWandCasting.getConsumptionModifier(stack, null, Aspect.FIRE, true), 0.0001F);

        assertFalse(wand.consumeAllVisCrafting(stack, null, new AspectList().add(Aspect.AIR, 1), false));
    }

    private TestArcaneWorkbench newWorkbench() {
        TestArcaneWorkbench tile = new TestArcaneWorkbench();
        tile.setWorld(this.world);
        tile.setPos(BlockPos.ORIGIN);
        return tile;
    }

    private static int slot(int x, int y) {
        return x + y * 3;
    }

    private static class TestInternalMethodHandler extends InternalMethodHandler {
        private final java.util.HashSet<String> completeResearch = new java.util.HashSet<>();

        void allowResearch(String key) {
            this.completeResearch.add(key);
        }

        @Override
        public boolean isResearchComplete(String username, String researchkey) {
            return researchkey == null || researchkey.isEmpty() || this.completeResearch.contains(researchkey);
        }
    }

    private static class DummyWorld extends World {
        DummyWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "stage9b"),
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
                    return "stage9b_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return false;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return false;
        }
    }

    private static class DummyPlayer extends EntityPlayer {
        DummyPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return this.capabilities.isCreativeMode;
        }
    }

    private static class TestArcaneWorkbench extends TileArcaneWorkbench {
        @Override
        public void markDirty() {
        }
    }

    private static class TestWandItem extends ItemWandCasting {
        int actualConsumeCalls;

        @Override
        public boolean consumeAllVisCrafting(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit) {
            if (doit) this.actualConsumeCalls++;
            return true;
        }
    }

    private static class TestContainerItem extends Item {
        private final Item container = new Item();

        @Override
        public boolean hasContainerItem(ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
            return new ItemStack(this.container, 1, 0);
        }

        ItemStack containerStack() {
            return new ItemStack(this.container, 1, 0);
        }
    }

    private static class PlayerLockedRecipe implements IArcaneRecipe {
        private final String playerName;
        private final Item input;
        private final Item output;

        PlayerLockedRecipe(String playerName, Item input, Item output) {
            this.playerName = playerName;
            this.input = input;
            this.output = output;
        }

        @Override
        public boolean matches(IInventory inventory, World world, EntityPlayer player) {
            return player != null && this.playerName.equals(player.getName())
                    && inventory.getStackInSlot(0).getItem() == this.input;
        }

        @Override
        public ItemStack getCraftingResult(IInventory inventory) {
            return new ItemStack(this.output);
        }

        @Override
        public int getRecipeSize() {
            return 1;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(this.output);
        }

        @Override
        public AspectList getAspects() {
            return new AspectList().add(Aspect.AIR, 1);
        }

        @Override
        public AspectList getAspects(IInventory inventory) {
            return this.getAspects();
        }

        @Override
        public String getResearch() {
            return "";
        }
    }
}
