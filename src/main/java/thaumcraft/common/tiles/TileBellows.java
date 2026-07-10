package thaumcraft.common.tiles;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;

public class TileBellows extends TileThaumcraft implements ITickable {
    public float inflation = 1.0F;
    public byte orientation = 0;
    public boolean onVanillaFurnace = false;
    public int delay = 0;

    private boolean direction = false;
    private boolean firstrun = true;

    @Override
    public void update() {
        if (this.world == null) return;

        if (this.world.isRemote) {
            if (this.gettingPower()) return;

            if (this.firstrun) {
                this.inflation = 0.35F + this.world.rand.nextFloat() * 0.55F;
            }
            this.firstrun = false;

            if (this.inflation > 0.35F && !this.direction) this.inflation -= 0.075F;
            if (this.inflation <= 0.35F && !this.direction) this.direction = true;
            if (this.inflation < 1.0F && this.direction) this.inflation += 0.025F;
            if (this.inflation >= 1.0F && this.direction) {
                this.direction = false;
                this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                        SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.01F,
                        0.5F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F, false);
            }
            return;
        }

        if (this.onVanillaFurnace && !this.gettingPower() && ++this.delay >= 2) {
            this.delay = 0;
            EnumFacing dir = EnumFacing.byIndex(this.orientation);
            TileEntity tile = this.world.getTileEntity(this.pos.offset(dir));
            if (tile instanceof TileEntityFurnace) {
                TileEntityFurnace furnace = (TileEntityFurnace) tile;
                int cookTime = furnace.getField(2);
                int totalCookTime = furnace.getField(3);
                if (cookTime > 0 && cookTime < Math.max(1, totalCookTime - 1)) {
                    furnace.setField(2, cookTime + 1);
                    furnace.markDirty();
                }
            }
        }
    }

    public boolean gettingPower() {
        return this.world != null && this.world.isBlockPowered(this.pos);
    }

    public static int getBellows(World world, BlockPos pos, EnumFacing[] directions) {
        int bellows = 0;
        for (EnumFacing dir : directions) {
            TileEntity tile = world.getTileEntity(pos.offset(dir));
            if (!(tile instanceof TileBellows)) continue;

            TileBellows bellowsTile = (TileBellows) tile;
            int opposite = dir.getOpposite().getIndex();
            if (bellowsTile.orientation == opposite && !bellowsTile.gettingPower()) {
                ++bellows;
            }
        }
        return bellows;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = nbt.getByte("orientation");
        this.onVanillaFurnace = nbt.getBoolean("onVanillaFurnace");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", this.orientation);
        nbt.setBoolean("onVanillaFurnace", this.onVanillaFurnace);
    }
}
