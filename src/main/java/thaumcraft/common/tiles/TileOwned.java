package thaumcraft.common.tiles;

import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.TileThaumcraft;

public class TileOwned extends TileThaumcraft {
    public String owner = "";
    public ArrayList<String> accessList = new ArrayList<>();
    public boolean safeToRemove = false;

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
