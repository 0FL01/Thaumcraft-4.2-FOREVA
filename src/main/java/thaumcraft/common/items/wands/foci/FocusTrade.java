package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public class FocusTrade extends ItemFocusBasic {

    public FocusTrade() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x00CED1;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.AIR, 500).add(Aspect.EXCHANGE, 250);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        // Phase 8: open trade GUI
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "TRADE";
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
