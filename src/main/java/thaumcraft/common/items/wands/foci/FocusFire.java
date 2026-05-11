package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusFire extends ItemFocusBasic {

    public FocusFire() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF4500;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.FIRE, 500).add(Aspect.ENERGY, 250);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: fire projectile
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "FIRE";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
