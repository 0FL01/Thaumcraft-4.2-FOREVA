package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusHellbat extends ItemFocusBasic {

    public FocusHellbat() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF0000;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.FIRE, 1000).add(Aspect.FLIGHT, 500).add(Aspect.BEAST, 250);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: summon hellbat projectile
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "HELLBAT";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
