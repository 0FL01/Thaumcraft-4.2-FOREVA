package thaumcraft.common.items.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.wands.IWandRodOnUpdate;

public class WandRodPrimalOnUpdate implements IWandRodOnUpdate {

    @Override
    public void onUpdate(ItemStack itemstack, EntityPlayer player) {
        // Primal rod special effects - TBD
        // Self-repair, vis regen bonus, etc.
    }
}
