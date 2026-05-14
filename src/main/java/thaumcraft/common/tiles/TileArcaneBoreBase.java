package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.TCSounds;

public class TileArcaneBoreBase extends TileThaumcraft implements ITickable, IWandable, IEssentiaTransport {
    public EnumFacing orientation = EnumFacing.NORTH;

    @Override
    public void update() {
    }

    public boolean drawEssentia() {
        if (this.world == null) return false;
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world,
                    this.pos.getX(), this.pos.getY(), this.pos.getZ(), facing);
            if (!(tile instanceof IEssentiaTransport)) continue;
            IEssentiaTransport transport = (IEssentiaTransport) tile;
            EnumFacing remote = facing.getOpposite();
            if (!transport.canOutputTo(remote)) return false;
            if (transport.getSuctionAmount(remote) < this.getSuctionAmount(facing)
                    && transport.takeEssentia(Aspect.ENTROPY, 1, remote) == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = EnumFacing.byIndex(nbt.getInteger("orientation"));
        if (this.orientation == null) this.orientation = EnumFacing.NORTH;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.orientation.getIndex());
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        this.orientation = EnumFacing.byIndex(side);
        if (this.orientation == null) this.orientation = EnumFacing.NORTH;
        this.markDirty();
        if (world != null) {
            world.playSound(null, this.pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.3F, 1.9F + world.rand.nextFloat() * 0.2F);
            if (!world.isRemote) {
                world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
            }
        }
        if (player != null) {
            player.swingArm(EnumHand.MAIN_HAND);
        }
        return 0;
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
    public boolean isConnectable(EnumFacing face) { return true; }

    @Override
    public boolean canInputFrom(EnumFacing face) { return true; }

    @Override
    public boolean canOutputTo(EnumFacing face) { return false; }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing face) { return Aspect.ENTROPY; }

    @Override
    public int getSuctionAmount(EnumFacing face) { return face != this.orientation ? 128 : 0; }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }

    @Override
    public Aspect getEssentiaType(EnumFacing face) { return null; }

    @Override
    public int getEssentiaAmount(EnumFacing face) { return 0; }

    @Override
    public int getMinimumSuction() { return 0; }

    @Override
    public boolean renderExtendedTube() { return true; }
}
