package thaumcraft.common.tiles;

import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockMetalDevice;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TileThaumatorium extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport, ISidedInventory {
    private static final int[] SLOTS = new int[]{0};

    public ItemStack inputStack = ItemStack.EMPTY;
    public AspectList essentia = new AspectList();
    public ArrayList<Integer> recipeHash = new ArrayList<Integer>();
    public ArrayList<AspectList> recipeEssentia = new ArrayList<AspectList>();
    public ArrayList<String> recipePlayer = new ArrayList<String>();
    public int currentCraft = -1;
    public int maxRecipes = 1;
    public EnumFacing facing = EnumFacing.NORTH;
    public Aspect currentSuction = null;
    public Container eventHandler;

    private int venting = 0;
    private int counter = 0;
    private boolean heated = false;
    private CrucibleRecipe currentRecipe = null;

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.world.isRemote) {
            if (this.venting > 0) {
                --this.venting;
                float fx = 0.1f - this.world.rand.nextFloat() * 0.2f;
                float fz = 0.1f - this.world.rand.nextFloat() * 0.2f;
                float fy = 0.1f - this.world.rand.nextFloat() * 0.2f;
                float fx2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                float fz2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                float fy2 = 0.1f - this.world.rand.nextFloat() * 0.2f;
                Thaumcraft.proxy.drawVentParticles(
                        this.world,
                        this.pos.getX() + 0.5f + fx + this.facing.getXOffset() / 2.0f,
                        this.pos.getY() + 0.5f + fy,
                        this.pos.getZ() + 0.5f + fz + this.facing.getZOffset() / 2.0f,
                        this.facing.getXOffset() / 4.0f + fx2,
                        fy2,
                        this.facing.getZOffset() / 4.0f + fz2,
                        0xFFFFFF);
            }
            return;
        }

        if (this.counter == 0 || this.counter % 40 == 0) {
            this.heated = this.checkHeat();
            this.getUpgrades();
        }
        ++this.counter;

        if (!this.heated || this.gettingPower() || this.counter % 5 != 0 || this.recipeHash.isEmpty()) return;
        if (this.inputStack.isEmpty()) {
            this.currentSuction = null;
            return;
        }

        if (this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size()
                || this.currentRecipe == null || !this.currentRecipe.catalystMatches(this.inputStack)) {
            this.currentCraft = -1;
            this.currentRecipe = null;
            for (int i = 0; i < this.recipeHash.size(); ++i) {
                CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(i));
                if (recipe == null || !recipe.catalystMatches(this.inputStack)) continue;
                this.currentCraft = i;
                this.currentRecipe = recipe;
                break;
            }
        }

        if (this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size() || this.currentRecipe == null) return;

        TileEntity outputInventory = this.world.getTileEntity(this.pos.offset(this.facing));
        if (outputInventory instanceof IInventory) {
            ItemStack remainder = InventoryUtils.placeItemStackIntoInventory(this.getCurrentOutputRecipe(),
                    (IInventory) outputInventory, this.facing.getOpposite().getIndex(), false);
            if (!remainder.isEmpty()) return;
        }

        boolean done = true;
        this.currentSuction = null;
        AspectList required = this.recipeEssentia.get(this.currentCraft);
        for (Aspect aspect : required.getAspectsSorted()) {
            if (this.essentia.getAmount(aspect) >= required.getAmount(aspect)) continue;
            this.currentSuction = aspect;
            done = false;
            break;
        }

        if (done) {
            this.completeRecipe();
        } else if (this.currentSuction != null) {
            this.fill();
        }
    }

    boolean checkHeat() {
        IBlockState state = this.world.getBlockState(this.pos.down(2));
        Material material = state.getMaterial();
        return material == Material.FIRE || material == Material.LAVA
                || (state.getBlock() == ConfigBlocks.blockAiry && state.getValue(BlockAiry.TYPE) == 1);
    }

    public ItemStack getCurrentOutputRecipe() {
        if (this.currentCraft >= 0 && this.currentCraft < this.recipeHash.size()) {
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(this.recipeHash.get(this.currentCraft));
            if (recipe != null) return recipe.getRecipeOutput().copy();
        }
        return ItemStack.EMPTY;
    }

    private void completeRecipe() {
        if (this.currentRecipe == null || this.currentCraft < 0 || this.currentCraft >= this.recipeHash.size()) return;
        if (!this.currentRecipe.matches(this.essentia, this.inputStack)) return;
        if (this.decrStackSize(0, 1).isEmpty()) return;

        this.essentia = new AspectList();
        ItemStack output = this.getCurrentOutputRecipe();
        String playerName = this.recipePlayer.size() > this.currentCraft ? this.recipePlayer.get(this.currentCraft) : "";
        EntityPlayer player = this.world.getPlayerEntityByName(playerName);
        if (player != null) {
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, output, new InventoryFake(new ItemStack[]{this.inputStack}));
        }

        TileEntity outputInventory = this.world.getTileEntity(this.pos.offset(this.facing));
        if (outputInventory instanceof IInventory) {
            output = InventoryUtils.placeItemStackIntoInventory(output, (IInventory) outputInventory,
                    this.facing.getOpposite().getIndex(), true);
        }

        if (!output.isEmpty()) {
            EntityItem entity = new EntityItem(this.world,
                    (double) this.pos.getX() + 0.5D + (double) this.facing.getXOffset() * 0.66D,
                    (double) this.pos.getY() + 0.33D + (double) this.facing.getOpposite().getYOffset(),
                    (double) this.pos.getZ() + 0.5D + (double) this.facing.getZOffset() * 0.66D,
                    output.copy());
            entity.motionX = 0.075F * (float) this.facing.getXOffset();
            entity.motionY = 0.025F;
            entity.motionZ = 0.075F * (float) this.facing.getZOffset();
            this.world.spawnEntity(entity);
            this.world.addBlockEvent(this.pos, this.getBlockType(), 0, 0);
        }

        this.world.playSound(null,
                (double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D,
                (double) this.pos.getZ() + 0.5D,
                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS,
                0.25F,
                2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
        this.currentCraft = -1;
        this.currentRecipe = null;
        this.markDirtyAndSync();
    }

    void fill() {
        for (int y = 0; y <= 1; ++y) {
            for (EnumFacing dir : EnumFacing.values()) {
                if (dir == this.facing || dir == EnumFacing.DOWN || (y == 0 && dir == EnumFacing.UP)) continue;

                TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world,
                        this.pos.getX(), this.pos.getY() + y, this.pos.getZ(), dir);
                if (!(tile instanceof IEssentiaTransport)) continue;

                IEssentiaTransport transport = (IEssentiaTransport) tile;
                EnumFacing inputSide = dir.getOpposite();
                if (transport.getEssentiaAmount(inputSide) <= 0
                        || transport.getSuctionAmount(inputSide) >= this.getSuctionAmount(null)
                        || this.getSuctionAmount(null) < transport.getMinimumSuction()) continue;

                int taken = transport.takeEssentia(this.currentSuction, 1, inputSide);
                if (taken <= 0) continue;
                this.addToContainer(this.currentSuction, taken);
                return;
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getByte("facing"));
        this.essentia.readFromNBT(nbt);
        this.maxRecipes = Math.max(1, nbt.getByte("maxrec"));

        this.recipeEssentia = new ArrayList<AspectList>();
        this.recipeHash = new ArrayList<Integer>();
        this.recipePlayer = new ArrayList<String>();
        int[] hashes = nbt.getIntArray("recipes");
        for (int hash : hashes) {
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
            if (recipe == null) continue;
            this.recipeEssentia.add(recipe.aspects.copy());
            this.recipePlayer.add("");
            this.recipeHash.add(hash);
        }

        this.inputStack = ItemStack.EMPTY;
        NBTTagList items = nbt.getTagList("Items", 10);
        if (items.tagCount() > 0) {
            this.inputStack = new ItemStack(items.getCompoundTagAt(0));
        }

        NBTTagList players = nbt.getTagList("OutputPlayer", 8);
        for (int i = 0; i < players.tagCount() && i < this.recipePlayer.size(); ++i) {
            this.recipePlayer.set(i, players.getStringTagAt(i));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("facing", (byte) this.facing.getIndex());
        nbt.setByte("maxrec", (byte) this.maxRecipes);
        this.essentia.writeToNBT(nbt);

        int[] hashes = new int[this.recipeHash.size()];
        for (int i = 0; i < this.recipeHash.size(); ++i) hashes[i] = this.recipeHash.get(i);
        nbt.setIntArray("recipes", hashes);

        NBTTagList items = new NBTTagList();
        if (!this.inputStack.isEmpty()) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) 0);
            this.inputStack.writeToNBT(itemTag);
            items.appendTag(itemTag);
        }
        nbt.setTag("Items", items);

        NBTTagList players = new NBTTagList();
        for (String name : this.recipePlayer) {
            if (name != null) players.appendTag(new NBTTagString(name));
        }
        nbt.setTag("OutputPlayer", players);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0 || this.currentRecipe == null) return amount;
        int needed = this.currentRecipe.aspects.getAmount(aspect) - this.essentia.getAmount(aspect);
        if (needed <= 0) return amount;
        int add = Math.min(needed, amount);
        this.essentia.add(aspect, add);
        this.markDirtyAndSync();
        return amount - add;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect != null && this.essentia.getAmount(aspect) >= amount) {
            this.essentia.remove(aspect, amount);
            this.markDirtyAndSync();
            return true;
        }
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return this.essentia.getAmount(aspect) >= amount;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return this.essentia.getAmount(aspect);
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return true;
    }

    @Override
    public AspectList getAspects() {
        return this.essentia;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.essentia = aspects == null ? new AspectList() : aspects.copy();
        this.markDirtyAndSync();
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id >= 0) {
            if (this.world != null && this.world.isRemote) this.venting = 7;
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != this.facing;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face != this.facing;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        this.currentSuction = aspect;
    }

    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return this.currentSuction;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return this.currentSuction != null ? 128 : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.inputStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0 || this.inputStack.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack;
        if (this.inputStack.getCount() <= count) {
            stack = this.inputStack;
            this.inputStack = ItemStack.EMPTY;
        } else {
            stack = this.inputStack.splitStack(count);
            if (this.inputStack.getCount() <= 0) this.inputStack = ItemStack.EMPTY;
        }
        if (this.eventHandler != null) this.eventHandler.onCraftMatrixChanged(this);
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index != 0 || this.inputStack.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.inputStack;
        this.inputStack = ItemStack.EMPTY;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index != 0) return;
        this.inputStack = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
        if (this.eventHandler != null) this.eventHandler.onCraftMatrixChanged(this);
        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.alchemyfurnace";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public int getField(int id) {
        return id == 0 ? this.currentCraft : this.maxRecipes;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) this.currentCraft = value;
        if (id == 1) this.maxRecipes = value;
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        this.inputStack = ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return this.inputStack.isEmpty();
    }

    public boolean gettingPower() {
        return this.world != null && (this.world.isBlockPowered(this.pos)
                || this.world.isBlockPowered(this.pos.down())
                || this.world.isBlockPowered(this.pos.up()));
    }

    public void getUpgrades() {
        if (this.world == null) return;
        int max = 1;
        for (int y = 0; y <= 1; ++y) {
            for (EnumFacing dir : EnumFacing.values()) {
                if (dir == EnumFacing.DOWN || dir == this.facing) continue;
                BlockPos check = this.pos.add(dir.getXOffset(), y + dir.getYOffset(), dir.getZOffset());
                IBlockState state = this.world.getBlockState(check);
                if (state.getBlock() != ConfigBlocks.blockMetalDevice || state.getValue(BlockMetalDevice.TYPE) != 12) continue;
                TileEntity te = this.world.getTileEntity(check);
                if (!(te instanceof TileBrainbox) || ((TileBrainbox) te).facing != dir.getOpposite()) continue;
                max += 2;
            }
        }

        if (max == this.maxRecipes) return;
        this.maxRecipes = max;
        while (this.recipeHash.size() > this.maxRecipes) {
            int index = this.recipeHash.size() - 1;
            this.recipeHash.remove(index);
            if (index < this.recipeEssentia.size()) this.recipeEssentia.remove(index);
            if (index < this.recipePlayer.size()) this.recipePlayer.remove(index);
        }
        while (this.recipeEssentia.size() > this.maxRecipes) this.recipeEssentia.remove(this.recipeEssentia.size() - 1);
        while (this.recipePlayer.size() > this.maxRecipes) this.recipePlayer.remove(this.recipePlayer.size() - 1);
        if (this.currentCraft >= this.recipeHash.size()) {
            this.currentCraft = -1;
            this.currentRecipe = null;
            this.currentSuction = null;
        }
        this.markDirtyAndSync();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, 0, -1), this.pos.add(2, 2, 2));
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
