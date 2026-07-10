package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.blocks.BlockAlchemyFurnace;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class TileAlchemyFurnaceAdvanced extends TileThaumcraft implements ITickable {
    public AspectList aspects = new AspectList();
    public int vis;
    public int maxVis = 500;
    int bellows = -1;
    public int heat;
    public int power1;
    public int power2;
    public int maxPower = 500;
    public boolean destroy;
    int count;
    int processed;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1,
                this.pos.getX() + 2, this.pos.getY() + 2, this.pos.getZ() + 2);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.aspects.readFromNBT(nbt);
        this.vis = nbt.hasKey("vis") ? nbt.getShort("vis") : this.aspects.visSize();
        this.heat = nbt.getShort("heat");
        this.power1 = nbt.getShort("power1");
        this.power2 = nbt.getShort("power2");
        this.processed = nbt.getShort("processed");
        this.destroy = nbt.getBoolean("destroy");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        this.aspects.writeToNBT(nbt);
        nbt.setShort("vis", (short) this.vis);
        nbt.setShort("heat", (short) this.heat);
        nbt.setShort("power1", (short) this.power1);
        nbt.setShort("power2", (short) this.power2);
        nbt.setShort("processed", (short) this.processed);
        nbt.setBoolean("destroy", this.destroy);
    }

    @Override
    public void update() {
        if (this.world == null || this.pos == null) {
            return;
        }
        ++this.count;
        if (this.world.isRemote) {
            return;
        }
        if (this.destroy) {
            this.destroy = false;
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof BlockAlchemyFurnace) {
                ((BlockAlchemyFurnace) state.getBlock()).restoreStructure(this.world, this.pos, true);
            }
            return;
        }
        if (this.processed > 0) {
            --this.processed;
        }
        if (this.count % 5 != 0) {
            return;
        }

        int previousHeat = this.heat;
        int previousLight = BlockAlchemyFurnace.getHeatLight(this.heat, this.maxPower);
        int previousPower1 = this.power1;
        int previousPower2 = this.power2;
        if (this.heat > 0) {
            --this.heat;
        }
        if (this.heat <= this.maxPower) {
            this.heat += VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                    Aspect.FIRE, 50);
        }
        if (this.power1 <= this.maxPower) {
            this.power1 += VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                    Aspect.ENTROPY, 50);
        }
        if (this.power2 <= this.maxPower) {
            this.power2 += VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                    Aspect.WATER, 50);
        }
        if (previousHeat != this.heat || previousPower1 != this.power1 || previousPower2 != this.power2) {
            this.sync(previousLight != BlockAlchemyFurnace.getHeatLight(this.heat, this.maxPower));
        }
    }

    public boolean process(ItemStack stack) {
        if (this.processed != 0 || !this.canSmelt(stack)) {
            return false;
        }
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
        int amount = tags.visSize();
        if (amount * 2 > this.heat || amount > this.power1 || amount > this.power2) {
            return false;
        }
        this.heat -= amount * 2;
        this.power1 -= amount;
        this.power2 -= amount;
        this.processed = (int) (5.0F
                + Math.max(0.0F, (1.0F - (float) this.heat / (float) this.maxPower) * 100.0F));
        this.aspects.add(tags);
        this.vis = this.aspects.visSize();
        this.syncContents(true);
        return true;
    }

    private boolean canSmelt(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
        return tags != null && tags.size() > 0
                && tags.visSize() + this.aspects.visSize() <= this.maxVis;
    }

    void sync(boolean relight) {
        this.markDirty();
        if (this.world != null && !this.world.isRemote && this.pos != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
            if (relight) {
                this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
            }
        }
    }

    public void syncContents(boolean relight) {
        this.sync(relight);
        if (this.world == null || this.world.isRemote || this.pos == null) {
            return;
        }
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            BlockPos nozzlePos = this.pos.offset(direction);
            IBlockState nozzleState = this.world.getBlockState(nozzlePos);
            if (nozzleState.getBlock() instanceof BlockAlchemyFurnace
                    && ((BlockAlchemyFurnace) nozzleState.getBlock()).getMetaFromState(nozzleState)
                    == BlockAlchemyFurnace.LOWER_NOZZLE) {
                this.world.updateComparatorOutputLevel(nozzlePos, nozzleState.getBlock());
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.pos != null) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        if (this.world != null && this.pos != null) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }
}
