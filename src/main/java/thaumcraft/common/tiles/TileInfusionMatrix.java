package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.wands.IWandable;

public class TileInfusionMatrix extends TileThaumcraft implements ITickable, IWandable, IAspectContainer {
    public boolean active = false;
    public boolean crafting = false;
    public boolean checkSurroundings = true;
    public int symmetry = 0;
    public int instability = 0;
    public int count = 0;
    public int craftCount = 0;
    public float startUp = 0.0F;

    private AspectList recipeEssentia = new AspectList();

    @Override
    public void update() {
        if (this.world == null) return;
        ++this.count;

        if (!this.world.isRemote) {
            if (this.count % (this.crafting ? 20 : 100) == 0 && !this.validLocation()) {
                this.active = false;
                this.crafting = false;
                this.markDirtyAndSync();
            }
        } else if (this.startUp > 0.0F) {
            this.startUp -= this.startUp / 10.0F;
            if (this.startUp < 0.001F) this.startUp = 0.0F;
        }
    }

    public boolean validLocation() {
        TileEntity center = this.world.getTileEntity(this.pos.down(2));
        if (!(center instanceof TilePedestal)) return false;
        if (!(this.world.getTileEntity(this.pos.add(1, -2, 1)) instanceof TileInfusionPillar)) return false;
        if (!(this.world.getTileEntity(this.pos.add(1, -2, -1)) instanceof TileInfusionPillar)) return false;
        if (!(this.world.getTileEntity(this.pos.add(-1, -2, -1)) instanceof TileInfusionPillar)) return false;
        return this.world.getTileEntity(this.pos.add(-1, -2, 1)) instanceof TileInfusionPillar;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.active = nbt.getBoolean("active");
        this.crafting = nbt.getBoolean("crafting");
        this.instability = nbt.getShort("instability");
        this.recipeEssentia.readFromNBT(nbt);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("active", this.active);
        nbt.setBoolean("crafting", this.crafting);
        nbt.setShort("instability", (short) this.instability);
        this.recipeEssentia.writeToNBT(nbt);
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (world.isRemote) return 0;
        if (!this.active && this.validLocation()) {
            this.active = true;
            this.startUp = 1.0F;
            this.markDirtyAndSync();
            return 0;
        }
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    @Override
    public AspectList getAspects() {
        return this.recipeEssentia;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.recipeEssentia = aspects == null ? new AspectList() : aspects.copy();
        this.markDirtyAndSync();
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return amount;
        this.recipeEssentia.add(tag, amount);
        this.markDirtyAndSync();
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0 || this.recipeEssentia.getAmount(tag) < amount) return false;
        this.recipeEssentia.remove(tag, amount);
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (aspects == null || !this.doesContainerContain(aspects)) return false;
        for (Aspect aspect : aspects.getAspects()) {
            this.recipeEssentia.remove(aspect, aspects.getAmount(aspect));
        }
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return tag != null && this.recipeEssentia.getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return false;
        for (Aspect aspect : aspects.getAspects()) {
            if (this.recipeEssentia.getAmount(aspect) < aspects.getAmount(aspect)) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == null ? this.recipeEssentia.visSize() : this.recipeEssentia.getAmount(tag);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(2, 2, 2));
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
