package thaumcraft.common.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileMagicWorkbenchCharger extends TileVisRelay {
    public short orientation = 0;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX(),
                this.pos.getY() - 1,
                this.pos.getZ(),
                this.pos.getX() + 1,
                this.pos.getY() + 1,
                this.pos.getZ() + 1);
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (this.world == null || this.world.isRemote) {
            return;
        }
        TileEntity below = this.world.getTileEntity(this.pos.down());
        if (!(below instanceof TileMagicWorkbench)) {
            return;
        }
        TileMagicWorkbench workbench = (TileMagicWorkbench) below;
        ItemStack wand = workbench.getStackInSlot(10);
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemWandCasting)
                || ((ItemWandCasting) wand.getItem()).isStaff(wand)) {
            return;
        }

        ItemWandCasting wandItem = (ItemWandCasting) wand.getItem();
        AspectList room = wandItem.getAspectsWithRoom(wand);
        if (room == null || room.size() <= 0) {
            return;
        }
        boolean changed = false;
        for (Aspect aspect : room.getAspects()) {
            int drain = Math.min(5, ItemWandCasting.getMaxVis(wand) - ItemWandCasting.getVis(wand, aspect));
            if (drain <= 0) {
                continue;
            }
            int consumed = this.consumeVis(aspect, drain);
            if (consumed > 0) {
                ItemWandCasting.addRealVis(wand, aspect, consumed, true);
                changed = true;
            }
        }
        if (changed) {
            workbench.onWandVisChanged();
        }
    }
}
