package thaumcraft.common.tiles;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.blocks.BlockManaPod;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.research.ResearchManager;

public class TileManaPod extends TileThaumcraft implements IAspectContainer {

    public Aspect aspect = null;

    @Override
    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspect = Aspect.getAspect(nbttagcompound.getString("aspect"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        if (this.aspect != null) {
            nbttagcompound.setString("aspect", this.aspect.getTag());
        }
    }

    public void checkGrowth() {
        if (this.world == null || this.world.isRemote) return;
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() != ConfigBlocks.blockManaPod) return;

        int age = state.getValue(BlockManaPod.TYPE);
        if (age < 7) {
            age++;
            state = state.withProperty(BlockManaPod.TYPE, age);
            this.world.setBlockState(this.pos, state, 3);
        }

        if (age > 2) {
            if (age == 3) {
                chooseCrossbredAspect();
            }
            if (this.aspect == null) {
                ArrayList<Aspect> primals = Aspect.getPrimalAspects();
                this.aspect = this.world.rand.nextInt(8) == 0 || primals.isEmpty()
                        ? Aspect.PLANT
                        : primals.get(this.world.rand.nextInt(primals.size()));
                markAspectDirty();
            }
        }
    }

    private void chooseCrossbredAspect() {
        AspectList neighbors = new AspectList();
        if (this.aspect != null) {
            neighbors.add(this.aspect, 1);
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
            if (tile instanceof TileManaPod && ((TileManaPod) tile).aspect != null) {
                neighbors.add(((TileManaPod) tile).aspect, 1);
            }
        }
        if (neighbors.size() > 1) {
            Aspect[] aspects = neighbors.getAspects();
            ArrayList<Aspect> candidates = new ArrayList<>();
            for (int i = 0; i < aspects.length; i++) {
                candidates.add(aspects[i]);
                for (int j = 0; j < aspects.length; j++) {
                    if (i == j) continue;
                    Aspect combination = ResearchManager.getCombinationResult(aspects[i], aspects[j]);
                    if (combination != null) {
                        candidates.add(combination);
                        candidates.add(combination);
                    }
                }
            }
            if (!candidates.isEmpty()) {
                this.aspect = candidates.get(this.world.rand.nextInt(candidates.size()));
                markAspectDirty();
            }
        }
        if (neighbors.size() >= 1 && this.aspect == null) {
            this.aspect = neighbors.getAspectsSortedAmount()[0];
            markAspectDirty();
        }
    }

    private void markAspectDirty() {
        this.markDirty();
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 3);
    }

    @Override
    public AspectList getAspects() {
        if (this.aspect == null || this.world == null) return null;
        IBlockState state = this.world.getBlockState(this.pos);
        return state.getBlock() == ConfigBlocks.blockManaPod && state.getValue(BlockManaPod.TYPE) == 7
                ? new AspectList().add(this.aspect, 1)
                : null;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
}
