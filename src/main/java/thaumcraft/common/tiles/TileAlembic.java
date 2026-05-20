package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;

public class TileAlembic extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, IWandable {
    public Aspect aspect = null;
    public Aspect aspectFilter = null;
    public int amount = 0;
    public int maxAmount = 32;
    public int facing = 2;
    public boolean aboveAlembic = false;
    public boolean aboveFurnace = false;

    // --- NBT ---

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        if (nbt.hasKey("aspect")) {
            this.aspect = Aspect.getAspect(nbt.getString("aspect"));
        } else {
            this.aspect = null;
        }
        this.aspectFilter = Aspect.getAspect(nbt.getString("AspectFilter"));
        this.amount = nbt.getInteger("amount");
        if (nbt.hasKey("maxAmount")) this.maxAmount = nbt.getInteger("maxAmount");
        this.facing = nbt.getByte("facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        if (this.aspect != null) {
            nbt.setString("aspect", this.aspect.getTag());
        }
        if (this.aspectFilter != null) {
            nbt.setString("AspectFilter", this.aspectFilter.getTag());
        }
        nbt.setInteger("amount", this.amount);
        nbt.setByte("facing", (byte) this.facing);
    }

    // --- IAspectContainer ---

    @Override
    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (this.aspect != null && this.amount > 0) {
            al.add(this.aspect, this.amount);
        }
        return al;
    }

    @Override
    public void setAspects(AspectList list) {
        if (list == null || list.getAspects().length == 0) {
            this.aspect = null;
            this.amount = 0;
        } else {
            this.aspect = list.getAspects()[0];
            this.amount = Math.min(list.getAmount(this.aspect), this.maxAmount);
        }
        this.markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int requested) {
        if (!this.doesContainerAccept(tag)) return requested;
        if (this.aspect == null) {
            this.aspect = tag;
        }
        int add = Math.min(requested, this.maxAmount - this.amount);
        this.amount += add;
        this.markDirty();
        return requested - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int requested) {
        if (tag == null || !tag.equals(this.aspect) || this.amount < requested) return false;
        this.amount -= requested;
        if (this.amount <= 0) {
            this.aspect = null;
            this.amount = 0;
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList list) {
        if (list == null || list.getAspects().length != 1) return false;
        Aspect tag = list.getAspects()[0];
        int req = list.getAmount(tag);
        return this.takeFromContainer(tag, req);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return tag != null && tag.equals(this.aspect) && this.amount >= amt;
    }

    @Override
    public boolean doesContainerContain(AspectList list) {
        if (list == null || list.getAspects().length != 1) return false;
        Aspect tag = list.getAspects()[0];
        int req = list.getAmount(tag);
        return this.doesContainerContainAmount(tag, req);
    }

    @Override
    public int containerContains(Aspect tag) {
        if (tag != null && tag.equals(this.aspect)) return this.amount;
        return 0;
    }

    // --- IEssentiaTransport ---

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != EnumFacing.byIndex(this.facing) && face != EnumFacing.DOWN;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face != EnumFacing.byIndex(this.facing) && face != EnumFacing.DOWN;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspect;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.amount;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    public void getAppearance() {
        this.aboveAlembic = false;
        this.aboveFurnace = false;
        if (this.world == null) return;
        if (this.world.getBlockState(this.pos.down()).getBlock() == thaumcraft.common.config.ConfigBlocks.blockStoneDevice
                && thaumcraft.common.config.ConfigBlocks.blockStoneDevice.getMetaFromState(this.world.getBlockState(this.pos.down())) == 0) {
            this.aboveFurnace = true;
        }
        if (this.world.getBlockState(this.pos.down()).getBlock() == thaumcraft.common.config.ConfigBlocks.blockMetalDevice
                && thaumcraft.common.config.ConfigBlocks.blockMetalDevice.getMetaFromState(this.world.getBlockState(this.pos.down())) == 1) {
            this.aboveAlembic = true;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1,
                this.pos.getX() + 2, this.pos.getY() + 1, this.pos.getZ() + 2);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.getAppearance();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.getAppearance();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.getAppearance();
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (side <= 1) return 0;
        this.facing = side;
        if (player != null) player.swingArm(EnumHand.MAIN_HAND);
        this.markDirty();
        if (world != null && !world.isRemote) world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) { return wandstack; }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {}
}
