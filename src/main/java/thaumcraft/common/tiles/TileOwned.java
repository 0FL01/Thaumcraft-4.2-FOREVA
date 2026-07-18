package thaumcraft.common.tiles;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;

public class TileOwned extends TileThaumcraft {
    public String owner = "";
    public ArrayList<String> accessList = new ArrayList<>();
    public boolean safeToRemove = false;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList access = compound.getTagList("access", 10);
        this.accessList = new ArrayList<>();
        for (int i = 0; i < access.tagCount(); i++) {
            this.accessList.add(access.getCompoundTagAt(i).getString("name"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound out = super.writeToNBT(compound);
        NBTTagList access = new NBTTagList();
        if (this.accessList != null) {
            for (String name : this.accessList) {
                NBTTagCompound entry = new NBTTagCompound();
                entry.setString("name", name);
                access.appendTag(entry);
            }
        }
        out.setTag("access", access);
        return out;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.owner = compound.getString("owner");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setString("owner", this.owner == null ? "" : this.owner);
    }
}
