package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.ArrayList;
import java.util.List;

public class TileWandPedestal
extends TilePedestal
implements ITickable {

    public int counter = 0;
    public boolean somethingChanged = false;
    public boolean draining = false;
    public int drainX = 0;
    public int drainY = 0;
    public int drainZ = 0;
    public int drainColor = 0;
    private List<BlockPos> nodes = null;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(2.0D, 2.0D, 2.0D);
    }

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }
        if (this.nodes == null) {
            this.findNodes();
        }

        this.counter++;
        boolean recalc = false;
        ItemStack stack = this.getStackInSlot(0);

        if (this.counter % 20 == 0 && this.somethingChanged && this.nodes != null && !this.nodes.isEmpty() && !stack.isEmpty()) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.somethingChanged = false;
        }

        if (this.counter % 5 == 0 && this.nodes != null && !this.nodes.isEmpty() && !stack.isEmpty()) {
            boolean hasThingy = false;
            IBlockState above = this.world.getBlockState(this.pos.up());
            if (above.getBlock() == ConfigBlocks.blockStoneDevice && ConfigBlocks.blockStoneDevice.getMetaFromState(above) == 8) {
                hasThingy = true;
            }

            boolean drained = false;
            if (stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                int min = ("iron".equals(wand.getCap(stack).getTag()) || "wood".equals(wand.getRod(stack).getTag())) ? 0 : 1;
                AspectList room = wand.getAspectsWithRoom(stack);
                drained = this.tryDrainNodesForWand(stack, room, min, hasThingy);
            } else if (stack.getItem() instanceof ItemAmuletVis) {
                ItemAmuletVis amulet = (ItemAmuletVis) stack.getItem();
                AspectList room = amulet.getAspectsWithRoom(stack);
                drained = this.tryDrainNodesForAmulet(stack, amulet, room, 1, hasThingy);
            }

            this.draining = drained;
            if (!drained) {
                recalc = true;
            }
        }

        if (this.counter % 100 == 0 && (recalc || this.nodes == null || this.nodes.isEmpty())) {
            this.findNodes();
        }
    }

    private boolean tryDrainNodesForWand(ItemStack stack, AspectList room, int min, boolean hasThingy) {
        if (room == null || room.size() <= 0) {
            return false;
        }
        for (BlockPos nodePos : this.nodes) {
            TileEntity te = this.world.getTileEntity(nodePos);
            if (!(te instanceof INode) || te instanceof TileJarNode) continue;
            INode node = (INode) te;
            AspectList nodeAspects = node.getAspects();
            if (nodeAspects == null || nodeAspects.size() <= 0) continue;

            for (Aspect aspect : room.getAspects()) {
                if (aspect == null || nodeAspects.getAmount(aspect) <= min) continue;
                ItemWandCasting.addVis(stack, aspect, 1, true);
                node.takeFromContainer(aspect, 1);
                this.somethingChanged = true;
                if (this.world.isRemote) this.setDrainVisual(nodePos, aspect.getColor());
                return true;
            }

            if (!hasThingy) continue;
            for (Aspect compound : nodeAspects.getAspects()) {
                if (compound == null || compound.isPrimal() || nodeAspects.getAmount(compound) <= min) continue;
                AspectList primals = ResearchManager.reduceToPrimals(new AspectList().add(compound, 1));
                for (Aspect primal : room.getAspects()) {
                    if (primal == null || primals.getAmount(primal) <= 0) continue;
                    ItemWandCasting.addVis(stack, primal, 1, true);
                    node.takeFromContainer(compound, 1);
                    this.somethingChanged = true;
                    if (this.world.isRemote) this.setDrainVisual(nodePos, compound.getColor());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryDrainNodesForAmulet(ItemStack stack, ItemAmuletVis amulet, AspectList room, int min, boolean hasThingy) {
        if (room == null || room.size() <= 0) {
            return false;
        }
        for (BlockPos nodePos : this.nodes) {
            TileEntity te = this.world.getTileEntity(nodePos);
            if (!(te instanceof INode) || te instanceof TileJarNode) continue;
            INode node = (INode) te;
            AspectList nodeAspects = node.getAspects();
            if (nodeAspects == null || nodeAspects.size() <= 0) continue;

            for (Aspect aspect : room.getAspects()) {
                if (aspect == null || nodeAspects.getAmount(aspect) <= min) continue;
                amulet.addVis(stack, aspect, 1, true);
                node.takeFromContainer(aspect, 1);
                this.somethingChanged = true;
                if (this.world.isRemote) this.setDrainVisual(nodePos, aspect.getColor());
                return true;
            }

            if (!hasThingy) continue;
            for (Aspect compound : nodeAspects.getAspects()) {
                if (compound == null || compound.isPrimal() || nodeAspects.getAmount(compound) <= min) continue;
                AspectList primals = ResearchManager.reduceToPrimals(new AspectList().add(compound, 1));
                for (Aspect primal : room.getAspects()) {
                    if (primal == null || primals.getAmount(primal) <= 0) continue;
                    amulet.addVis(stack, primal, 1, true);
                    node.takeFromContainer(compound, 1);
                    this.somethingChanged = true;
                    if (this.world.isRemote) this.setDrainVisual(nodePos, compound.getColor());
                    return true;
                }
            }
        }
        return false;
    }

    private void setDrainVisual(BlockPos nodePos, int color) {
        this.drainX = nodePos.getX();
        this.drainY = nodePos.getY();
        this.drainZ = nodePos.getZ();
        this.drainColor = color;
    }

    private void findNodes() {
        this.nodes = new ArrayList<>();
        for (int xx = -8; xx <= 8; xx++) {
            for (int yy = -8; yy <= 8; yy++) {
                for (int zz = -8; zz <= 8; zz++) {
                    BlockPos checkPos = this.pos.add(xx, yy, zz);
                    TileEntity te = this.world.getTileEntity(checkPos);
                    if (te instanceof INode) {
                        this.nodes.add(checkPos);
                    }
                }
            }
        }
    }
}
