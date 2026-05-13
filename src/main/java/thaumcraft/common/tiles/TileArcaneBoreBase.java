package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.TCSounds;

public class TileArcaneBoreBase extends TileThaumcraft implements ITickable, IWandable {
    public EnumFacing orientation = EnumFacing.NORTH;

    @Override
    public void update() {
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
}
