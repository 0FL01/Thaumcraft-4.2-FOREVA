package thaumcraft.common.tiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;

public class TileArcaneFurnace extends TileThaumcraft implements ITickable {
    private final ItemStack[] furnaceItemStacks = new ItemStack[32];
    public int furnaceCookTime = 0;
    public int furnaceMaxCookTime = 0;
    public int speedyTime = 0;
    public int facingX = -5;
    public int facingZ = -5;

    public TileArcaneFurnace() {
        for (int i = 0; i < this.furnaceItemStacks.length; i++) {
            this.furnaceItemStacks[i] = ItemStack.EMPTY;
        }
    }

    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    public ItemStack getStackInSlot(int i) {
        return this.furnaceItemStacks[i];
    }

    public ItemStack decrStackSize(int i, int count) {
        ItemStack stack = this.furnaceItemStacks[i];
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getCount() <= count) {
            this.furnaceItemStacks[i] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        ItemStack split = stack.splitStack(count);
        if (stack.getCount() <= 0) {
            this.furnaceItemStacks[i] = ItemStack.EMPTY;
        }
        this.markDirty();
        return split;
    }

    public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.furnaceItemStacks[i] = itemstack;
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
            itemstack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    private int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < this.furnaceItemStacks.length; i++) {
            this.furnaceItemStacks[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < 0 || slot >= this.furnaceItemStacks.length) {
                continue;
            }
            this.furnaceItemStacks[slot] = new ItemStack(entry);
        }
        this.furnaceCookTime = nbt.getShort("CookTime");
        this.speedyTime = nbt.getShort("SpeedyTime");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setShort("CookTime", (short) this.furnaceCookTime);
        nbt.setShort("SpeedyTime", (short) this.speedyTime);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
            ItemStack stack = this.furnaceItemStacks[i];
            if (stack.isEmpty()) {
                continue;
            }
            NBTTagCompound entry = new NBTTagCompound();
            entry.setByte("Slot", (byte) i);
            stack.writeToNBT(entry);
            list.appendTag(entry);
        }
        nbt.setTag("Items", list);
    }

    @Override
    public void update() {
        if (this.world == null || this.pos == null) {
            return;
        }
        if (this.facingX == -5) {
            this.getFacing();
        }
        if (!this.world.isRemote) {
            boolean cookedFlag = false;
            if (this.furnaceCookTime > 0) {
                --this.furnaceCookTime;
                cookedFlag = true;
            }
            if (cookedFlag && this.speedyTime > 0) {
                --this.speedyTime;
            }
            if (this.speedyTime <= 0) {
                this.speedyTime = VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.FIRE, 5);
            }
            if (this.furnaceMaxCookTime == 0) {
                this.furnaceMaxCookTime = this.calcCookTime();
            }
            if (this.furnaceCookTime > this.furnaceMaxCookTime) {
                this.furnaceCookTime = this.furnaceMaxCookTime;
            }
            if (this.furnaceCookTime == 0 && cookedFlag) {
                for (int i = 0; i < this.getSizeInventory(); ++i) {
                    ItemStack source = this.furnaceItemStacks[i];
                    if (source.isEmpty()) {
                        continue;
                    }
                    ItemStack smelt = FurnaceRecipes.instance().getSmeltingResult(source);
                    if (smelt.isEmpty()) {
                        continue;
                    }
                    this.ejectItem(smelt.copy(), source);
                    this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 3, 0);
                    source.shrink(1);
                    if (source.getCount() <= 0) {
                        this.furnaceItemStacks[i] = ItemStack.EMPTY;
                    }
                    this.markDirty();
                    break;
                }
            }
            if (this.furnaceCookTime == 0 && !cookedFlag) {
                for (int i = 0; i < this.getSizeInventory(); ++i) {
                    if (this.canSmelt(i)) {
                        this.furnaceCookTime = this.furnaceMaxCookTime = this.calcCookTime();
                        this.markDirty();
                        break;
                    }
                }
            }
        }
    }

    private int getBellows() {
        int bellows = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir == EnumFacing.UP) {
                continue;
            }
            BlockPos bellowsPos = this.pos.add(dir.getXOffset() * 2, dir.getYOffset() * 2, dir.getZOffset() * 2);
            if (!(this.world.getTileEntity(bellowsPos) instanceof TileBellows)) {
                continue;
            }
            TileBellows tile = (TileBellows) this.world.getTileEntity(bellowsPos);
            if (tile.orientation != dir.getOpposite().getIndex() || this.world.isBlockPowered(bellowsPos)) {
                continue;
            }
            bellows++;
        }
        return Math.min(3, bellows);
    }

    private int calcCookTime() {
        return (this.speedyTime > 0 ? 80 : 140) - 20 * this.getBellows();
    }

    public boolean addItemsToInventory(ItemStack items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack stack = this.furnaceItemStacks[i];
            if (!stack.isEmpty() && ItemStack.areItemsEqual(stack, items) && stack.getCount() + items.getCount() <= stack.getMaxStackSize()) {
                stack.grow(items.getCount());
                if (!this.canSmelt(i)) {
                    this.destroyItem(i);
                }
                this.markDirty();
                return true;
            }
            if (!stack.isEmpty()) {
                continue;
            }
            this.setInventorySlotContents(i, items.copy());
            if (!this.canSmelt(i)) {
                this.destroyItem(i);
            }
            this.markDirty();
            return true;
        }
        return false;
    }

    private void destroyItem(int slot) {
        this.furnaceItemStacks[slot] = ItemStack.EMPTY;
        if (this.world != null) {
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.3F,
                    2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
            Thaumcraft.proxy.drawGenericParticles(this.world,
                    this.pos.getX() + this.world.rand.nextFloat(),
                    this.pos.getY() + 1.0D,
                    this.pos.getZ() + this.world.rand.nextFloat(),
                    0.0D, 0.08D, 0.0D,
                    1.0F, 1.0F, 1.0F, 0.95F,
                    false, 49, 1, 1, 12, 0, 0.5F, 1);
        }
    }

    private void getFacing() {
        this.facingX = 0;
        this.facingZ = 0;
        if (this.world.getBlockState(this.pos.west()).getBlock() == ConfigBlocks.blockArcaneFurnace
                && ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.west())) == 10) {
            this.facingX = -1;
        } else if (this.world.getBlockState(this.pos.east()).getBlock() == ConfigBlocks.blockArcaneFurnace
                && ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.east())) == 10) {
            this.facingX = 1;
        } else {
            this.facingZ = this.world.getBlockState(this.pos.north()).getBlock() == ConfigBlocks.blockArcaneFurnace
                    && ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.north())) == 10 ? -1 : 1;
        }
    }

    public void ejectItem(ItemStack items, ItemStack furnaceItemStack) {
        if (items == null || items.isEmpty() || this.world == null) {
            return;
        }
        ItemStack bit = items.copy();
        int bellows = this.getBellows();
        float lx = 0.5F + this.facingX * 1.2F;
        float lz = 0.5F + this.facingZ * 1.2F;
        float mx = this.facingX == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.03F : this.facingX * 0.13F;
        float mz = this.facingZ == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.03F : this.facingZ * 0.13F;

        EntityItem entityItem = new EntityItem(this.world, this.pos.getX() + lx, this.pos.getY() + 0.4F, this.pos.getZ() + lz, items);
        entityItem.motionX = mx;
        entityItem.motionY = 0.0D;
        entityItem.motionZ = mz;
        this.world.spawnEntity(entityItem);

        ItemStack bonus = ThaumcraftApi.getSmeltingBonus(furnaceItemStack);
        if (bonus != null && !bonus.isEmpty()) {
            bonus = bonus.copy();
            if (bellows == 0) {
                if (this.world.rand.nextInt(4) == 0) {
                    bonus.grow(1);
                }
            } else {
                for (int a = 0; a < bellows; ++a) {
                    if (this.world.rand.nextFloat() < 0.44F) {
                        bonus.grow(1);
                    }
                }
            }
            if (bonus.getCount() > 0) {
                mx = this.facingX == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.03F : this.facingX * 0.13F;
                mz = this.facingZ == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.03F : this.facingZ * 0.13F;
                EntityItem bonusItem = new EntityItem(this.world, this.pos.getX() + lx, this.pos.getY() + 0.4F, this.pos.getZ() + lz, bonus);
                bonusItem.motionX = mx;
                bonusItem.motionY = 0.0D;
                bonusItem.motionZ = mz;
                this.world.spawnEntity(bonusItem);
            }
        }

        int xpAmount = items.getCount();
        float xpScale = FurnaceRecipes.instance().getSmeltingExperience(bit);
        if (xpScale == 0.0F) {
            xpAmount = 0;
        } else if (xpScale < 1.0F) {
            int floor = MathHelper.floor(xpAmount * xpScale);
            if (floor < MathHelper.ceil(xpAmount * xpScale) && Math.random() < xpAmount * xpScale - floor) {
                floor++;
            }
            xpAmount = floor;
        }
        while (xpAmount > 0) {
            int split = EntityXPOrb.getXPSplit(xpAmount);
            xpAmount -= split;
            EntityXPOrb xp = new EntityXPOrb(this.world, this.pos.getX() + lx, this.pos.getY() + 0.4F, this.pos.getZ() + lz, split);
            mx = this.facingX == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.025F : this.facingX * 0.13F;
            mz = this.facingZ == 0 ? (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.025F : this.facingZ * 0.13F;
            xp.motionX = mx;
            xp.motionY = 0.0D;
            xp.motionZ = mz;
            this.world.spawnEntity(xp);
        }
    }

    private boolean canSmelt(int slot) {
        ItemStack stack = this.furnaceItemStacks[slot];
        return !stack.isEmpty() && !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 3) {
            if (this.world != null && this.world.isRemote && this.pos != null) {
                for (int i = 0; i < 5; ++i) {
                    Thaumcraft.proxy.drawGenericParticles(this.world,
                            this.pos.getX() + 0.5D + (this.world.rand.nextFloat() - 0.5F) * 0.4F,
                            this.pos.getY() + 0.5D + this.world.rand.nextFloat() * 0.2F,
                            this.pos.getZ() + 0.5D + (this.world.rand.nextFloat() - 0.5F) * 0.4F,
                            this.facingX * 0.02D, 0.02D, this.facingZ * 0.02D,
                            1.0F, 1.0F, 1.0F, 0.95F,
                            false, 49, 1, 1, 12, 0, 0.5F, 1);
                    this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                            SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                            0.1F + this.world.rand.nextFloat() * 0.1F,
                            0.9F + this.world.rand.nextFloat() * 0.15F, false);
                }
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }
}
