package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusPrimal extends ItemFocusBasic {

    public FocusPrimal() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList()
                .add(Aspect.AIR, 500)
                .add(Aspect.FIRE, 500)
                .add(Aspect.WATER, 500)
                .add(Aspect.EARTH, 500)
                .add(Aspect.ORDER, 500)
                .add(Aspect.ENTROPY, 500);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: primal ray
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "PRIMAL";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
