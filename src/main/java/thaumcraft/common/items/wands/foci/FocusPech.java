package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusPech extends ItemFocusBasic {

    public FocusPech() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFD700;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.EARTH, 500).add(Aspect.EXCHANGE, 500);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: open Pech trade GUI
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "PECH";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
