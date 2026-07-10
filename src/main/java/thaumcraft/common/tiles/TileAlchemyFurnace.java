package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.EnumSkyBlock;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class TileAlchemyFurnace extends TileThaumcraft implements ITickable, ISidedInventory {
    private static final int[] SLOTS_BOTTOM = new int[]{1};
    private static final int[] SLOTS_TOP = new int[0];
    private static final int[] SLOTS_SIDES = new int[]{0};

    public AspectList aspects = new AspectList();
    public int vis = 0;
    public int smeltTime = 100;
    public int furnaceBurnTime = 0;
    public int currentItemBurnTime = 0;
    public int furnaceCookTime = 0;

    private final int maxVis = 50;
    private int bellows = -1;
    private boolean speedBoost = false;
    private ItemStack[] furnaceItemStacks = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
    private String customName;
    private int count = 0;

    @Override
    public void update() {
        if (this.world == null) return;

        boolean wasBurning = this.furnaceBurnTime > 0;
        boolean dirty = false;
        ++this.count;

        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }

        if (!this.world.isRemote) {
            if (this.bellows < 0) {
                this.getBellows();
            }

            this.pushAspectsToAlembics();

            if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                ItemStack fuel = this.furnaceItemStacks[1];
                this.currentItemBurnTime = this.furnaceBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
                if (this.furnaceBurnTime > 0) {
                    dirty = true;
                    this.speedBoost = !fuel.isEmpty()
                            && fuel.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 0));
                    if (!fuel.isEmpty()) {
                        fuel.shrink(1);
                        if (fuel.getCount() <= 0) {
                            this.furnaceItemStacks[1] = fuel.getItem().hasContainerItem(fuel)
                                    ? fuel.getItem().getContainerItem(fuel)
                                    : ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime >= this.smeltTime) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    dirty = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }

            if (wasBurning != this.furnaceBurnTime > 0) {
                dirty = true;
                this.notifyUpdate();
                this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
            }
        }

        if (dirty) {
            this.markDirty();
        }
    }

    private void pushAspectsToAlembics() {
        if (this.aspects.size() <= 0 || this.count % (this.speedBoost ? 20 : 40) != 0) return;

        AspectList exclude = new AspectList();
        for (int y = 1; y < 5; ++y) {
            TileEntity tile = this.world.getTileEntity(this.pos.up(y));
            if (!(tile instanceof TileAlembic)) break;

            TileAlembic alembic = (TileAlembic) tile;
            if (alembic.aspect != null && alembic.amount < alembic.maxAmount
                    && (alembic.aspectFilter == null || alembic.aspectFilter == alembic.aspect)
                    && this.aspects.getAmount(alembic.aspect) > 0) {
                Aspect aspect = alembic.aspect;
                if (this.takeFromContainer(aspect, 1) && alembic.addToContainer(aspect, 1) == 0) {
                    exclude.merge(aspect, 1);
                    this.notifyUpdate(alembic);
                }
            }
        }

        for (int y = 1; y < 5; ++y) {
            TileEntity tile = this.world.getTileEntity(this.pos.up(y));
            if (!(tile instanceof TileAlembic)) break;

            TileAlembic alembic = (TileAlembic) tile;
            if (alembic.aspect != null && alembic.amount != 0) continue;

            Aspect aspect;
            if (alembic.aspectFilter != null) {
                aspect = alembic.aspectFilter;
                if (!this.takeFromContainer(aspect, 1)) continue;
            } else {
                aspect = this.takeRandomAspect(exclude);
            }
            if (aspect == null) continue;

            if (alembic.addToContainer(aspect, 1) == 0) {
                this.notifyUpdate(alembic);
                break;
            }
        }
    }

    private boolean canSmelt() {
        ItemStack input = this.furnaceItemStacks[0];
        if (input.isEmpty()) return false;

        AspectList tags = ThaumcraftCraftingManager.getObjectTags(input);
        tags = ThaumcraftCraftingManager.getBonusTags(input, tags);
        if (tags == null || tags.size() == 0) return false;

        int amount = tags.visSize();
        if (amount > this.maxVis - this.vis) return false;

        this.smeltTime = Math.max(1, (int) ((float) (amount * 10) * (1.0F - 0.125F * (float) this.bellows)));
        return true;
    }

    public void getBellows() {
        this.bellows = TileBellows.getBellows(this.world, this.pos, EnumFacing.values());
    }

    public void smeltItem() {
        if (!this.canSmelt()) return;

        AspectList tags = ThaumcraftCraftingManager.getObjectTags(this.furnaceItemStacks[0]);
        tags = ThaumcraftCraftingManager.getBonusTags(this.furnaceItemStacks[0], tags);
        if (tags == null) return;

        for (Aspect aspect : tags.getAspects()) {
            this.aspects.add(aspect, tags.getAmount(aspect));
        }
        this.vis = this.aspects.visSize();
        this.furnaceItemStacks[0].shrink(1);
        if (this.furnaceItemStacks[0].getCount() <= 0) {
            this.furnaceItemStacks[0] = ItemStack.EMPTY;
        }
        this.notifyUpdate();
    }

    public static boolean isItemFuel(ItemStack stack) {
        return !stack.isEmpty() && TileEntityFurnace.getItemBurnTime(stack) > 0;
    }

    public Aspect takeRandomAspect(AspectList exclude) {
        if (this.aspects.size() <= 0) return null;

        AspectList temp = this.aspects.copy();
        if (exclude != null && exclude.size() > 0) {
            for (Aspect aspect : exclude.getAspects()) {
                temp.remove(aspect);
            }
        }
        if (temp.size() <= 0) return null;

        Aspect aspect = temp.getAspects()[this.world.rand.nextInt(temp.getAspects().length)];
        this.aspects.remove(aspect, 1);
        this.vis = this.aspects.visSize();
        this.markDirty();
        this.notifyUpdate();
        return aspect;
    }

    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (this.aspects != null && aspect != null && this.aspects.getAmount(aspect) >= amount) {
            this.aspects.remove(aspect, amount);
            this.vis = this.aspects.visSize();
            this.markDirty();
            this.notifyUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.furnaceBurnTime = nbt.getShort("BurnTime");
        this.currentItemBurnTime = nbt.getShort("CurrentBurnTime");
        this.furnaceCookTime = nbt.getShort("CookTime");
        this.speedBoost = nbt.getBoolean("speedBoost");
        this.aspects.readFromNBT(nbt);
        this.vis = nbt.hasKey("Vis")
                ? Math.max(0, nbt.getShort("Vis"))
                : this.aspects.visSize();
        if (nbt.hasKey("CustomName")) {
            this.customName = nbt.getString("CustomName");
        }

        this.furnaceItemStacks = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
        NBTTagList items = nbt.getTagList("Items", 10);
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound itemTag = items.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.furnaceItemStacks.length) {
                this.furnaceItemStacks[slot] = new ItemStack(itemTag);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setShort("BurnTime", (short) this.furnaceBurnTime);
        nbt.setShort("CurrentBurnTime", (short) this.currentItemBurnTime);
        nbt.setShort("CookTime", (short) this.furnaceCookTime);
        nbt.setShort("Vis", (short) this.vis);
        nbt.setBoolean("speedBoost", this.speedBoost);
        this.aspects.writeToNBT(nbt);
        if (this.hasCustomName()) {
            nbt.setString("CustomName", this.customName);
        }

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
            if (this.furnaceItemStacks[i].isEmpty()) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            this.furnaceItemStacks[i].writeToNBT(itemTag);
            items.appendTag(itemTag);
        }
        nbt.setTag("Items", items);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.relight();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.relight();
    }

    private void relight() {
        if (this.world != null && this.pos != null) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }

    public int getCookProgressScaled(int scale) {
        return this.furnaceCookTime * scale / Math.max(1, this.smeltTime);
    }

    public int getContentsScaled(int scale) {
        return this.vis * scale / this.maxVis;
    }

    public int getBurnTimeRemainingScaled(int scale) {
        if (this.currentItemBurnTime == 0) this.currentItemBurnTime = 200;
        return this.furnaceBurnTime * scale / this.currentItemBurnTime;
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.furnaceItemStacks.length ? this.furnaceItemStacks[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= this.furnaceItemStacks.length || this.furnaceItemStacks[index].isEmpty()) return ItemStack.EMPTY;

        ItemStack stack;
        if (this.furnaceItemStacks[index].getCount() <= count) {
            stack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = ItemStack.EMPTY;
        } else {
            stack = this.furnaceItemStacks[index].splitStack(count);
            if (this.furnaceItemStacks[index].getCount() <= 0) this.furnaceItemStacks[index] = ItemStack.EMPTY;
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index < 0 || index >= this.furnaceItemStacks.length || this.furnaceItemStacks[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.furnaceItemStacks[index];
        this.furnaceItemStacks[index] = ItemStack.EMPTY;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= this.furnaceItemStacks.length) return;
        this.furnaceItemStacks[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.alchemyfurnace";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setGuiDisplayName(String name) {
        this.customName = name;
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
        if (index == 0) {
            AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
            tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
            return tags != null && tags.size() > 0;
        }
        return index == 1 && isItemFuel(stack);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) return SLOTS_BOTTOM;
        if (side == EnumFacing.UP) return SLOTS_TOP;
        return SLOTS_SIDES;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.DOWN && this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.DOWN || index != 1 || stack.getItem() == Items.BUCKET;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return this.furnaceBurnTime;
            case 1: return this.currentItemBurnTime;
            case 2: return this.furnaceCookTime;
            case 3: return this.smeltTime;
            case 4: return this.vis;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: this.furnaceBurnTime = value; break;
            case 1: this.currentItemBurnTime = value; break;
            case 2: this.furnaceCookTime = value; break;
            case 3: this.smeltTime = value; break;
            case 4: this.vis = value; break;
            default: break;
        }
    }

    @Override
    public int getFieldCount() {
        return 5;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
            this.furnaceItemStacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.furnaceItemStacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    private void notifyUpdate() {
        this.notifyUpdate(this);
    }

    private void notifyUpdate(TileEntity tile) {
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(tile.getPos(), this.world.getBlockState(tile.getPos()),
                    this.world.getBlockState(tile.getPos()), 3);
        }
    }
}
