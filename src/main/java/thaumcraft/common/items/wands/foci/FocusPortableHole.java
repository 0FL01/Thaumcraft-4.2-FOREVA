package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusPortableHole extends ItemFocusBasic {

    public FocusPortableHole() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x2E0854;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.EARTH, 1000).add(Aspect.VOID, 500).add(Aspect.ENTROPY, 250);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: create portable hole
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "PORTABLE_HOLE";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
